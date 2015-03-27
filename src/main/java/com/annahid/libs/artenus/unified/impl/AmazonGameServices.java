package com.annahid.libs.artenus.unified.impl;

import com.amazon.ags.api.AGResponseCallback;
import com.amazon.ags.api.AGResponseHandle;
import com.amazon.ags.api.AmazonGamesCallback;
import com.amazon.ags.api.AmazonGamesClient;
import com.amazon.ags.api.AmazonGamesFeature;
import com.amazon.ags.api.AmazonGamesStatus;
import com.amazon.ags.api.achievements.AchievementsClient;
import com.amazon.ags.api.achievements.GetAchievementResponse;
import com.amazon.ags.api.leaderboards.LeaderboardsClient;
import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.unified.GameServices;

import java.util.EnumSet;
import java.util.List;

final class AmazonGameServices implements GameServices {
	AmazonGamesClient agsClient;
	AmazonLoginManager loginManager;

	public AmazonGameServices(AmazonLoginManager loginManager) {
		this.loginManager = loginManager;
	}

	AmazonGamesCallback callback = new AmazonGamesCallback() {
		@Override
		public void onServiceNotReady(AmazonGamesStatus status) {
			loginManager.setGamesLoggedIn(false);
		}

		@Override
		public void onServiceReady(AmazonGamesClient amazonGamesClient) {
			agsClient = amazonGamesClient;
			loginManager.setGamesLoggedIn(true);
		}
	};

	//list of features your game uses (in this example, achievements and leaderboards)
	EnumSet<AmazonGamesFeature> myGameFeatures = EnumSet.of(
			AmazonGamesFeature.Achievements, AmazonGamesFeature.Leaderboards);

	public void onResume() {
		AmazonGamesClient.initialize(Artenus.getInstance(), callback, myGameFeatures);
	}

	public void onPause() {
		if (agsClient != null) {
			AmazonGamesClient.release();
		}
	}

	@Override
	public void submitScore(String lbId, int score, Object payload) {
		LeaderboardsClient lbClient = agsClient.getLeaderboardsClient();
		lbClient.submitScore(lbId, score);
	}

	@Override
	public void showLeaderboard() {
		LeaderboardsClient lbClient = agsClient.getLeaderboardsClient();
		lbClient.showLeaderboardsOverlay();
	}

	@Override
	public void showLeaderboard(String lbId) {
		LeaderboardsClient lbClient = agsClient.getLeaderboardsClient();
		lbClient.showLeaderboardOverlay(lbId);
	}

	@Override
	public void unlockAchievement(String achievementId) {
		AchievementsClient acClient = agsClient.getAchievementsClient();
		acClient.updateProgress(achievementId, 100.0f);
	}

	@Override
	public void incrementAchievement(final String achievementId, final int amount, final int max) {
		final AchievementsClient acClient = agsClient.getAchievementsClient();

		AGResponseHandle<GetAchievementResponse> handle = acClient.getAchievement(
				achievementId, "");

		handle.setCallback(new AGResponseCallback<GetAchievementResponse>() {
			@Override
			public void onComplete(GetAchievementResponse getAchievementResponse) {
				float progress = getAchievementResponse.getAchievement().getProgress();
				progress += (float) amount / (float) max;
				acClient.updateProgress(achievementId, progress);
			}
		});
	}

	@Override
	public void revealAchievement(String achievementId) {
		// Not supported
	}

	@Override
	public void showAchievements() {
		AchievementsClient acClient = agsClient.getAchievementsClient();
		acClient.showAchievementsOverlay();
	}

	@Override
	public void ensureAchievements(List<String> achievementIds) {
		for (String achievementId : achievementIds)
			unlockAchievement(achievementId);
	}

	@Override
	public boolean supportsAchievements() {
		return true;
	}
}
