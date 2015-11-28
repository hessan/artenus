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

import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Superclass of unified ad managers. If a {@code UnifiedServices} implementation supports ads, it
 * should provide a sub-class of this class.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings({ "UnusedDeclaration" })
public abstract class AdManager {
    /**
     * Signal identifier used to show/hide ad.
     */
    private static final int MESSAGE_SHOW_AD = 113;

    /**
     * Signal identifier used to destroy ad.
     */
    private static final int MESSAGE_DESTROY_AD = 114;

    /**
     * Holds the ad layout managed by this ad manager.
     */
    protected AdLayout adLayout = null;

    /**
     * Holds the handler for IPC messaging with this {@code AdManager} instance.
     */
    MyHandler handler = null;

    /**
     * Holds the event handler responding to ad placement changes.
     */
    private AdPlacementListener listener;

    /**
     * Holds the store-specific ad unit identifier.
     */
    private String adUnitId = null;

    /**
     * Sets the game layout on which ads should be placed.
     *
     * @param adLayout Target layout
     */
    public final void setAdLayout(AdLayout adLayout) {
        this.adLayout = adLayout;
        handler = new MyHandler(this);
    }

    /**
     * Hides or displays the ad unit at a specified location.
     *
     * @param show Value indicating ad placement options
     */
    public final void showAd(Placements show) {
        sendSignalToMainThread(MESSAGE_SHOW_AD, show.ordinal());
    }

    /**
     * Hides the ad unit. This is the same as calling {@link AdManager#showAd(Placements)} with the
     * {@link Placements#HIDDEN} option.
     */
    public final void hideAd() {
        showAd(Placements.HIDDEN);
    }

    /**
     * Releases resources associated with the ad unit when it is no longer needed. An example of
     * a situation where this method needs to be called is when the user buys the full version of
     * the app using IAP, and ads need to be removed.
     */
    public final void destroyAd() {
        sendSignalToMainThread(MESSAGE_DESTROY_AD, 0);
    }

    /**
     * Gets the height of the ad unit currently displayed.
     *
     * @return The height of the ad unit in pixels, or 0 if it is not present
     */
    public final int getAdHeight() {
        if (adLayout == null)
            return 0;

        return adLayout.getAdHeight();
    }

    /**
     * Gets the currently assigned ad unit identifier.
     *
     * @return Ad unit identifier
     */
    protected String getAdUnitId() {
        return adUnitId;
    }

    /**
     * Sets the identifier for the ad unit that should be displayed over the stage by this ad
     * manager. The format of the identifier depends on the specific implementation.
     *
     * @param adUnitId Ad unit identifier
     */
    public void setAdUnitId(String adUnitId) {
        this.adUnitId = adUnitId;
    }

    /**
     * Implemented by subclasses to return a view corresponding to the ad unit overlay. The exact
     * sub-type of the returned {@code View} depends on the specific implementation.
     *
     * @return The ad view
     */
    protected abstract View getAdView();

    /**
     * Implemented by subclasses to destroy a view corresponding to an ad unit. Subclasses usually
     * do not need to do type checking, as the framework only passes ad units created by the same
     * instance as the argument.
     *
     * @param adView The ad view previously created by this ad manager
     */
    protected abstract void destroyAdView(View adView);

    final AdPlacementListener getAdPlacementListener() {
        return listener;
    }

    /**
     * Sets the listener that is responsible for handling ad placement events.
     *
     * @param listener Ad placement listener, or {@code null} to remove the current listener
     */
    public final void setAdPlacementListener(AdPlacementListener listener) {
        this.listener = listener;
    }

    /**
     * Uses the handler to apply the ad placement change request on the UI thread.
     *
     * @param msgId Message identifier
     * @param param Message parameter
     */
    final void sendSignalToMainThread(int msgId, int param) {
        if (handler != null) {
            final Message msg = new Message();
            msg.what = msgId;
            msg.arg1 = param;
            handler.sendMessage(msg);
        }
    }

    /**
     * Ad placement and visibility options.
     */
    public enum Placements {
        /**
         * Option indicating that the ad should be hidden.
         */
        HIDDEN,

        /**
         * Option indicating that the ad unit should be displayed at the top-left corner.
         */
        TOP_LEFT,

        /**
         * Option indicating that the ad unit should be displayed at the top center.
         */
        TOP_CENTER,

        /**
         * Option indicating that the ad unit should be displayed at the top-right corner.
         */
        TOP_RIGHT,

        /**
         * Option indicating that the ad unit should be displayed at the bottom-left corner.
         */
        BOTTOM_LEFT,

        /**
         * Option indicating that the ad unit should be displayed at the bottom center.
         */
        BOTTOM_CENTER,

        /**
         * Option indicating that the ad unit should be displayed at the bottom-right corner.
         */
        BOTTOM_RIGHT
    }

    /**
     * Handles ad placement changes on the UI thread.
     */
    private static class MyHandler extends Handler {
        /**
         * Holds the reference to the ad manager.
         */
        private WeakReference<AdManager> admRef;

        /**
         * Creates a new instance of the handler.
         *
         * @param adm Ad manager instance
         */
        public MyHandler(AdManager adm) {
            admRef = new WeakReference<>(adm);
        }

        /**
         * Handles an incoming message.
         *
         * @param msg The message
         */
        public void handleMessage(Message msg) {
            final AdManager adm = admRef.get();

            if (adm.adLayout == null)
                return;

            if (msg.what == MESSAGE_SHOW_AD) {
                View adView = adm.getAdView();

                if (adView == null)
                    return;

                Placements show = Placements.values()[msg.arg1];
                adm.adLayout.showAd(show);

                if (adm.listener != null)
                    adm.listener.onAdVisibilityChange(show != Placements.HIDDEN);
            } else if (msg.what == MESSAGE_DESTROY_AD) {
                final View adView = adm.getAdView();

                if (adView != null) {
                    adm.adLayout.removeView(adView);
                    adm.destroyAdView(adView);
                }
            }
        }
    }
}
