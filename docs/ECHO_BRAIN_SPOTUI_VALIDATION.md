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

## Acceso Spotify sin WebView obligatoria

La corrección previa de WebView del commit [`c422321`](https://github.com/jobjgmailcom/Spotui/commit/c422321b58ee692bd85f35597dcf48cfd84a4f1b) no resolvió el dispositivo reportado: Spotify seguía devolviendo una página vacía o «Something went wrong». Por ello, esta revisión no presenta esa intervención como una solución válida y retira la WebView, el formulario de credenciales, la inyección JavaScript y la captura automática de cookies de Spotify.

| Capa | Comportamiento de esta revisión |
| --- | --- |
| Audio | Spotui conserva Deezer como primera fuente de stream, con las fuentes lossless/YouTube existentes como respaldo. La ausencia de metadatos Spotify no bloquea llegar a la aplicación ni configurar Deezer. |
| Metadatos | Spotify sigue siendo la fuente de catálogo, búsqueda, radio, sincronización de biblioteca y candidatas Echo Brain cuando exista una sesión heredada o una futura conexión admitida. |
| Echo Brain | El interruptor, reglas 90/80/70/60, deduplicación, cooldown, diversidad y la cola no cambian. Si no hay metadatos Spotify, Ajustes explica que no puede recuperar ni inyectar nuevas candidatas. |
| Seguridad y recuperación | La ruta de acceso deja de pedir o capturar contraseñas. La pantalla informativa permite continuar a Spotui y configurar Deezer sin pantalla negra ni un formulario Spotify integrado. |

No se debe interpretar esta revisión como que Spotify se conectó de forma automática: sin una conexión Spotify admitida, las funciones de catálogo/radio y la inyección de candidatas permanecen explícitamente no disponibles. Esta decisión separa ese límite de la reproducción de audio mediante Deezer.
