# Echo Brain attribution

This Spotui adaptation adds a local queue planner and a guarded Spotify-radio insertion flow inspired by the Echo Brain work supplied by the user. The adaptation is distributed under Spotui's GPLv3 license and keeps the existing Spotui, Spotify metadata, YouTube stream-resolution, queue, and crossfade components intact.

Echo Brain does not add accounts, telemetry, remote inference, secrets, keystores, background polling, or an external recommendation service. It only reuses Spotui's existing recommendation call after the user enables the setting.
