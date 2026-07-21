package org.albaspazio.core.screen

import android.view.Choreographer
import kotlin.math.ceil

/**
 * Manages frame-based stimulus timing synchronization with Choreographer.
 * Provides utilities to convert millisecond timings to frame-based scheduling.
 */
object FrameSyncManager {

    /**
     * Calculates the frame number where a stimulus should be delivered,
     * given a target timing in milliseconds from now.
     *
     * @param targetMs Target timing in milliseconds from now
     * @param framePeriodMs Duration of one frame in milliseconds
     * @return Frame number (0-indexed) where the stimulus should be delivered
     */
    fun getTargetFrame(targetMs: Long, framePeriodMs: Float): Long {
        return ceil(targetMs.toFloat() / framePeriodMs).toLong()
    }

    /**
     * Calculates the delay (in ms) from now until a target frame occurs.
     *
     * @param targetFrame Target frame number
     * @param frameOffsetMs Milliseconds until the next frame sync from now
     * @param framePeriodMs Duration of one frame in milliseconds
     * @return Delay in milliseconds until the target frame
     */
    fun getDelayUntilFrame(targetFrame: Long, frameOffsetMs: Float, framePeriodMs: Float): Long {
        return (frameOffsetMs + (targetFrame - 1) * framePeriodMs).toLong()
    }

    /**
     * Schedules a callback to be invoked at a specific frame sync.
     * Registers with Choreographer and counts frame callbacks until target is reached.
     *
     * @param targetFrame Target frame number to invoke the callback
     * @param onFrameReached Callback to invoke when the target frame is reached
     */
    fun postAtFrame(targetFrame: Long, onFrameReached: () -> Unit) {
        if (targetFrame <= 0) {
            onFrameReached()
            return
        }

        var frameCounter = 0L
        val frameCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                frameCounter++
                if (frameCounter >= targetFrame) {
                    onFrameReached()
                } else {
                    Choreographer.getInstance().postFrameCallback(this)
                }
            }
        }
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }
}
