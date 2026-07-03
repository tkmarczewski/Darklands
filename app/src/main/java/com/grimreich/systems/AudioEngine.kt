package com.grimreich.systems

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.grimreich.R
import com.grimreich.core.GameRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gameRepository: dagger.Lazy<GameRepository>
) {
    companion object {
        private const val TAG = "AudioEngine"
    }

    private var musicPlayer: MediaPlayer? = null
    private var currentTrackResId: Int = 0
    private var currentStability: Int = 100
    private val lock = Any()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        // FIX (BUG-2): Observe stability changes in real-time via StateFlow
        // instead of reading stale snapshots
        gameRepository.get().gameState
            .onEach { state ->
                currentStability = state.world.globalStability
            }
            .launchIn(scope)
    }

    fun playMusic(resId: Int, loop: Boolean = true) {
        synchronized(lock) {
            if (currentTrackResId == resId) return

            stopMusicInternal()
            try {
                // FIX (BUG-2): Use the observable stability instead of stale snapshot
                musicPlayer = MediaPlayer.create(context, resId)?.apply {
                    isLooping = loop
                    
                    // --- PITCH WOBBLE (Project Cipher) ---
                    if (currentStability < 15) {
                        try {
                            // Simulating a "dying record player" effect
                            val pitch = 0.85f + (android.os.SystemClock.elapsedRealtime() % 300) / 1000f
                            setPlaybackParams(playbackParams.setPitch(pitch))
                        } catch (e: Exception) {
                            // Some devices might not support pitch shifting
                            Log.w(TAG, "Pitch shifting not supported on this device", e)
                        }
                    }

                    start()
                }
                currentTrackResId = resId
            } catch (e: Exception) {
                Log.e(TAG, "Blad odtwarzania utworu resId=$resId", e)
                musicPlayer = null
                currentTrackResId = 0
            }
        }
    }

    fun stopMusic() {
        synchronized(lock) {
            stopMusicInternal()
        }
    }

    private fun stopMusicInternal() {
        try {
            musicPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Blad podczas zwalniania MediaPlayer", e)
        } finally {
            musicPlayer = null
            currentTrackResId = 0
        }
    }

    fun playForRoute(route: String) {
        // FIX (BUG-2): Use the cached stability to avoid race conditions
        val track = when {
            currentStability < 20 -> R.raw.ost_glitch_ambient
            route.contains("main_menu") -> R.raw.ost_main_menu
            route.contains("city") -> {
                val state = gameRepository.get().currentState()
                val currentCity = state.grimCurrentRegion.lowercase()
                when {
                    currentCity.contains("zakon") || currentCity.contains("fortress") -> R.raw.ost_faction_order
                    currentCity.contains("serce") || currentCity.contains("heart") -> R.raw.ost_magic_location
                    else -> R.raw.ost_city
                }
            }
            route.contains("combat") -> {
                val state = gameRepository.get().currentState()
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

    fun getCurrentStability(): Int = currentStability
}
