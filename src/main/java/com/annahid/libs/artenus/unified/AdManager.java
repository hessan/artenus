package com.annahid.libs.artenus.unified;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * The superclasses of unified ad managers. If a {@code UnifiedServices}
 * implementation supports ads, it should provide a sub-class of this class.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings({"UnusedDeclaration"})
public abstract class AdManager {
	/**
	 * Signal identifier used to show/hide ad.
	 */
	private static final int MESSAGE_SHOW_AD = 113;
	/**
	 * Signal identifier used to destroy ad.
	 */
	private static final int MESSAGE_DESTROY_AD = 114;

	private static class MyHandler extends Handler {
		private WeakReference<AdManager> admRef;

		public MyHandler(AdManager adm) {
			admRef = new WeakReference<>(adm);
		}

		public void handleMessage(Message msg) {
			final AdManager adm = admRef.get();

			if (adm.adLayout == null)
				return;

			if (msg.what == MESSAGE_SHOW_AD) {
				View adView = adm.getAdView();

				if (adView == null)
					return;

				Show show = Show.values()[msg.arg1];

				adm.adLayout.showAd(show);

				if (adm.listener != null)
					adm.listener.onAdVisibilityChange(show != Show.HIDDEN);
			} else if (msg.what == MESSAGE_DESTROY_AD) {
				final View adView = adm.getAdView();

				if (adView != null) {
					adm.adLayout.removeView(adView);
					adm.destroyAdView(adView);
				}
			}
		}
	}

	/**
	 * Specifies whether and where an ad view can be displayed.
	 */
	public enum Show {
		/**
		 * An option that indicates the ad should be hidden.
		 */
		HIDDEN,

		/**
		 * Indicates the ad unit should be displayed at the top-left corner.
		 */
		TOP_LEFT,

		/**
		 * Indicates the ad unit should be displayed at the top center.
		 */
		TOP_CENTER,

		/**
		 * Indicates the ad unit should be displayed at the top-right corner.
		 */
		TOP_RIGHT,

		/**
		 * Indicates the ad unit should be displayed at the bottom-left corner.
		 */
		BOTTOM_LEFT,

		/**
		 * An option that indicates the ad unit should be displayed at the bottom center.
		 */
		BOTTOM_CENTER,

		/**
		 * Indicates the ad unit should be displayed at the bottom-right corner.
		 */
		BOTTOM_RIGHT
	}

	protected AdLayout adLayout = null;

	/**
	 * The handler for IPC messaging with this {@code AdManager} instance.
	 */
	public MyHandler handler = null;

	private AdPlacementListener listener;
	private String adUnitId = null;

	/**
	 * Sets the game layout on which ads should be placed.
	 *
	 * @param adLayout    Target layout
	 */
	public final void setAdLayout(AdLayout adLayout) {
		this.adLayout = adLayout;
		handler = new MyHandler(this);
	}

	/**
	 * Hides or displays the ad unit at a specified location.
	 *
	 * @param show	Value indicating ad placement options
	 */
	public final void showAd(Show show) {
		sendSignalToMainThread(MESSAGE_SHOW_AD, show.ordinal());
	}

	/**
	 * Hides the ad unit. This is the same as calling {@link AdManager#showAd(Show)} with the
	 * {@link Show#HIDDEN} option.
	 */
	public final void hideAd() {
		showAd(Show.HIDDEN);
	}

	/**
	 * This method should be called to release resources associated with the ad unit when it is
	 * no longer needed. An example of such situation is that the user buys the full version of the
	 * app using IAP, and ads need to be removed.
	 */
	public final void destroyAd() {
		sendSignalToMainThread(MESSAGE_DESTROY_AD, 0);
	}

	/**
	 * Gets the height of the ad unit currently displayed.
	 *
	 * @return	The height of the ad unit in pixels, or 0 if it is not present
	 */
	public final int getAdHeight() {
		if (adLayout == null)
			return 0;

		return adLayout.getAdHeight();
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
	 * Sets the identifier for the ad unit that should be displayed over the stage by this ad
	 * manager. The format of the identifier depends on the specific implementation.
	 *
	 * @param adUnitId	Ad unit identifier
	 */
	public void setAdUnitId(String adUnitId) {
		this.adUnitId = adUnitId;
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
	 * This method should be implemented by subclasses to return a view corresponding to the ad unit
	 * overlay. The exact sub-type of the returned {@code View} depends on the specific
	 * implementation.
	 *
	 * @return	The ad view
	 */
	protected abstract View getAdView();

	/**
	 * This method should be implemented by subclasses to destroy a view corresponding to an ad
	 * unit. Subclasses usually do not need to do type checking, as the framework only passes ad
	 * units created by the same instance as the argument.
	 *
	 * @param adView The ad view previously created by this ad manager
	 */
	protected abstract void destroyAdView(View adView);

	AdPlacementListener getAdPlacementListener() {
		return listener;
	}

	void sendSignalToMainThread(int msgId, int param) {
		if (handler != null) {
			final Message msg = new Message();
			msg.what = msgId;
			msg.arg1 = param;
			handler.sendMessage(msg);
		}
	}

}