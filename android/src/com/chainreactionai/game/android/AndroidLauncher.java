package com.chainreactionai.game.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.chainreactionai.game.ChainReactionAIGame;
import com.chainreactionai.game.IGoogleServices;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class AndroidLauncher extends AndroidApplication implements
		IGoogleServices {

	private GameHelper _gameHelper;
	private final static int REQUEST_CODE_UNUSED = 9002, REQUEST_ACHIEVEMENTS = 9001;
	private Preferences stats;

	// @Override
	// protected void onCreate (Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// AndroidApplicationConfiguration config = new
	// AndroidApplicationConfiguration();
	//
	// // Changes in config to disable Accelerometer and Compass so as
	// // to conserve battery.
	// config.useAccelerometer = false;
	// config.useCompass = false;
	//
	// // Initialize the game
	// initialize(new ChainReactionAIGame(), config);
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the GameHelper.
		_gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		_gameHelper.enableDebugLog(false);

		GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
			@Override
			public void onSignInSucceeded() {
				if (stats.getBoolean("scoreUpdatedFlag", false) == false) {
					loadScoreOfLeaderBoard();
            	}
			}

			@Override
			public void onSignInFailed() {
			}
		};

		_gameHelper.setup(gameHelperListener);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		// Changes in config to disable Accelerometer and Compass so as
		// to conserve battery.
		config.useAccelerometer = false;
		config.useCompass = false;

		// Initialize the game

		// The rest of your onCreate() code here...
		initialize(new ChainReactionAIGame(this), config);
		stats = Gdx.app.getPreferences("chainReactionStatistics");
	}

	@Override
	protected void onStart() {
		super.onStart();
		_gameHelper.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		_gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		_gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void signIn() {
		try {
			runOnUiThread(new Runnable() {
				// @Override
				public void run() {
					_gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (Exception e) {
			Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage()
					+ ".");
		}
	}

	@Override
	public void signOut() {
		try {
			runOnUiThread(new Runnable() {
				// @Override
				public void run() {
					_gameHelper.signOut();
				}
			});
		} catch (Exception e) {
			Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage()
					+ ".");
		}
	}

	@Override
	public void rateGame() {
		// Replace the end of the URL with the package of your game
		String str = "https://play.google.com/store/apps/details?id=com.chainreactionai.game.android";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}

	@Override
	public void submitScore(long score) {
		if (isSignedIn() == true) {
			
			Games.Leaderboards.submitScore(_gameHelper.getApiClient(),
					getString(R.string.leaderboard_overall_score), score);
			if (stats.getBoolean("scoreUpdatedFlag", false) == false) {
				stats.putBoolean("scoreUpdatedFlag", true);
				stats.flush();
			}
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
					_gameHelper.getApiClient(),
					getString(R.string.leaderboard_overall_score)), REQUEST_CODE_UNUSED);
		} else {
			// Maybe sign in here then redirect to submitting score?
		}
	}

	@Override
	public void showScores() {
		if (isSignedIn() == true)
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
					_gameHelper.getApiClient(),
					getString(R.string.leaderboard_overall_score)), REQUEST_CODE_UNUSED);
		else {
			signIn();
			// Maybe sign in here then redirect to showing scores?
		}
	}

	@Override
	public boolean isSignedIn() {
		return _gameHelper.isSignedIn();
	}

	@Override
	public void showAchievement() {
		if (isSignedIn() == true)
			startActivityForResult(
				Games.Achievements.getAchievementsIntent(_gameHelper
						.getApiClient()), REQUEST_ACHIEVEMENTS);
		else {
			signIn();
			// Maybe sign in here then redirect to showing achievements?
		}
	}

	@Override
	public void getAchievement(String achievementCode) {
		if (isSignedIn() == true)
			Games.Achievements.unlock(_gameHelper.getApiClient(), achievementCode);
	}

	@Override
	public void getIncAchievement(String achievementCode, int inc) {
		if (isSignedIn() == true)
			Games.Achievements.increment(_gameHelper.getApiClient(),
				achievementCode, inc);
	}
	
	private void loadScoreOfLeaderBoard() {
		if (isSignedIn() == true) {
		    Games.Leaderboards.loadCurrentPlayerLeaderboardScore(_gameHelper.getApiClient(), getString(R.string.leaderboard_overall_score), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
		        @Override
		        public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
		            if (isScoreResultValid(scoreResult)) {
		                long score = scoreResult.getScore().getRawScore();
		                // Update local score if the user already has a score on some other device,
		                // or if the user is re-installing the app
		                if (score > 0) {
		                	// System.out.println("Got score: "+score);
		                	if (stats.getInteger("OverallScore", 0) > 0) {
		                		stats.putBoolean("scoreUpdatedFlag", true);
		        				stats.flush();
		                		submitScore(score+stats.getInteger("OverallScore", 0));
		                		stats.putInteger("OverallScore", (int)score+stats.getInteger("OverallScore", 0));
		                		stats.flush();
		                	}
		                	else {
		                		stats.putInteger("OverallScore", (int)score);
		                		stats.flush();
		                	}
		                }
		            }
		        }
		    });
		}
	}

	private boolean isScoreResultValid(final Leaderboards.LoadPlayerScoreResult scoreResult) {
	    return scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null;
	}

}