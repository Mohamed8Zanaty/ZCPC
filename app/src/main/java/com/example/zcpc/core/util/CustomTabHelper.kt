package com.example.zcpc.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.browser.customtabs.CustomTabsIntent

fun openCustomTab(context: Context, url: String) {
    val uri = url.toUri()

    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()

    try {
        customTabsIntent.launchUrl(context, uri)
    } catch (e: ActivityNotFoundException) {
        val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
        fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(fallbackIntent)
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
        }
    }
}