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

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.unified.GameServices;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.util.List;

final class GoogleGameServices implements GameServices {
	private static final int REQUEST_ACHIEVEMENTS = 9015;
	private static final int REQUEST_LEADERBOARD = 9016;
	private static final int REQUEST_LEADERBOARDS = 9017;

	GoogleApiClient mGoogleApiClient = null;

	public void setGoogleApiClient(GoogleApiClient client) {
		mGoogleApiClient = client;
	}


	@Override
	public void submitScore(String lbId, int score, Object payload) {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
			Games.Leaderboards.submitScore(mGoogleApiClient, lbId, score);
	}

	@Override
	public void showLeaderboard() {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
			Artenus.getInstance().startActivityForResult(
					Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),
					REQUEST_LEADERBOARDS);

	}

	@Override
	public void showLeaderboard(String lbId) {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
			Artenus.getInstance().startActivityForResult(
					Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, lbId),
					REQUEST_LEADERBOARD);
	}

	@Override
	public void unlockAchievement(String achievementId) {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
			Games.Achievements.unlock(mGoogleApiClient, achievementId);
	}

	@Override
	public void revealAchievement(String achievementId) {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
			Games.Achievements.reveal(mGoogleApiClient, achievementId);
	}

	@Override
	public void incrementAchievement(String achievementId, int amount, int max) {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
			Games.Achievements.increment(mGoogleApiClient, achievementId, amount);
	}

	@Override
	public void showAchievements() {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
			Artenus.getInstance().startActivityForResult(
					Games.Achievements.getAchievementsIntent(mGoogleApiClient),
					REQUEST_ACHIEVEMENTS);
	}

	@Override
	public void ensureAchievements(List<String> achievementIds) {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
			for (String achievementId : achievementIds)
				Games.Achievements.unlock(mGoogleApiClient, achievementId);
	}

	@Override
	public boolean supportsAchievements() {
		return true;
	}
}
