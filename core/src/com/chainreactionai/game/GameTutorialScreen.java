/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author karti_000
 *
 */
public class GameTutorialScreen implements Screen {

	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int HEIGHT_RULES_SCREEN_BUTTONS = 60;
	final private int WIDTH_RULES_SCREEN_BUTTONS = 275;
	final private int WIDTH_ANIMATION_BUTTONS = 135;
	final private int HEIGHT_ANIMATION_BUTTONS = 45;
	final private int INVERSE_CHANCES_OF_NEW_BALLS = ChainReactionAIGame.INVERSE_CHANCES_OF_NEW_BALLS;
	final private int MAX_Z_DIST_OF_NEW_BALLS = ChainReactionAIGame.MAX_Z_DIST_OF_NEW_BALLS;
	final private int MIN_Z_DIST_OF_NEW_BALLS = ChainReactionAIGame.MIN_Z_DIST_OF_NEW_BALLS;
	final private int MAX_SPEED_OF_BALLS = ChainReactionAIGame.MAX_SPEED_OF_BALLS;
	final private int MIN_SPEED_OF_BALLS = ChainReactionAIGame.MIN_SPEED_OF_BALLS;
	final private int MAX_NUMBER_OF_BALLS_AT_A_MOMENT = ChainReactionAIGame.MAX_NUMBER_OF_BALLS_AT_A_MOMENT;
	private int numBalls;
	final private int MAX_NUMBER_OF_PLAYERS = ChainReactionAIGame.MAX_NUMBER_PLAYERS;
	private Stage stage = new Stage();
	private Table table = new Table(), container = new Table();
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Label tuteOne, tuteTwo, tuteThree, tuteFour, tuteHeadingOne, tuteHeadingTwo;
	private ScrollPane scroll;
	private Color[] colors;
	private boolean animationInit = false;
	// Trying 3D Graphics
	private Model[] models;
	private ModelInstance[] instances;
	private ModelBatch modelBatch;
	private PerspectiveCamera cam;
	private Environment environment;
	private ArrayList<Integer> startZPosition, distNow, xVal, yVal, color, speed;
	private Random rand;
	// Trying ImageButton
	private ImageButton backButtonImg = new ImageButton(ChainReactionAIGame.mainMenuButtonDraw, ChainReactionAIGame.mainMenuPressedButtonDraw),
						rulesButtonImg = new ImageButton(ChainReactionAIGame.rulesButtonDraw, ChainReactionAIGame.rulesPressedButtonDraw);
	private ImageButton humanCpuToggleButton;
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"),
			new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private Image img = new Image(ChainReactionAIGame.texture);
	
	// Constructor
	public GameTutorialScreen(ChainReactionAIGame game) {
		ChainReactionAIGame.currentScreen = 2;
		myGame = game;
		// Initialize ArrayLists
		xVal = new ArrayList<Integer>();
		yVal = new ArrayList<Integer>();
		color = new ArrayList<Integer>();
		startZPosition = new ArrayList<Integer>();
		distNow = new ArrayList<Integer>();
		speed = new ArrayList<Integer>();
		numBalls = 0;
		animationInit = true;
		create();
	}
	
