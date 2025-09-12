package com.example.taaza.today.core.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.example.taaza.today.R

fun openUrlInCustomTab(context: Context, url: String) {
    // Define colors for toolbar
    val colorParams = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(ContextCompat.getColor(context, R.color.purple_500)) // your toolbar color
        .build()

    val builder = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(colorParams) // replaces deprecated setToolbarColor
        .setShowTitle(true)
        .setShareState(CustomTabsIntent.SHARE_STATE_ON)

    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}
