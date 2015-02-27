package com.annahid.libs.artenus.unified;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A view group for an ad-driven game. It takes two {@code View} objects as children. The first one
 * will be the main view and will take up the entire region, and the second one will be considered
 * as an ad unit, and will be placed accordingly.
 * 
 * @author Hessan Feghhi
 */
public final class AdLayout extends ViewGroup {

	private int adShown = AdManager.SHOW_HIDDEN;
	private int adHeight = 0;

	/**
	 * Constructs an {@code AdLayout} for the given application context.
	 *
	 * @param context The application context
	 */
	public AdLayout(Context context) {
		super(context);
	}

	/**
	 * Constructs an {@code AdLayout} for the given application context
	 * with the given set of XML attributes.
	 *
	 * @param context The application context
	 * @param attrs   The set of attributes
	 */
	public AdLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

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
					case AdManager.SHOW_TOP_LEFT:
						child.layout(l, t, l + mw, t + mh);
						break;
					case AdManager.SHOW_TOP_CENTER:
						child.layout((r + l - mw) / 2, t, (r + l + mw) / 2, t + mh);
						break;
					case AdManager.SHOW_TOP_RIGHT:
						child.layout(r - mw, t, r, t + mh);
						break;
					case AdManager.SHOW_BOTTOM_LEFT:
						child.layout(l, b - mh, l + mw, b);
						break;
					case AdManager.SHOW_BOTTOM_CENTER:
						child.layout((r + l - mw) / 2, b - mh, (r + l + mw) / 2, b);
						break;
					case AdManager.SHOW_BOTTOM_RIGHT:
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
		return adShown == AdManager.SHOW_HIDDEN ? 0 : adHeight;
	}

	/**
	 * Shows the ad at a given location or hides it.
	 *
	 * @param show The location to display the ad, or ADLAYOUT_HIDDEN to hide it
	 */
	final void showAd(int show) {
		final int count = getChildCount();
		final int prevShow = adShown;

		adShown = show;

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);

			if (!(child instanceof GLSurfaceView))
				child.setVisibility(
						show == AdManager.SHOW_HIDDEN ? View.INVISIBLE : View.VISIBLE);
		}

		if (prevShow != show && show != View.INVISIBLE)
			requestLayout();
	}
}
