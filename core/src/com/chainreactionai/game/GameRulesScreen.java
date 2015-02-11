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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @author Parnami
 *
 */
public class GameRulesScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int HEIGHT_RULES_SCREEN_BUTTONS = 60;
	final private int WIDTH_RULES_SCREEN_BUTTONS = 275;
	final private int WIDTH_ANIMATION_BUTTONS = 113;
	final private int HEIGHT_ANIMATION_BUTTONS = 114;
	final private int INVERSE_CHANCES_OF_NEW_BALLS = ChainReactionAIGame.INVERSE_CHANCES_OF_NEW_BALLS;
	final private int MAX_Z_DIST_OF_NEW_BALLS = ChainReactionAIGame.MAX_Z_DIST_OF_NEW_BALLS;
	final private int MIN_Z_DIST_OF_NEW_BALLS = ChainReactionAIGame.MIN_Z_DIST_OF_NEW_BALLS;
	final private int MAX_SPEED_OF_BALLS = ChainReactionAIGame.MAX_SPEED_OF_BALLS;
	final private int MIN_SPEED_OF_BALLS = ChainReactionAIGame.MIN_SPEED_OF_BALLS;
	final private int MAX_NUMBER_OF_BALLS_AT_A_MOMENT = ChainReactionAIGame.MAX_NUMBER_OF_BALLS_AT_A_MOMENT;
	final private int MAX_CORNER_SPLIT_WAIT_TIME = 50;
	final private int MAX_MIDDLE_SPLIT_WAIT_TIME = 50;
	final private int MAX_EDGE_SPLIT_WAIT_TIME = 50;
	final private int MAX_WIN_ADJOINING_RECT_SPLIT_WAIT_TIME = 50;
	final private int MAX_RECURSIVELY_BETWEEN_SPLIT_WAIT_TIME = 50;
	final private int MAX_RECURSIVELY_SPLIT_WAIT_TIME = 100;
	private int numBalls, cornerSplitWaitTime, middleSplitWaitTime, edgeSplitWaitTime, winAdjoiningRectAfterSplitWaitTime, recursivelySplitWaitTime;
	final private int MAX_NUMBER_OF_PLAYERS = ChainReactionAIGame.MAX_NUMBER_PLAYERS;
	private int textChoice;
	private Stage stage = new Stage();
	private Table table = new Table(), container = new Table();
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Label rulesOne, rulesTwo, rulesThree, rulesFour, rulesFive, rulesHeading;
	private int currentImage;
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
	private Drawable cornerBeforeSplitButtonDraw, cornerAfterSplitButtonDraw, middleBeforeSplitButtonDraw, middleAfterSplitButtonDraw,
					 edgeBeforeSplitButtonDraw, edgeAfterSplitButtonDraw, recursivelyBeforeSplitButtonDraw, recursivelyAfterSplitButtonDraw,
					 winAdjoiningRectBeforeSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, recursivelyBetweenSplitButtonDraw;
	// Trying ImageButton
	private ImageButton backButtonImg = new ImageButton(ChainReactionAIGame.mainMenuButtonDraw, ChainReactionAIGame.mainMenuPressedButtonDraw),
			nextButtonImg = new ImageButton(ChainReactionAIGame.nextButtonDraw, ChainReactionAIGame.nextPressedButtonDraw);
	private ImageButton cornerSplitButton, middleSplitButton, edgeSplitButton, recursivelySplitButton, winAdjoiningRectSplitButton;
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"),
			new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private Image img = new Image(ChainReactionAIGame.texture);
	
	// Constructor
	public GameRulesScreen(ChainReactionAIGame game, int textChoice) {
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
		this.textChoice = textChoice;
		create();
	}
	
	// Constructor
	public GameRulesScreen(ChainReactionAIGame game, ArrayList<Integer> xVal, ArrayList<Integer> yVal, ArrayList<Integer> color, ArrayList<Integer> startZPosition, ArrayList<Integer> distNow, ArrayList<Integer> speed, int numBalls, int textChoice) {
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
		this.textChoice = textChoice;
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
        cornerBeforeSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/cornerBeforeSplit.png"))));
        cornerAfterSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/cornerAfterSplit.png"))));
        middleBeforeSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/middleBeforeSplit.png"))));
        middleAfterSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/middleAfterSplit.png"))));
        edgeBeforeSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/edgeBeforeSplit.png"))));
        edgeAfterSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/edgeAfterSplit.png"))));
        recursivelyBeforeSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/recursivelySplitBeforeSplit.png"))));
        recursivelyBetweenSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/recursivelySplitBetweenSplit.png"))));
        recursivelyAfterSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/recursivelySplitAfterSplit.png"))));
        winAdjoiningRectBeforeSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/winAdjoiningRectangleBeforeSplit.png"))));
        winAdjoiningRectAfterSplitButtonDraw = (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture("RulesAnimation/winAdjoiningRectangleAfterSplit.png"))));
        cornerSplitButton = new ImageButton(cornerBeforeSplitButtonDraw);
        middleSplitButton = new ImageButton(middleBeforeSplitButtonDraw);
        edgeSplitButton = new ImageButton(edgeBeforeSplitButtonDraw);
        recursivelySplitButton = new ImageButton(recursivelyBeforeSplitButtonDraw);
        winAdjoiningRectSplitButton = new ImageButton(winAdjoiningRectBeforeSplitButtonDraw);
        cornerSplitButton.getImageCell().expand().fill();
        middleSplitButton.getImageCell().expand().fill();
        edgeSplitButton.getImageCell().expand().fill();
        recursivelySplitButton.getImageCell().expand().fill();
        winAdjoiningRectSplitButton.getImageCell().expand().fill();
        
        cornerSplitWaitTime = 0;
        middleSplitWaitTime = 0;
        edgeSplitWaitTime = 0;
        recursivelySplitWaitTime = 0;
        winAdjoiningRectAfterSplitWaitTime = 0;
        // Initializing and adding the rules to Table.
        rulesHeading = new Label("GAME RULES", ChainReactionAIGame.skin);
        rulesHeading.setFontScale((float)(heightUpscaleFactor));
		rulesOne = new Label("There's only one rule for the game - Eliminate your "
				+ "opponent's atoms! Players take turns to place "
				+ "their atoms on a square. If a square reaches "
				+ "critical mass, one atom apiece spreads out to each "
				+ "of the adjacent squares. If a player loses all "
				+ "their atoms, they are out of the game! A player "
				+ "can only place their atoms on a blank square "
				+ "or one occupied by atoms of their own colour.\n\n"
				+ "Let's walk you through an interactive tutorial to learn the rules "
				+ "of the game. Click on the cells pointed at by the arrows "
				+ "to understand the way that the game works.\n", skin);
		rulesTwo = new Label("Critical mass is 2 for the cells in the "
				+ "corners ie. the atoms will split in two available "
				+ "horizontal and vertical directions as shown in the image below.", skin);
		rulesThree = new Label("Critical mass is 3 for the cells along "
				+ "the edges ie. the atoms will split and one atom each "
				+ "will be placed in the 3 available horizontal and vertical "
				+ "directions as shown in the image below.", skin);
		rulesFour = new Label("Similarly, critical mass is 4 for the rest of "
				+ "the cells of the grid as shown in the image below. ", skin);
		rulesFive = new Label("When your atom reaches a given cell on splitting, you "
				+ "occupy that cell and your new atom gets added to the previously "
				+ "existing atoms present in that cell. Click on "
				+ "the two images below to see it in action.", skin);
		rulesOne.setFontScale((float)(heightUpscaleFactor));
		rulesOne.setWrap(true);
		rulesTwo.setFontScale((float)(heightUpscaleFactor));
		rulesTwo.setWrap(true);
		rulesThree.setFontScale((float)(heightUpscaleFactor));
		rulesThree.setWrap(true);
		rulesFour.setFontScale((float)(heightUpscaleFactor));
		rulesFour.setWrap(true);
		rulesFive.setFontScale((float)(heightUpscaleFactor));
		rulesFive.setWrap(true);
		if (textChoice == 1) {
			table.add(rulesHeading).padBottom(10).row();
			table.add(rulesOne).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
		}
		else if (textChoice == 2) {
			table.add(rulesTwo).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
			table.add(cornerSplitButton).size(WIDTH_ANIMATION_BUTTONS*widthUpscaleFactor, HEIGHT_ANIMATION_BUTTONS*widthUpscaleFactor).padBottom(20).row();
		}
		else if (textChoice == 3) {
			table.add(rulesThree).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
			table.add(edgeSplitButton).size(WIDTH_ANIMATION_BUTTONS*widthUpscaleFactor, HEIGHT_ANIMATION_BUTTONS*widthUpscaleFactor).padBottom(20).row();
		}
		else if (textChoice == 4) {
			table.add(rulesFour).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
			table.add(middleSplitButton).size(WIDTH_ANIMATION_BUTTONS*widthUpscaleFactor, HEIGHT_ANIMATION_BUTTONS*widthUpscaleFactor).padBottom(20).row();
		}
		else if (textChoice == 5) {
			table.add(rulesFive).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
			table.add(winAdjoiningRectSplitButton).size(WIDTH_ANIMATION_BUTTONS*widthUpscaleFactor, HEIGHT_ANIMATION_BUTTONS*widthUpscaleFactor).padBottom(10).row();
			table.add(recursivelySplitButton).size(WIDTH_ANIMATION_BUTTONS*widthUpscaleFactor, HEIGHT_ANIMATION_BUTTONS*widthUpscaleFactor).padBottom(20).row();
		}
		nextButtonImg.getImageCell().expand().fill();
		backButtonImg.getImageCell().expand().fill();
		if (textChoice < 5)
			table.add(nextButtonImg).size(WIDTH_RULES_SCREEN_BUTTONS*widthUpscaleFactor, HEIGHT_RULES_SCREEN_BUTTONS*widthUpscaleFactor).padBottom(15).row();
		if (textChoice == 5)
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
		// Attaching the ClickListener to the buttons
		backButtonImg.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new MainMenuScreen(myGame, xVal, yVal, color, startZPosition, distNow, speed, numBalls));
			}
		});
		nextButtonImg.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new GameRulesScreen(myGame, xVal, yVal, color, startZPosition, distNow, speed, numBalls, textChoice+1));
			}
		});
		cornerSplitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ImageButtonStyle temp = new ImageButtonStyle(cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw);
				cornerSplitButton.setStyle(temp);
				cornerSplitWaitTime += 1;
				cornerSplitButton.removeListener(this);
			}
		});
		middleSplitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ImageButtonStyle temp = new ImageButtonStyle(middleAfterSplitButtonDraw, middleAfterSplitButtonDraw, middleAfterSplitButtonDraw, middleAfterSplitButtonDraw, middleAfterSplitButtonDraw, middleAfterSplitButtonDraw);
				middleSplitButton.setStyle(temp);
				middleSplitWaitTime += 1;
				middleSplitButton.removeListener(this);
			}
		});
		edgeSplitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ImageButtonStyle temp = new ImageButtonStyle(edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw);
				edgeSplitButton.setStyle(temp);
				edgeSplitWaitTime += 1;
				edgeSplitButton.removeListener(this);
			}
		});
		recursivelySplitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ImageButtonStyle temp = new ImageButtonStyle(recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw);
				recursivelySplitButton.setStyle(temp);
				recursivelySplitWaitTime += 1;
				recursivelySplitButton.removeListener(this);
			}
		});
		winAdjoiningRectSplitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ImageButtonStyle temp = new ImageButtonStyle(winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw);
				winAdjoiningRectSplitButton.setStyle(temp);
				winAdjoiningRectAfterSplitWaitTime += 1;
				winAdjoiningRectSplitButton.removeListener(this);
			}
		});
		currentImage = 0;
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
		if (cornerSplitWaitTime > 0) {
			cornerSplitWaitTime += 1;
			if (cornerSplitWaitTime > MAX_CORNER_SPLIT_WAIT_TIME) {
				cornerSplitWaitTime = 0;
				ImageButtonStyle temp = new ImageButtonStyle(cornerBeforeSplitButtonDraw, cornerBeforeSplitButtonDraw, cornerBeforeSplitButtonDraw, cornerBeforeSplitButtonDraw, cornerBeforeSplitButtonDraw, cornerBeforeSplitButtonDraw);
				cornerSplitButton.setStyle(temp);
				cornerSplitButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						ImageButtonStyle temp = new ImageButtonStyle(cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw, cornerAfterSplitButtonDraw);
						cornerSplitButton.setStyle(temp);
						cornerSplitWaitTime += 1;
						cornerSplitButton.removeListener(this);
					}
				});
			}
		}
		if (middleSplitWaitTime > 0) {
			middleSplitWaitTime += 1;
			if (middleSplitWaitTime > MAX_MIDDLE_SPLIT_WAIT_TIME) {
				middleSplitWaitTime = 0;
				ImageButtonStyle temp = new ImageButtonStyle(middleBeforeSplitButtonDraw, middleBeforeSplitButtonDraw, middleBeforeSplitButtonDraw, middleBeforeSplitButtonDraw, middleBeforeSplitButtonDraw, middleBeforeSplitButtonDraw);
				middleSplitButton.setStyle(temp);
				middleSplitButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						ImageButtonStyle temp = new ImageButtonStyle(middleAfterSplitButtonDraw, middleAfterSplitButtonDraw, middleAfterSplitButtonDraw, middleAfterSplitButtonDraw, middleAfterSplitButtonDraw, middleAfterSplitButtonDraw);
						middleSplitButton.setStyle(temp);
						middleSplitWaitTime += 1;
						middleSplitButton.removeListener(this);
					}
				});
			}
		}
		if (edgeSplitWaitTime > 0) {
			edgeSplitWaitTime += 1;
			if (edgeSplitWaitTime > MAX_EDGE_SPLIT_WAIT_TIME) {
				edgeSplitWaitTime = 0;
				ImageButtonStyle temp = new ImageButtonStyle(edgeBeforeSplitButtonDraw, edgeBeforeSplitButtonDraw, edgeBeforeSplitButtonDraw, edgeBeforeSplitButtonDraw, edgeBeforeSplitButtonDraw, edgeBeforeSplitButtonDraw);
				edgeSplitButton.setStyle(temp);
				edgeSplitButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						ImageButtonStyle temp = new ImageButtonStyle(edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw, edgeAfterSplitButtonDraw);
						edgeSplitButton.setStyle(temp);
						edgeSplitWaitTime += 1;
						edgeSplitButton.removeListener(this);
					}
				});
			}
		}
		if (recursivelySplitWaitTime > 0) {
			recursivelySplitWaitTime += 1;
			if ((recursivelySplitWaitTime > MAX_RECURSIVELY_BETWEEN_SPLIT_WAIT_TIME) && (recursivelySplitWaitTime < MAX_RECURSIVELY_SPLIT_WAIT_TIME)) {
				ImageButtonStyle temp = new ImageButtonStyle(recursivelyAfterSplitButtonDraw, recursivelyAfterSplitButtonDraw, recursivelyAfterSplitButtonDraw, recursivelyAfterSplitButtonDraw, recursivelyAfterSplitButtonDraw, recursivelyAfterSplitButtonDraw);
				recursivelySplitButton.setStyle(temp);
			} else if (recursivelySplitWaitTime > MAX_RECURSIVELY_SPLIT_WAIT_TIME) {
				recursivelySplitWaitTime = 0;
				ImageButtonStyle temp = new ImageButtonStyle(recursivelyBeforeSplitButtonDraw, recursivelyBeforeSplitButtonDraw, recursivelyBeforeSplitButtonDraw, recursivelyBeforeSplitButtonDraw, recursivelyBeforeSplitButtonDraw, recursivelyBeforeSplitButtonDraw);
				recursivelySplitButton.setStyle(temp);
				recursivelySplitButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						ImageButtonStyle temp = new ImageButtonStyle(recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw, recursivelyBetweenSplitButtonDraw);
						recursivelySplitButton.setStyle(temp);
						recursivelySplitWaitTime += 1;
						recursivelySplitButton.removeListener(this);
					}
				});
			}
		}
		if (winAdjoiningRectAfterSplitWaitTime > 0) {
			winAdjoiningRectAfterSplitWaitTime += 1;
			if (winAdjoiningRectAfterSplitWaitTime > MAX_WIN_ADJOINING_RECT_SPLIT_WAIT_TIME) {
				winAdjoiningRectAfterSplitWaitTime = 0;
				ImageButtonStyle temp = new ImageButtonStyle(winAdjoiningRectBeforeSplitButtonDraw, winAdjoiningRectBeforeSplitButtonDraw, winAdjoiningRectBeforeSplitButtonDraw, winAdjoiningRectBeforeSplitButtonDraw, winAdjoiningRectBeforeSplitButtonDraw, winAdjoiningRectBeforeSplitButtonDraw);
				winAdjoiningRectSplitButton.setStyle(temp);
				winAdjoiningRectSplitButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						ImageButtonStyle temp = new ImageButtonStyle(winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw);
						winAdjoiningRectSplitButton.setStyle(temp);
						winAdjoiningRectAfterSplitWaitTime += 1;
						winAdjoiningRectSplitButton.removeListener(this);
					}
				});
			}
		}
		stage.act(delta);
		stage.draw();
		currentImage += 1;
		if (currentImage >= 90) {
			currentImage = 0;
		}
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
