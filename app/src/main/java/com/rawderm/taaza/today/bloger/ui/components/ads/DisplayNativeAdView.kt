package com.rawderm.taaza.today.bloger.ui.components.ads

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.ads.nativead.NativeAd
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdBodyView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdButton
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdCallToActionView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdHeadlineView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdIconView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdMediaView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdPriceView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdStoreView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdView

@Composable
fun DisplayNativeAdView(nativeAd: NativeAd, modifier: Modifier = Modifier) {
    NativeAdView(nativeAd,modifier) {
        Box(modifier) {

            /* 1. full-screen media (background) */
            NativeAdMediaView(Modifier.fillMaxSize())

            /* 2. foreground content */
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                /* top block */
                Column(Modifier.weight(1f, fill = false)) {

                    /* 2a. icon */
                    nativeAd.icon?.let { ic ->
                        NativeAdIconView(Modifier.size(48.dp)) {
                            Image(
                                bitmap = ic.drawable!!.toBitmap().asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    /* 2b. headline */
                    nativeAd.headline?.let {
                        NativeAdHeadlineView {
                            Text(text = it, style = MaterialTheme.typography.headlineSmall)
                        }
                    }

                    /* 2c. body */
                    nativeAd.body?.let {
                        Spacer(Modifier.height(4.dp))
                        NativeAdBodyView {
                            Text(text = it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                /* bottom block (price / store / CTA) */
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        nativeAd.price?.let { p ->
                            NativeAdPriceView { Text(p) }
                            Spacer(Modifier.width(4.dp))
                        }
                        nativeAd.store?.let { s ->
                            NativeAdStoreView { Text(s) }
                        }
                    }

                    nativeAd.callToAction?.let { cta ->
                        NativeAdCallToActionView {
                            NativeAdButton(cta)
                        }
                    }
                }
            }
        }
    }
}