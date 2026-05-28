package org.albaspazio.core

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.sqrt

object DeviceUtils {

    /**
     * Determines if the current device is a tablet based on screen size and density
     */
    fun isTablet(context: Context): Boolean {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        // Calculate screen size in inches
        val widthInches = displayMetrics.widthPixels / displayMetrics.xdpi
        val heightInches = displayMetrics.heightPixels / displayMetrics.ydpi
        val diagonalInches = sqrt((widthInches * widthInches) + (heightInches * heightInches))

        // Also check configuration for large/xlarge screens
        val configuration = context.resources.configuration
        val isLargeScreen = (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE

        // Consider it a tablet if:
        // 1. Diagonal is 7 inches or larger, OR
        // 2. Android considers it a large/xlarge screen
        return diagonalInches >= 7.0 || isLargeScreen
    }

    /**
     * Gets the screen size category
     */
    fun getScreenSizeCategory(context: Context): ScreenSize {
        val configuration = context.resources.configuration
        return when (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> ScreenSize.SMALL
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> ScreenSize.NORMAL
            Configuration.SCREENLAYOUT_SIZE_LARGE -> ScreenSize.LARGE
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> ScreenSize.XLARGE
            else -> ScreenSize.NORMAL
        }
    }

    /**
     * Gets detailed device information for debugging
     */
    fun getDeviceInfo(context: Context): DeviceInfo {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val widthInches = displayMetrics.widthPixels / displayMetrics.xdpi
        val heightInches = displayMetrics.heightPixels / displayMetrics.ydpi
        val diagonalInches = sqrt((widthInches * widthInches) + (heightInches * heightInches))

        return DeviceInfo(
            screenWidthPixels = displayMetrics.widthPixels,
            screenHeightPixels = displayMetrics.heightPixels,
            densityDpi = displayMetrics.densityDpi,
            density = displayMetrics.density,
            diagonalInches = diagonalInches,
            screenSize = getScreenSizeCategory(context),
            isTablet = isTablet(context)
        )
    }

    enum class ScreenSize {
        SMALL, NORMAL, LARGE, XLARGE
    }

    data class DeviceInfo(
        val screenWidthPixels: Int,
        val screenHeightPixels: Int,
        val densityDpi: Int,
        val density: Float,
        val diagonalInches: Float,
        val screenSize: ScreenSize,
        val isTablet: Boolean
    )
}