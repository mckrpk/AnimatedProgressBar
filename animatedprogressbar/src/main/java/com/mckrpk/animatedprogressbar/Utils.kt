package com.mckrpk.animatedprogressbar

import android.content.Context
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


fun getThemeColor(context: Context, @AttrRes resName: Int, @ColorRes fallbackColorRes: Int): Int {
    val outValue = TypedValue()
    val wasResolved = context.theme.resolveAttribute(resName, outValue, true)
    return if (wasResolved)
        outValue.data
    else
        ContextCompat.getColor(context, fallbackColorRes)
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