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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
	private Image img;
	private Stage stage = new Stage();
//	private PrintWriter out;

	public SplashScreen(ChainReactionAIGame game) {
		prevTime = System.currentTimeMillis();
		
		// Monte Carso Simulations Stuff
//		if (MONTE_CARLO) {
//			try {
//				out = new PrintWriter("monteCarloResults1.txt");
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			int numPlayers = 2;
//			int[] difficultyLevelList = new int[numPlayers]; int[] heuristicNumber = new int[numPlayers];
//			difficultyLevelList[0] = 1; difficultyLevelList[1] = 4;
////			difficultyLevelList[2] = 1; difficultyLevelList[3] = 2;
//			heuristicNumber[0] = 12; heuristicNumber[1] = 12;
////			heuristicNumber[2] = 12; heuristicNumber[3] = 12;
//			MonteCarlo monteCarlo;
//			for (int i = 0; i < 2; ++i)
//			{
//				for (int j = 0; j < 2; ++j)
//				{
//					difficultyLevelList[0] = i; difficultyLevelList[1] = j;
////					System.out.println("\nLevel "+i+" vs Level "+j);
//					monteCarlo = new MonteCarlo(numPlayers, difficultyLevelList, heuristicNumber, true, out);
//					if (i+j > 10)
//						monteCarlo.runSimulations(100);
//					else
//						monteCarlo.runSimulations(200);
//				}
//			}
////			monteCarlo = new MonteCarlo(numPlayers, difficultyLevelList, heuristicNumber, true);		
////			monteCarlo.runSimulations(1000);
//			out.close();
//			Gdx.app.exit();
//		}
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
		img = new Image(splashScreenBackground);
		img.setFillParent(true);
		stage.addActor(img);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		newTime = System.currentTimeMillis();
		// Tell the camera to update its matrices.
		camera.update();
		stage.act();
		stage.draw();

		// When user clicks on the screen, go to the main menu screen
		if (newTime - prevTime > 1000) {
			// Load the imageButton textures which were previously loaded in ChainReactionAIGame.java
			loadAllTextures();
			if (!ChainReactionAIGame.GRAYED_OUT)
				myGame.setScreen(new MainMenuScreen(myGame));
			else
				myGame.setScreen(new TutorialTextScreen(myGame, 1));
		}
	}
	
	void loadAllTextures() {
		
		// Load the drawables for image buttons
		ChainReactionAIGame.achievementsButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/achievements.jpg"))));
		ChainReactionAIGame.achievementsGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/achievementsGray.jpg"))));
		ChainReactionAIGame.achievementsPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/achievementsPressed.png"))));
		ChainReactionAIGame.backButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/backb.jpg"))));
		ChainReactionAIGame.backGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/backbGray.jpg"))));
		ChainReactionAIGame.backPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/backbPressed.png"))));
		ChainReactionAIGame.exitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/exit.jpg"))));
		ChainReactionAIGame.exitGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/exitGray.jpg"))));
		ChainReactionAIGame.exitPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/exitPressed.png"))));
		ChainReactionAIGame.leaderboardButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/leaderboard.jpg"))));
		ChainReactionAIGame.leaderboardGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/leaderboardGray.jpg"))));
		ChainReactionAIGame.leaderboardPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/leaderboardPressed.png"))));
		ChainReactionAIGame.mainMenuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/mainMenu.jpg"))));
		ChainReactionAIGame.mainMenuGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/mainMenuGray.jpg"))));
		ChainReactionAIGame.mainMenuPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/mainMenuPressed.png"))));
		ChainReactionAIGame.newGameButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/newGame.jpg"))));
		ChainReactionAIGame.newGameGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/newGameGray.jpg"))));
		ChainReactionAIGame.newGamePressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/newGamePressed.png"))));
		ChainReactionAIGame.pauseButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pause.jpg"))));
		ChainReactionAIGame.pauseGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pauseGray.jpg"))));
		ChainReactionAIGame.pausePressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pausePressed.png"))));
		ChainReactionAIGame.playButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/play.jpg"))));
		ChainReactionAIGame.playGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playGray.jpg"))));
		ChainReactionAIGame.playPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playPressed.png"))));
		ChainReactionAIGame.playAgainButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playAgain.jpg"))));
		ChainReactionAIGame.playAgainGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playAgainGray.jpg"))));
		ChainReactionAIGame.playAgainPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/playAgainPressed.png"))));
		ChainReactionAIGame.resumeButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/resume.jpg"))));
		ChainReactionAIGame.resumeGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/resumeGray.jpg"))));
		ChainReactionAIGame.resumePressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/resumePressed.png"))));
		ChainReactionAIGame.rulesButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/rules.jpg"))));
		ChainReactionAIGame.rulesGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/rulesGray.jpg"))));
		ChainReactionAIGame.rulesPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/rulesPressed.png"))));
		ChainReactionAIGame.submitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/submit.jpg"))));
		ChainReactionAIGame.submitGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/submitGray.jpg"))));
		ChainReactionAIGame.submitPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/submitPressed.png"))));
		ChainReactionAIGame.tutorialButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/tutorial.jpg"))));
		ChainReactionAIGame.tutorialGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/tutorialGray.jpg"))));
		ChainReactionAIGame.tutorialPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/tutorialPressed.png"))));
		ChainReactionAIGame.pressedHumanCpuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/pressedHumanCpu.png"))));
		ChainReactionAIGame.unpressedHumanButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/unpressedHuman.png"))));
		ChainReactionAIGame.unpressedCpuButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/unpressedCPU.png"))));
		ChainReactionAIGame.logoDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("Logo.png"))));
		ChainReactionAIGame.nextButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/next.jpg"))));
		ChainReactionAIGame.nextGrayButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/nextGray.jpg"))));
		ChainReactionAIGame.nextPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/nextPressed.png"))));
		ChainReactionAIGame.skipButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/skip.jpg"))));
		ChainReactionAIGame.skipPressedButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("buttons/skipPressed.png"))));
		
		// Loading game background image
		ChainReactionAIGame.texture = new Texture(Gdx.files.internal("back.jpg"));
		ChainReactionAIGame.textureGray = new Texture(Gdx.files.internal("backGray.jpg"));
		ChainReactionAIGame.mainGameScreenTexture = new Texture(Gdx.files.internal("mainGameScreenBack.jpg"));
				
		// Loading the skin
		ChainReactionAIGame.skin = new Skin(Gdx.files.internal("data/uiskin.json"),
				new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));
		ChainReactionAIGame.sliderSkin = new Skin(Gdx.files.internal("data/ui-color.json"),
				new TextureAtlas(Gdx.files.internal("data/ui-yellow.atlas")));
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
