package com.music.spotui.data.preferences

import android.content.Context
import com.music.spotui.data.entity.SongsModel

enum class EchoBrainSimilarity(val label: String, val minimumScore: Int) {
    STRICT("90% · Muy estricta", 90),
    HIGH("80% · Alta", 80),
    BALANCED("70% · Equilibrada", 70),
    FLEXIBLE("60% · Flexible", 60),
}

object EchoBrainSettings {
    private const val KEY_ENABLED = "echo_brain_enabled"
    private const val KEY_SIMILARITY = "echo_brain_similarity"
    private const val KEY_ALTERNATIVES = "echo_brain_allow_alternatives"
    private const val KEY_DIAGNOSTIC = "echo_brain_last_diagnostic"
    private const val COOLDOWN_PREFIX = "echo_brain_cooldown_"
    const val COOLDOWN_MILLIS = 24L * 60L * 60L * 1000L
    const val ARTIST_WINDOW = 8

    private fun echoPrefs(context: Context) = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean = echoPrefs(context).getBoolean(KEY_ENABLED, false)
    fun setEnabled(context: Context, value: Boolean) = echoPrefs(context).edit().putBoolean(KEY_ENABLED, value).apply()

    fun getSimilarity(context: Context): EchoBrainSimilarity =
        runCatching {
            EchoBrainSimilarity.valueOf(
                echoPrefs(context).getString(KEY_SIMILARITY, EchoBrainSimilarity.STRICT.name).orEmpty(),
            )
        }.getOrDefault(EchoBrainSimilarity.STRICT)

    fun setSimilarity(context: Context, value: EchoBrainSimilarity) =
        echoPrefs(context).edit().putString(KEY_SIMILARITY, value.name).apply()

    fun allowAlternativeVersions(context: Context): Boolean =
        echoPrefs(context).getBoolean(KEY_ALTERNATIVES, false)

    fun setAllowAlternativeVersions(context: Context, value: Boolean) =
        echoPrefs(context).edit().putBoolean(KEY_ALTERNATIVES, value).apply()

    fun setDiagnostic(context: Context, value: String) =
        echoPrefs(context).edit().putString(KEY_DIAGNOSTIC, value).apply()

    fun getDiagnostic(context: Context): String =
        echoPrefs(context).getString(KEY_DIAGNOSTIC, "Echo Brain está listo") ?: "Echo Brain está listo"

    fun recordCooldown(context: Context, songId: Int) =
        echoPrefs(context).edit().putLong("$COOLDOWN_PREFIX$songId", System.currentTimeMillis()).apply()

    fun cooldownSongIds(context: Context, candidates: List<SongsModel>): Set<Int> {
        val now = System.currentTimeMillis()
        val preferences = echoPrefs(context)
        return candidates.asSequence()
            .filter { candidate -> now - preferences.getLong("$COOLDOWN_PREFIX${candidate.id}", 0L) < COOLDOWN_MILLIS }
            .map { it.id }
            .toSet()
    }
}
