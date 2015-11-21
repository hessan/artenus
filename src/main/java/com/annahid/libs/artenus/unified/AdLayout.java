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

package com.annahid.libs.artenus.unified;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Provides a view group for an ad-driven game. It takes two {@code View} objects as children. The
 * first one will be the main view and will take up the entire region, and the second one will be
 * considered as an ad unit, and will be placed accordingly.
 *
 * @author Hessan Feghhi
 */
public final class AdLayout extends ViewGroup {
    /**
     * Indicates how the ad is currently displayed.
     */
    private AdManager.Show adShown = AdManager.Show.HIDDEN;

    /**
     * Holds the visible height of the banner ad.
     */
    private int adHeight = 0;

    /**
     * Creates an {@code AdLayout} for the given application context.
     *
     * @param context The application context
     */
    public AdLayout(Context context) {
        super(context);
    }

    /**
     * Creates an {@code AdLayout} for the given application context with the given set of XML
     * attributes.
     *
     * @param context Application context
     * @param attrs   Set of attributes
     */
    public AdLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates an {@code AdLayout} for the given application context with the given set of
     * XML attributes.
     *
     * @param context  Application context
     * @param attrs    Set of attributes
     * @param defStyle Default style for the
     */
    public AdLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(widthMeasureSpec);

        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        super.setMeasuredDimension(
                resolveSize(widthSize, widthMeasureSpec),
                resolveSize(heightSize, heightMeasureSpec)
        );
    }

    @Override
    protected final void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child instanceof GLSurfaceView)
                child.layout(l, t, r, b);
            else {
                final int mw = child.getMeasuredWidth(), mh = child.getMeasuredHeight();
                adHeight = Math.max(adHeight, mh);

                AdPlacementListener listener = UnifiedServices
                        .getInstance()
                        .getAdManager()
                        .getAdPlacementListener();

                if (listener != null)
                    listener.onHeightChange(adHeight);

                child.bringToFront();

                switch (adShown) {
                    case TOP_LEFT:
                        child.layout(l, t, l + mw, t + mh);
                        break;
                    case TOP_CENTER:
                        child.layout((r + l - mw) / 2, t, (r + l + mw) / 2, t + mh);
                        break;
                    case TOP_RIGHT:
                        child.layout(r - mw, t, r, t + mh);
                        break;
                    case BOTTOM_LEFT:
                        child.layout(l, b - mh, l + mw, b);
                        break;
                    case BOTTOM_CENTER:
                        child.layout((r + l - mw) / 2, b - mh, (r + l + mw) / 2, b);
                        break;
                    case BOTTOM_RIGHT:
                        child.layout(r - mw, b - mh, r, b);
                        break;
                }
            }
        }
    }

    /**
     * Gets the height of the ad currently shown.
     *
     * @return The height in pixels
     */
    final int getAdHeight() {
        return adShown == AdManager.Show.HIDDEN ? 0 : adHeight;
    }

    /**
     * Shows the ad at a given location or hides it.
     *
     * @param show The location to display the ad, or HIDDEN to hide it
     */
    final void showAd(AdManager.Show show) {
        final int count = getChildCount();
        final AdManager.Show prevShow = adShown;

        adShown = show;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (!(child instanceof GLSurfaceView))
                child.setVisibility(
                        show == AdManager.Show.HIDDEN ? View.INVISIBLE : View.VISIBLE);
        }

        if (prevShow != show && show != AdManager.Show.HIDDEN)
            requestLayout();
    }
}
