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
public class NumHumanPlayersScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 480;
	private Stage stage = new Stage();
	private Table table = new Table();
	private int maxNumberOfHumans;
	private Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"),
			new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));
	private TextButton[] buttons;
	private Label title = new Label("Number of human players", skin);

	public NumHumanPlayersScreen(ChainReactionAIGame game, int maxNumHumans) {
		myGame = game;
		maxNumberOfHumans = maxNumHumans;
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
		table.add(title).padBottom(40).row();
		buttons = new TextButton[maxNumberOfHumans];
		for (int i = 1; i <= maxNumberOfHumans; i += 1) {
			buttons[i - 1] = new TextButton(String.valueOf(i), skin);
			table.add(buttons[i - 1]).size(150, 60).padBottom(20).row();
		}
		table.setFillParent(true);
		stage.addActor(table);
		if (maxNumberOfHumans >= 1) {
			buttons[0].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Same way we moved here from the Splash Screen
					// We set it to new Splash because we got no other screens
					// otherwise you put the screen there where you want to go
					int j;
					boolean[] tempCPU = new boolean[1];
					for (j = 0; j <= 0; j += 1) {
						tempCPU[j] = false;
					}
					myGame.setScreen(new NumOpponentScreen(myGame, 5, tempCPU));
				}
			});
		}
		if (maxNumberOfHumans >= 2) {
			buttons[1].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Same way we moved here from the Splash Screen
					// We set it to new Splash because we got no other screens
					// otherwise you put the screen there where you want to go
					int j;
					boolean[] tempCPU = new boolean[2];
					for (j = 0; j <= 1; j += 1) {
						tempCPU[j] = false;
					}
					myGame.setScreen(new NumOpponentScreen(myGame, 4, tempCPU));
				}
			});
		}
		if (maxNumberOfHumans >= 3) {
			buttons[2].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Same way we moved here from the Splash Screen
					// We set it to new Splash because we got no other screens
					// otherwise you put the screen there where you want to go
					int j;
					boolean[] tempCPU = new boolean[3];
					for (j = 0; j <= 2; j += 1) {
						tempCPU[j] = false;
					}
					myGame.setScreen(new NumOpponentScreen(myGame, 3, tempCPU));
				}
			});
		}
		if (maxNumberOfHumans >= 4) {
			buttons[3].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Same way we moved here from the Splash Screen
					// We set it to new Splash because we got no other screens
					// otherwise you put the screen there where you want to go
					int j;
					boolean[] tempCPU = new boolean[4];
					for (j = 0; j <= 3; j += 1) {
						tempCPU[j] = false;
					}
					myGame.setScreen(new NumOpponentScreen(myGame, 2, tempCPU));
				}
			});
		}
		if (maxNumberOfHumans >= 5) {
			buttons[4].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Same way we moved here from the Splash Screen
					// We set it to new Splash because we got no other screens
					// otherwise you put the screen there where you want to go
					int j;
					boolean[] tempCPU = new boolean[5];
					for (j = 0; j <= 4; j += 1) {
						tempCPU[j] = false;
					}
					myGame.setScreen(new NumOpponentScreen(myGame, 1, tempCPU));
				}
			});
		}
		if (maxNumberOfHumans >= 6) {
			buttons[5].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Same way we moved here from the Splash Screen
					// We set it to new Splash because we got no other screens
					// otherwise you put the screen there where you want to go
					int j;
					boolean[] tempCPU = new boolean[6];
					for (j = 0; j <= 5; j += 1) {
						tempCPU[j] = false;
					}
					myGame.setScreen(new NumOpponentScreen(myGame, 0, tempCPU));
				}
			});
		}
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
