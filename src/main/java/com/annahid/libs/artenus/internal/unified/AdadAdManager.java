/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.annahid.libs.artenus.internal.unified;

import android.view.View;
import android.widget.LinearLayout;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.unified.AdManager;

import ir.adad.AdView;

final class AdadAdManager extends AdManager {
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
