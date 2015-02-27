package com.annahid.libs.artenus.unified;

/**
 * Interface for classes that listen to ad placement events. Ad-driven games implement this
 * interface to adjust game content according the size of ad units.
 */
public interface AdPlacementListener {
	/**
	 * Called when the ad unit changes state from hidden to visible, or vice versa.
	 *
	 * @param visible	{@code true} if the ad unit is visible, {@code false} otherwise
	 */
	public void onAdVisibilityChange(boolean visible);

	/**
	 * Called when the height of the ad unit changes. This includes when it virtually
	 * becomes 0 as a result of it being hidden.
	 *
	 * @param height	New height of the ad unit
	 */
	public void onHeightChange(int height);
}
