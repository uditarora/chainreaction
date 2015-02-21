/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Kartik Parnami
 * 
 */
public class TutorialTextScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int WIDTH_NEXT_BUTTON = 275;
	final private int HEIGHT_NEXT_BUTTON = 60;
	final private int WIDTH_ANIMATION_BUTTONS = 135;
	final private int HEIGHT_ANIMATION_BUTTONS = 45;
	private float heightUpscaleFactor, widthUpscaleFactor;
	final private int INVERSE_CHANCES_OF_NEW_BALLS = ChainReactionAIGame.INVERSE_CHANCES_OF_NEW_BALLS;
	final private int MAX_Z_DIST_OF_NEW_BALLS = ChainReactionAIGame.MAX_Z_DIST_OF_NEW_BALLS;
	final private int MIN_Z_DIST_OF_NEW_BALLS = ChainReactionAIGame.MIN_Z_DIST_OF_NEW_BALLS;
	final private int MAX_SPEED_OF_BALLS = ChainReactionAIGame.MAX_SPEED_OF_BALLS;
	final private int MIN_SPEED_OF_BALLS = ChainReactionAIGame.MIN_SPEED_OF_BALLS;
	final private int MAX_NUMBER_OF_BALLS_AT_A_MOMENT = ChainReactionAIGame.MAX_NUMBER_OF_BALLS_AT_A_MOMENT;
	private int numBalls;
	private Stage stage = new Stage();
	private Table table = new Table();
	private int MAX_NUMBER_OF_PLAYERS = ChainReactionAIGame.MAX_NUMBER_PLAYERS;
	private ImageButton nextButton, skipButton;
	private Label title, titleFollowup;
	private Color[] colors;
	private boolean animationInit = false;
	private int textChoice, numPlayers, difficultyLevels;
	// Trying 3D Graphics
	private Model[] models;
	private ModelInstance[] instances;
	private ModelBatch modelBatch;
	private PerspectiveCamera cam;
	private Environment environment;
	private ArrayList<Integer> startZPosition, distNow, xVal, yVal, color, speed;
	private Random rand;
	private Image img = new Image(ChainReactionAIGame.texture);
	private ArrayList<Boolean> isCPU = new ArrayList<Boolean>();
	private ArrayList<Integer> difficultyLevelList = new ArrayList<Integer>();
	private ImageButton humanCpuToggleButton;
	
	// For setting showTutorial flag
	private Preferences stats;
	
	public TutorialTextScreen(ChainReactionAIGame game, int numPlayers, int numDifficultyLevels, int textChoice, ArrayList<Boolean> isCPU, ArrayList<Integer> difficultyLevelList) {
		ChainReactionAIGame.currentScreen = 1;
		myGame = game;
		this.textChoice = textChoice;
		this.numPlayers = numPlayers;
		this.difficultyLevels = numDifficultyLevels;
		for (int i = 0; i < numPlayers; i += 1) {
			this.isCPU.add(isCPU.get(i));
		}
		for (int i = 0; i < numPlayers; i += 1) {
			this.difficultyLevelList.add(difficultyLevelList.get(i));
		}
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
	
	public TutorialTextScreen(ChainReactionAIGame game, int textChoice) {
		ChainReactionAIGame.currentScreen = 1;
		myGame = game;
		this.textChoice = textChoice;
		this.numPlayers = 0;
		this.difficultyLevels = 0;
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
	
	public TutorialTextScreen(ChainReactionAIGame game, int numPlayers, int numDifficultyLevels, int textChoice, ArrayList<Integer> xVal, ArrayList<Integer> yVal, ArrayList<Integer> color, ArrayList<Integer> startZPosition, ArrayList<Integer> distNow, ArrayList<Integer> speed, int numBalls) {
		int i;
		ChainReactionAIGame.currentScreen = 1;
		myGame = game;
		this.textChoice = textChoice;
		this.numPlayers = numPlayers;
		this.difficultyLevels = numDifficultyLevels;
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
        humanCpuToggleButton = new ImageButton(ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw);
        humanCpuToggleButton.getImageCell().expand().fill();
		if (textChoice == 1) {
			title = new Label("Here for the first time? "
					+ "Let's get you familiar with the interface of the game. "
					+ "You can invoke this tutorial again anytime, from the main menu.", ChainReactionAIGame.skin);
		} else if (textChoice == 2) {
			title = new Label("Now you will choose the number of opponents. You can select "
					+ "up to 5 opponents using the slider.", ChainReactionAIGame.skin);
		} else if (textChoice == 3) {
			title = new Label("Next, for each player you can toggle whether the player "
					+ "is a CPU bot or a human player using the given red button.", ChainReactionAIGame.skin);
			titleFollowup = new Label("You can also vary the difficulty level of each CPU"
					+ " bot using the sliders alongside.", ChainReactionAIGame.skin);
			titleFollowup.setWrap(true);
			titleFollowup.setFontScale(heightUpscaleFactor/2);
		} else if (textChoice == 4) {
			title = new Label("Now you're ready to rumble. You can enjoy this game against your friends by "
					+ "choosing all human players, go solo against up to 5 "
					+ "CPU bots, or have a mix of both!\n\nHave fun, and may the force be with you!", ChainReactionAIGame.skin);
			// Set showTutorial flag to false
			ChainReactionAIGame.GRAYED_OUT = false;
			stats = Gdx.app.getPreferences("chainReactionStatistics");
			stats.putBoolean("showTutorial", false);
			stats.flush();
		}
		title.setWrap(true);
		title.setFontScale(heightUpscaleFactor/2);
		table.add(title).width(420*widthUpscaleFactor).padBottom(20*heightUpscaleFactor).row();
		if (textChoice == 3) {
			table.add(humanCpuToggleButton).size(WIDTH_ANIMATION_BUTTONS*widthUpscaleFactor, HEIGHT_ANIMATION_BUTTONS*widthUpscaleFactor).padBottom(10).row();
			humanCpuToggleButton.setChecked(true);
			table.add(titleFollowup).width(420*widthUpscaleFactor).padBottom(20*heightUpscaleFactor).row();
		}
		nextButton = new ImageButton(ChainReactionAIGame.nextButtonDraw, ChainReactionAIGame.nextPressedButtonDraw);
		nextButton.getImageCell().expand().fill();
		table.add(nextButton).size(WIDTH_NEXT_BUTTON*widthUpscaleFactor, HEIGHT_NEXT_BUTTON*widthUpscaleFactor).padBottom(20).row();
		if (textChoice == 1) {
			skipButton = new ImageButton(ChainReactionAIGame.skipButtonDraw, ChainReactionAIGame.skipPressedButtonDraw);
			skipButton.getImageCell().expand().fill();
			table.add(skipButton).size(WIDTH_NEXT_BUTTON*widthUpscaleFactor, HEIGHT_NEXT_BUTTON*widthUpscaleFactor).padBottom(2).row();
		}
		table.setFillParent(true);
		// Adding the table to the stage.
		img.setFillParent(true);
		stage.addActor(img);
		stage.addActor(table);
		// Attaching the ClickListener to the next button.
		if (textChoice == 1) {
			nextButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new MainMenuScreen(myGame));
				}
			});
			skipButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// Set showTutorial flag to false
					ChainReactionAIGame.GRAYED_OUT = false;
					stats = Gdx.app.getPreferences("chainReactionStatistics");
					stats.putBoolean("showTutorial", false);
					stats.flush();
					myGame.setScreen(new MainMenuScreen(myGame));
				}
			});
		} else if (textChoice == 2) {
			nextButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new NumPlayersScreen(myGame));
				}
			});
		} else if (textChoice == 3) {
			nextButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new ChooseOpponentsAndLevelsScreen(myGame, numPlayers, difficultyLevels));
				}
			});
		} else if (textChoice == 4) {
			nextButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new MainGameScreenChar(myGame, isCPU, difficultyLevelList));
				}
			});
		}
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
		if (animationInit) {
			modelBatch.begin(cam);
			createAnimation();
			drawAnimation();
			modelBatch.end();
		}
		if (textChoice == 3) {
			if (humanCpuToggleButton.isChecked()) {
				ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedHumanButtonDraw);
				humanCpuToggleButton.setStyle(temp);
			} else {
				ImageButtonStyle temp = new ImageButtonStyle(ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw, ChainReactionAIGame.pressedHumanCpuButtonDraw, ChainReactionAIGame.unpressedCpuButtonDraw);
				humanCpuToggleButton.setStyle(temp);
			}
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
//		skin.dispose();
	}
}
