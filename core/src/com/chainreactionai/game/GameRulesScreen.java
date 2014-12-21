/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
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
public class GameRulesScreen implements Screen {
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
	private Label rules;
	private TextButtonStyle backButtonStyler;
	Texture[] images = new Texture[15];
	private int currentImage;
	private ScrollPane scroll;
	
	// Constructor
	public GameRulesScreen(ChainReactionAIGame game) {
		ChainReactionAIGame.currentScreen = 2;
		myGame = game;
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
		// Initializing and adding the rules to Table.
		rules = new Label("There's only one rule - Eliminate your\n"
				+ "opponent's atoms! Players take turns to place\n"
				+ "their atoms on a square. If a square reaches\n"
				+ "critical mass, one atom spreads out to each\n"
				+ "of the adjacent squares. If a player loses all\n"
				+ "their atoms, they are out of the game! A player\n"
				+ "can only place their atoms on a blank square\n"
				+ "or one occupied by atoms of their own colour.\n"
				+ "Critical mass is 2 for the rectangles in the\n"
				+ "corners, 3 for the rectangles along the edges\n"
				+ "and 4 for the rest of the rectangles of the grid.\n"
				+ "If your atom reaches a given rectangle, you\n"
				+ "become the winning player for that rectangle\n"
				+ "and your new ball gets added to the previously\n"
				+ "existing balls present in that rectangle.", skin);
		rules.setFontScale((float)((1+(heightUpscaleFactor-1)/2)));
		table.add(rules).padLeft(10).padRight(10).padBottom(10).row();
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
	
	private void loadImages () {
		images[0] = new Texture("0.png");
		images[1] = new Texture("1.png");
		images[2] = new Texture("2.png");
		images[3] = new Texture("3.png");
		images[4] = new Texture("4.png");
		images[5] = new Texture("5.png");
		images[6] = new Texture("6.png");
		images[7] = new Texture("7.png");
		images[8] = new Texture("8.png");
		images[9] = new Texture("9.png");
		images[10] = new Texture("10.png");
		images[11] = new Texture("11.png");
		images[12] = new Texture("12.png");
		images[13] = new Texture("13.png");
		images[14] = new Texture("14.png");
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
