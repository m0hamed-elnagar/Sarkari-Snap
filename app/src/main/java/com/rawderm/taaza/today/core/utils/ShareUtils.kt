package com.rawderm.taaza.today.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.rawderm.taaza.today.R

object ShareUtils {

    fun whatsApp(context: Context, text: String, url: String) =
        share(context, text, url, "com.whatsapp")

    fun telegram(context: Context, text: String, url: String) =
        share(context, text, url, "org.telegram.messenger")

    fun messenger(context: Context, text: String, url: String) =
        share(context, text, url, "com.facebook.orca")

    fun twitter(context: Context, text: String, url: String) =
        shareOrBrowser(
            context,
            text,
            url,
            pkg = "com.twitter.android",
            browserUrl = {
                "${context.getString(R.string.twitter_intent_base)}?" +
                        "text=${Uri.encode(text)}&url=${Uri.encode(url)}"
            }
        )


    fun systemChooser(context: Context, text: String, url: String) =
        genericShare(context, text, url)

    /* ------ internal ------ */
    private fun share(
        context: Context,
        text: String,
        url: String,
        pkg: String
    ) = try {
        context.startActivity(newSendIntent(text, url).apply { setPackage(pkg) })
    } catch (_: Exception) {
        genericShare(context, text, url)
    }

    private fun shareOrBrowser(
        context: Context,
        text: String,
        url: String,
        pkg: String,
        browserUrl: () -> String
    ) = try {
        context.startActivity(newSendIntent(text, url).apply { setPackage(pkg) })
    } catch (_: Exception) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(browserUrl())))
        } catch (_: Exception) {
            genericShare(context, text, url)
        }
    }

    private fun genericShare(context: Context, text: String, url: String) =
        context.startActivity(
            Intent.createChooser(
                newSendIntent(text, url),
                context.getString(R.string.share_via)
            )
        )

    private fun newSendIntent(text: String, url: String) =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "$text $url")
        }
}