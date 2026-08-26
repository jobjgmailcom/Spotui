package com.music.spotui.playback

import com.music.spotui.data.entity.SongsModel
import com.music.spotui.data.preferences.EchoBrainSimilarity
import java.text.Normalizer
import java.util.Locale

data class EchoBrainChoice(
    val song: SongsModel,
    val similarity: Int,
    val reason: String,
)

/**
 * Pure, deterministic selector for candidates returned by Spotui's own Spotify
 * recommendations endpoint. It never fetches data or mutates the queue.
 */
object EchoBrainQueuePlanner {
    private val variantPattern = Regex(
        "\\b(remix|remaster(?:ed)?|live|acoustic|karaoke|instrumental|slowed|sped up|radio edit|extended|version)\\b",
        RegexOption.IGNORE_CASE,
    )

    fun choose(
        seed: SongsModel,
        candidates: List<SongsModel>,
        currentQueue: List<SongsModel>,
        similarity: EchoBrainSimilarity,
        allowAlternativeVersions: Boolean,
        cooldownSongIds: Set<Int>,
        recentArtistKeys: Set<String>,
    ): EchoBrainChoice? {
        val existingIds = currentQueue.mapTo(mutableSetOf()) { it.id }
        val existingIdentities = currentQueue.mapTo(mutableSetOf()) { canonicalIdentity(it) }
        val seedArtist = primaryArtist(seed.singer)
        val seedTitle = canonicalText(seed.title)

        return candidates.asSequence()
            .filter { it.id > 0 && it.id !in existingIds && it.id !in cooldownSongIds }
            .filter { canonicalIdentity(it) !in existingIdentities }
            .filter { candidate ->
                allowAlternativeVersions || (
                    !isAlternativeVersion(candidate) && canonicalText(candidate.title) != seedTitle
                )
            }
            .map { candidate ->
                val candidateArtist = primaryArtist(candidate.singer)
                val score = similarityScore(seed, candidate)
                Triple(candidate, candidateArtist, score)
            }
            .filter { (_, candidateArtist, score) ->
                score >= similarity.minimumScore &&
                    (candidateArtist !in recentArtistKeys || candidateArtist == seedArtist)
            }
            .sortedWith(
                compareByDescending<Triple<SongsModel, String, Int>> { it.third }
                    .thenBy { canonicalText(it.first.title) }
                    .thenBy { it.first.id },
            )
            .map { (candidate, candidateArtist, score) ->
                val reason = when {
                    candidateArtist == seedArtist && canonicalText(candidate.album) == canonicalText(seed.album) ->
                        "Spotify radio · artista y álbum coinciden"
                    candidateArtist == seedArtist -> "Spotify radio · artista coincide"
                    canonicalText(candidate.album).isNotBlank() &&
                        canonicalText(candidate.album) == canonicalText(seed.album) -> "Spotify radio · álbum coincide"
                    else -> "Spotify radio · recomendación segura"
                }
                EchoBrainChoice(candidate, score, reason)
            }
            .firstOrNull()
    }

    fun canonicalIdentity(song: SongsModel): String =
        "${canonicalText(song.title)}|${primaryArtist(song.singer)}"

    fun primaryArtist(value: String): String =
        canonicalText(value.split(Regex("(?i)\\s+(feat\\.|featuring|with|&|x)\\s+"), limit = 2).firstOrNull().orEmpty())

    fun isAlternativeVersion(song: SongsModel): Boolean =
        variantPattern.containsMatchIn(song.title) || variantPattern.containsMatchIn(song.album)

    private fun similarityScore(seed: SongsModel, candidate: SongsModel): Int {
        var score = 60 // Spotify supplied the candidate from its track radio.
        if (primaryArtist(seed.singer).isNotBlank() && primaryArtist(seed.singer) == primaryArtist(candidate.singer)) {
            score += 20
        }
        val seedAlbum = canonicalText(seed.album)
        if (seedAlbum.isNotBlank() && seedAlbum == canonicalText(candidate.album)) score += 10
        return score.coerceAtMost(90)
    }

    private fun canonicalText(value: String): String =
        Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}"), "")
            .lowercase(Locale.ROOT)
            .replace(Regex("[\\[(][^\\])]*[\\])]|[^a-z0-9]+"), " ")
            .trim()
            .replace(Regex("\\s+"), " ")
}
