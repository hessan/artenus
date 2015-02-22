package com.annahid.libs.artenus.unified;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

@SuppressWarnings({"UnusedDeclaration"})
public abstract class AdManager {
	/**
	 * Signal identifier used to show/hide ad
	 */
	private static final int MESSAGE_SHOW_AD = 113;
	/**
	 * Signal identifier used to destroy ad
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

				adm.adLayout.showAd(msg.arg1);

				if (adm.listener != null)
					adm.listener.onAdVisibilityChange(msg.arg1 != SHOW_HIDDEN);
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
	 * Indicates that the Ad is hidden.
	 */
	public static final int SHOW_HIDDEN = 0;

	/**
	 * Indicates the the Ad is displayed on the top-left corner.
	 */
	public static final int SHOW_TOP_LEFT = 1;

	/**
	 * Indicates the the Ad is displayed on the top center.
	 */
	public static final int SHOW_TOP_CENTER = 2;

	/**
	 * Indicates the the Ad is displayed on the top-right corner.
	 */
	public static final int SHOW_TOP_RIGHT = 3;

	/**
	 * Indicates the the Ad is displayed on the bottom-left corner.
	 */
	public static final int SHOW_BOTTOM_LEFT = 4;

	/**
	 * Indicates the the Ad is displayed on the bottom center.
	 */
	public static final int SHOW_BOTTOM_CENTER = 5;

	/**
	 * Indicates the the Ad is displayed on the bottom-right corner.
	 */
	public static final int SHOW_BOTTOM_RIGHT = 6;

	protected AdLayout adLayout = null;

	/**
	 * The handler for IPC messaging with this {@code AdManager} instance.
	 */
	public MyHandler handler = null;

	private AdPlacementListener listener;
	private String adUnitId = null;

	public final void setAdLayout(AdLayout adLayout) {
		this.adLayout = adLayout;
		handler = new MyHandler(this);
	}

	public final void showAd(int show) {
		sendSignalToMainThread(MESSAGE_SHOW_AD, show);
	}

	public final void hideAd() {
		showAd(SHOW_HIDDEN);
	}

	public final void destroyAd() {
		sendSignalToMainThread(MESSAGE_DESTROY_AD, 0);
	}

	public final int getAdHeight() {
		if (adLayout == null)
			return 0;

		return adLayout.getAdHeight();
	}

	public final void setAdPlacementListener(AdPlacementListener listener) {
		this.listener = listener;
	}

	public void setAdUnitId(String adUnitId) {
		this.adUnitId = adUnitId;
	}

	protected String getAdUnitId() {
		return adUnitId;
	}

	protected abstract View getAdView();

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
