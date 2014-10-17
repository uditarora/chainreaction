/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * @author Parnami
 * 
 */
public class MainMenuScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	private Texture splashScreenBackground;
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 480;
	private Stage stage = new Stage();
    private Table table = new Table();

    private Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"), new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));

    private TextButton buttonPlay = new TextButton("Play", skin), buttonExit = new TextButton("Exit", skin);
    private Label title = new Label("Game Title",skin);

    public MainMenuScreen(ChainReactionAIGame game) {
		myGame = game;
		create();
	}
    
    private void create() {
    	batch = new SpriteBatch();
		// Show the world to be 440*480 no matter the
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH_SCREEN, HEIGHT_SCREEN);
    	//The elements are displayed in the order you add them.
        //The first appear on top, the last at the bottom.
        table.add(title).padBottom(40).row();
        table.add(buttonPlay).size(150,60).padBottom(20).row();
        table.add(buttonExit).size(150,60).padBottom(20).row();

        table.setFillParent(true);
        stage.addActor(table);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			myGame.setScreen(new MainGameScreen());
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
