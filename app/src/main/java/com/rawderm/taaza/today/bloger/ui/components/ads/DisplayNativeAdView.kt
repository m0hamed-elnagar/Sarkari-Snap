package com.rawderm.taaza.today.bloger.ui.components.ads

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.ads.nativead.NativeAd
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdAttribution
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdBodyView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdButton
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdCallToActionView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdHeadlineView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdIconView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdMediaView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdPriceView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdStarRatingView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdStoreView
import com.rawderm.taaza.today.bloger.ui.components.ads.compose_util.NativeAdView

@Composable
        /** Display a native ad with a user defined template. */
fun DisplayNativeAdView(nativeAd: NativeAd) {

    NativeAdView(nativeAd) {
        Column(
            modifier = Modifier
                .fillMaxWidth() // width only
                .height(320.dp)
                .padding(8.dp)
        ) {
            // Ad attribution
            NativeAdAttribution(text = "Ad")

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Icon
                nativeAd.icon?.let { icon ->
                    NativeAdIconView(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 8.dp)
                    ) {
                        icon.drawable?.toBitmap()?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Ad icon",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    // Headline
                    nativeAd.headline?.let { headline ->
                        NativeAdHeadlineView {
                            Text(
                                text = headline,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 2
                            )
                        }
                    }

                    // Star rating
                    nativeAd.starRating?.let { rating ->
                        NativeAdStarRatingView {
                            Text(
                                text = "★ $rating",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            // Body
            nativeAd.body?.let { body ->
                NativeAdBodyView(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3
                    )
                }
            }

            // Media view with fixed height
            NativeAdMediaView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .width(150.dp)
                    .padding(vertical = 8.dp)
            )

            // Bottom row with price, store, and CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Price
                    nativeAd.price?.let { price ->
                        NativeAdPriceView(
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(text = price)
                        }
                    }
                }
                // If available, display the store asset.
                nativeAd.store?.let {
                    NativeAdStoreView(Modifier
                        .padding(5.dp)
                        .align(Alignment.CenterVertically)) {
                        Text(text = it)
                    }
                }
                // If available, display the call to action asset.
                // Note: The Jetpack Compose button implements a click handler which overrides the native
                // ad click handler, causing issues. Use the NativeAdButton which does not implement a
                // click handler. To handle native ad clicks, use the NativeAd AdListener onAdClicked
                // callback.
                nativeAd.callToAction?.let { callToAction ->
                    NativeAdCallToActionView {
                        NativeAdButton(text = callToAction)
                    }
                }
            }
        }
    }
}
