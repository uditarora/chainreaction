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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * @author Kartik Parnami
 *
 */

public class ChooseOpponentsAndLevelsScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int WIDTH_HUMAN_CPU_MENUS = 90;
	final private int HEIGHT_HUMAN_CPU_MENUS = 30;
	final private int WIDTH_SUBMIT_BUTTON = 275;
	final private int HEIGHT_SUBMIT_BUTTON = 60;
	final private int WIDTH_SLIDER = 170;
	final private int HEIGHT_SLIDER = 30;
	final private int HEIGHT_KNOB = 25;
	final private int WIDTH_KNOB = 18;
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Stage stage = new Stage();
	private Table table = new Table(), container = new Table();
	private ScrollPane scroll;
	private int NUMBER_OF_PLAYERS, NUMBER_OF_DIFFICULTY_LEVELS;
	private ImageButton submitButton;
	private Array<ImageButton> userSelectButtons;
	private Array<Slider> plySliders;
	private Array<Label> plyLabels;
	private Array<Boolean> userSelectIsHuman;
	ArrayList<Boolean> isCPU = new ArrayList<Boolean>();
	ArrayList<Integer> plyLevelList = new ArrayList<Integer>();
	// Trying 3D Graphics
	private PerspectiveCamera cam;
	private Image img = new Image(ChainReactionAIGame.texture);
	
	// Constructor which initializes the number of players passed from
	// NumPlayersScreen and the number if difficulty levels allowed.
	public ChooseOpponentsAndLevelsScreen(ChainReactionAIGame game, int numPlayers, int numDifficultyLevels) {
		myGame = game;
		NUMBER_OF_PLAYERS = numPlayers;
		NUMBER_OF_DIFFICULTY_LEVELS = numDifficultyLevels;
		create();
	}

	// Initializer function.
	private void create() {
		batch = new SpriteBatch();
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
	    // Creating the ToggleButton for whether the player should be 
		// human or CPU.
		ImageButton tempButton = new ImageButton(ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw);
		userSelectButtons = new Array<ImageButton>();
		userSelectIsHuman = new Array<Boolean>();
		tempButton.setChecked(true);
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			if (i != 0) {
				tempButton = new ImageButton(ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw);
				tempButton.setChecked(false);
				tempButton.getImageCell().expand().fill();
			}
			userSelectButtons.add(tempButton);
			userSelectIsHuman.add(tempButton.isChecked());
		}
		// Creating the Sliders for what should be the 
		// difficulty level of a given CPU player.
		plySliders = new Array<Slider>();
		plyLabels = new Array<Label>();
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			plySliders.add(new Slider(1, NUMBER_OF_DIFFICULTY_LEVELS, 1, false, ChainReactionAIGame.sliderSkin));
			plyLabels.add(new Label(String.valueOf((int)plySliders.get(i).getValue()), ChainReactionAIGame.skin));
		}
		// To allow the sliders to be dragged properly
		InputListener stopTouchDown = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
			    return false;
			}
		};
		// Adding the Slider to the Table.
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			Label tempLabel = new Label("Player " + String.valueOf(i+1) + ":", ChainReactionAIGame.skin);
			tempLabel.setFontScale(heightUpscaleFactor/2);
			table.add(tempLabel);
			table.add(userSelectButtons.get(i)).size(WIDTH_HUMAN_CPU_MENUS*(widthUpscaleFactor), (HEIGHT_HUMAN_CPU_MENUS*widthUpscaleFactor)).row();
			// To allow the sliders to be dragged properly
			plySliders.get(i).addListener(stopTouchDown);
			plySliders.get(i).getStyle().knob.setMinHeight(HEIGHT_KNOB*heightUpscaleFactor);
			plySliders.get(i).getStyle().knob.setMinWidth(WIDTH_KNOB*heightUpscaleFactor);
			plySliders.get(i).getStyle().background.setMinHeight((float)HEIGHT_KNOB*heightUpscaleFactor/5);
			if (i < NUMBER_OF_PLAYERS-1)
				table.add(plySliders.get(i)).height(HEIGHT_SLIDER*heightUpscaleFactor).width(WIDTH_SLIDER*widthUpscaleFactor).padBottom(10*heightUpscaleFactor);
			else
				table.add(plySliders.get(i)).height(HEIGHT_SLIDER*heightUpscaleFactor).width(WIDTH_SLIDER*widthUpscaleFactor).padBottom(20*heightUpscaleFactor);
			// Add the labels containing the currently selected plyLevel
			Label tempLabel2 = plyLabels.get(i);
			tempLabel2.setFontScale(heightUpscaleFactor/2);
			table.add(tempLabel2).padBottom(10).row();
		}
		// Creating and adding the submit button to the Table.
		submitButton = new ImageButton(ChainReactionAIGame.submitButtonDraw, ChainReactionAIGame.submitPressedButtonDraw);
		submitButton.getImageCell().expand().fill();
		table.add(submitButton).size(WIDTH_SUBMIT_BUTTON*widthUpscaleFactor, HEIGHT_SUBMIT_BUTTON*widthUpscaleFactor).row();
		// Scroll pane consisting of the Table.
		scroll = new ScrollPane(table);
		// Container is the outside covering which contains the
		// ScrollPane.
		container.setFillParent(true);
		container.add(scroll).fill().expand().row();
		// Adding container to stage.
		img.setFillParent(true);
		stage.addActor(img);
		stage.addActor(container);
		// Adding ClickListener to the submit button
		if (!ChainReactionAIGame.GRAYED_OUT) {
			submitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Same way we moved here from the Splash Screen
					// We set it to new Splash because we got no other screens
					// otherwise you put the screen there where you want to go
					int j;
					for (j = 0; j < NUMBER_OF_PLAYERS; j += 1) {
						if (userSelectButtons.get(j).isChecked()) {
							isCPU.add(false);
						} else {
							isCPU.add(true);
						}
						plyLevelList.add((int) (plySliders.get(j).getValue()));
					}
					myGame.setScreen(new MainGameScreenChar(myGame, isCPU, plyLevelList));
				}
			});
		} else {
			submitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Same way we moved here from the Splash Screen
					// We set it to new Splash because we got no other screens
					// otherwise you put the screen there where you want to go
					int j;
					for (j = 0; j < NUMBER_OF_PLAYERS; j += 1) {
						if (userSelectButtons.get(j).isChecked()) {
							isCPU.add(false);
						} else {
							isCPU.add(true);
						}
						plyLevelList.add((int) (plySliders.get(j).getValue()));
					}
					myGame.setScreen(new TutorialTextScreen(myGame, NUMBER_OF_PLAYERS, NUMBER_OF_DIFFICULTY_LEVELS, 4, isCPU, plyLevelList));
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
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			if (userSelectButtons.get(i).isChecked()) {
				ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw);
				userSelectButtons.get(i).setStyle(temp);
				plySliders.get(i).setDisabled(true);
			} else {
				ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw);
				userSelectButtons.get(i).setStyle(temp);
				plySliders.get(i).setDisabled(false);
			}
		}
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			plyLabels.get(i).setText(String.valueOf((int)plySliders.get(i).getValue()));
		}
		stage.act();
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			myGame.setScreen(new NumPlayersScreen(myGame));
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
