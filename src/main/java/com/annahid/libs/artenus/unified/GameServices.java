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
     * @param payload Additional information to accompany the score. This parameter is not
     *                necessarily used by a unified services provider. An example use case of this
     *                parameter is score validation using some extra in-game states and information.
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
     * Reveals a hidden achievement. This is different from unlocking, as it is still going to be
     * displayed as not achieved. Some providers do not support this function, in which case this
     * method will have no effect.
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
     * @param max           Maximum step count. Some providers have this already in their database.
     *                      But it is recommended that you provide this number for global support.
     */
    void incrementAchievement(String achievementId, int amount, int max);

    /**
     * Displays an achievements dialog.
     */
    void showAchievements();

    /**
     * Ensures that all the achievements in a given list are unlocked. This method will unlock any
     * achievement in the list that is not unlocked. For other items, it has no effect.
     *
     * @param achievementIds The list of string identifiers for the achievements
     */
    void ensureAchievements(List<String> achievementIds);

    /**
     * Indicates whether an achievement is unlocked (achieved). Depending on the app-store, there
     * may be a loading delay before this method returns up-to-date results for all achievements.
     *
     * @param achievementId Achievement identifier
     *
     * @return {@code true} if the achievement is unlocked, {@code false} otherwise
     */
    boolean isAchievementUnlocked(String achievementId);

    /**
     * Indicates whether this {@code GameServices} supports achievements.
     *
     * @return {@code true} if achievements are supported, and {@code false} otherwise
     */
    boolean supportsAchievements();
}
