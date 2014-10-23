/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 650;
	private Stage stage = new Stage();
	private Table table = new Table();
	private int MAX_NUMBER_OF_PLAYERS = 6, NUMBER_OF_DIFFICULTY_LEVELS = 4;
	private Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"),
			new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));
	private TextButton submitButton;
	private Label title;
	private SelectBox<String> selectBox;

	public NumPlayersScreen(ChainReactionAIGame game) {
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
		title = new Label("Choose number of players", skin);
		table.add(title).padBottom(40).row();
		selectBox = new SelectBox<String>(skin);
		selectBox.setMaxListCount(MAX_NUMBER_OF_PLAYERS);
		Array<String> tempStringArr = new Array<String>();
		for (int i = 1; i < MAX_NUMBER_OF_PLAYERS; i += 1) {
			tempStringArr.add(String.valueOf(i+1));
		}
		selectBox.setItems(tempStringArr);
		table.add(selectBox).size(100, 40).padBottom(20).row();
		submitButton = new TextButton(new String("Submit"), skin);
		table.add(submitButton).size(100, 40).padBottom(20).row();
		table.setFillParent(true);
		stage.addActor(table);
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
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
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
