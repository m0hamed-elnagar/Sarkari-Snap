package com.example.taaza.today.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun shareViaWhatsApp(context: Context, text: String, url: String) {
    val shareText = "$text $url"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        setPackage("com.whatsapp")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
        shareViaMore(context, text, url) // Fallback to generic share
    }
}

fun shareViaTelegram(context: Context, text: String, url: String) {
    val shareText = "$text $url"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        setPackage("org.telegram.messenger")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Telegram not installed.", Toast.LENGTH_SHORT).show()
        shareViaMore(context, text, url) // Fallback to generic share
    }
}

fun shareViaX(context: Context, text: String, url: String) {
    val shareText = "$text $url"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        setPackage("com.twitter.android") // Package name for X/Twitter
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // X/Twitter app not installed, try opening in browser
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "https://twitter.com/intent/tweet?text=${Uri.encode(text)}&url=${
                    Uri.encode(
                        url
                    )
                }"
            )
        )
        try {
            context.startActivity(browserIntent)
        } catch (e2: Exception) {
            Toast.makeText(
                context,
                "X (Twitter) not installed and browser could not open.",
                Toast.LENGTH_SHORT
            ).show()
            shareViaMore(context, text, url) // Fallback to generic share
        }
    }
}

fun shareViaFacebook(context: Context, text: String, url: String) {
    val shareText = "$text $url"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText) // Changed to include both text and URL
        setPackage("com.facebook.katana")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Facebook app not installed, try opening in browser (sharing dialog)
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "https://www.facebook.com/sharer/sharer.php?u=${Uri.encode(url)}&quote=${
                    Uri.encode(
                        text
                    )
                }"
            )
        )
        try {
            context.startActivity(browserIntent)
        } catch (e2: Exception) {
            Toast.makeText(
                context,
                "Facebook not installed and browser could not open.",
                Toast.LENGTH_SHORT
            ).show()
            shareViaMore(context, text, url) // Fallback to generic share
        }
    }
}

fun shareViaMessenger(context: Context, text: String, url: String) {
    val shareText = "$text $url"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        setPackage("com.facebook.orca") // Messenger package
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Messenger not installed.", Toast.LENGTH_SHORT).show()
        shareViaMore(context, text, url) // fallback
    }
}

fun shareViaMore(context: Context, text: String, url: String) {
    val shareText = "$text $url"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, text)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Share via"))
    } catch (e: Exception) {
        Toast.makeText(context, "No app can handle this request.", Toast.LENGTH_SHORT).show()
    }
}
