package com.annahid.libs.artenus.unified;

import java.util.List;

/**
 * Interface for classes that provide game services, such as achievements and leaderboards. An
 * implementation of unified services should provide an implementation of this interface if it
 * supports game services.
 *
 * @see com.annahid.libs.artenus.unified.UnifiedServices
 */
@SuppressWarnings("unused")
public interface GameServices {
	/**
	 * Submits a score to a given leaderboard.
	 *
	 * @param lbId    Leaderboard string identifier
	 * @param score   The score to be submitted
	 * @param payload Additional information to accompany the score. This parameter
	 *                does not have any effect for current providers and is reserved for future use.
	 *                An example of use of this parameter is score validation using some extra in-game
	 *                states and information.
	 */
	void submitScore(String lbId, int score, Object payload);

	/**
	 * Displays all leaderboards associated with this game.
	 */
	void showLeaderboard();

	/**
	 * Displays the desired leaderboard.
	 *
	 * @param lbId The string identifier of the leaderboard
	 */
	void showLeaderboard(String lbId);

	/**
	 * Unlocks an achievement and considers it as achieved.
	 *
	 * @param achievementId The string identifier of the achievement
	 */
	void unlockAchievement(String achievementId);

	/**
	 * Reveals a hidden achievement. This is different from unlocking, as it
	 * is still going to be displayed as not achieved. Some providers do not
	 * support this function, in which case this method will have no effect.
	 *
	 * @param achievementId The string identifier of the achievement
	 */
	void revealAchievement(String achievementId);

	/**
	 * Increments a progressive achievement. Once the progress is complete,
	 * the achievement will be unlocked.
	 *
	 * @param achievementId The string identifier of the achievement
	 * @param amount        The number of steps to progress
	 * @param max           Maximum step count. Some providers have this already in
	 *                      their database. But it is recommended that you provide this number for
	 *                      global support.
	 */
	void incrementAchievement(String achievementId, int amount, int max);

	/**
	 * Displays an achievements dialog.
	 */
	void showAchievements();

	/**
	 * Ensures that all the achievements in a given list are unlocked. This method
	 * will unlock any achievement in the list that is not unlocked. For other
	 * items, it has no effect.
	 *
	 * @param achievementIds The list of string identifiers for the achievements
	 */
	void ensureAchievements(List<String> achievementIds);

	/**
	 * Indicates whether this {@code GameServices} supports achievements.
	 *
	 * @return {@code true} if achievements are supported, and {@code false} otherwise
	 */
	boolean supportsAchievements();
}
