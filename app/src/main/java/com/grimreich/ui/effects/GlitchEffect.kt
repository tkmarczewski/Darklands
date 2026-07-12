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
 * PERFORMANCE OPTIMIZED: Random calls are cached per tick.
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

    // CACHED CALCULATIONS: Pre-compute random values to avoid heavy calls in graphicsLayer
    val glitchParams by remember(tick, intensity) {
        derivedStateOf {
            val hasJitter = Random.nextFloat() < 0.4f * intensity
            val jitter = if (hasJitter) {
                IntOffset(
                    Random.nextInt(-15, 16) * intensity.toInt().coerceAtLeast(1),
                    Random.nextInt(-8, 9) * intensity.toInt().coerceAtLeast(1)
                )
            } else IntOffset.Zero

            val hasScale = Random.nextFloat() < 0.1f * intensity
            val scaleX = if (hasScale) 1f + (Random.nextFloat() - 0.5f) * 0.05f * intensity else 1f
            val scaleY = if (hasScale) 1f + (Random.nextFloat() - 0.5f) * 0.05f * intensity else 1f

            val hasBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && intensity > 0.8f && Random.nextFloat() < 0.2f
            
            val hasUIFlow = intensity > 3.0f && Random.nextFloat() < 0.05f
            val rotation = if (hasUIFlow) (Random.nextFloat() - 0.5f) * 10f else 0f
            val alpha = if (hasUIFlow) 0.6f + Random.nextFloat() * 0.4f else 1.0f

            GlitchParams(jitter, scaleX, scaleY, hasBlur, rotation, alpha)
        }
    }

    this.graphicsLayer {
        translationX = glitchParams.jitter.x.toFloat()
        translationY = glitchParams.jitter.y.toFloat()
        scaleX = glitchParams.scaleX
        scaleY = glitchParams.scaleY
        rotationZ = glitchParams.rotation
        alpha = glitchParams.alpha

        // Apply Blur on Android 12+ if intensity is high
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && glitchParams.hasBlur) {
            renderEffect = RenderEffect.createBlurEffect(
                4f * intensity, 4f * intensity, android.graphics.Shader.TileMode.CLAMP
            ).asComposeRenderEffect()
        }
    }
}

private data class GlitchParams(
    val jitter: IntOffset,
    val scaleX: Float,
    val scaleY: Float,
    val hasBlur: Boolean,
    val rotation: Float,
    val alpha: Float
)
