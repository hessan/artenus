package com.annahid.libs.artenus.internal.unified;

import android.view.View;
import android.widget.LinearLayout;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.unified.AdManager;

final class AmazonAdManager extends AdManager {
	@Override
	protected View getAdView() {
		int childCount = adLayout.getChildCount();

		for (int i = 0; i < childCount; i++) {
			final View view = adLayout.getChildAt(i);

			if (view instanceof com.amazon.device.ads.AdLayout)
				return view;
		}

		return null;
	}

	public void onCreate() {
		AdRegistration.setAppKey(getAdUnitId());

		com.amazon.device.ads.AdLayout adView = (com.amazon.device.ads.AdLayout) getAdView();

		if (adView == null) {
			adView = new com.amazon.device.ads.AdLayout(Artenus.getInstance());

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			adLayout.addView(adView, lp);

			final AdTargetingOptions adOptions = new AdTargetingOptions();
			adView.loadAd(adOptions);
			adView.setListener(new AdListener() {
				@Override
				public void onAdLoaded(Ad ad, AdProperties adProperties) {
					adLayout.requestLayout();
				}

				@Override
				public void onAdFailedToLoad(Ad ad, AdError adError) {

				}

				@Override
				public void onAdExpanded(Ad ad) {
					adLayout.requestLayout();
				}

				@Override
				public void onAdCollapsed(Ad ad) {
					adLayout.requestLayout();
				}

				@Override
				public void onAdDismissed(Ad ad) {

				}
			});
		}
	}

	@Override
	protected void destroyAdView(View adView) {
		((com.amazon.device.ads.AdLayout) adView).destroy();
	}
}
