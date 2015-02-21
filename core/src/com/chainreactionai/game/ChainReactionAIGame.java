package com.chainreactionai.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

// Initial class which is executed by all the projects.
public class ChainReactionAIGame extends Game {
	// WIDTH and HEIGHT if the device on which we are playing.
	public static int WIDTH;
	public static int HEIGHT;
	public static int currentScreen;
	public static int MAX_NUMBER_PLAYERS;
	public static boolean muteStatus = false;
	
	// For background animation
	public static int INVERSE_CHANCES_OF_NEW_BALLS = 40;
	public static int MAX_Z_DIST_OF_NEW_BALLS = 900;
	public static int MIN_Z_DIST_OF_NEW_BALLS = 300;
	public static int MAX_SPEED_OF_BALLS = 8;
	public static int MIN_SPEED_OF_BALLS = 2;
	public static int MAX_NUMBER_OF_BALLS_AT_A_MOMENT = 10;
	public static int MAX_ARRAY_SIZE = 250;
	public static Texture texture, mainGameScreenTexture, textureGray, splashTexture;
	public static Skin skin, sliderSkin;
	
	// For achievements
	public static final String achievement_almost_there="CgkIu_W6xfwCEAIQDg";
	public static final String achievement_beginners_luck= "CgkIu_W6xfwCEAIQAw";
	public static final String achievement_conqueror_quattro="CgkIu_W6xfwCEAIQEg";
	public static final String achievement_decimus="CgkIu_W6xfwCEAIQEQ";
	public static final String achievement_experienced_professional="CgkIu_W6xfwCEAIQGQ";
	public static final String achievement_high_five="CgkIu_W6xfwCEAIQEw";
	public static final String achievement_i_eight_a_lot="CgkIu_W6xfwCEAIQFg";
	public static final String achievement_king_of_the_world="CgkIu_W6xfwCEAIQHg";
	public static final String achievement_like_seriously="CgkIu_W6xfwCEAIQGw";
	public static final String achievement_lucky_number_seven="CgkIu_W6xfwCEAIQFQ";
	public static final String achievement_no_longer_an_amateur="CgkIu_W6xfwCEAIQDw";
	public static final String achievement_quick_mover="CgkIu_W6xfwCEAIQHA";
	public static final String achievement_secret_of_the_universe="CgkIu_W6xfwCEAIQHw";
	public static final String achievement_silver_jubilee="CgkIu_W6xfwCEAIQGg";
	public static final String achievement_sore_loser="CgkIu_W6xfwCEAIQHQ";
	public static final String achievement_thats_a_six="CgkIu_W6xfwCEAIQFA";
	public static final String achievement_third_times_a_charm="CgkIu_W6xfwCEAIQEA";
	public static final String achievement_welcome_to_the_jungle="CgkIu_W6xfwCEAIQDg";
	public static final String achievement_you_are_the_champion="CgkIu_W6xfwCEAIQGA";
	
	// For getting showTutorial flag
	private Preferences stats;
	public static boolean GRAYED_OUT;
	
	final private boolean DEBUG = false;
	
	public static IGoogleServices googleServices;

	public ChainReactionAIGame(IGoogleServices googleServices)
	{
		super();
		this.googleServices = googleServices;
	}

	
	// Drawables for ImageButtons
	public static Drawable achievementsButtonDraw, achievementsGrayButtonDraw, achievementsPressedButtonDraw,
		backButtonDraw, backGrayButtonDraw, backPressedButtonDraw, exitButtonDraw, exitGrayButtonDraw,
		exitPressedButtonDraw, leaderboardButtonDraw, leaderboardGrayButtonDraw, leaderboardPressedButtonDraw,
		mainMenuButtonDraw, mainMenuGrayButtonDraw, mainMenuPressedButtonDraw, newGameButtonDraw, newGameGrayButtonDraw,
		newGamePressedButtonDraw, pauseButtonDraw, pauseGrayButtonDraw, pausePressedButtonDraw,
		playButtonDraw, playGrayButtonDraw, playPressedButtonDraw, playAgainButtonDraw, playAgainGrayButtonDraw,
		playAgainPressedButtonDraw, resumeButtonDraw, resumeGrayButtonDraw, resumePressedButtonDraw,
		rulesButtonDraw, rulesGrayButtonDraw, rulesPressedButtonDraw, submitButtonDraw, submitGrayButtonDraw, submitPressedButtonDraw,
		tutorialButtonDraw, tutorialGrayButtonDraw, tutorialPressedButtonDraw, pressedHumanCpuButtonDraw,
		unpressedHumanButtonDraw, unpressedCpuButtonDraw, logoDraw, nextButtonDraw, nextGrayButtonDraw,
		nextPressedButtonDraw, skipButtonDraw, skipPressedButtonDraw, muteActivateButton, muteInactivateButton;

	@Override
	public void create() {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		// Logs the HEIGHT and WIDTH of screen under the DEBUG flag.
		if (DEBUG) {
			Gdx.app.log("Screen Size", WIDTH + " " + HEIGHT);
		}
		MAX_NUMBER_PLAYERS = 6;
		
		// Determine whether to show tutorial or not
        stats = Gdx.app.getPreferences("chainReactionStatistics");
        GRAYED_OUT = stats.getBoolean("showTutorial", true);
		
		//Shift to the splash screen
		setScreen(new SplashScreen(this));
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}