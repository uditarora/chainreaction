package com.chainreactionai.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

// Initial class which is executed by all the projects.
public class ChainReactionAIGame extends Game {
	// WIDTH and HEIGHT if the device on which we are playing.
	public static int WIDTH;
	public static int HEIGHT;
	public static int currentScreen;
	public static int MAX_NUMBER_PLAYERS;
	
	// For background animation
	public static int INVERSE_CHANCES_OF_NEW_BALLS = 40;
	public static int MAX_Z_DIST_OF_NEW_BALLS = 900;
	public static int MIN_Z_DIST_OF_NEW_BALLS = 300;
	public static int MAX_SPEED_OF_BALLS = 8;
	public static int MIN_SPEED_OF_BALLS = 2;
	public static int MAX_NUMBER_OF_BALLS_AT_A_MOMENT = 10;
	public static int MAX_ARRAY_SIZE = 250;
	public static Texture texture, mainGameScreenTexture, textureGray;
	public static Skin skin;
	
	// For achievements
	public static final String achievement_almost_there="CgkIu_W6xfwCEAIQDg";
	public static final String achievement_beginners_luck= "CgkIu_W6xfwCEAIQAw";
	public static final String achievement_conqueror_quattro="CgkIu_W6xfwCEAIQEg";
	public static final String achievement_decimus="CgkIu_W6xfwCEAIQEQ";
	public static final String achievement_experienced_professional="CgkIu_W6xfwCEAIQGQ";
	public static final String achievement_high_five="CgkIu_W6xfwCEAIQEw";
	public static final String achievement_i_eight_a_lot="CgkIu_W6xfwCEAIQFg";
	public static final String achievement_king_of_the_world="CgkIu_W6xfwCEAIQHg";
	public static final String achievement_like_seriously="CgkIu_W6xfwCEAIQEw";
	public static final String achievement_lucky_number_seven="CgkIu_W6xfwCEAIQFQ";
	public static final String achievement_no_longer_an_amateur="CgkIu_W6xfwCEAIQDw";
	public static final String achievement_quick_mover="CgkIu_W6xfwCEAIQHA";
	public static final String achievement_secret_of_the_universe="CgkIu_W6xfwCEAIQHw";
	public static final String achievement_silver_jubilee="CgkIu_W6xfwCEAIQGG";
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
		mainMenuButtonDraw, mainMenuGrayButtonDraw, mainMenuPressedButtonDraw, multiPlayerButtonDraw,
		multiPlayerGrayButtonDraw, multiPlayerPressedButtonDraw, newGameButtonDraw, newGameGrayButtonDraw,
		newGamePressedButtonDraw, pauseButtonDraw, pauseGrayButtonDraw, pausePressedButtonDraw,
		playButtonDraw, playGrayButtonDraw, playPressedButtonDraw, playAgainButtonDraw, playAgainGrayButtonDraw,
		playAgainPressedButtonDraw, resumeButtonDraw, resumeGrayButtonDraw, resumePressedButtonDraw,
		rulesButtonDraw, rulesGrayButtonDraw, rulesPressedButtonDraw, singlePlayerButtonDraw,
		singlePlayerGrayButtonDraw, singlePlayerPressedButtonDraw, statsButtonDraw, statsGrayButtonDraw,
		statsPressedButtonDraw, submitButtonDraw, submitGrayButtonDraw, submitPressedButtonDraw,
		tutorialButtonDraw, tutorialGrayButtonDraw, tutorialPressedButtonDraw, pressedHumanCpuButtonDraw,
		unpressedHumanButtonDraw, unpressedCpuButtonDraw, logoDraw, nextButtonDraw, nextGrayButtonDraw,
		nextPressedButtonDraw;

	@Override
	public void create() {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		// Logs the HEIGHT and WIDTH of screen under the DEBUG flag.
		if (DEBUG) {
			Gdx.app.log("Screen Size", WIDTH + " " + HEIGHT);
		}
		MAX_NUMBER_PLAYERS = 6;
		
		// Load the drawables for image buttons
		achievementsButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/achievements.jpg"))));
		achievementsGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/achievementsGray.jpg"))));
		achievementsPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/achievementsPressed.png"))));
		backButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/backb.jpg"))));
		backGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/backbGray.jpg"))));
		backPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/backbPressed.png"))));
		exitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/exit.jpg"))));
		exitGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/exitGray.jpg"))));
		exitPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/exitPressed.png"))));
		leaderboardButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/leaderboard.jpg"))));
		leaderboardGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/leaderboardGray.jpg"))));
		leaderboardPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/leaderboardPressed.png"))));
		mainMenuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/mainMenu.jpg"))));
		mainMenuGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/mainMenuGray.jpg"))));
		mainMenuPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/mainMenuPressed.png"))));
		multiPlayerButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/multiPlayer.jpg"))));
		multiPlayerGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/multiPlayerGray.jpg"))));
		multiPlayerPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/multiPlayerPressed.png"))));
		newGameButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/newGame.jpg"))));
		newGameGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/newGameGray.jpg"))));
		newGamePressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/newGamePressed.png"))));
		pauseButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pause.jpg"))));
		pauseGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pauseGray.jpg"))));
		pausePressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pausePressed.png"))));
		playButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/play.jpg"))));
		playGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playGray.jpg"))));
		playPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playPressed.png"))));
		playAgainButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playAgain.jpg"))));
		playAgainGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playAgainGray.jpg"))));
		playAgainPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playAgainPressed.png"))));
		resumeButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/resume.jpg"))));
		resumeGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/resumeGray.jpg"))));
		resumePressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/resumePressed.png"))));
		rulesButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/rules.jpg"))));
		rulesGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/rulesGray.jpg"))));
		rulesPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/rulesPressed.png"))));
		singlePlayerButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/singlePlayer.jpg"))));
		singlePlayerGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/singlePlayerGray.jpg"))));
		singlePlayerPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/singlePlayerPressed.png"))));
		statsButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/statistics.jpg"))));
		statsGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/statisticsGray.jpg"))));
		statsPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/statisticsPressed.png"))));
		submitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/submit.jpg"))));
		submitGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/submitGray.jpg"))));
		submitPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/submitPressed.png"))));
		tutorialButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/tutorial.jpg"))));
		tutorialGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/tutorialGray.jpg"))));
		tutorialPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/tutorialPressed.png"))));
		pressedHumanCpuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pressedHumanCpu.png"))));
		unpressedHumanButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/unpressedHuman.png"))));
		unpressedCpuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/unpressedCPU.png"))));
		logoDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("Logo.png"))));
		nextButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/next.jpg"))));
		nextGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/nextGray.jpg"))));
		nextPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/nextPressed.png"))));
		
		// Loading game background image
		texture = new Texture(Gdx.files.internal("back.jpg"));
		textureGray = new Texture(Gdx.files.internal("backGray.jpg"));
		mainGameScreenTexture = new Texture(Gdx.files.internal("mainGameScreenBack.jpg"));
		
		// Loading the skin
		skin = new Skin(Gdx.files.internal("data/uiskin.json"),
				new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));
		
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