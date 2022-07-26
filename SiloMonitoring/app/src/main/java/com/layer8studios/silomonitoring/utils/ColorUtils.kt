package com.layer8studios.silomonitoring.utils

import android.graphics.Color


object ColorUtils {

    fun getInterpolatedColor(colorStart: Int, colorEnd: Int, percent: Int): Int {
        return Color.rgb(
            interpolate(Color.red(colorStart), Color.red(colorEnd), percent),
            interpolate(Color.green(colorStart), Color.green(colorEnd), percent),
            interpolate(Color.blue(colorStart), Color.blue(colorEnd), percent)
        )
    }

    private fun interpolate(colorStart: Int, colorEnd: Int, percent: Int): Int {
        return (Math.min(colorStart, colorEnd) * (100 - percent) + Math.max(colorStart, colorEnd) * percent) / 100
    }

}