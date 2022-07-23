package com.example.sharedexoplayer

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.view.setMargins
import androidx.transition.Transition
import androidx.transition.TransitionValues
import java.lang.Math.abs

class MyAnimation() : Transition() {
    private val PROPNAME_X = "com.example.sharedexoplayer.MyAnimation:x"
    private val PROPNAME_Y = "com.example.sharedexoplayer.MyAnimation:y"
    private val PROPNAME_WIDTH = "com.example.sharedexoplayer.MyAnimation:with"
    private val PROPNAME_HEIGHT = "com.example.sharedexoplayer.MyAnimation:height"

    var targetView: View? = null

    override fun captureStartValues(transitionValues: TransitionValues) {
        capture(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        capture(transitionValues)
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        startValues ?: return null
        endValues ?: return null
        val x = getState(startValues, endValues, PROPNAME_X) ?: return null
        val y = getState(startValues, endValues, PROPNAME_Y) ?: return null
        val width = getState(startValues, endValues, PROPNAME_WIDTH) ?: return null
        val height = getState(startValues, endValues, PROPNAME_HEIGHT) ?: return null
        val view = targetView ?: endValues.view ?: return null
        return ValueAnimator
            .ofFloat(0F, 1F)
            .apply {
                addUpdateListener { valAnim ->
                    val progress = valAnim.animatedValue as Float
                    view.layoutParams = FrameLayout.LayoutParams(
                        width.next(progress).toInt(),
                        height.next(progress).toInt()
                    ).apply {
                        setMargins(x.next(progress).toInt(), y.next(progress).toInt(), 0, 0)
                    }
                }
            }
    }

    private fun capture(transitionValues: TransitionValues) {
        transitionValues.view?.let { view ->
            transitionValues.values[PROPNAME_X] = view.x
            transitionValues.values[PROPNAME_Y] = view.y
            transitionValues.values[PROPNAME_WIDTH] = view.width.toFloat()
            transitionValues.values[PROPNAME_HEIGHT] = view.height.toFloat()
        }
    }

    private fun getState(
        startValues: TransitionValues,
        endValues: TransitionValues,
        key: String
    ): State? {
        val start = startValues.values[key] as? Float ?: return null
        val end = endValues.values[key] as? Float ?: return null
        return State(start, end, end - start)
    }

    data class State(val start: Float, val end: Float, val diff: Float) {

        fun next(progress: Float): Float {
            return start + diff * progress
        }
    }
}