package com.grimreich.ui.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.grimreich.core.GameRepository
import kotlin.random.Random

/**
 * Custom view that renders "Reality Leak" glitch effects based on Global Stability.
 */
class GlitchOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var stability: Int = 100
    private val updateRunnable = Runnable { 
        invalidate() 
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        stability = GameRepository.state.world.globalStability
        if (stability >= 70) return

        val intensity = (70 - stability).coerceAtLeast(0)
        
        // Random horizontal shifts (Reality Jitter)
        if (Random.nextInt(100) < intensity) {
            val shift = Random.nextInt(intensity) - (intensity / 2)
            canvas.translate(shift.toFloat(), 0f)
        }

        // Trigger next frame for animation
        postDelayed(updateRunnable, 100)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(updateRunnable)
    }
}
