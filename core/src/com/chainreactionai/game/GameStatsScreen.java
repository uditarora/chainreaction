/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Parnami
 *
 */
public class GameStatsScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int HEIGHT_RULES_SCREEN_BUTTONS = 60;
	final private int WIDTH_RULES_SCREEN_BUTTONS = 275;
	private Stage stage = new Stage();
	private Table table = new Table(), container = new Table();
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Label stat, statHeadingGame, statHeadingStatistics;
	Texture[] images = new Texture[15];
	private ScrollPane scroll;
	final private int NUMBER_OF_DIFFICULTY_LEVELS;
	private Preferences stats;
	// Trying 3D Graphics
	private PerspectiveCamera cam;
	private ImageButton backButton = new ImageButton(ChainReactionAIGame.backButtonDraw, ChainReactionAIGame.backPressedButtonDraw);
	private Image img = new Image(ChainReactionAIGame.texture);
	
	// Constructor
	public GameStatsScreen(ChainReactionAIGame game) {
		myGame = game;
		NUMBER_OF_DIFFICULTY_LEVELS = 10;
		stats = Gdx.app.getPreferences("chainReactionStatistics");
		create();
	}
	
	// Initializer method
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
	    // Initializing and adding the stats to Table.
		String keyWon, keyLost;
		int numLost, numWon;
		statHeadingGame = new Label("GAME", ChainReactionAIGame.skin);
		statHeadingStatistics = new Label("STATISTICS", ChainReactionAIGame.skin);
		statHeadingGame.setFontScale((float)heightUpscaleFactor);
		statHeadingStatistics.setFontScale((float)heightUpscaleFactor);
		table.add(statHeadingGame).row();
		table.add(statHeadingStatistics).row();
		for (int i = 1; i <= NUMBER_OF_DIFFICULTY_LEVELS; i += 1) {
			keyWon = "wonLevel"+i;
			keyLost = "lostLevel"+i;
			numLost = stats.getInteger(keyLost, 0);
			numWon = stats.getInteger(keyWon, 0);
			stat = new Label("Level "+i+"- Won: "+numWon+", Lost: "+numLost, ChainReactionAIGame.skin);
			stat.setFontScale((float)heightUpscaleFactor/2);
			table.add(stat).padLeft(10).padRight(10).padBottom(10).row();
		}
		// Adds the backButton to the Table.
		backButton.getImageCell().expand().fill();
		table.add(backButton).size(WIDTH_RULES_SCREEN_BUTTONS*widthUpscaleFactor, HEIGHT_RULES_SCREEN_BUTTONS*widthUpscaleFactor).padBottom(10*heightUpscaleFactor).row();
		// Scroll pane consisting of the Table.
		scroll = new ScrollPane(table);
		// Container is the outside covering which contains the
		// ScrollPane.
		container.setFillParent(true);
		container.add(scroll).fill().expand().row();
		// Adding container to stage.
		img.setFillParent(true);
		stage.addActor(img);
		stage.addActor(container);
		// Attaching the ClickListener to the back button.
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new MainMenuScreen(myGame));
			}
		});
		// loadImages();
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
		stage.act();
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			myGame.setScreen(new MainMenuScreen(myGame));
		}
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
