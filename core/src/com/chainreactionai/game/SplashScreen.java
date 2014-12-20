/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * @author Parnami
 * 
 */
public class SplashScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	private Texture splashScreenBackground;
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 480;
	final boolean MONTE_CARLO = false;
	private long prevTime, newTime;

	public SplashScreen(ChainReactionAIGame game) {
		prevTime = System.currentTimeMillis();
		if (MONTE_CARLO) {
			int numPlayers = 4;
			int[] plyLevelList = new int[numPlayers]; int[] heuristicNumber = new int[numPlayers];
			plyLevelList[0] = 1; plyLevelList[1] = 1;
			plyLevelList[2] = 1; plyLevelList[3] = 2;
			heuristicNumber[0] = 12; heuristicNumber[1] = 12;
			heuristicNumber[2] = 12; heuristicNumber[3] = 12;
			MonteCarlo monteCarlo = new MonteCarlo(numPlayers, plyLevelList, heuristicNumber);
			
			monteCarlo.runSimulations(100);
			Gdx.app.exit();
		}
		myGame = game;
		create();
	}

	// Initialization function
	private void create() {
		batch = new SpriteBatch();
		// Show the world to be 440*480 no matter the
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH_SCREEN, HEIGHT_SCREEN);
		// Load the SplashScreen image.
		splashScreenBackground = new Texture("SplashScreen.jpg");
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		newTime = System.currentTimeMillis();
		// Tell the camera to update its matrices.
		camera.update();

		// Tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(splashScreenBackground, 0, 0);
		batch.end();

		// When user clicks on the screen, go to the main menu screen
		if (newTime - prevTime > 1000) {
			myGame.setScreen(new MainMenuScreen(myGame));
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
