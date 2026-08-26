# Validación de Echo Brain en Spotui

La adaptación se publicó en [`jobjgmailcom/Spotui`](https://github.com/jobjgmailcom/Spotui) mediante los commits [`ae23cf9`](https://github.com/jobjgmailcom/Spotui/commit/ae23cf9f4f6e918ad9046cd64517c9ba043acf35) y [`4353121`](https://github.com/jobjgmailcom/Spotui/commit/4353121eac1ffa66eca42c19c32328dcad490458). Echo Brain reutiliza las recomendaciones Spotify que ya consume Spotui, inserta una candidata inmediatamente detrás de la pista activa y no sustituye la cola existente ni altera la resolución de streams.

| Área | Resultado |
| --- | --- |
| Activación | Visible en **Settings → Echo Brain** con interruptor, umbrales 90/80/70/60, control de versiones alternativas y diagnóstico local. |
| Protecciones | Deduplicación por ID e identidad título/artista, cooldown local de 24 horas, ventana de ocho artistas y una guarda por semilla. |
| Eficiencia | No hay sondeo, cuenta adicional, telemetría, servicio externo ni precarga de streams. Sólo se consulta la radio existente después de activar la función. |
| Compatibilidad | Se actualizó AGP a 8.10.1 y Gradle a 8.11.1 porque Kotlin 2.2 requiere R8 8.10.21; esto corrige el fallo D8 del empaquetado base. [1] [2] |
| CI | La ejecución [33009453251](https://github.com/jobjgmailcom/Spotui/actions/runs/33009453251) aprobó pruebas, ensamblado y auditoría con Java 17, `--no-daemon` y un trabajador. |

La APK auditada corresponde a `com.music.spotui` 1.4/14, incluye firma v2 y los ABIs disponibles en el instalador. El SHA-256 es `8bbc61c1d493409a69ad51e81b7f12643a3cbaec9a7567002ccfcee2999d35f7`. El escaneo de DEX y ZIP no detectó Firebase, Google Play Services ni Media3 Cast.

## Referencias

[1] [Compatibilidad oficial AGP, D8/R8 y Kotlin](https://developer.android.com/build/kotlin-support)

[2] [Notas de AGP 8.10.0](https://developer.android.com/build/releases/agp-8-10-0-release-notes)

## Recuperación de inicio de sesión Spotify

La captura del usuario mostró que la WebView de inicio de sesión podía quedar negra sin exponer un error. El commit [`c422321`](https://github.com/jobjgmailcom/Spotui/commit/c422321b58ee692bd85f35597dcf48cfd84a4f1b) espera a que la eliminación de cookies termine antes de navegar, usa una identidad de Chrome compatible y evita contenido mixto. También añade estados visibles de carga, detección de errores de red/HTTP, salida del proceso de renderizado y un botón **Reintentar** que crea una WebView limpia.

La CI [33012075279](https://github.com/jobjgmailcom/Spotui/actions/runs/33012075279) aprobó pruebas, ensamblado y auditoría FOSS. La APK resultante mantiene `com.music.spotui` 1.4/14, firma v2 y ausencia de Firebase, GMS y Media3 Cast. Su SHA-256 es `e74fd0f94301d3214484d87dcb9027331266c968e165a90da9fe66142e5b5817`.

[3] [Referencia oficial WebViewClient](https://developer.android.com/reference/android/webkit/WebViewClient)
