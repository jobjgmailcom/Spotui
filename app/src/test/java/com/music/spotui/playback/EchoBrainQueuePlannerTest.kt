package com.music.spotui.playback

import com.music.spotui.data.entity.SongsModel
import com.music.spotui.data.preferences.EchoBrainSimilarity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EchoBrainQueuePlannerTest {
    private fun song(
        id: Int,
        title: String,
        artist: String,
        album: String = "",
    ) = SongsModel(id, title, album, artist, "", "query-$id", "spotify-$id")

    @Test
    fun `strict mode requires the strongest artist and album signal`() {
        val seed = song(1, "Signal", "Artist A", "Album A")
        val selected = EchoBrainQueuePlanner.choose(
            seed,
            listOf(song(2, "Related", "Artist A", "Album A"), song(3, "Other", "Artist B", "Album B")),
            listOf(seed), EchoBrainSimilarity.STRICT, false, emptySet(), emptySet(),
        )
        assertEquals(2, selected?.song?.id)
        assertEquals(90, selected?.similarity)
    }

    @Test
    fun `planner rejects duplicates cooldowns and alternative versions`() {
        val seed = song(1, "Signal", "Artist A", "Album A")
        val selected = EchoBrainQueuePlanner.choose(
            seed,
            listOf(song(2, "Signal Remix", "Artist A", "Album A"), song(3, "Safe", "Artist A", "Album A")),
            listOf(seed, song(3, "Safe", "Artist A", "Album A")),
            EchoBrainSimilarity.FLEXIBLE, false, setOf(2), emptySet(),
        )
        assertNull(selected)
    }

    @Test
    fun `planner avoids recent artists unless they are the active seed artist`() {
        val seed = song(1, "Signal", "Artist A")
        val selected = EchoBrainQueuePlanner.choose(
            seed,
            listOf(song(2, "Artist B track", "Artist B"), song(3, "Artist A track", "Artist A")),
            listOf(seed), EchoBrainSimilarity.FLEXIBLE, false, emptySet(), setOf("artist b"),
        )
        assertEquals(3, selected?.song?.id)
    }
}
