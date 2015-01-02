package com.chainreactionai.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	public static int MAX_Z_DIST_OF_NEW_BALLS = 600;
	public static int MIN_Z_DIST_OF_NEW_BALLS = 200;
	public static int MAX_SPEED_OF_BALLS = 3;
	public static int MIN_SPEED_OF_BALLS = 1;
	public static int MAX_NUMBER_OF_BALLS_AT_A_MOMENT = 5;
	public static int MAX_ARRAY_SIZE = 150;
	
	final private boolean DEBUG = false;
	
	// Drawables for ImageButtons
	public static Drawable backButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("backButton.jpg"))));
	public static Drawable cpuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/cpu_button.png"))));
	public static Drawable exitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/exit_button.png"))));
	public static Drawable humanButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/human_button.png"))));
	public static Drawable mainMenuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/main_menu_button.png"))));
	public static Drawable newGameButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/new_game_button.png"))));
	public static Drawable playButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/play_button.png"))));
	public static Drawable resumeButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/resume_button.png"))));
	public static Drawable rulesButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/rules_button.png"))));
	public static Drawable statsButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/stats_button.png"))));
	public static Drawable submitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/submit_button.png"))));

	@Override
	public void create() {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		// Logs the HEIGHT and WIDTH of screen under the DEBUG flag.
		if (DEBUG) {
			Gdx.app.log("Screen Size", WIDTH + " " + HEIGHT);
		}
		MAX_NUMBER_PLAYERS = 6;
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