	// Constructor
	public GameTutorialScreen(ChainReactionAIGame game, ArrayList<Integer> xVal, ArrayList<Integer> yVal, ArrayList<Integer> color, ArrayList<Integer> startZPosition, ArrayList<Integer> distNow, ArrayList<Integer> speed, int numBalls) {
		int i;
		ChainReactionAIGame.currentScreen = 2;
		myGame = game;
		// Initialize ArrayLists
		this.xVal = new ArrayList<Integer>();
		this.yVal = new ArrayList<Integer>();
		this.color = new ArrayList<Integer>();
		this.startZPosition = new ArrayList<Integer>();
		this.distNow = new ArrayList<Integer>();
		this.speed = new ArrayList<Integer>();
		this.numBalls = numBalls;
		// Copy ArrayLists
		for (i = 0; i < xVal.size(); i += 1) {
			this.xVal.add(xVal.get(i));
			this.yVal.add(yVal.get(i));
			this.color.add(color.get(i));
			this.startZPosition.add(startZPosition.get(i));
			this.distNow.add(distNow.get(i));
			this.speed.add(speed.get(i));
		}
		create();
		animationInit = true;
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
		// Initialize colors
		colors = new Color[MAX_NUMBER_OF_PLAYERS];
		colors[0] = Color.WHITE;
		colors[1] = Color.BLUE;
		colors[2] = Color.MAROON;
		colors[3] = Color.ORANGE;
		colors[4] = Color.PURPLE;
		colors[5] = Color.GREEN;
		// Trying 3D graphics
		cam = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float camZ = ((float)1440*720/1240)*((float)Gdx.graphics.getHeight()/Gdx.graphics.getWidth());
		cam.position.set(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, camZ);
	    cam.lookAt(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, 0);
	    cam.near = 1f;
	    cam.far = 4000f;
	    cam.update();
	    // Building models for spheres of different colors
	    ModelBuilder modelBuilder = new ModelBuilder();
		models = new Model[MAX_NUMBER_OF_PLAYERS];
		instances = new ModelInstance[MAX_NUMBER_OF_PLAYERS];
		for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i += 1) {
			models[i] = modelBuilder.createSphere(25f, 25f, 25f, 30, 30, new Material(ColorAttribute.createDiffuse(colors[i])), Usage.Position | Usage.Normal | Usage.TextureCoordinates);
			instances[i] = new ModelInstance(models[i]);
		}
		modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        rand = new Random();
        humanCpuToggleButton = new ImageButton(ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw);
        // Initializing and adding the rules to Table.
        tuteHeadingOne = new Label("GAME", ChainReactionAIGame.skin);
        tuteHeadingOne.setFontScale((float)((1+(heightUpscaleFactor-1)/2)));
		tuteHeadingTwo = new Label("TUTORIAL", ChainReactionAIGame.skin);
		tuteHeadingTwo.setFontScale((float)((1+(heightUpscaleFactor-1)/2)));
		tuteOne = new Label("This is an interactive tutorial to learn about the interface "
				+ "of the game. Click on the boxes that the arrows point "
				+ "to understand the way that the game works.\n"
				+ "On clicking Play, you see a screen having a slider "
				+ "to decide the number of opponents you want. These can be either humans "
				+ "or CPU. On clicking submit we reach the next screen where "
				+ "you can choose the specifications for different players. "
				+ "You can select whether the player is a human or CPU by clicking "
				+ "on the red toggle button with the green light signifying whether the "
				+ "player is a human or a CPU.", skin);
		tuteTwo = new Label("There is also a slider attached to each player "
				+ "to decide the difficulty level of the player if the player "
				+ "is a CPU. If a player is a human then this slider does not "
				+ "hold any significance.", skin);
		tuteThree = new Label("On cicking submit we reach the main gameplay screen, "
				+ "where the players can play and enjoy. They can pause the game at any time "
				+ "during the gameplay by clicking the pause button and reach the pase menu.", skin);
		tuteFour = new Label("If you are playing the game for the first time, check out the rules once.", skin);
		tuteOne.setFontScale((float)((1+(heightUpscaleFactor-1)/2)));
		tuteOne.setWrap(true);
		tuteTwo.setFontScale((float)((1+(heightUpscaleFactor-1)/2)));
		tuteTwo.setWrap(true);
		tuteThree.setFontScale((float)((1+(heightUpscaleFactor-1)/2)));
		tuteThree.setWrap(true);
		tuteFour.setFontScale((float)((1+(heightUpscaleFactor-1)/2)));
		tuteFour.setWrap(true);
		table.add(tuteHeadingOne).padBottom(10).row();
		table.add(tuteHeadingTwo).padBottom(10).row();
		table.add(tuteOne).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
		table.add(humanCpuToggleButton).size(WIDTH_ANIMATION_BUTTONS*widthUpscaleFactor, HEIGHT_ANIMATION_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		humanCpuToggleButton.setChecked(true);
		table.add(tuteTwo).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
		table.add(tuteThree).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
		table.add(tuteFour).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
		table.add(rulesButtonImg).size(WIDTH_RULES_SCREEN_BUTTONS*widthUpscaleFactor, HEIGHT_RULES_SCREEN_BUTTONS*widthUpscaleFactor).padBottom(2).row();
		table.add(backButtonImg).size(WIDTH_RULES_SCREEN_BUTTONS*widthUpscaleFactor, HEIGHT_RULES_SCREEN_BUTTONS*widthUpscaleFactor).padBottom(2).row();
		// Scroll pane consisting of the Table.
		scroll = new ScrollPane(table);
		// Container is the outside coverung which contains the
		// ScrollPane.
		container.setFillParent(true);
		container.add(scroll).fill().expand();
		// Adding container to stage.
		img.setFillParent(true);
		stage.addActor(img);
		stage.addActor(container);
		// Attaching the ClickListener to the back button.
		rulesButtonImg.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new GameRulesScreen(myGame, xVal, yVal, color, startZPosition, distNow, speed, numBalls));
			}
		});
		backButtonImg.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new MainMenuScreen(myGame, xVal, yVal, color, startZPosition, distNow, speed, numBalls));
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
		if (animationInit) {
			modelBatch.begin(cam);
			createAnimation();
			drawAnimation();
			modelBatch.end();
		}
		if (humanCpuToggleButton.isChecked()) {
			ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw);
			humanCpuToggleButton.setStyle(temp);
		} else {
			ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw);
			humanCpuToggleButton.setStyle(temp);
		}
		stage.act(delta);
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			myGame.setScreen(new MainMenuScreen(myGame, xVal, yVal, color, startZPosition, distNow, speed, numBalls));
		}
	}
	
	private void createAnimation() {
		int newOrNot, xCoord, yCoord, zCoord, speedOfBall;
		newOrNot = rand.nextInt(INVERSE_CHANCES_OF_NEW_BALLS);
		if (numBalls == 0) {
			clearBallsList();
		}
		if (xVal.size() > ChainReactionAIGame.MAX_ARRAY_SIZE)
			return;
		if ((newOrNot == 0 || (numBalls == 0)) && numBalls < MAX_NUMBER_OF_BALLS_AT_A_MOMENT) {
			zCoord = rand.nextInt(MAX_Z_DIST_OF_NEW_BALLS);
			if (zCoord < MIN_Z_DIST_OF_NEW_BALLS) {
				zCoord += MIN_Z_DIST_OF_NEW_BALLS;
			}
			startZPosition.add(zCoord);
			distNow.add(0);
			xCoord = rand.nextInt(WIDTH_SCREEN);
			xVal.add(xCoord);
			yCoord = rand.nextInt(HEIGHT_SCREEN);
			yVal.add(yCoord);
			color.add(rand.nextInt(MAX_NUMBER_OF_PLAYERS));
			speedOfBall = rand.nextInt(MAX_SPEED_OF_BALLS) + 1;
			if (speedOfBall < MIN_SPEED_OF_BALLS) {
				speedOfBall += MIN_SPEED_OF_BALLS;
			}
			speed.add(speedOfBall);
			numBalls += 1;
		}
	}
	
	private void drawAnimation() {
		int xCoord, yCoord, zCoord;
		for (int i = 0; i < startZPosition.size(); i += 1) {
			xCoord = xVal.get(i);
			if (xCoord != -1) {
				yCoord = yVal.get(i);
				zCoord = -startZPosition.get(i) + distNow.get(i);
				instances[color.get(i)].transform.setTranslation(xCoord, yCoord, zCoord);
				modelBatch.render(instances[color.get(i)], environment);
				distNow.set(i, distNow.get(i) + speed.get(i));
				if (distNow.get(i) - startZPosition.get(i) > 0) {
					deleteBallFromList(i);
				}
			}
		}
	}
	
	private void deleteBallFromList(int index) {
		xVal.set(index, -1);
		numBalls -= 1;
	}
	
	private void clearBallsList () {
		xVal.clear();
		yVal.clear();
		color.clear();
		startZPosition.clear();
		distNow.clear();
		speed.clear();
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
