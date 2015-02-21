/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Kartik Parnami
 * 
 */
public class TutorialTextScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int WIDTH_NEXT_BUTTON = 275;
	final private int HEIGHT_NEXT_BUTTON = 60;
	final private int WIDTH_ANIMATION_BUTTONS = 135;
	final private int HEIGHT_ANIMATION_BUTTONS = 45;
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Stage stage = new Stage();
	private Table table = new Table();
	private ImageButton nextButton, skipButton;
	private Label title, titleFollowup;
	private int textChoice, numPlayers, difficultyLevels;
	// Trying 3D Graphics
	private PerspectiveCamera cam;
	private Image img = new Image(ChainReactionAIGame.texture);
	private ArrayList<Boolean> isCPU = new ArrayList<Boolean>();
	private ArrayList<Integer> difficultyLevelList = new ArrayList<Integer>();
	private ImageButton humanCpuToggleButton, rulesButton;
	
	// For setting showTutorial flag
	private Preferences stats;
	
	public TutorialTextScreen(ChainReactionAIGame game, int numPlayers, int numDifficultyLevels, int textChoice, ArrayList<Boolean> isCPU, ArrayList<Integer> difficultyLevelList) {
		ChainReactionAIGame.currentScreen = 1;
		myGame = game;
		this.textChoice = textChoice;
		this.numPlayers = numPlayers;
		this.difficultyLevels = numDifficultyLevels;
		for (int i = 0; i < numPlayers; i += 1) {
			this.isCPU.add(isCPU.get(i));
		}
		for (int i = 0; i < numPlayers; i += 1) {
			this.difficultyLevelList.add(difficultyLevelList.get(i));
		}
		create();
	}
	
	public TutorialTextScreen(ChainReactionAIGame game, int textChoice) {
		ChainReactionAIGame.currentScreen = 1;
		myGame = game;
		this.textChoice = textChoice;
		this.numPlayers = 0;
		this.difficultyLevels = 0;
		// Initialize ArrayLists
		create();
	}
	
	public TutorialTextScreen(ChainReactionAIGame game, int numPlayers, int numDifficultyLevels, int textChoice) {
		ChainReactionAIGame.currentScreen = 1;
		myGame = game;
		this.textChoice = textChoice;
		this.numPlayers = numPlayers;
		this.difficultyLevels = numDifficultyLevels;
		create();
	}
	
	// Initialization function
	private void create() {
		stats = Gdx.app.getPreferences("chainReactionStatistics");
		batch = new SpriteBatch();
		Gdx.input.setCatchBackKey(true);
		// The elements are displayed in the order you add them.
		// The first appear on top, the last at the bottom.
		// Up-scale Factors are used to get proper sized buttons
		// upscaled or downscaled according to the Screen Dimensions
		heightUpscaleFactor = ((float)(ChainReactionAIGame.HEIGHT))/HEIGHT_SCREEN;
		widthUpscaleFactor = ((float)(ChainReactionAIGame.WIDTH))/WIDTH_SCREEN;
		// Trying 3D graphics
		cam = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float camZ = ((float)1440*720/1240)*((float)Gdx.graphics.getHeight()/Gdx.graphics.getWidth());
		cam.position.set(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, camZ);
	    cam.lookAt(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, 0);
	    cam.near = 1f;
	    cam.far = 4000f;
	    cam.update();
	    // Initializing and adding the title to Table.
        humanCpuToggleButton = new ImageButton(ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw);
        humanCpuToggleButton.getImageCell().expand().fill();
		rulesButton = new ImageButton(ChainReactionAIGame.rulesButtonDraw, ChainReactionAIGame.rulesPressedButtonDraw);
		rulesButton.getImageCell().expand().fill();
		if (textChoice == 1) {
			title = new Label("Here for the first time? "
					+ "Let's get you familiar with the interface of the game. "
					+ "You can invoke this tutorial again anytime, from the main menu.", ChainReactionAIGame.skin);
		} else if (textChoice == 2) {
			title = new Label("Now you will choose the number of opponents. You can select "
					+ "up to 5 opponents using the slider.", ChainReactionAIGame.skin);
		} else if (textChoice == 3) {
			title = new Label("Next, for each player you can toggle whether the player "
					+ "is a CPU bot or a human player using the given red button.", ChainReactionAIGame.skin);
			titleFollowup = new Label("You can also vary the difficulty level of each CPU"
					+ " bot using the sliders alongside.", ChainReactionAIGame.skin);
			titleFollowup.setWrap(true);
			titleFollowup.setFontScale(heightUpscaleFactor/2);
		} else if (textChoice == 4) {
			title = new Label("Now you're ready to rumble. You can enjoy this game against your friends by "
					+ "choosing all human players, go solo against up to 5 "
					+ "CPU bots, or have a mix of both!\nIf you are new to the Chain Reaction concept you can check out the rules by clicking the button underneath.\n\nHave fun, and may the force be with you!", ChainReactionAIGame.skin);
			// Set showTutorial flag to false
			ChainReactionAIGame.GRAYED_OUT = false;
			stats.putBoolean("showTutorial", false);
			stats.flush();
		}
		title.setWrap(true);
		title.setFontScale(heightUpscaleFactor/2);
		table.add(title).width(420*widthUpscaleFactor).padBottom(20*heightUpscaleFactor).row();
		if (textChoice == 3) {
			table.add(humanCpuToggleButton).size(WIDTH_ANIMATION_BUTTONS*widthUpscaleFactor, HEIGHT_ANIMATION_BUTTONS*widthUpscaleFactor).padBottom(10).row();
			humanCpuToggleButton.setChecked(true);
			table.add(titleFollowup).width(420*widthUpscaleFactor).padBottom(20*heightUpscaleFactor).row();
		}
		if (textChoice == 4) {
			table.add(rulesButton).size(WIDTH_NEXT_BUTTON*widthUpscaleFactor, HEIGHT_NEXT_BUTTON*widthUpscaleFactor).padBottom(20).row();
		}
		nextButton = new ImageButton(ChainReactionAIGame.nextButtonDraw, ChainReactionAIGame.nextPressedButtonDraw);
		nextButton.getImageCell().expand().fill();
		table.add(nextButton).size(WIDTH_NEXT_BUTTON*widthUpscaleFactor, HEIGHT_NEXT_BUTTON*widthUpscaleFactor).padBottom(20).row();
		if (textChoice == 1) {
			skipButton = new ImageButton(ChainReactionAIGame.skipButtonDraw, ChainReactionAIGame.skipPressedButtonDraw);
			skipButton.getImageCell().expand().fill();
			table.add(skipButton).size(WIDTH_NEXT_BUTTON*widthUpscaleFactor, HEIGHT_NEXT_BUTTON*widthUpscaleFactor).padBottom(2).row();
		}
		table.setFillParent(true);
		// Adding the table to the stage.
		img.setFillParent(true);
		stage.addActor(img);
		stage.addActor(table);
		// Attaching the ClickListener to the next button.
		if (textChoice == 1) {
			nextButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new MainMenuScreen(myGame));
				}
			});
			skipButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Set showTutorial flag to false
					ChainReactionAIGame.GRAYED_OUT = false;
					stats = Gdx.app.getPreferences("chainReactionStatistics");
					stats.putBoolean("showTutorial", false);
					stats.flush();
					myGame.setScreen(new MainMenuScreen(myGame));
				}
			});
		} else if (textChoice == 2) {
			nextButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new NumPlayersScreen(myGame));
				}
			});
		} else if (textChoice == 3) {
			nextButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new ChooseOpponentsAndLevelsScreen(myGame, numPlayers, difficultyLevels));
				}
			});
		} else if (textChoice == 4) {
			nextButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new MainGameScreenChar(myGame, isCPU, difficultyLevelList));
				}
			});
			rulesButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new GameRulesScreen(myGame, 1));
				}
			});
		}
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
		batch.setProjectionMatrix(cam.combined);
	    batch.begin();
	    batch.draw(ChainReactionAIGame.texture, 0, 0, WIDTH_SCREEN, HEIGHT_SCREEN);
	    batch.end();
		if (textChoice == 3) {
			if (humanCpuToggleButton.isChecked()) {
				ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw);
				humanCpuToggleButton.setStyle(temp);
			} else {
				ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw);
				humanCpuToggleButton.setStyle(temp);
			}
		}
		stage.act(delta);
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			if (textChoice == 1) {
				ChainReactionAIGame.GRAYED_OUT = false;
				stats.putBoolean("showTutorial", false);
				stats.flush();
				myGame.setScreen(new MainMenuScreen(myGame));
			} else if (textChoice == 2) {
				myGame.setScreen(new MainMenuScreen(myGame));
			} else if (textChoice == 3) {
				myGame.setScreen(new NumPlayersScreen(myGame));
			} else if (textChoice == 4) {
				myGame.setScreen(new ChooseOpponentsAndLevelsScreen(myGame, numPlayers, difficultyLevels));
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		//viewport.update(width, height);
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
