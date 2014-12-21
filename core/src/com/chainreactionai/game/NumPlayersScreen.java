/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * @author Kartik Parnami
 * 
 */
public class NumPlayersScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int HEIGHT_DROP_DOWN_MENUS = 35;
	final private int WIDTH_DROP_DOWN_MENUS = 150;
	final private int WIDTH_SUBMIT_BUTTON = 100;
	final private int HEIGHT_SUBMIT_BUTTON = 40;
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Stage stage = new Stage();
	private Table table = new Table();
	private int MAX_NUMBER_OF_PLAYERS = 6, NUMBER_OF_DIFFICULTY_LEVELS = 10;
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"),
			new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private TextButton submitButton;
	private Label title;
	private SelectBox<String> selectBox;
	private SelectBoxStyle selectBoxStyler;
	private TextButtonStyle submitButtonStyler;

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
		// Show the world to be 440*480 no matter the
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH_SCREEN, HEIGHT_SCREEN);
		// Initializing and adding the title to Table.
		title = new Label("Choose number of players", skin);
		title.setFontScale((1+(heightUpscaleFactor-1)/2));
		table.add(title).padBottom(10).row();
		// Initializing the Drop-Down menu
		selectBox = new SelectBox<String>(skin);
		selectBox.setMaxListCount(MAX_NUMBER_OF_PLAYERS);
		Array<String> tempStringArr = new Array<String>();
		for (int i = 1; i < MAX_NUMBER_OF_PLAYERS; i += 1) {
			tempStringArr.add(String.valueOf(i+1));
		}
		selectBox.setItems(tempStringArr);
		selectBoxStyler = new SelectBoxStyle(selectBox.getStyle());
		selectBoxStyler.font.setScale((1+(heightUpscaleFactor-1)/2));
		selectBox.setStyle(selectBoxStyler);
		selectBox.setHeight(50);
		System.out.println("Height: " + selectBox.getHeight() + " max height: " + selectBox.getMaxHeight());
		// Adding the DropDown to the Table.
		table.add(selectBox).size(WIDTH_DROP_DOWN_MENUS*(1+(widthUpscaleFactor-1)/2), HEIGHT_DROP_DOWN_MENUS*(1+(heightUpscaleFactor-1)/2)).padBottom(10).row();
		// Initializing and adding the Submit Button to Table.
		submitButton = new TextButton(new String("Submit"), skin);
		submitButtonStyler = new TextButtonStyle(submitButton.getStyle());
		submitButtonStyler.font.setScale((1+(heightUpscaleFactor-1)/2));
		submitButton.setStyle(submitButtonStyler);
		table.add(submitButton).size(WIDTH_SUBMIT_BUTTON*(1+(widthUpscaleFactor-1)/2), HEIGHT_SUBMIT_BUTTON*(1+(heightUpscaleFactor-1)/2)).padBottom(2).row();
		table.setFillParent(true);
		// Adding the table to the stage.
		stage.addActor(table);
		// Attaching the ClickListener to the submit button.
		submitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int chosenNumOfPlayers = Integer.parseInt(selectBox.getSelected());
				myGame.setScreen(new ChooseOpponentsAndLevelsScreen(myGame, chosenNumOfPlayers, NUMBER_OF_DIFFICULTY_LEVELS));
			}
		});
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
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
		skin.dispose();
	}
}
