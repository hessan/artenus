package com.annahid.libs.artenus.internal.unified;

import android.view.View;
import android.widget.LinearLayout;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.unified.AdManager;

import ir.adad.AdView;

public class AdadAdManager extends AdManager {
    @Override
    protected View getAdView() {
        int childCount = adLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View view = adLayout.getChildAt(i);

            if (view instanceof AdView)
                return view;
        }

        return null;
    }

    public void onCreate() {
        final String adUnitId = getAdUnitId();

        if (adUnitId == null)
            return;

        AdView adView = (AdView) getAdView();

        if (adView == null) {
            adView = new AdView(Artenus.getInstance());
            adView.setToken(adUnitId);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            adLayout.addView(adView, lp);
            adLayout.requestLayout();
        }
    }

    @Override
    protected void destroyAdView(View adView) {
        ((AdView) adView).destroy();
    }
}
