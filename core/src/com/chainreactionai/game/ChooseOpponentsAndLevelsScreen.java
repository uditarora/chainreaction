/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * @author Kartik Parnami
 *
 */

public class ChooseOpponentsAndLevelsScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int HEIGHT_DROP_DOWN_MENUS = 30;
	final private int WIDTH_DROP_DOWN_MENUS = 150;
	final private int WIDTH_SUBMIT_BUTTON = 100;
	final private int HEIGHT_SUBMIT_BUTTON = 40;
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Stage stage = new Stage();
	private Table table = new Table(), container = new Table();
	private ScrollPane scroll;
	private int NUMBER_OF_PLAYERS, NUMBER_OF_DIFFICULTY_LEVELS;
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"),
			new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private TextButton submitButton;
	private Label title;
	private Array< SelectBox<String> > userSelectBoxes;
	private Array<Slider> plySliders;
	private Array<Label> plyLabels;
	ArrayList<Boolean> isCPU = new ArrayList<Boolean>();
	ArrayList<Integer> plyLevelList = new ArrayList<Integer>();
	private TextButtonStyle submitButtonStyler;
	private SelectBoxStyle selectBoxStyle;

	// Constructor which initializes the number of players passed from
	// NumPlayersScreen and the number if difficulty levels allowed.
	public ChooseOpponentsAndLevelsScreen(ChainReactionAIGame game, int numPlayers, int numDifficultyLevels) {
		myGame = game;
		NUMBER_OF_PLAYERS = numPlayers;
		System.out.println(NUMBER_OF_PLAYERS);
		NUMBER_OF_DIFFICULTY_LEVELS = numDifficultyLevels;
		create();
	}

	// Initializer function.
	private void create() {
		batch = new SpriteBatch();
		// Show the world to be 440*480 no matter the
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH_SCREEN, HEIGHT_SCREEN);
		// The elements are displayed in the order you add them.
		// The first appear on top, the last at the bottom.
		// Up-scale Factors are used to get proper sized buttons
		// upscaled or downscaled according to the Screen Dimensions
		heightUpscaleFactor = ((float)(ChainReactionAIGame.HEIGHT))/HEIGHT_SCREEN;
		widthUpscaleFactor = ((float)(ChainReactionAIGame.WIDTH))/WIDTH_SCREEN;
		// Label for title.
		title = new Label("Choose specifications for the players", skin);
		title.setFontScale((1+(heightUpscaleFactor-1)/2));
		// Creating the DropDown for whether the player should be 
		// human or CPU.
		SelectBox<String> temp = new SelectBox<String> (skin);
		selectBoxStyle = new SelectBoxStyle(temp.getStyle());
		selectBoxStyle.font.setScale((1+(heightUpscaleFactor-1)/2));
		temp.setStyle(selectBoxStyle);
		Array<String> tempStringArr = new Array<String>();
		tempStringArr.add("Human");
		tempStringArr.add("CPU");
		userSelectBoxes = new Array< SelectBox<String> >();
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			userSelectBoxes.add(new SelectBox<String>(skin));
			selectBoxStyle = new SelectBoxStyle(userSelectBoxes.get(i).getStyle());
			selectBoxStyle.font.setScale((1+(heightUpscaleFactor-1)/2));
			userSelectBoxes.get(i).setStyle(selectBoxStyle);
			userSelectBoxes.get(i).setItems(tempStringArr);
			userSelectBoxes.get(i).setMaxListCount(2);
		}
		tempStringArr.clear();
		// Creating the DropDown for what should be the 
		// difficulty level of a given CPU player.
		plySliders = new Array<Slider>();
		plyLabels = new Array<Label>();
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			plySliders.add(new Slider(1, NUMBER_OF_DIFFICULTY_LEVELS, 1, false, skin));
			plyLabels.add(new Label(String.valueOf((int)plySliders.get(i).getValue()), skin));
		}
		// To allow the sliders to be dragged properly
		InputListener stopTouchDown = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
			    return false;
			}
		};
		
		// Adding the dropdowns to the Table.
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			Label tempLabel = new Label("Player " + String.valueOf(i+1) + ":", skin);
			tempLabel.setFontScale(heightUpscaleFactor);
			table.add(tempLabel);		
			// To allow the sliders to be dragged properly
			plySliders.get(i).addListener(stopTouchDown);
			table.add(plySliders.get(i)).size(WIDTH_DROP_DOWN_MENUS*widthUpscaleFactor);
			// Add the labels containing the currently selected plyLevel
			tempLabel = plyLabels.get(i);
			tempLabel.setFontScale(heightUpscaleFactor);
			table.add(tempLabel);
			table.add(userSelectBoxes.get(i)).size(WIDTH_DROP_DOWN_MENUS*(1+(widthUpscaleFactor-1)/2), HEIGHT_DROP_DOWN_MENUS*(1+(heightUpscaleFactor-1)/2)).padBottom(2).row();
		}
		// Creating and adding the submit button to the Table.
		submitButton = new TextButton(new String("Submit"), skin);
		submitButtonStyler = new TextButtonStyle(submitButton.getStyle());
		submitButtonStyler.font.setScale((1+(heightUpscaleFactor-1)/2));
		submitButton.setStyle(submitButtonStyler);
		table.add(submitButton).size(WIDTH_SUBMIT_BUTTON*(1+(widthUpscaleFactor-1)/2), HEIGHT_SUBMIT_BUTTON*(1+(heightUpscaleFactor-1)/2)).padBottom(20).padTop(10).row();
		// Scroll pane consisting of the Table.
		scroll = new ScrollPane(table);
		// Container is the outside covering which contains the
		// ScrollPane.
		container.setFillParent(true);
		container.add(scroll).fill().expand().row();
		// Adding container to stage.
		stage.addActor(container);
		// Adding ClickListener to the submit button
		submitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Same way we moved here from the Splash Screen
				// We set it to new Splash because we got no other screens
				// otherwise you put the screen there where you want to go
				int j;
				for (j = 0; j < NUMBER_OF_PLAYERS; j += 1) {
					if (userSelectBoxes.get(j).getSelectedIndex() == 0) {
						isCPU.add(false);
					} else {
						isCPU.add(true);
					}
					plyLevelList.add((int) (plySliders.get(j).getValue()));
				}
				myGame.setScreen(new MainGameScreenChar(myGame, isCPU, plyLevelList));
			}
		});
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
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
		skin.dispose();
	}
}
