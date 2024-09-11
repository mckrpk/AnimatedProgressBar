package com.mckrpk.animatedprogressbar

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes


fun getThemeColor(context: Context, @AttrRes resName: Int, @ColorRes fallbackColorRes: Int): Int {
    val outValue = TypedValue()
    val wasResolved = context.theme.resolveAttribute(resName, outValue, true)
    return if (wasResolved)
        outValue.data
    else
        context.resources.getColor(fallbackColorRes)
}

fun getThemeAccentColor(context: Context, @ColorRes fallbackColorRes: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getThemeColor(context, android.R.attr.colorAccent, fallbackColorRes)
    } else {
        val colorAttr = context.resources
            .getIdentifier("colorAccent", "attr", context.packageName)
        getThemeColor(context, colorAttr, fallbackColorRes)
    }
}

fun getThemePrimaryColor(context: Context, @ColorRes fallbackColorRes: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getThemeColor(context, android.R.attr.colorPrimary, fallbackColorRes)
    } else {
        val colorAttr = context.resources
            .getIdentifier("colorPrimary", "attr", context.packageName)
        getThemeColor(context, colorAttr, fallbackColorRes)
    }
}

fun getThemePrimaryDarkColor(context: Context, @ColorRes fallbackColorRes: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getThemeColor(context, android.R.attr.colorPrimaryDark, fallbackColorRes)
    } else {
        val colorAttr = context.resources
            .getIdentifier("colorPrimaryDark", "attr", context.packageName)
        getThemeColor(context, colorAttr, fallbackColorRes)
    }
}

fun dpToPx(dp: Int, context: Context): Float {
    return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}