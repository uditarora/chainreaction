/**
 * 
 */
package com.chainreactionai.game;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final boolean MONTE_CARLO = false;
	private long prevTime, newTime;
	private PrintWriter out;
	// Debug
	private boolean GRAYED_OUT = true;

	public SplashScreen(ChainReactionAIGame game) {
		prevTime = System.currentTimeMillis();
		if (MONTE_CARLO) {
			try {
				out = new PrintWriter("monteCarloResults1.txt");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			int numPlayers = 2;
			int[] difficultyLevelList = new int[numPlayers]; int[] heuristicNumber = new int[numPlayers];
			difficultyLevelList[0] = 1; difficultyLevelList[1] = 4;
//			difficultyLevelList[2] = 1; difficultyLevelList[3] = 2;
			heuristicNumber[0] = 12; heuristicNumber[1] = 12;
//			heuristicNumber[2] = 12; heuristicNumber[3] = 12;
			MonteCarlo monteCarlo;
			for (int i = 0; i < 2; ++i)
			{
				for (int j = 0; j < 2; ++j)
				{
					difficultyLevelList[0] = i; difficultyLevelList[1] = j;
//					System.out.println("\nLevel "+i+" vs Level "+j);
					monteCarlo = new MonteCarlo(numPlayers, difficultyLevelList, heuristicNumber, true, out);
					if (i+j > 10)
						monteCarlo.runSimulations(100);
					else
						monteCarlo.runSimulations(200);
				}
			}
//			monteCarlo = new MonteCarlo(numPlayers, difficultyLevelList, heuristicNumber, true);		
//			monteCarlo.runSimulations(1000);
			out.close();
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
			if (!GRAYED_OUT)
				myGame.setScreen(new MainMenuScreen(myGame));
			else
				myGame.setScreen(new TutorialTextScreen(myGame, 1));
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
