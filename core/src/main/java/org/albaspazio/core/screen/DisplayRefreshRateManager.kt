package org.albaspazio.core.screen

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.Window
import android.view.WindowManager
import kotlin.math.round

/**
 * Manages device display refresh rate detection and caching.
 *
 * This singleton class queries the device for supported refresh rates and caches
 * the maximum available rate. It provides utilities for converting milliseconds
 * to refresh points based on the device's refresh rate.
 *
 * The refresh rate is queried once per app lifecycle and cached to avoid repeated
 * system calls. The query adapts to different Android API levels:
 * - API 30+: Uses Display.getSupportedModes() for comprehensive mode info
 * - API <30: Falls back to Display.getRefreshRate() for basic rate
 *
 * Usage:
 * ```
 * // Initialize (typically in app startup)
 * DisplayRefreshRateManager.init(context)
 *
 * // Get refresh rate (frame period in ms)
 * val frameMs = DisplayRefreshRateManager.getFramePeriodMs()  // e.g., 8.33ms at 120Hz
 *
 * // Convert milliseconds to refresh points
 * val refreshPoints = DisplayRefreshRateManager.msToRefreshPoints(100f)  // 100ms → frame count
 * ```
 */
object DisplayRefreshRateManager {

    private var maxRefreshRate: Float = 60f  // Default to 60Hz
    private var isInitialized: Boolean = false
    private const val LOG_TAG = "DisplayRefreshRateManager"

    /**
     * Initializes the display refresh rate manager by querying the device.
     * Should be called once during app startup.
     *
     * @param context Application context used to access WindowManager
     */
    @Synchronized
    fun init(context: Context) {
        if (isInitialized) return

        maxRefreshRate = queryMaxRefreshRate(context)
        isInitialized = true

        Log.d(LOG_TAG, "Initialized with max refresh rate: ${maxRefreshRate}Hz (frame period: ${getFramePeriodMs()}ms)")
    }

    /**
     * Gets the maximum supported refresh rate in Hz.
     *
     * @return Refresh rate in Hz (e.g., 60, 90, 120, 144)
     * @throws IllegalStateException if init() was not called
     */
    fun getMaxRefreshRate(): Float {
        check(isInitialized) { "DisplayRefreshRateManager not initialized. Call init(context) first." }
        return maxRefreshRate
    }

    /**
     * Gets the frame period (time between frames) in milliseconds.
     *
     * @return Frame period in ms (e.g., 16.67ms at 60Hz, 8.33ms at 120Hz)
     * @throws IllegalStateException if init() was not called
     */
    fun getFramePeriodMs(): Float {
        check(isInitialized) { "DisplayRefreshRateManager not initialized. Call init(context) first." }
        return 1000f / maxRefreshRate
    }

    /**
     * Converts a time interval in milliseconds to refresh points (frames).
     * Uses standard rounding to nearest integer.
     *
     * @param timeMs Time interval in milliseconds
     * @return Number of frames rounded to nearest integer
     * @throws IllegalStateException if init() was not called
     */
    fun msToFrames(timeMs: Float): Int {
        check(isInitialized) { "DisplayRefreshRateManager not initialized. Call init(context) first." }
        val framePeriodMs = getFramePeriodMs()
        return round(timeMs / framePeriodMs).toInt()
    }

    /**
     * Converts frames to milliseconds.
     *
     * @param frames Number of frames
     * @return Time interval in milliseconds
     * @throws IllegalStateException if init() was not called
     */
    fun framesToMs(frames: Int): Float {
        check(isInitialized) { "DisplayRefreshRateManager not initialized. Call init(context) first." }
        return frames * getFramePeriodMs()
    }

    /**
     * Queries the device for the maximum supported refresh rate.
     * Uses different APIs depending on Android version.
     *
     * @param context Application context
     * @return Maximum refresh rate in Hz
     */
    private fun queryMaxRefreshRate(context: Context): Float {
        return try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // API 30+: Get all supported modes and extract max refresh rate
                val modes = display.supportedModes
                if (modes.isNotEmpty()) {
                    // Collect all refresh rates from all modes, including alternativeRefreshRates
                    val allRefreshRates = mutableListOf<Float>()
                    modes.forEach { mode ->
                        allRefreshRates.add(mode.refreshRate)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            allRefreshRates.addAll(mode.alternativeRefreshRates.toList())
                        }
                    }
                    allRefreshRates.maxOrNull() ?: display.refreshRate
                } else {
                    display.refreshRate
                }
            }
            else  display.refreshRate // API <30: Use simple getRefreshRate()

        } catch (e: Exception) {
            Log.w(LOG_TAG, "Failed to query refresh rate: ${e.message}. Using default 60Hz.")
            60f
        }
    }

    /**
     * Resets the manager state. Useful for testing or reinitializing.
     */
    fun reset() {
        maxRefreshRate = 60f
        isInitialized = false
    }

    /**
     * Sets the display to use the maximum available refresh rate.
     * Intelligently chooses the best refresh rate considering both refreshRate and alternativeRefreshRates.
     * tested with a single Display$Mode object from Display.getSupportedModes() that contains alternativeRefreshRates: a list of supported refresh rates
     *
     * @param context Application context
     * @param window Window to configure
     */
    fun setMaxRefreshRate(window: Window) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return  // Not supported on API <30
            }
            maxRefreshRate =  getMaxRefreshRate()

            // Set the refresh rate
            val params = window.attributes
            params.preferredRefreshRate = maxRefreshRate
            window.attributes = params
            Log.d(LOG_TAG, "Set display refresh rate to $maxRefreshRate Hz")

        } catch (e: Exception) {
            Log.w(LOG_TAG, "Failed to set max refresh rate: ${e.message}")
        }
    }
}