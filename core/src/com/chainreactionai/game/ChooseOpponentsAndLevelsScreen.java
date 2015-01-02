/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * @author Kartik Parnami
 *
 */

public class ChooseOpponentsAndLevelsScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int WIDTH_HUMAN_CPU_MENUS = 100;
	final private int HEIGHT_HUMAN_CPU_MENUS = 30;
	final private int WIDTH_SUBMIT_BUTTON = 150;
	final private int HEIGHT_SUBMIT_BUTTON = 40;
	final private int WIDTH_SLIDER = 150;
	final private int HEIGHT_SLIDER = 10;
	private float heightUpscaleFactor, widthUpscaleFactor;
	final private int MAX_NUM_PLAYERS = ChainReactionAIGame.MAX_NUMBER_PLAYERS;
	final private int INVERSE_CHANCES_OF_NEW_BALLS = ChainReactionAIGame.INVERSE_CHANCES_OF_NEW_BALLS;
	final private int MAX_Z_DIST_OF_NEW_BALLS = ChainReactionAIGame.MAX_Z_DIST_OF_NEW_BALLS;
	final private int MIN_Z_DIST_OF_NEW_BALLS = ChainReactionAIGame.MIN_Z_DIST_OF_NEW_BALLS;
	final private int MAX_SPEED_OF_BALLS = ChainReactionAIGame.MAX_SPEED_OF_BALLS;
	final private int MIN_SPEED_OF_BALLS = ChainReactionAIGame.MIN_SPEED_OF_BALLS;
	final private int MAX_NUMBER_OF_BALLS_AT_A_MOMENT = ChainReactionAIGame.MAX_NUMBER_OF_BALLS_AT_A_MOMENT;
	private int numBalls;
	private Stage stage = new Stage();
	private Table table = new Table(), container = new Table();
	private ScrollPane scroll;
	private int NUMBER_OF_PLAYERS, NUMBER_OF_DIFFICULTY_LEVELS;
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"),
			new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private ImageButton submitButton;
	private Label title;
	private Array<ImageButton> userSelectButtons;
	private Array<Slider> plySliders;
	private Array<Label> plyLabels;
	private Array<Boolean> userSelectIsHuman;
	ArrayList<Boolean> isCPU = new ArrayList<Boolean>();
	ArrayList<Integer> plyLevelList = new ArrayList<Integer>();
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
	
	// Constructor which initializes the number of players passed from
	// NumPlayersScreen and the number if difficulty levels allowed.
	public ChooseOpponentsAndLevelsScreen(ChainReactionAIGame game, int numPlayers, int numDifficultyLevels) {
		myGame = game;
		NUMBER_OF_PLAYERS = numPlayers;
		NUMBER_OF_DIFFICULTY_LEVELS = numDifficultyLevels;
		// Initialize ArrayLists
		xVal = new ArrayList<Integer>();
		yVal = new ArrayList<Integer>();
		color = new ArrayList<Integer>();
		startZPosition = new ArrayList<Integer>();
		distNow = new ArrayList<Integer>();
		speed = new ArrayList<Integer>();
		numBalls = 0;
		create();
		animationInit = true;
	}
	
	public ChooseOpponentsAndLevelsScreen(ChainReactionAIGame game, int numPlayers, int numDifficultyLevels, ArrayList<Integer> xVal, ArrayList<Integer> yVal, ArrayList<Integer> color, ArrayList<Integer> startZPosition, ArrayList<Integer> distNow, ArrayList<Integer> speed, int numBalls) {
		int i;
		myGame = game;
		NUMBER_OF_PLAYERS = numPlayers;
		NUMBER_OF_DIFFICULTY_LEVELS = numDifficultyLevels;
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

	// Initializer function.
	private void create() {
		batch = new SpriteBatch();
		// The elements are displayed in the order you add them.
		// The first appear on top, the last at the bottom.
		// Up-scale Factors are used to get proper sized buttons
		// upscaled or downscaled according to the Screen Dimensions
		heightUpscaleFactor = ((float)(ChainReactionAIGame.HEIGHT))/HEIGHT_SCREEN;
		widthUpscaleFactor = ((float)(ChainReactionAIGame.WIDTH))/WIDTH_SCREEN;
		// Initialize colors
		colors = new Color[MAX_NUM_PLAYERS];
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
		models = new Model[MAX_NUM_PLAYERS];
		instances = new ModelInstance[MAX_NUM_PLAYERS];
		for (int i = 0; i < MAX_NUM_PLAYERS; i += 1) {
			models[i] = modelBuilder.createSphere(25f, 25f, 25f, 30, 30, new Material(ColorAttribute.createDiffuse(colors[i])), Usage.Position | Usage.Normal | Usage.TextureCoordinates);
			instances[i] = new ModelInstance(models[i]);
		}
		modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        rand = new Random();
        // Label for title.
		title = new Label("Choose specifications for the players", skin);
		title.setFontScale((1+(heightUpscaleFactor-1)/2));
		// Creating the DropDown for whether the player should be 
		// human or CPU.
		ImageButton tempButton = new ImageButton(ChainReactionAIGame.humanButtonDraw);
		userSelectButtons = new Array<ImageButton>();
		userSelectIsHuman = new Array<Boolean>();
		tempButton.setChecked(true);
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			if (i != 0) {
				tempButton = new ImageButton(ChainReactionAIGame.cpuButtonDraw);
				tempButton.setChecked(false);
			}
			userSelectButtons.add(tempButton);
			userSelectIsHuman.add(tempButton.isChecked());
		}
		// Creating the DropDown for what should be the 
		// difficulty level of a given CPU player.
		plySliders = new Array<Slider>();
		plyLabels = new Array<Label>();
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			plySliders.add(new Slider(1, NUMBER_OF_DIFFICULTY_LEVELS, 1, false, skin));
			plyLabels.add(new Label(String.valueOf((int)plySliders.get(i).getValue()), skin));
		}
		// To allow the sliders to be dragged properly
		InputListener stopTouchDown = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
			    return false;
			}
		};
		// Adding the dropdowns to the Table.
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			Label tempLabel = new Label("Player " + String.valueOf(i+1) + ":", skin);
			tempLabel.setFontScale(heightUpscaleFactor);
			if (i == 0) {
				table.add(tempLabel).row().padTop(10);
			} else {
				table.add(tempLabel).row();
			}
			// To allow the sliders to be dragged properly
			plySliders.get(i).addListener(stopTouchDown);
			table.add(plySliders.get(i)).size(WIDTH_SLIDER*widthUpscaleFactor, HEIGHT_SLIDER*heightUpscaleFactor).padBottom(5).padTop(5);
			// Add the labels containing the currently selected plyLevel
			tempLabel = plyLabels.get(i);
			tempLabel.setFontScale(heightUpscaleFactor);
			table.add(tempLabel).size((30*widthUpscaleFactor), (15*heightUpscaleFactor)).padBottom(8).padTop(2).padLeft(5).row();
			table.add(userSelectButtons.get(i)).size(WIDTH_HUMAN_CPU_MENUS*(1+(widthUpscaleFactor-1)/2), HEIGHT_HUMAN_CPU_MENUS*(1+(heightUpscaleFactor-1)/2)).padBottom(10).padTop(5).row();
		}
		// Creating and adding the submit button to the Table.
		submitButton = new ImageButton(ChainReactionAIGame.submitButtonDraw);
		table.add(submitButton).size(WIDTH_SUBMIT_BUTTON*(1+(widthUpscaleFactor-1)/2), HEIGHT_SUBMIT_BUTTON*(1+(heightUpscaleFactor-1)/2)).row();
		// Scroll pane consisting of the Table.
		scroll = new ScrollPane(table);
		// Container is the outside covering which contains the
		// ScrollPane.
		container.setFillParent(true);
		container.add(scroll).fill().expand().row();
		// Adding container to stage.
		stage.addActor(container);
		// Adding ClickListener to the submit button
		submitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Same way we moved here from the Splash Screen
				// We set it to new Splash because we got no other screens
				// otherwise you put the screen there where you want to go
				int j;
				for (j = 0; j < NUMBER_OF_PLAYERS; j += 1) {
					if (userSelectButtons.get(j).isChecked()) {
						isCPU.add(false);
					} else {
						isCPU.add(true);
					}
					plyLevelList.add((int) (plySliders.get(j).getValue()));
				}
				myGame.setScreen(new MainGameScreenChar(myGame, isCPU, plyLevelList));
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
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			if (userSelectButtons.get(i).isChecked()) {
				ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.humanButtonDraw, ChainReactionAIGame.humanButtonDraw, ChainReactionAIGame.humanButtonDraw, ChainReactionAIGame.humanButtonDraw, ChainReactionAIGame.humanButtonDraw, ChainReactionAIGame.humanButtonDraw);
				userSelectButtons.get(i).setStyle(temp);
			} else {
				ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.cpuButtonDraw, ChainReactionAIGame.cpuButtonDraw, ChainReactionAIGame.cpuButtonDraw, ChainReactionAIGame.cpuButtonDraw, ChainReactionAIGame.cpuButtonDraw, ChainReactionAIGame.cpuButtonDraw);
				userSelectButtons.get(i).setStyle(temp);
			}
		}
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			plyLabels.get(i).setText(String.valueOf((int)plySliders.get(i).getValue()));
		}
		
		stage.act();
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			myGame.setScreen(new NumPlayersScreen(myGame, xVal, yVal, color, startZPosition, distNow, speed, numBalls));
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
			xCoord = rand.nextInt(WIDTH_SCREEN/2);
			if (xCoord >= (WIDTH_SCREEN/4)) {
				xCoord = (xCoord + (WIDTH_SCREEN/2));
			}
			xVal.add(xCoord);
			yCoord = rand.nextInt(HEIGHT_SCREEN);
			yVal.add(yCoord);
			color.add(rand.nextInt(MAX_NUM_PLAYERS));
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
