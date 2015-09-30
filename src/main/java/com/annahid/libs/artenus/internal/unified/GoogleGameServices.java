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
