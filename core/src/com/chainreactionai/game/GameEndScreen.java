/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Parnami
 *
 */
public class GameEndScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int WIDTH_BUTTONS = 275;
	final private int HEIGHT_BUTTONS = 60;
	private float widthUpscaleFactor;
	private ArrayList<Boolean> isCPU = new ArrayList<Boolean>();
	private ArrayList<Integer> difficultyLevelList = new ArrayList<Integer>();
	private Stage stage = new Stage();
	private Table table = new Table();
	private ImageButton buttonPlayAgain, buttonNewGame, buttonExit, buttonAchievements, buttonLeaderboard;
	private int winningPlayer, numMovesPlayed;
	private Label title;
	// Trying 3D Graphics
	private PerspectiveCamera cam;
	private Image img = new Image(ChainReactionAIGame.texture);
	
	public GameEndScreen (ChainReactionAIGame game, int winner, int numMovesPlayed, boolean[] isCPU, int[] difficultyLevels) {
		myGame = game;
		winningPlayer = winner;
		this.numMovesPlayed = numMovesPlayed;
		for (int i = 0; i < isCPU.length; i += 1) {
			this.isCPU.add(isCPU[i]);
			this.difficultyLevelList.add(difficultyLevels[i]);
		}
		create();
	}
	
	private void create() {
		batch = new SpriteBatch();
		// Up-scale Factors are used to get proper sized buttons
		// upscaled or downscaled according to the Screen Dimensions
		widthUpscaleFactor = ((float)(ChainReactionAIGame.WIDTH))/WIDTH_SCREEN;
		// Trying 3D graphics
		cam = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float camZ = ((float)1440*720/1240)*((float)Gdx.graphics.getHeight()/Gdx.graphics.getWidth());
		cam.position.set(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, camZ);
	    cam.lookAt(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, 0);
	    cam.near = 1f;
	    cam.far = 4000f;
	    cam.update();
	    // The elements are displayed in the order you add them.
		// The first appear on top, the last at the bottom.
		// Initialize and add the winning quote to the Table
		title = new Label("Player " + String.valueOf(winningPlayer + 1) + " has won the game after " + numMovesPlayed + " moves!", ChainReactionAIGame.skin);
		title.setFontScale(((float)ChainReactionAIGame.HEIGHT/HEIGHT_SCREEN)/2);
		table.add(title).padBottom(30*(float)ChainReactionAIGame.HEIGHT/HEIGHT_SCREEN).row();
		// Add the PlayAgain and Exit buttons to the Table.
		buttonPlayAgain = new ImageButton(ChainReactionAIGame.playAgainButtonDraw, ChainReactionAIGame.playAgainPressedButtonDraw);
		buttonNewGame = new ImageButton(ChainReactionAIGame.newGameButtonDraw, ChainReactionAIGame.newGamePressedButtonDraw);
		buttonExit = new ImageButton(ChainReactionAIGame.exitButtonDraw, ChainReactionAIGame.exitPressedButtonDraw);
		buttonAchievements = new ImageButton(ChainReactionAIGame.achievementsButtonDraw, ChainReactionAIGame.achievementsPressedButtonDraw);
		buttonLeaderboard = new ImageButton(ChainReactionAIGame.leaderboardButtonDraw, ChainReactionAIGame.leaderboardPressedButtonDraw);
		buttonPlayAgain.getImageCell().expand().fill();
		buttonNewGame.getImageCell().expand().fill();
		buttonExit.getImageCell().expand().fill();
		buttonAchievements.getImageCell().expand().fill();
		buttonLeaderboard.getImageCell().expand().fill();
		table.add(buttonPlayAgain).size(WIDTH_BUTTONS*widthUpscaleFactor, HEIGHT_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		table.add(buttonNewGame).size(WIDTH_BUTTONS*widthUpscaleFactor, HEIGHT_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		table.add(buttonAchievements).size(WIDTH_BUTTONS*widthUpscaleFactor, HEIGHT_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		table.add(buttonLeaderboard).size(WIDTH_BUTTONS*widthUpscaleFactor, HEIGHT_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		table.add(buttonExit).size(WIDTH_BUTTONS*widthUpscaleFactor, HEIGHT_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		table.setFillParent(true);
		// Add table to the stage.
		img.setFillParent(true);
		stage.addActor(img);
		stage.addActor(table);
		// Add ClickListeners to the Play Again and Exit buttons
		buttonPlayAgain.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Same way we moved here from the Splash Screen
				// We set it to new Splash because we got no other screens
				// otherwise you put the screen there where you want to go
				myGame.setScreen(new MainGameScreenChar(myGame, isCPU, difficultyLevelList));
			}
		});
		buttonNewGame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Same way we moved here from the Splash Screen
				// We set it to new Splash because we got no other screens
				// otherwise you put the screen there where you want to go
				myGame.setScreen(new NumPlayersScreen(myGame));
			}
		});
		buttonAchievements.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ChainReactionAIGame.googleServices.showAchievement();
			}
		});
		buttonLeaderboard.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ChainReactionAIGame.googleServices.showScores();
			}
		});
		buttonExit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		Gdx.input.setInputProcessor(stage);
		ChainReactionAIGame.googleServices.showScores();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
		stage.act();
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			myGame.setScreen(new MainMenuScreen(myGame));
		}
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
//		skin.dispose();
	}
}
