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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Parnami
 *
 */
public class GameEndScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 650;
	private Stage stage = new Stage();
	private Table table = new Table();
	private Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"),
			new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));
	private TextButton buttonPlayAgain = new TextButton("Play Again",
			skin),
			buttonExit = new TextButton("Exit", skin);
	private int winningPlayer;
	private Label title;
	
	public GameEndScreen (ChainReactionAIGame game, int winner) {
		myGame = game;
		winningPlayer = winner;
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
		title = new Label("Player " + String.valueOf(winningPlayer + 1) + " has won the game!", skin);
		table.add(title).padBottom(40).row();
		table.add(buttonPlayAgain).size(150, 60).padBottom(20).row();
		table.add(buttonExit).size(150, 60).padBottom(20).row();
		table.setFillParent(true);
		stage.addActor(table);
		buttonPlayAgain.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Same way we moved here from the Splash Screen
				// We set it to new Splash because we got no other screens
				// otherwise you put the screen there where you want to go
				myGame.setScreen(new NumPlayersScreen(myGame));
			}
		});
		buttonExit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
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
