/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;

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
public class ChooseOpponentsAndLevelsScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 650;
	private Stage stage = new Stage();
	private Table table = new Table();
	private int NUMBER_OF_PLAYERS, NUMBER_OF_DIFFICULTY_LEVELS;
	private Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"),
			new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));
	private TextButton submitButton;
	private Label title;
	private Array< SelectBox<String> > plySelectBoxes, userSelectBoxes;
	ArrayList<Boolean> isCPU = new ArrayList<Boolean>();
	ArrayList<Integer> plyLevelList = new ArrayList<Integer>();

	public ChooseOpponentsAndLevelsScreen(ChainReactionAIGame game, int numPlayers, int numDifficultyLevels) {
		myGame = game;
		NUMBER_OF_PLAYERS = numPlayers;
		System.out.println(NUMBER_OF_PLAYERS);
		NUMBER_OF_DIFFICULTY_LEVELS = numDifficultyLevels;
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
		title = new Label("Choose specifications for the players", skin);
		table.add(title).padBottom(2).row();
		Array<String> tempStringArr = new Array<String>();
		tempStringArr.add("Human");
		tempStringArr.add("CPU");
		userSelectBoxes = new Array< SelectBox<String> >();
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			userSelectBoxes.add(new SelectBox<String>(skin));
			userSelectBoxes.get(i).setItems(tempStringArr);
			userSelectBoxes.get(i).setMaxListCount(2);
		}
		tempStringArr.clear();
		plySelectBoxes = new Array< SelectBox<String> >();
		for (int i = 1; i <= NUMBER_OF_DIFFICULTY_LEVELS; i += 1) {
			tempStringArr.add("Level" + String.valueOf(i));
		}
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			plySelectBoxes.add(new SelectBox<String>(skin));
			plySelectBoxes.get(i).setItems(tempStringArr);
			userSelectBoxes.get(i).setMaxListCount(NUMBER_OF_DIFFICULTY_LEVELS);
		}
		tempStringArr.clear();
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			Label tempLabel = new Label("Player " + String.valueOf(i) + ":", skin);
			table.add(tempLabel).padBottom(0).row();
			table.add(userSelectBoxes.get(i)).padBottom(0).row();
			table.add(plySelectBoxes.get(i)).padBottom(2).row();
		}
		submitButton = new TextButton(new String("Submit"), skin);
		table.add(submitButton).size(100, 40).padBottom(20).row();
		table.setFillParent(true);
		stage.addActor(table);
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
					plyLevelList.add((plySelectBoxes.get(j).getSelectedIndex()) + 1);
				}
				myGame.setScreen(new MainGameScreen(isCPU, plyLevelList));
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
