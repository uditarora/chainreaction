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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Kartik Parnami
 * 
 */
public class NumPlayersScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int WIDTH_DROP_DOWN_MENUS = 150;
	final private int HEIGHT_DROP_DOWN_MENUS = 30;
	final private int WIDTH_SUBMIT_BUTTON = 100;
	final private int HEIGHT_SUBMIT_BUTTON = 40;
	private float heightUpscaleFactor, widthUpscaleFactor;
	final private int INVERSE_CHANCES_OF_NEW_BALLS = 40;
	final private int MAX_Z_DIST_OF_NEW_BALLS = 400;
	final private int MIN_Z_DIST_OF_NEW_BALLS = 200;
	final private int MAX_SPEED_OF_BALLS = 2;
	final private int MIN_SPEED_OF_BALLS = 1;
	final private int MAX_NUMBER_OF_BALLS_AT_A_MOMENT = 3;
	private int numBalls;
	private Stage stage = new Stage();
	private Table table = new Table();
	private int MAX_NUMBER_OF_PLAYERS = ChainReactionAIGame.MAX_NUMBER_PLAYERS, NUMBER_OF_DIFFICULTY_LEVELS = 10;
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"),
			new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private TextButton submitButton;
	private Label title;
	private TextButtonStyle submitButtonStyler;
	private Slider numPlayerSlider;
	private Label numPlayerLabel;
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
	
	public NumPlayersScreen(ChainReactionAIGame game) {
		ChainReactionAIGame.currentScreen = 1;
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
	
	public NumPlayersScreen(ChainReactionAIGame game, ArrayList<Integer> xVal, ArrayList<Integer> yVal, ArrayList<Integer> color, ArrayList<Integer> startZPosition, ArrayList<Integer> distNow, ArrayList<Integer> speed, int numBalls) {
		int i;
		ChainReactionAIGame.currentScreen = 1;
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

	// Initialization function
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
		// Initializing and adding the title to Table.
		title = new Label("Choose number of players", skin);
		title.setFontScale((1+(heightUpscaleFactor-1)/2));
		table.add(title).padBottom(10).row();
		// Initializing the Drop-Down menu
		numPlayerSlider = new Slider(2, MAX_NUMBER_OF_PLAYERS, 1, false, skin);
		// Adding the DropDown to the Table.
		table.add(numPlayerSlider).size(WIDTH_DROP_DOWN_MENUS*widthUpscaleFactor, HEIGHT_DROP_DOWN_MENUS*heightUpscaleFactor).padBottom(10).row();
		// To allow the sliders to be dragged properly
		InputListener stopTouchDown = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
			    return false;
			}
		};
		numPlayerSlider.addListener(stopTouchDown);
		// Label Initialize
		numPlayerLabel = new Label("2", skin);
		table.add(numPlayerLabel).padBottom(20).row();
		// Initializing and adding the Submit Button to Table.
		submitButton = new TextButton(new String("Submit"), skin);
		submitButtonStyler = new TextButtonStyle(submitButton.getStyle());
		submitButtonStyler.font.setScale((1+(heightUpscaleFactor-1)/2));
		submitButton.setStyle(submitButtonStyler);
		table.add(submitButton).size(WIDTH_SUBMIT_BUTTON*(1+(widthUpscaleFactor-1)/2), HEIGHT_SUBMIT_BUTTON*(1+(heightUpscaleFactor-1)/2)).padBottom(2).row();
		table.setFillParent(true);
		// Adding the table to the stage.
		stage.addActor(table);
		// Attaching the ClickListener to the submit button.
		submitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int chosenNumOfPlayers = (int)(numPlayerSlider.getValue());
				myGame.setScreen(new ChooseOpponentsAndLevelsScreen(myGame, chosenNumOfPlayers, NUMBER_OF_DIFFICULTY_LEVELS, xVal, yVal, color, startZPosition, distNow, speed, numBalls));
			}
		});
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
		numPlayerLabel.setText(String.valueOf((int)(numPlayerSlider.getValue())));
		stage.act(delta);
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			myGame.setScreen(new MainMenuScreen(myGame, xVal, yVal, color, startZPosition, distNow, speed, numBalls));
		}
	}
	
	private void createAnimation() {
		int newOrNot, xCoord, yCoord, zCoord, speedOfBall;
		newOrNot = rand.nextInt(INVERSE_CHANCES_OF_NEW_BALLS);
		if ((newOrNot == 0 || (numBalls == 0)) && numBalls < MAX_NUMBER_OF_BALLS_AT_A_MOMENT) {
			if (numBalls == 0) {
				clearBallsList();
			}
			zCoord = rand.nextInt(MAX_Z_DIST_OF_NEW_BALLS);
			if (zCoord < MIN_Z_DIST_OF_NEW_BALLS) {
				zCoord += MIN_Z_DIST_OF_NEW_BALLS;
			}
			startZPosition.add(zCoord);
			distNow.add(0);
			xCoord = rand.nextInt(WIDTH_SCREEN/2);
			if (xCoord >= (WIDTH_SCREEN/4)) {
				xCoord = (xCoord + (WIDTH_SCREEN/2));
			}
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
		//viewport.update(width, height);
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
