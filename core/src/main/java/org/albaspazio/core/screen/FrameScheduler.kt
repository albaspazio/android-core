package org.albaspazio.core.screen

import android.view.Choreographer
import java.util.*

/**
 * Centralized frame scheduler for multiple stimuli in a trial.
 * Maintains a queue of tasks to be executed at specific frames,
 * with a single FrameCallback registered to Choreographer.
 */
object FrameScheduler {
    
    private data class FrameTask(val targetFrame: Long, val callback: () -> Unit) : Comparable<FrameTask> {
        override fun compareTo(other: FrameTask): Int = targetFrame.compareTo(other.targetFrame)
    }

    private val taskQueue = PriorityQueue<FrameTask>()
    var currFrame = 0L
    private var frameCallback: Choreographer.FrameCallback? = null
    private var isRunning = false
    private var lastFrameTimeNanos = 0L
    private var currentFrameOffsetMs = 0f

    /**
     * Schedules a callback to be invoked at a specific frame.
     *
     * @param targetFrame Target frame number
     * @param callback Callback to invoke when the frame is reached
     */
    fun schedule(targetFrame: Long, callback: () -> Unit) {
        taskQueue.add(FrameTask(targetFrame, callback))
    }

    /**
     * Gets the current milliseconds remaining until the next frame sync.
     *
     * @param framePeriodMs Duration of one frame in milliseconds
     * @return Milliseconds until next VSYNC
     */
    fun getCurrentFrameOffsetMs(framePeriodMs: Float): Float {
        return currentFrameOffsetMs
    }

    /**
     * Starts the frame scheduler by registering with Choreographer.
     * Must be called after all tasks are scheduled.
     */
    fun start(framePeriodMs: Float = 16.67f) {
        if (isRunning) return
        
        isRunning = true
        currFrame = 0L
        
        frameCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                currFrame++
                lastFrameTimeNanos = frameTimeNanos
                
                // Calculate current frame offset
                val now = System.nanoTime()
                val timeSinceVsync = (now - frameTimeNanos) / 1_000_000f  // convert to ms
                currentFrameOffsetMs = framePeriodMs - timeSinceVsync
                
                // Execute all tasks whose frame has been reached
                while (taskQueue.isNotEmpty() && taskQueue.peek().targetFrame <= currFrame) {
                    taskQueue.poll().callback()
                }
                
                // Continue if there are more tasks, otherwise stop
                if (taskQueue.isNotEmpty()) {
                    Choreographer.getInstance().postFrameCallback(this)
                } else {
                    isRunning = false
                }
            }
        }
        
        Choreographer.getInstance().postFrameCallback(frameCallback!!)
    }

    /**
     * Resets the scheduler state for a new trial.
     * Clears all scheduled tasks and stops the scheduler.
     */
    fun reset() {
        if (frameCallback != null && isRunning) {
            // Note: We can't remove a specific callback from Choreographer,
            // but setting isRunning = false will prevent further processing
            isRunning = false
        }
        taskQueue.clear()
        currFrame = 0L
        frameCallback = null
        currentFrameOffsetMs = 0f
        lastFrameTimeNanos = 0L
    }
}
