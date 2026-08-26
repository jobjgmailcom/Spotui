package com.music.spotui.ui.screens

/** User-facing messages for main-frame WebView failures. Kept pure for JVM tests. */
object SpotifyLoginWebPolicy {
    fun networkError(code: Int, description: String): String =
        "Spotify no se pudo cargar (error $code: ${description.ifBlank { "sin detalle" }}). Comprueba tu red y vuelve a intentar."

    fun httpError(statusCode: Int): String =
        "Spotify rechazó la página de inicio de sesión (HTTP $statusCode). Vuelve a intentar en unos momentos."

    const val RENDERER_ERROR =
        "El navegador interno de Spotify se detuvo. Pulsa Reintentar para abrir una sesión nueva."

    const val EMPTY_PAGE_ERROR =
        "Spotify no mostró el formulario de inicio de sesión. Pulsa Reintentar para recargarlo."
}
