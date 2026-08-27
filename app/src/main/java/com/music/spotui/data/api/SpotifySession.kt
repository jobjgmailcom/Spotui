package com.music.spotui.data.api

import android.content.Context

/**
 * Keeps a legacy Spotify web session only for users who already had one stored
 * before embedded Spotify login was removed. New sessions are not collected by
 * the application and playback never depends on this value.
 */
object SpotifySession {
    private const val PREFS = "spotify_session"
    private const val KEY_SP_DC = "sp_dc"

    // Optional compile-time default so the app has data on first launch.
    // Leave blank to require runtime configuration.
    private const val DEFAULT_SP_DC = ""

    fun spDc(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SP_DC, null)?.takeIf { it.isNotBlank() } ?: DEFAULT_SP_DC
    }

    fun setSpDc(context: Context, value: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SP_DC, value.trim())
            .apply()
    }
}
