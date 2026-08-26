package com.music.spotui.playback

import com.music.spotui.MyApplication
import com.music.spotui.data.entity.SongsModel
import com.music.spotui.data.preferences.EchoBrainSettings
import com.music.spotui.di.CurrentSongState
import com.music.spotui.ui.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bridges playback events to one local, guarded queue insertion. The coordinator
 * reuses Spotui's normal Spotify recommendations source and never touches streams.
 */
@Singleton
class EchoBrainCoordinator @Inject constructor(
    private val currentSongState: CurrentSongState,
    private val repository: AppRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val activeSeeds = ConcurrentHashMap.newKeySet<Int>()

    init {
        currentSongState.onTrackStarted = { seed, queue, index, repeatOne ->
            scheduleInjection(seed, queue, index, repeatOne)
        }
    }

    /** Ensures field injection creates this coordinator for both UI and Android Auto playback. */
    fun activate() = Unit

    private fun scheduleInjection(
        seed: SongsModel,
        queueSnapshot: List<SongsModel>,
        activeIndex: Int,
        repeatOne: Boolean,
    ) {
        val context = MyApplication.instance
        if (!EchoBrainSettings.isEnabled(context)) return
        if (repeatOne || seed.spotifyTrackId.isBlank()) {
            EchoBrainSettings.setDiagnostic(
                context,
                if (repeatOne) "Echo Brain omitido: repetir pista está activo" else "Echo Brain omitido: falta ID de Spotify",
            )
            return
        }
        if (!activeSeeds.add(seed.id)) return

        scope.launch {
            try {
                val candidates = repository.provideRecommendations(listOf(seed.spotifyTrackId))
                val recentArtists = queueSnapshot
                    .take(activeIndex + 1)
                    .takeLast(EchoBrainSettings.ARTIST_WINDOW)
                    .map { EchoBrainQueuePlanner.primaryArtist(it.singer) }
                    .filter { it.isNotBlank() }
                    .toSet()
                val choice = EchoBrainQueuePlanner.choose(
                    seed = seed,
                    candidates = candidates,
                    currentQueue = queueSnapshot,
                    similarity = EchoBrainSettings.getSimilarity(context),
                    allowAlternativeVersions = EchoBrainSettings.allowAlternativeVersions(context),
                    cooldownSongIds = EchoBrainSettings.cooldownSongIds(context, candidates),
                    recentArtistKeys = recentArtists,
                )
                if (choice == null) {
                    EchoBrainSettings.setDiagnostic(context, "Echo Brain: no encontró una recomendación segura")
                    return@launch
                }

                withContext(Dispatchers.Main.immediate) {
                    val latestQueue = currentSongState.queue.value
                    val latestIndex = latestQueue.indexOfFirst { it.id == seed.id }
                    if (currentSongState.songId.value != seed.id || latestIndex < 0) {
                        EchoBrainSettings.setDiagnostic(context, "Echo Brain: la pista cambió antes de insertar")
                        return@withContext
                    }
                    if (latestQueue.any { it.id == choice.song.id } ||
                        latestQueue.any { EchoBrainQueuePlanner.canonicalIdentity(it) == EchoBrainQueuePlanner.canonicalIdentity(choice.song) }
                    ) {
                        EchoBrainSettings.setDiagnostic(context, "Echo Brain: recomendación ya presente en la cola")
                        return@withContext
                    }
                    val updatedQueue = latestQueue.toMutableList().apply {
                        add((latestIndex + 1).coerceAtMost(size), choice.song)
                    }
                    currentSongState.updateQueue(updatedQueue)
                    EchoBrainSettings.recordCooldown(context, choice.song.id)
                    EchoBrainSettings.setDiagnostic(context, "Echo Brain · ${choice.similarity}% · ${choice.reason}")
                }
            } catch (_: Exception) {
                EchoBrainSettings.setDiagnostic(context, "Echo Brain: no se pudieron cargar recomendaciones")
            } finally {
                activeSeeds.remove(seed.id)
            }
        }
    }
}
