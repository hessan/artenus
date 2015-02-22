package com.annahid.libs.artenus.unified.impl;

import android.view.View;

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
			adLayout.addView(adView);
			adLayout.requestLayout();

			final AdTargetingOptions adOptions = new AdTargetingOptions();
			adView.loadAd(adOptions);
		}
	}

	@Override
	protected void destroyAdView(View adView) {
		((com.amazon.device.ads.AdLayout) adView).destroy();
	}
}
