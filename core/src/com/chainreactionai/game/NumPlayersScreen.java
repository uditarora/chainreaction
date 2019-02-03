/**
 * 
 */
package com.chainreactionai.game;

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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Kartik Parnami
 * 
 */
public class NumPlayersScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int WIDTH_SLIDER = 170;
	final private int HEIGHT_KNOB = 25;
	final private int WIDTH_KNOB = 18;
	final private int WIDTH_SUBMIT_BUTTON = 275;
	final private int HEIGHT_SUBMIT_BUTTON = 60;
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Stage stage = new Stage();
	private Table table = new Table();
	private int MAX_NUMBER_OF_PLAYERS = ChainReactionAIGame.MAX_NUMBER_PLAYERS, NUMBER_OF_DIFFICULTY_LEVELS = 10;
	private ImageButton submitButton;
	private Label title;
	private Slider numPlayerSlider;
	private Label numPlayerLabel;
	// Trying 3D Graphics
	private PerspectiveCamera cam;
	private Image img = new Image(ChainReactionAIGame.texture);
	
	public NumPlayersScreen(ChainReactionAIGame game) {
		ChainReactionAIGame.currentScreen = 1;
		myGame = game;
		create();
	}

	// Initialization function
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
	    // Initializing and adding the title to Table.
		title = new Label("Choose number of opponents", ChainReactionAIGame.skin);
		title.setFontScale(heightUpscaleFactor/2);
		table.add(title).padBottom(10).row();
		// Initializing the Slider
		numPlayerSlider = new Slider(1, MAX_NUMBER_OF_PLAYERS-1, 1, false, ChainReactionAIGame.sliderSkin);
		numPlayerSlider.getStyle().knob.setMinHeight(HEIGHT_KNOB*heightUpscaleFactor);
		numPlayerSlider.getStyle().knob.setMinWidth(WIDTH_KNOB*heightUpscaleFactor);
		numPlayerSlider.getStyle().background.setMinHeight((float)HEIGHT_KNOB*heightUpscaleFactor/5);
		// Adding the Slider to the Table.
		table.add(numPlayerSlider).size(WIDTH_SLIDER*widthUpscaleFactor).padTop(5).padBottom(5);
		// To allow the sliders to be dragged properly
		InputListener stopTouchDown = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
			    return false;
			}
		};
		numPlayerSlider.addListener(stopTouchDown);
		// Label Initialize
		numPlayerLabel = new Label("1", ChainReactionAIGame.skin);
		numPlayerLabel.setFontScale(heightUpscaleFactor/2);
		table.add(numPlayerLabel).padBottom(7).padTop(3).row();
		// Initializing and adding the Submit Button to Table.
		submitButton = new ImageButton(ChainReactionAIGame.submitButtonDraw, ChainReactionAIGame.submitPressedButtonDraw);
		submitButton.getImageCell().expand().fill();
		table.add(submitButton).size(WIDTH_SUBMIT_BUTTON*widthUpscaleFactor, HEIGHT_SUBMIT_BUTTON*widthUpscaleFactor).padBottom(2).row();
		table.setFillParent(true);
		// Adding the table to the stage.
		img.setFillParent(true);
		stage.addActor(img);
		stage.addActor(table);
		// Attaching the ClickListener to the submit button.
		if (!ChainReactionAIGame.GRAYED_OUT) {
			submitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					int chosenNumOfPlayers = (int)(numPlayerSlider.getValue())+1;
					myGame.setScreen(new ChooseOpponentsAndLevelsScreen(myGame, chosenNumOfPlayers, NUMBER_OF_DIFFICULTY_LEVELS));
				}
			});
		} else {
			submitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					int chosenNumOfPlayers = (int)(numPlayerSlider.getValue())+1;
					myGame.setScreen(new TutorialTextScreen(myGame, chosenNumOfPlayers, NUMBER_OF_DIFFICULTY_LEVELS, 3));
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
		numPlayerLabel.setText(String.valueOf((int)(numPlayerSlider.getValue())));
		stage.act(delta);
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			myGame.setScreen(new MainMenuScreen(myGame));
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
