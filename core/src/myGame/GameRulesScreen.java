/**
 * 
 */
package myGame;

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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.chainreactionai.game.ChainReactionAIGame;
import com.chainreactionai.game.MainMenuScreen;

/**
 * @author Parnami
 *
 */
public class GameRulesScreen implements Screen {
	SpriteBatch batch;
	private OrthographicCamera camera;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 480;
	final private int HEIGHT_RULES_SCREEN_BUTTONS = 60;
	final private int WIDTH_RULES_SCREEN_BUTTONS = 150;
	private Stage stage = new Stage();
	private Table table = new Table();
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"),
			new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private TextButton backButton;
	private Label rules;
	private TextButtonStyle backButtonStyler;
	
	// Constructor
	public GameRulesScreen(ChainReactionAIGame game) {
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
		rules = new Label("There's only one rule - Eliminate your opponent's\n"
				+ "balls! Players take turns to place their balls on a\n"
				+ "square. If a square reaches critical mass, the balls\n"
				+ "spread out to the surrounding squares. If a player\n"
				+ "loses all their balls, they are out of the game!\n"
				+ "A player can only place their balls on a blank\n"
				+ "square or one occupied by balls of their own colour.", skin);
		rules.setFontScale((float)((1+(heightUpscaleFactor-1)/2)));
		table.add(rules).padBottom(10).row();
		// Adds the backButton to the Table.
		backButton = new TextButton(new String("Back"), skin);
		backButtonStyler = new TextButtonStyle(backButton.getStyle());
		backButtonStyler.font.setScale((1+(heightUpscaleFactor-1)/2));
		backButton.setStyle(backButtonStyler);
		table.add(backButton).size(WIDTH_RULES_SCREEN_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_RULES_SCREEN_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(20).row();
		table.setFillParent(true);
		// Adding the table to the stage.
		stage.addActor(table);
		// Attaching the ClickListener to the back button.
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new MainMenuScreen(myGame));
			}
		});
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
		
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
