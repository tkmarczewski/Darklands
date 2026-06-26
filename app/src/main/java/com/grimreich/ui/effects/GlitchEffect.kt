package com.grimreich.ui.effects

import android.graphics.RenderEffect
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import kotlin.random.Random

/**
 * Applies a visual "reality glitch" effect to a Composable.
 * Uses jitter (shaking) and optional blur on API 31+.
 */
fun Modifier.glitchEffect(active: Boolean, intensity: Float = 1f): Modifier = composed {
    if (!active) return@composed this

    // Continuous animation to drive randomness
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    val tick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tick"
    )

    // Compute random jitter offsets based on the animation tick
    val randomOffset = remember(tick) {
        if (Random.nextFloat() < 0.4f * intensity) {
            IntOffset(
                Random.nextInt(-15, 16) * intensity.toInt().coerceAtLeast(1),
                Random.nextInt(-8, 9) * intensity.toInt().coerceAtLeast(1)
            )
        } else {
            IntOffset.Zero
        }
    }

    this.graphicsLayer {
        translationX = randomOffset.x.toFloat()
        translationY = randomOffset.y.toFloat()
        
        // Scale fluctuation
        if (Random.nextFloat() < 0.1f * intensity) {
            scaleX = 1f + (Random.nextFloat() - 0.5f) * 0.05f * intensity
            scaleY = 1f + (Random.nextFloat() - 0.5f) * 0.05f * intensity
        }

        // Apply Blur on Android 12+ if intensity is high
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && intensity > 0.8f) {
            if (Random.nextFloat() < 0.2f) {
                renderEffect = RenderEffect.createBlurEffect(
                    4f * intensity, 4f * intensity, android.graphics.Shader.TileMode.CLAMP
                ).asComposeRenderEffect()
            }
        }

        // --- UI DECAY (Phase 6) ---
        if (intensity > 3.0f && Random.nextFloat() < 0.05f) {
            rotationZ = (Random.nextFloat() - 0.5f) * 10f
            alpha = 0.6f + Random.nextFloat() * 0.4f
        }
    }
}
