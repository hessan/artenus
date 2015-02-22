package com.annahid.libs.artenus.unified;

import android.content.Context;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public abstract class GameServices {
	private Context mContext;

	protected GameServices() {
		mContext = null;
	}

	public final Context getContext() {
		return mContext;
	}

	public void onCreate(Context context) {
		mContext = context;
	}

	/**
	 * Submits a score to a given leaderboard.
	 *
	 * @param lbId    Leaderboard string identifier.
	 * @param score   The score to be submitted.
	 * @param payload Additional information to accompany the score. This parameter
	 *                does not have any effect for current providers and is reserved for future use.
	 *                An example of use of this parameter is score validation using some extra in-game
	 *                states and information.
	 */
	public abstract void submitScore(String lbId, int score, Object payload);

	/**
	 * Displays all leaderboards associated with this game.
	 */
	public abstract void showLeaderboard();

	/**
	 * Displays the desired leaderboard.
	 *
	 * @param lbId The string identifier of the leaderboard.
	 */
	public abstract void showLeaderboard(String lbId);

	/**
	 * Unlocks an achievement and considers it as achieved.
	 *
	 * @param achievementId The string identifier of the achievement.
	 */
	public abstract void unlockAchievement(String achievementId);

	/**
	 * Reveals a hidden achievement. This is different from unlocking, as it
	 * is still going to be displayed as not achieved. Some providers do not
	 * support this function, in which case this method will have no effect.
	 *
	 * @param achievementId The string identifier of the achievement.
	 */
	public abstract void revealAchievement(String achievementId);

	/**
	 * Increments a progressive achievement. Once the progress is complete,
	 * the achievement will be unlocked.
	 *
	 * @param achievementId The string identifier of the achievement.
	 * @param amount        The number of steps to progress.
	 * @param max           Maximum step count. Some providers have this already in
	 *                      their database. But it is recommended that you provide this number for
	 *                      global support.
	 */
	public abstract void incrementAchievement(String achievementId, int amount, int max);

	/**
	 * Displays an achievements dialog.
	 */
	public abstract void showAchievements();

	/**
	 * Ensures that all the achievements in a given list are unlocked. This method
	 * will unlock any achievement in the list that is not unlocked. For other
	 * items, it has no effect.
	 *
	 * @param achievementIds The list of string identifiers for the achievements.
	 */
	public abstract void ensureAchievements(List<String> achievementIds);

	/**
	 * Indicates whether this {@code GameServices} supports achievements.
	 *
	 * @return {@code true} if achievements are supported, and {@code false} otherwise.
	 */
	public abstract boolean supportsAchievements();
}
