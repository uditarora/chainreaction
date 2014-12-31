/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Parnami
 *
 */
public class GameStatsScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int HEIGHT_RULES_SCREEN_BUTTONS = 60;
	final private int WIDTH_RULES_SCREEN_BUTTONS = 150;
	private Stage stage = new Stage();
	private Table table = new Table(), container = new Table();
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"),
			new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private TextButton backButton;
	private Label stat;
	private TextButtonStyle backButtonStyler;
	Texture[] images = new Texture[15];
	private int currentImage;
	private ScrollPane scroll;
	final private int NUMBER_OF_DIFFICULTY_LEVELS;
	private Preferences stats;
	
	// Constructor
	public GameStatsScreen(ChainReactionAIGame game) {
		ChainReactionAIGame.currentScreen = 2;
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
		// Show the world to be 440*480 no matter the
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH_SCREEN, HEIGHT_SCREEN);
		// Initializing and adding the stats to Table.
		String keyWon, keyLost;
		int numLost, numWon;
		for (int i = 1; i <= NUMBER_OF_DIFFICULTY_LEVELS; i += 1) {
			keyWon = "wonLevel"+i;
			keyLost = "lostLevel"+i;
			numLost = stats.getInteger(keyLost, 0);
			numWon = stats.getInteger(keyWon, 0);
			System.out.println("Level "+i+"- Won: "+numWon+", Lost: "+numLost);
			stat = new Label("Level "+i+"- Won: "+numWon+", Lost: "+numLost, skin);
			stat.setFontScale((float)heightUpscaleFactor);
			table.add(stat).padLeft(10).padRight(10).padBottom(10).row();
		}
		
		// Adds the backButton to the Table.
		backButton = new TextButton(new String("Back"), skin);
		backButtonStyler = new TextButtonStyle(backButton.getStyle());
		backButtonStyler.font.setScale((1+(heightUpscaleFactor-1)/2));
		backButton.setStyle(backButtonStyler);
		table.add(backButton).size(WIDTH_RULES_SCREEN_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_RULES_SCREEN_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(20).row();
		table.setFillParent(true);
		// Scroll pane consisting of the Table.
		scroll = new ScrollPane(table);
		// Container is the outside coverung which contains the
		// ScrollPane.
		container.setFillParent(true);
		container.add(scroll).fill().expand().row();
		// Adding container to stage.
		stage.addActor(container);
		// Attaching the ClickListener to the back button.
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new MainMenuScreen(myGame));
			}
		});
		currentImage = 0;
		// loadImages();
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		//batch.begin();
		//batch.draw(images[currentImage/6], (WIDTH_SCREEN - 366)/2, 0);
		//batch.end();
		stage.draw();
		currentImage += 1;
		if (currentImage >= 90) {
			currentImage = 0;
		}
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
