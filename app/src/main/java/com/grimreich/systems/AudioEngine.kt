package com.grimreich.systems

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.grimreich.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gameRepository: dagger.Lazy<com.grimreich.core.GameRepository>
) {
    companion object {
        private const val TAG = "AudioEngine"
    }

    private var musicPlayer: MediaPlayer? = null
    private var currentTrackResId: Int = 0

    fun playMusic(resId: Int, loop: Boolean = true) {
        if (currentTrackResId == resId) return

        stopMusic()
        try {
            val state = gameRepository.get().currentState()
            val stability = state.world.globalStability
            
            musicPlayer = MediaPlayer.create(context, resId).apply {
                isLooping = loop
                
                // --- PITCH WOBBLE (Project Cipher) ---
                if (stability < 10) {
                    // Simulating a "dying record player" effect
                    setPlaybackParams(playbackParams.setPitch(0.8f + (android.os.SystemClock.elapsedRealtime() % 400) / 1000f))
                }

                start()
            }
            // Bug fix: only update currentTrackResId on success, not before catch
            currentTrackResId = resId
        } catch (e: Exception) {
            Log.e(TAG, "Blad odtwarzania utworu resId=$resId", e)
            // Ensure player state is clean after failure
            musicPlayer = null
            currentTrackResId = 0
        }
    }

    fun stopMusic() {
        try {
            musicPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (e: Exception) {
            // Guard against OBS-05: IllegalStateException during rapid release or double release
        } finally {
            musicPlayer = null
            currentTrackResId = 0
        }
    }

    fun playForRoute(route: String) {
        val state = gameRepository.get().currentState()
        val stability = state.world.globalStability

        val track = when {
            stability < 20 -> R.raw.ost_glitch_ambient
            route.contains("main_menu") -> R.raw.ost_main_menu
            route.contains("city") -> {
                val currentCity = state.grimCurrentRegion.lowercase()
                when {
                    currentCity.contains("zakon") || currentCity.contains("fortress") -> R.raw.ost_faction_order
                    currentCity.contains("serce") || currentCity.contains("heart") -> R.raw.ost_magic_location
                    else -> R.raw.ost_city
                }
            }
            route.contains("combat") -> {
                if (state.combat.enemyMaxHp > 100) R.raw.ost_combat_boss else R.raw.ost_combat_normal
            }
            route.contains("expedition") || route.contains("events") -> R.raw.ost_exploration
            route.contains("tavern") -> R.raw.ost_tavern
            route.contains("market") -> R.raw.ost_market
            route.contains("ending") -> R.raw.ost_epilogue
            route.contains("death") || route.contains("ritual") -> R.raw.ost_death
            else -> R.raw.ost_main_theme
        }
        playMusic(track)
    }
}
