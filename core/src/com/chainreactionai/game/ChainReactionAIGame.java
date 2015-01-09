package com.chainreactionai.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
	public static Texture texture, mainGameScreenTexture;
	public static Skin skin;
	
	final private boolean DEBUG = false;
	
	// Drawables for ImageButtons
	public static Drawable backButtonDraw, exitButtonDraw, mainMenuButtonDraw, newGameButtonDraw,
		playButtonDraw, resumeButtonDraw, rulesButtonDraw, statsButtonDraw, submitButtonDraw, 
		pressedButtonDraw, unpressedHumanButtonDraw, unpressedCpuButtonDraw, pauseButtonDraw, logoDraw;

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
		backButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/backb.jpg"))));
		exitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/exit.jpg"))));
		mainMenuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/main-menu.jpg"))));
		newGameButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/new-game.jpg"))));
		pauseButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pause.jpg"))));
		playButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/play.jpg"))));
		resumeButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/resume.jpg"))));
		rulesButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/rules.jpg"))));
		statsButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/statistics.jpg"))));
		submitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/submit.jpg"))));
		pressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pressed.png"))));
		unpressedHumanButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/unpressedHuman.png"))));
		unpressedCpuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/unpressedCPU.png"))));
		logoDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("Logo.png"))));
		
		// Loading game background image
		texture = new Texture(Gdx.files.internal("back.jpg"));
		mainGameScreenTexture = new Texture(Gdx.files.internal("mainGameScreenBack.jpg"));
		
		// Loading the skin
		skin = new Skin(Gdx.files.internal("data/uiskin.json"),
				new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));
		
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