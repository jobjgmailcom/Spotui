# Diseño de Echo Brain para Spotui

Spotui mantiene una única fuente de audio resuelta y una cola en memoria. Por ello, Echo Brain no modifica el reproductor Media3 ni reemplaza la lista del usuario: inserta una única pista candidata en la posición posterior a la activa dentro de `CurrentSongState.queue`.

| Aspecto | Decisión de diseño |
| --- | --- |
| Candidatas | Se reutiliza `provideRecommendations()` de Spotui, ya sembrado por el identificador de la pista Spotify activa. No se añade un servicio de recomendación, cuenta ni telemetría nuevos. |
| Similitud | Selector 90/80/70/60. El planificador aplica una señal base de radio Spotify y refuerza por artista principal y álbum; nunca inserta por debajo del nivel seleccionado. |
| Seguridad de cola | Una única inserción tras la pista activa; se preservan todos los elementos existentes y se deduplica por ID e identidad canónica título/artista. |
| Versiones y repetición | Las variantes se bloquean por defecto. El mismo ID queda en cooldown local de 24 horas y se evita la repetición de artistas recientes. |
| Eficiencia | Una guarda por semilla evita solicitudes simultáneas. Sólo consulta cuando inicia una pista válida y el interruptor está activo. No existe sondeo, proceso persistente, IA remota ni descarga anticipada. |
| Integración | `CurrentSongState` notifica al coordinador tanto para avance normal como crossfade y controles de Android Auto; la inserción vuelve al hilo principal y conserva el shuffle. |

La configuración visible estará en **Settings → Echo Brain**, con interruptor, nivel de similitud, variantes alternativas y una línea de diagnóstico local. Todo el estado de protección se guarda únicamente en `SharedPreferences` del dispositivo.
