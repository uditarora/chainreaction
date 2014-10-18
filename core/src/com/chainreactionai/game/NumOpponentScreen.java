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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Parnami
 * 
 */
public class NumOpponentScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 480;
	private Stage stage = new Stage();
    private Table table = new Table();
    private int maxNumberOfOpponents;
    private int numOfHumans;
    private ArrayList<Boolean> isCPU = new ArrayList<Boolean>();
    private ArrayList<Boolean> tempCPU = new ArrayList<Boolean>();
    private Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"), new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));
    private TextButton[] buttons;
    private Label title = new Label("Game Title",skin);

    public NumOpponentScreen(ChainReactionAIGame game, int maxNumOpponents, boolean[] CPU) {
		myGame = game;
		maxNumberOfOpponents = maxNumOpponents;
		numOfHumans = CPU.length;
		for (int i = 0; i < CPU.length; i += 1) {
			isCPU.add(CPU[i]);
		}
		create();
	}
    
    private void create() {
    	NumOpponentScreen that = this;
    	batch = new SpriteBatch();
		// Show the world to be 440*480 no matter the
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH_SCREEN, HEIGHT_SCREEN);
    	//The elements are displayed in the order you add them.
        //The first appear on top, the last at the bottom.
        table.add(title).padBottom(40).row();
        buttons  = new TextButton[maxNumberOfOpponents];
        for (int i = 1; i <= maxNumberOfOpponents; i += 1) {
        	buttons[i-1] = new TextButton(String.valueOf(i), skin);
        	table.add(buttons[i-1]).size(150, 60).padBottom(20).row();
        }
        table.setFillParent(true);
        stage.addActor(table);
        for (int i = 0; i < maxNumberOfOpponents; i += 1) {
        	buttons[i].addListener(new ClickListener(){
	            @Override
	            public void clicked(InputEvent event, float x, float y) {
	                //Same way we moved here from the Splash Screen
	                //We set it to new Splash because we got no other screens
	                //otherwise you put the screen there where you want to go
	            	// TODO: Add new players to isCPU list.
	            	that.changeToGameScreen(i+1);
	            }
	        });
        }
        Gdx.input.setInputProcessor(stage);
    }
    
    public void changeToGameScreen(int numCPUOpponents) {
    	int j;
    	tempCPU.clear();
    	for (j = 0; j < isCPU.size(); j += 1) {
    		tempCPU.add(isCPU.get(j));
    	}
    	for (j = 1; j <= numCPUOpponents; j += 1) {
    		tempCPU.add(true);
    	}
    	myGame.setScreen(new MainGameScreen(tempCPU));
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
