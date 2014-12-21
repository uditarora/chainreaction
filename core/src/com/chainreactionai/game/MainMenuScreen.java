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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Kartik Parnami
 * 
 */
public class MainMenuScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int HEIGHT_MAIN_MENU_BUTTONS = 60;
	final private int WIDTH_MAIN_MENU_BUTTONS = 150;
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Stage stage = new Stage();
	private Table table = new Table();
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"),
			new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private TextButton buttonPlay = new TextButton("Play",
			skin), buttonExit = new TextButton("Exit", skin), buttonRules = new TextButton("Rules", skin);
	private Label title = new Label("Chain Reaction", skin);
	private TextButtonStyle playButtonStyler;
	
	public MainMenuScreen(ChainReactionAIGame game) {
		ChainReactionAIGame.currentScreen = 0;
		myGame = game;
		create();
	}

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
		// Adds the title and buttons to the Table.
		title.setFontScale((1+(heightUpscaleFactor-1)/2));
		table.add(title).padBottom(40).row();
		playButtonStyler = new TextButtonStyle(buttonPlay.getStyle());
		playButtonStyler.font.setScale((1+(heightUpscaleFactor-1)/2));
		buttonPlay.setStyle(playButtonStyler);
		table.add(buttonPlay).size(WIDTH_MAIN_MENU_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_MAIN_MENU_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(20).row();
		buttonRules.setStyle(playButtonStyler);
		table.add(buttonRules).size(WIDTH_MAIN_MENU_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_MAIN_MENU_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(20).row();
		buttonExit.setStyle(playButtonStyler);
		table.add(buttonExit).size(WIDTH_MAIN_MENU_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_MAIN_MENU_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(20).row();
		table.setFillParent(true);
		// Adding the table to stage.
		stage.addActor(table);
		// Attaching ClickListeners to the Play and Exit buttons.
		buttonPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new NumPlayersScreen(myGame));
			}
		});
		buttonRules.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new GameRulesScreen(myGame));
			}
		});
		buttonExit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			Gdx.app.exit();
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
