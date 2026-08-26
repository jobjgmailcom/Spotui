package com.music.spotui.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpotifyLoginWebPolicyTest {
    @Test
    fun `network errors preserve the actionable code`() {
        assertTrue(SpotifyLoginWebPolicy.networkError(-8, "timeout").contains("-8: timeout"))
    }

    @Test
    fun `http errors preserve the server status`() {
        assertEquals(
            "Spotify rechazó la página de inicio de sesión (HTTP 403). Vuelve a intentar en unos momentos.",
            SpotifyLoginWebPolicy.httpError(403),
        )
    }
}
