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
    private var lastRequestedRoute: String = ""
    private var currentStability: Int = 100
    private val lock = Any()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        // FIX (BUG-2): Observe stability changes in real-time via StateFlow.
        // Triggers automatic track switching when crossing critical thresholds.
        gameRepository.get().gameState
            .onEach { state ->
                val oldStability = currentStability
                currentStability = state.world.globalStability
                
                // REACTION: If stability drops below threshold, force switch to glitch track
                if (oldStability >= 20 && currentStability < 20) {
                    playMusic(R.raw.ost_glitch_ambient)
                } 
                // RECOVERY: If stability recovers, return to intended area music
                else if (oldStability < 20 && currentStability >= 20) {
                    recoverMusic()
                }
                
                applyDynamicEffects()
            }
            .launchIn(scope)
    }

    private fun applyDynamicEffects() {
        synchronized(lock) {
            val player = musicPlayer
            if (player != null && try { player.isPlaying } catch (e: Exception) { false }) {
                // Pitch shifting effect for extreme instability
                if (currentStability < 10) {
                    try {
                        val pitch = 0.8f + (android.os.SystemClock.elapsedRealtime() % 400) / 1000f
                        player.setPlaybackParams(player.playbackParams.setPitch(pitch))
                    } catch (ignore: Exception) {
                        resetPitch(player)
                    }
                } else {
                    resetPitch(player)
                }
            }
        }
    }

    private fun resetPitch(player: MediaPlayer) {
        try {
            if (player.playbackParams.pitch != 1.0f) {
                player.setPlaybackParams(player.playbackParams.setPitch(1.0f))
            }
        } catch (e: Exception) {}
    }

    fun playMusic(resId: Int, loop: Boolean = true) {
        synchronized(lock) {
            if (currentTrackResId == resId) return

            // BUG-05 (R4): Rapid switching could leak MediaPlayer if creation fails
            // Ensure full cleanup before attempting new creation
            stopMusicInternal()
            
            try {
                val newPlayer = MediaPlayer.create(context, resId)
                if (newPlayer == null) {
                    Log.e(TAG, "FATAL: Resource not found or MediaPlayer error for resId=$resId")
                    return
                }
                
                musicPlayer = newPlayer.apply {
                    isLooping = loop
                    start()
                }
                currentTrackResId = resId
                applyDynamicEffects()
            } catch (e: Exception) {
                Log.e(TAG, "Blad odtwarzania utworu resId=$resId", e)
                stopMusicInternal() // Clean up any partial state
            }
        }
    }

    fun stopMusic() {
        synchronized(lock) {
            stopMusicInternal()
            lastRequestedRoute = ""
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
        lastRequestedRoute = route
        
        // If world is already glitchy, override the request but save the route for later recovery
        if (currentStability < 20) {
            playMusic(R.raw.ost_glitch_ambient)
            return
        }

        val track = evaluateTrackForRoute(route)
        playMusic(track)
    }

    private fun recoverMusic() {
        if (lastRequestedRoute.isNotBlank()) {
            val track = evaluateTrackForRoute(lastRequestedRoute)
            playMusic(track)
        }
    }

    private fun evaluateTrackForRoute(route: String): Int {
        return when {
            route.contains("main_menu") -> R.raw.ost_main_menu
            route.contains("city") -> {
                val state = gameRepository.get().currentState()
                val currentCity = state.world.locationId.lowercase()
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
    }

    fun getCurrentStability(): Int = currentStability
}
