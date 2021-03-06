/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * @author Kartik Parnami
 * 
 */

public class MainGameScreenChar implements Screen {
	SpriteBatch batch;
	final private int GRID_SIZE_X = 6;
	final private int GRID_SIZE_Y = 8;
	final private int HEIGHT_PAUSE_BUTTON = 27;
	final private int WIDTH_PAUSE_BUTTON = 123;
	final private int PAD_BOTTOM_PAUSE_BUTTON = 10;
	final private int PAD_TOP_PAUSE_BUTTON = 10;
	final private int PAD_LEFT_PAUSE_BUTTON = 10;
	final private int THREE_D_EFFECT_DISTANCE_FOR_GRID = 24;
	final private int WIDTH_SCREEN = 448;
	final private float WIDTH_RECTANGLE = ((float)(WIDTH_SCREEN)/GRID_SIZE_X);
	final private float HEIGHT_RECTANGLE = WIDTH_RECTANGLE;
	final private float WIDTH_INNER_RECTANGLE = WIDTH_RECTANGLE - (((float)(THREE_D_EFFECT_DISTANCE_FOR_GRID))/GRID_SIZE_X);
	final private float HEIGHT_INNER_RECTANGLE = ((float)((HEIGHT_RECTANGLE * GRID_SIZE_Y) - THREE_D_EFFECT_DISTANCE_FOR_GRID))/GRID_SIZE_Y;
	final private int HEIGHT_SCREEN = (int)((GRID_SIZE_Y * HEIGHT_RECTANGLE) + PAD_BOTTOM_PAUSE_BUTTON + HEIGHT_PAUSE_BUTTON + PAD_TOP_PAUSE_BUTTON) + 1;
	final private int HEIGHT_PAUSE_MENU_BUTTONS = 60;
	final private int WIDTH_PAUSE_MENU_BUTTONS = 275;
	final private int WIDTH_SLIDER = 180;
	final private int HEIGHT_KNOB = 20;
	final private int HEIGHT_MUTE = 50;
	final private int WIDTH_MUTE = 50;
	final private int MAX_NUM_PLAYERS = ChainReactionAIGame.MAX_NUMBER_PLAYERS;
	private int INVERSE_SPEED_OF_BALL_VIBRATION = 28;
	private int NUMBER_OF_PLAYERS, breakingAway, splittableBreakingAway;
	private Texture pauseButtonImg = new Texture(Gdx.files.internal("buttons/pause.jpg")),
					undoButtonImg = new Texture(Gdx.files.internal("buttons/undo.jpg"));
	
	private Array<Rectangle> rectangularGrid, innerRectangularGrid;
	private GameBoardChar gameBoard;
	private int clickCoordX, clickCoordY, currentPlayer, numberOfMovesPlayed, gameState, maxPlyLevel;
	private float heightUpscaleFactor, widthUpscaleFactor;
	private double percentageMovesSearched, incrementValForPercentageMovesSearched;
	private boolean clickOnEdge, isBackButtonPressed;
	MyInputProcessor inputProcessor;
	private boolean[] isCPU, lostPlayer;
	private int[] difficultyLevels, heuristicNumbers;
	private boolean gameOver, moveCompleted;
	private ChainReactionAIGame myGame;
	private Stage stage = new Stage();
	private Table table = new Table();
	private ImageButton resumeButton, exitButton, playAgainButton, newGameButton, mainMenuButton, muteActiveButton, muteInactiveButton, muteButton;
	private Slider animationSpeedSlider;
	private Position highlightPos = new Position(-1, -1);
	private GameSolverChar solver;
	private long prevTime, newTime;
//	private FileHandle handle = Gdx.files.external("data/myfile.txt");
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private Color[] colors;
	// Trying 3D Graphics
	private Model[] models;
	private ModelInstance[] instances;
	private ModelBatch modelBatch;
	private PerspectiveCamera cam;
	private Environment environment;
	private Image img;
	//Sounds
	private Sound splitSound;
	private boolean playThisTimeBallPlace;
	// Stats to be stored
	private Preferences stats;
	// Stuff for undo option
	private char[][] prevRectangleWinner;
	private char[][] prevNumAtomsInRectangle;
	private int prevNumMovesPlayed, prevCurrentPlayer;
	private boolean undoUsedFlag = false;
	// All debug printing should go under this flag.
	final private boolean DEBUG = false;
	final private boolean DEBUG_CPU = false;
	
	
	// Constructor to initialize which player is CPU and which is human.
	// Also sets difficulty levels for CPU players.
	public MainGameScreenChar(ChainReactionAIGame game, ArrayList<Boolean> CPU, ArrayList<Integer> difficultyLevelList) {
		myGame = game;
		NUMBER_OF_PLAYERS = CPU.size();
		if (DEBUG_CPU)
			NUMBER_OF_PLAYERS = 2;
		isCPU = new boolean[NUMBER_OF_PLAYERS];
		lostPlayer = new boolean[NUMBER_OF_PLAYERS];
		difficultyLevels = new int[NUMBER_OF_PLAYERS];
		heuristicNumbers = new int[NUMBER_OF_PLAYERS];
		if (DEBUG)
			System.out.println("WIDTH: " + WIDTH_SCREEN + " HEIGHT: " + HEIGHT_SCREEN);
		
		//Simulating with only CPU Players for testing
		if (DEBUG_CPU) {
			for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
				isCPU[i] = true;
				difficultyLevels[i] = 102;
			}
			isCPU[1] = false;
		}
		else {
			if (DEBUG)
				System.out.println(CPU.size());
			
			for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
				isCPU[i] = CPU.get(i);
				if (isCPU[i]) {
					difficultyLevels[i] = difficultyLevelList.get(i);
					if (DEBUG)
						System.out.println("isCPU[" + i + "] = " + isCPU[i] + " with difficulty Level = " + difficultyLevels[i]);
				} else {
					if (DEBUG)
						System.out.println("isCPU[" + i + "] = " + isCPU[i]);
				}
			}
		}
		setGameState(0);
		stats = Gdx.app.getPreferences("chainReactionStatistics");
		
		// Testing statistics
		if (DEBUG) {
			String keyWon, keyLost; int numWon, numLost;
			for (int i = 1; i < 11; ++i)
			{
				keyWon = "wonLevel"+i;
				keyLost = "lostLevel"+i;
				numLost = stats.getInteger(keyLost, 0);
				numWon = stats.getInteger(keyWon, 0);
				System.out.println("Level "+i+"- Won: "+numWon+", Lost: "+numLost);
			}
		}
		
		this.splitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/balls.mp3"));
		// Flag to keep track if undo was used, and prevent achievements and 
		// leaderboards from getting updated if it was.
		undoUsedFlag = false;
		create();
	}

	private void create() {
		batch = new SpriteBatch();
		Color c = batch.getColor();
		batch.setColor(c.r, c.g, c.b, 0.3f);
		// Initializing stuff.
		//playThisTime = false;
		rectangularGrid = new Array<Rectangle>();
		innerRectangularGrid = new Array<Rectangle>();
		gameBoard = new GameBoardChar(GRID_SIZE_X, GRID_SIZE_Y, NUMBER_OF_PLAYERS);
		inputProcessor = new MyInputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		inputProcessor.unsetTouchDown();
		numberOfMovesPlayed = currentPlayer = 0;
		breakingAway = 0;
		isBackButtonPressed = false;
		// Initialize colors
		colors = new Color[MAX_NUM_PLAYERS];
		colors[0] = Color.WHITE;
		colors[1] = Color.BLUE;
		colors[2] = Color.YELLOW;
		colors[3] = Color.RED;
		colors[4] = Color.PURPLE;
		colors[5] = Color.GREEN;
		// Up-scale Factors are used to get proper sized buttons
		// upscaled or downscaled according to the Screen Dimensions
		heightUpscaleFactor = ((float)(ChainReactionAIGame.HEIGHT))/HEIGHT_SCREEN;
		widthUpscaleFactor = ((float)(ChainReactionAIGame.WIDTH))/WIDTH_SCREEN;
		maxPlyLevel = 0;
		prevTime = System.currentTimeMillis();
		for (int i = 0; i < difficultyLevels.length; i += 1) {
			if (getCurrentPlyLevel(difficultyLevels[i]) > maxPlyLevel) {
				maxPlyLevel = getCurrentPlyLevel(difficultyLevels[i]);
			}
		}
		if (difficultyLevels[currentPlayer] == 10)
			percentageMovesSearched = 0.75;
		else if (maxPlyLevel < 3)
			percentageMovesSearched = 0.2 + 1/(double)(maxPlyLevel);
		else if (maxPlyLevel < 5)
			percentageMovesSearched = 0.1 + 1/(double)(maxPlyLevel);
		else
			percentageMovesSearched = 1/(double)(maxPlyLevel);
		incrementValForPercentageMovesSearched = 1/(double)(3*maxPlyLevel*maxPlyLevel);
		resumeButton = new ImageButton(ChainReactionAIGame.resumeButtonDraw, ChainReactionAIGame.resumePressedButtonDraw);
		playAgainButton = new ImageButton(ChainReactionAIGame.playAgainButtonDraw, ChainReactionAIGame.playAgainPressedButtonDraw);
		newGameButton = new ImageButton(ChainReactionAIGame.newGameButtonDraw, ChainReactionAIGame.newGamePressedButtonDraw);
		mainMenuButton = new ImageButton(ChainReactionAIGame.mainMenuButtonDraw, ChainReactionAIGame.mainMenuPressedButtonDraw);
		exitButton = new ImageButton(ChainReactionAIGame.exitButtonDraw, ChainReactionAIGame.exitPressedButtonDraw);
		muteActiveButton = new ImageButton(ChainReactionAIGame.muteActivateButton, ChainReactionAIGame.muteActivateButton, ChainReactionAIGame.muteInactivateButton);
		muteInactiveButton = new ImageButton(ChainReactionAIGame.muteInactivateButton, ChainReactionAIGame.muteInactivateButton, ChainReactionAIGame.muteActivateButton); 
		muteButton = ChainReactionAIGame.muteStatus ? muteInactiveButton :muteActiveButton;
//		handle.writeString("--------------------------------------------------------------------------\r\n", true);
		
		// Populating the Pause menu with the buttons.
		img = new Image(ChainReactionAIGame.texture);
		img.setFillParent(true);
		muteButton.getImageCell().expand().fill();
		resumeButton.getImageCell().expand().fill();
		playAgainButton.getImageCell().expand().fill();
		newGameButton.getImageCell().expand().fill();
		mainMenuButton.getImageCell().expand().fill();
		exitButton.getImageCell().expand().fill();
		
		// Slider to change animation speed
		animationSpeedSlider = new Slider(0, 200, 20, false, ChainReactionAIGame.sliderSkin);
		animationSpeedSlider.getStyle().knob.setMinHeight(HEIGHT_KNOB*heightUpscaleFactor);
		animationSpeedSlider.setValue(stats.getInteger("animationSpeed", 0));
		Label label = new Label("Animation Speed", ChainReactionAIGame.skin);
		label.setFontScale(heightUpscaleFactor/2);
		table.add(label).align(Align.center).width(WIDTH_SLIDER*widthUpscaleFactor);
		table.add(muteButton).size(WIDTH_MUTE*widthUpscaleFactor, HEIGHT_MUTE*widthUpscaleFactor).row();
		table.add(animationSpeedSlider).width(WIDTH_SLIDER*widthUpscaleFactor).padBottom(20*widthUpscaleFactor).align(Align.center).row();
		// To allow the sliders to be dragged properly
		InputListener stopTouchDown = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
			    return false;
			}
		};
		animationSpeedSlider.addListener(stopTouchDown);

		table.add(resumeButton).size(WIDTH_PAUSE_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_PAUSE_MENU_BUTTONS*widthUpscaleFactor).padBottom(2).padLeft(45*widthUpscaleFactor).row();
		table.add(playAgainButton).size(WIDTH_PAUSE_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_PAUSE_MENU_BUTTONS*widthUpscaleFactor).padBottom(2).padLeft(45*widthUpscaleFactor).row();
		table.add(newGameButton).size(WIDTH_PAUSE_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_PAUSE_MENU_BUTTONS*widthUpscaleFactor).padBottom(2).padLeft(45*widthUpscaleFactor).row();
		table.add(mainMenuButton).size(WIDTH_PAUSE_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_PAUSE_MENU_BUTTONS*widthUpscaleFactor).padBottom(2).padLeft(45*widthUpscaleFactor).row();
		table.add(exitButton).size(WIDTH_PAUSE_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_PAUSE_MENU_BUTTONS*widthUpscaleFactor).padBottom(2).padLeft(45*widthUpscaleFactor).row();

		table.setFillParent(true);
		
		//Attaching click handlers to the pause menu buttons.
		resumeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				resume();
			}
		});
		playAgainButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Same way we moved here from the Splash Screen
				// We set it to new Splash because we got no other screens
				// otherwise you put the screen there where you want to go
				ArrayList<Integer> difficultyLevelList = new ArrayList<Integer>();
				ArrayList<Boolean> isCPUList = new ArrayList<Boolean>();
				for (int i = 0; i < isCPU.length; i += 1) {
					isCPUList.add(isCPU[i]);
					difficultyLevelList.add(difficultyLevels[i]);
				}
				myGame.setScreen(new MainGameScreenChar(myGame, isCPUList, difficultyLevelList));
			}
		});
		newGameButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (numberOfMovesPlayed < 1)
					ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_like_seriously);
				myGame.setScreen(new NumPlayersScreen(myGame));
			}
		});
		mainMenuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (numberOfMovesPlayed < 1)
					ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_like_seriously);
				myGame.setScreen(new MainMenuScreen(myGame));
			}
		});
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (numberOfMovesPlayed < 1)
					ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_like_seriously);
				Gdx.app.exit();
			}
		});
		muteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				muteButton = ChainReactionAIGame.muteStatus ? muteInactiveButton : muteActiveButton;
				ChainReactionAIGame.muteStatus = !ChainReactionAIGame.muteStatus;
			}
		});
		
		
		
		// Load default values into arrays
		setDimsForRectangles();
		setNoPlayerHasLost();
		setHeuristicNumbers();
		gameOver = false;
		moveCompleted = true;
		
		// Trying 3D graphics
		cam = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		float camZ = ((float)1440*720/1240)*((float)Gdx.graphics.getHeight()/Gdx.graphics.getWidth());
		if (DEBUG)
			Gdx.app.log("size", "CamZ: "+camZ);
		cam.position.set(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, camZ);
		
	    cam.lookAt(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, 0);
	    cam.near = 1f;
	    cam.far = 4000f;
	    cam.update();
		
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
        
        // Undo option
        prevRectangleWinner = new char[GRID_SIZE_X][GRID_SIZE_Y];
		prevNumAtomsInRectangle = new char[GRID_SIZE_X][GRID_SIZE_Y];
		copyCurrentBoardToPrevBoard();
	}

	// This function loads the dimensions for all the
	// rectangles into the 2-D Array
	private void setDimsForRectangles() {
		for (int i = 0; i < GRID_SIZE_X; i += 1) {
			for (int j = 0; j < GRID_SIZE_Y; j += 1) {
				Rectangle tempBlock = new Rectangle();
				tempBlock.x = (float) (i * WIDTH_RECTANGLE);
				tempBlock.y = (float) (j * HEIGHT_RECTANGLE);
				tempBlock.height = (float) (HEIGHT_RECTANGLE);
				tempBlock.width = (float) (WIDTH_RECTANGLE);
				if (DEBUG)
					System.out.println("Outer: " + tempBlock.x + " " + tempBlock.y);
				rectangularGrid.add(tempBlock);
				tempBlock = new Rectangle();
				tempBlock.x = (float) (i * WIDTH_INNER_RECTANGLE) + ((float)(THREE_D_EFFECT_DISTANCE_FOR_GRID))/2;
				tempBlock.y = (float) (j * HEIGHT_INNER_RECTANGLE) + ((float)(THREE_D_EFFECT_DISTANCE_FOR_GRID))/2;
				tempBlock.height = (float) (HEIGHT_INNER_RECTANGLE);
				tempBlock.width = (float) (WIDTH_INNER_RECTANGLE);
				if (DEBUG)
					System.out.println("Inner : " + tempBlock.x + " " + tempBlock.y);
				innerRectangularGrid.add(tempBlock);
			}
		}
	}
	
	// Populates the lostPlayer array to show that no player
	// has lost the game initially.
	private void setNoPlayerHasLost() {
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			lostPlayer[i] = false;
		}
	}
	
	private void setHeuristicNumbers() {
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			heuristicNumbers[i] = 12;
		}
		if (DEBUG_CPU) {
			// Set heuristic numbers for DEBUG_CPU
		}
	}

	// This function sets the gameState to given argument val.
	private void setGameState(int state) {
		gameState = state;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(((float)(15)/256), ((float)(15)/256), ((float)(15)/256), 1);
		batch.setProjectionMatrix(cam.combined);
		// If game is not paused
		if (gameState == 0) {
			batch.begin();
		    batch.draw(ChainReactionAIGame.mainGameScreenTexture, 0, 0, WIDTH_SCREEN, HEIGHT_SCREEN);
		    batch.end();
			// Check if any player has lost the game and doesn't permit
			// it to play any further.
			if (numberOfMovesPlayed > NUMBER_OF_PLAYERS) {
				for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
					if (!lostPlayer[i]) {
						if (gameBoard.hasLost(i)) {
							lostPlayer[i] = true;
						}
					}
				}
			}
			// If the move is done after the animation
			if (moveCompleted) {
				// If the player has not lost the game yet
				if (lostPlayer[currentPlayer] == false) { 
					// Check if current player is CPU and play its move
					if (isCPU[currentPlayer] && !gameOver) {
						try {
							Thread.sleep((long) (261 - (animationSpeedSlider.getValue())));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (solver != null) {
							if (solver.getIsThreadComplete()) {
								// Get the position where we should place the new ball
								// so that the GameSolver's best move is executed.
								Position winningMove = solver.getAnswerPosition();
								if(winningMove == null) {
									if (DEBUG)
										System.out.println("Error Time.");
								}
								// Initialize the animation for changing the Board.
								gameBoard.changeBoard2(winningMove.coordX, winningMove.coordY, currentPlayer);
								playThisTimeBallPlace = false;
								// Store these coordinates so they can be shown as highlighted.
								highlightPos.coordX = winningMove.coordX;
								highlightPos.coordY = winningMove.coordY;
								// Set the moveCompleted flag to be false, so as to start the animation.
								moveCompleted = false;
								numberOfMovesPlayed += 1;
								percentageMovesSearched += incrementValForPercentageMovesSearched;
								solver = null;
								INVERSE_SPEED_OF_BALL_VIBRATION *= 7;
							}
						} else {
							if (DEBUG)
								System.out.println("Reached CPU");
							// Initializing the GameSolver
							newTime = System.currentTimeMillis();
							if (DEBUG)
								Gdx.app.log("mainPlayerAndPercentageMoves","MainPlayer: " + currentPlayer + " with percentageMovesSearched: " + percentageMovesSearched + " numMovesPlayed: " + numberOfMovesPlayed + " and Time taken : " + ((newTime - prevTime)/1000));
//							handle.writeString("MainPlayer: " + currentPlayer + " with percentageMovesSearched: " + percentageMovesSearched + " numMovesPlayed: " + numberOfMovesPlayed + " and Time taken : " + ((newTime - prevTime)/1000) +"\r\n", true);
							solver = new GameSolverChar(gameBoard, currentPlayer,
									NUMBER_OF_PLAYERS, lostPlayer, getCurrentPlyLevel(difficultyLevels[currentPlayer]), percentageMovesSearched, heuristicNumbers[currentPlayer]);
							prevTime = System.currentTimeMillis();
							Thread t = new Thread(solver);
					        t.start();
					        INVERSE_SPEED_OF_BALL_VIBRATION = INVERSE_SPEED_OF_BALL_VIBRATION/7;
							if (DEBUG)
								System.out.println("GameSolver initialized");
						}
					}
				} else {
					// Giving the chance to the next player to play.
					currentPlayer = (currentPlayer + 1) % NUMBER_OF_PLAYERS;
				}
			} else {
				try {
					Thread.sleep((long) (200 - (animationSpeedSlider.getValue()*0.6)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Play Sound
				// Animates board to show new board with next level of BFS
				// calls done. Returns whether the BFS is complete or not.
				if (playThisTimeBallPlace) {
					if(!ChainReactionAIGame.muteStatus){
						this.splitSound.stop();
						this.splitSound.play();
					}
					playThisTimeBallPlace = !playThisTimeBallPlace;
				} else {
					playThisTimeBallPlace = !playThisTimeBallPlace;
				}
				moveCompleted = gameBoard.nextBoard(currentPlayer);
				// Empties the highlight position so every recursive split
				// is not highlighted.
				highlightPos.coordX = -1;
				highlightPos.coordY = -1;
				// Check after the move if it was the winning move or not.
				if (moveCompleted) {
					if (gameBoard.isWinningPosition(currentPlayer)
							&& numberOfMovesPlayed > 1) {
						gameOver = true;
						if (DEBUG)
							System.out.println("Player " + currentPlayer
								+ " has won the game!");
						
						int humans = 0, humanPlayer = 0;
						for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
							if (!isCPU[i]) {
								humans++;
								humanPlayer = i;
							}
						}
						if (humans == 1) {
							if (currentPlayer == humanPlayer)
								updateAchievements(humanPlayer, true);
							else
								updateAchievements(humanPlayer, false);
						}
						
						// Update statistics
						if (NUMBER_OF_PLAYERS == 2 && (isCPU[0] == true || isCPU[1] == true) && !(isCPU[0] && isCPU[1])) {
							updateStatistics();
						}
						
						myGame.setScreen(new GameEndScreen(myGame, currentPlayer, numberOfMovesPlayed, isCPU, difficultyLevels));
					}
					currentPlayer = (currentPlayer + 1) % NUMBER_OF_PLAYERS;
					if (DEBUG)
						System.out.println("Move time.");
				}
			}
			// process user input
			if (inputProcessor.isTouchedDown() && !gameOver) {
				inputProcessor.unsetTouchDown();
				// Log the clicks for Debugging
				if(DEBUG) {
					Gdx.app.log("Click found at", inputProcessor.getXCoord() + " " + inputProcessor.getYCoord());
					Gdx.app.log("Click found at", normalizeClickCoord(inputProcessor.getXCoord(), ChainReactionAIGame.WIDTH, WIDTH_SCREEN) + " " + normalizeClickCoord(inputProcessor.getYCoord(), ChainReactionAIGame.HEIGHT, HEIGHT_SCREEN));
				}
				if (gameState == 0) {
					// Used to process the clicks
					if (moveCompleted) {
						if (lostPlayer[currentPlayer] == false) {
							processUserInputForMove();
						}
					}
					// Used to process pause button click
					processPauseAction();
					// Used to process undo button click
					if (!isCPU[currentPlayer])
						processUndoAction();
				}
			}
			// Rendering here to have board updated
			// after every player's move.
			// Tell the camera to update its matrices.
			cam.update();
	
			// Tell the SpriteBatch to render in the
			// coordinate system specified by the camera.
			batch = (SpriteBatch)stage.getBatch();
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			batch.draw(pauseButtonImg, PAD_LEFT_PAUSE_BUTTON, ((GRID_SIZE_Y * HEIGHT_RECTANGLE) + PAD_BOTTOM_PAUSE_BUTTON), WIDTH_PAUSE_BUTTON, HEIGHT_PAUSE_BUTTON);
			batch.draw(undoButtonImg, WIDTH_SCREEN - PAD_LEFT_PAUSE_BUTTON - WIDTH_PAUSE_BUTTON, ((GRID_SIZE_Y * HEIGHT_RECTANGLE) + PAD_BOTTOM_PAUSE_BUTTON), WIDTH_PAUSE_BUTTON, HEIGHT_PAUSE_BUTTON);
			batch.end();
			shapeRenderer.setProjectionMatrix(cam.combined);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setAutoShapeType(true);
			shapeRenderer.setColor(1, 1, 0, 1);
			modelBatch.begin(cam);
			drawGameBoard();
	        modelBatch.end();
	        shapeRenderer.end();		
		} else {
			// If the game is paused, add the pause menu to the stage.
			img.setFillParent(true);
			stage.addActor(img);
			stage.addActor(table);
			stage.act();
			stage.draw();
			Gdx.input.setInputProcessor(stage);
		}
		if (Gdx.input.isKeyJustPressed(Keys.BACK) && !isBackButtonPressed) {
			isBackButtonPressed = true;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (isBackButtonPressed) {
			if (DEBUG)
				System.out.println("BackButton");
			if (gameState == 1) {
				resume();
			} else {
				pause();
			}
			isBackButtonPressed = false;
		}
	}

	// This function processes the User input for a new move and changes
	// the board accordingly.
	private void processUserInputForMove() {
		// Checking whether the click is on an edge or a box.
		// If on edge, then reject the click.
		float coordX = inputProcessor.getXCoord(), coordY = inputProcessor.getYCoord(), distOfPauseButtonFromTop, distOfGridFromBottom, distOfGridFromTop, heightOfGrid;
		distOfPauseButtonFromTop = (ChainReactionAIGame.HEIGHT - (((float)(ChainReactionAIGame.WIDTH * 8))/6 + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor) + (PAD_BOTTOM_PAUSE_BUTTON * heightUpscaleFactor)))/2;
		distOfGridFromBottom = distOfPauseButtonFromTop;
		distOfGridFromTop = distOfPauseButtonFromTop + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor) + (PAD_BOTTOM_PAUSE_BUTTON * heightUpscaleFactor);
		if (DEBUG)
			System.out.println("distOfPauseButtonFromTop: " + distOfPauseButtonFromTop + " distOfGridFromBottom: " + distOfGridFromBottom);
		if (coordY < distOfGridFromTop || coordY > ChainReactionAIGame.HEIGHT - distOfGridFromBottom) {
			return;
		}
		heightOfGrid = ChainReactionAIGame.HEIGHT - distOfGridFromBottom - distOfGridFromTop;
		clickOnEdge = false;
		clickCoordX = (int)((coordX/widthUpscaleFactor)/WIDTH_RECTANGLE);
		// Try to find clickOnEdge in X - coordinate
		clickCoordY = (int)(((coordY - distOfGridFromTop)/widthUpscaleFactor)/HEIGHT_RECTANGLE);
		clickCoordY = GRID_SIZE_Y - clickCoordY - 1;
		if (DEBUG) {
			System.out.println("distOfPauseButtonFromTop: " + distOfPauseButtonFromTop + " distOfGridFromBottom: " + distOfGridFromBottom);
			System.out.println("distOfGridFromTop: " + distOfGridFromTop + " heightOfGrid: " + heightOfGrid);
			System.out.println("clickCoordX: " + clickCoordX + " clickCoordY: " + clickCoordY);
		}
		// If the click is within bounds of any one rectangle
		if (!clickOnEdge) {
			// Checking the move's validity and changing the board
			// accordingly.
			// Also passing the move to the next player.
			
			if (!isCPU[currentPlayer] && gameBoard.isValidMove(clickCoordX, clickCoordY,
					currentPlayer)) {
				if(numberOfMovesPlayed >= 1)
					copyCurrentBoardToPrevBoard();
				gameBoard.changeBoard2(clickCoordX, clickCoordY,
						currentPlayer);
				highlightPos.coordX = clickCoordX;
				highlightPos.coordY = clickCoordY;
				if (DEBUG)
					System.out.println("Hightlight set " + clickCoordX + " " + clickCoordY);
				moveCompleted = false;
				numberOfMovesPlayed += 1;
				percentageMovesSearched += incrementValForPercentageMovesSearched;
				playThisTimeBallPlace = false;
			}
		}
	}
	
	// This function processes the user input if the user 
	// tries to pause the game.
	private void processPauseAction() {
		// Checks if the click is on the pause button, else returns
		float coordX = inputProcessor.getXCoord(), coordY = inputProcessor.getYCoord(), distOfPauseButtonFromTop, distOfGridFromTop;
		distOfPauseButtonFromTop = (ChainReactionAIGame.HEIGHT - (((float)(ChainReactionAIGame.WIDTH * 8))/6 + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor) + (PAD_BOTTOM_PAUSE_BUTTON * heightUpscaleFactor)))/2;
		distOfGridFromTop = distOfPauseButtonFromTop + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor) + (PAD_BOTTOM_PAUSE_BUTTON * heightUpscaleFactor);
		if (coordY > distOfGridFromTop || coordY < distOfPauseButtonFromTop) {
			return;
		}
		if (coordX > ((WIDTH_PAUSE_BUTTON + PAD_LEFT_PAUSE_BUTTON) * widthUpscaleFactor) || coordX < PAD_LEFT_PAUSE_BUTTON) {
			return;
		}
		if (DEBUG)
			Gdx.app.log("Pause Button Debug", "coordX: " + coordX + " coordY: " + coordY + " coordX is smaller than: " + WIDTH_PAUSE_BUTTON * widthUpscaleFactor);
		pause();
	}
	
	private void processUndoAction() {
		if(isCPU[currentPlayer])
			return;
		float coordX = inputProcessor.getXCoord(), coordY = inputProcessor.getYCoord(), distOfUndoButtonFromTop, distOfGridFromTop;
		if (DEBUG)
			Gdx.app.log("Undo Button Debug", "coordX: " + coordX + " coordY: " + coordY + " coordX is smaller than: " + WIDTH_PAUSE_BUTTON * widthUpscaleFactor + " Also, numberOfMoves " + numberOfMovesPlayed + ", prevNumMoves: " + prevNumMovesPlayed);
		distOfUndoButtonFromTop = (ChainReactionAIGame.HEIGHT - (((float)(ChainReactionAIGame.WIDTH * 8))/6 + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor) + (PAD_BOTTOM_PAUSE_BUTTON * heightUpscaleFactor)))/2;
		distOfGridFromTop = distOfUndoButtonFromTop + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor) + (PAD_BOTTOM_PAUSE_BUTTON * heightUpscaleFactor);
		if (coordY > distOfGridFromTop || coordY < distOfUndoButtonFromTop) {
			return;
		}
		if (coordX > ((WIDTH_SCREEN - PAD_LEFT_PAUSE_BUTTON) * widthUpscaleFactor) || coordX < ((WIDTH_SCREEN - WIDTH_PAUSE_BUTTON - PAD_LEFT_PAUSE_BUTTON)*widthUpscaleFactor)) {
			return;
		}
		if (DEBUG)
			Gdx.app.log("Undo Button Debug", "coordX: " + coordX + " coordY: " + coordY + " coordX is smaller than: " + WIDTH_PAUSE_BUTTON * widthUpscaleFactor);
		copyPrevBoardToCurrentBoard();
		undoUsedFlag = true;
	}
	
	// Copies current board to previous board just before a human makes a move.
	private void copyCurrentBoardToPrevBoard() {
		int i, j;
		prevNumMovesPlayed = numberOfMovesPlayed;
		prevCurrentPlayer = currentPlayer;
		for (i = 0; i < GRID_SIZE_X; i += 1) {
			for (j = 0; j < GRID_SIZE_Y; j += 1) {
				prevNumAtomsInRectangle[i][j] = gameBoard.numAtomsInRectangle[i][j];
				prevRectangleWinner[i][j] = gameBoard.rectangleWinner[i][j];
			}
		}
	}
	
	// Copies previous board to current board if undo button is pressed.
	private void copyPrevBoardToCurrentBoard() {
		int i, j;
		numberOfMovesPlayed = prevNumMovesPlayed;
		currentPlayer = prevCurrentPlayer;
		for (i = 0; i < GRID_SIZE_X; i += 1) {
			for (j = 0; j < GRID_SIZE_Y; j += 1) {
				gameBoard.numAtomsInRectangle[i][j] = prevNumAtomsInRectangle[i][j];
				gameBoard.rectangleWinner[i][j] = prevRectangleWinner[i][j];
			}
		}
	}
	
	// Function to draw the game board using the three
	// arrays.
	private void drawGameBoard() {
		Iterator<Rectangle> iter = rectangularGrid.iterator();
		Iterator<Rectangle> innerIter = innerRectangularGrid.iterator();
		int i, j, count = 0;
		// Goes through all the rectangles pre-fed into the 
		// rectangularGrid and draws them to the board.
		while (iter.hasNext()) {
			Rectangle tempBlock = iter.next();
			Rectangle tempBlock2 = innerIter.next();
			i = count / GRID_SIZE_Y;
			j = count % GRID_SIZE_Y;
			shapeRenderer.set(ShapeType.Line);
			shapeRenderer.setColor(colors[currentPlayer]);
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y);
			shapeRenderer.line(tempBlock.x, tempBlock.y + 1, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + 1);
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x + 1, tempBlock.y, tempBlock.x + 1, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE + 1, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE + 1);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE + 1, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE + 1, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock2.x, tempBlock2.y, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y);
			shapeRenderer.line(tempBlock2.x, tempBlock2.y + 1, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y + 1);
			shapeRenderer.line(tempBlock2.x, tempBlock2.y, tempBlock2.x, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
			shapeRenderer.line(tempBlock2.x + 1, tempBlock2.y, tempBlock2.x + 1, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
			shapeRenderer.line(tempBlock2.x, tempBlock2.y + HEIGHT_INNER_RECTANGLE, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
			shapeRenderer.line(tempBlock2.x, tempBlock2.y + HEIGHT_INNER_RECTANGLE + 1, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y + HEIGHT_INNER_RECTANGLE + 1);
			shapeRenderer.line(tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
			shapeRenderer.line(tempBlock2.x + WIDTH_INNER_RECTANGLE + 1, tempBlock2.y, tempBlock2.x + WIDTH_INNER_RECTANGLE + 1, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock2.x, tempBlock2.y);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y);
			shapeRenderer.line(tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE, tempBlock2.x, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
			// Checks if a given rectangle has to be highlighted indicating a move.
			if (highlightPos.coordX == i && highlightPos.coordY == j) {
				drawHighlight(tempBlock2.x, tempBlock2.y);
				if (DEBUG)
					System.out.println("Hightlight Time");
				shapeRenderer.setColor(Color.RED);
			}
			if (gameBoard.getRectangleWinner(i, j) == -1) {
				// Empty
			} else {
				if (gameBoard.getNumAtomsInRectangle(i, j) > 4) {
					// Empty
				} else {
					drawBalls(tempBlock.x, tempBlock.y, gameBoard.getNumAtomsInRectangle(i, j), gameBoard.getRectangleWinner(i, j), i ,j);
				}
			}
			count++;
		}
		breakingAway += 1;
		splittableBreakingAway += 2;
		if (breakingAway >= 2 * INVERSE_SPEED_OF_BALL_VIBRATION)
			breakingAway = 0;
		if (splittableBreakingAway >= 2 * INVERSE_SPEED_OF_BALL_VIBRATION)
			splittableBreakingAway = 0;
	}
	
	private void drawHighlight (float x, float y) {
		shapeRenderer.setColor(Color.YELLOW);
		for (int counter = 1; counter <= (WIDTH_INNER_RECTANGLE - 1); counter += 1) {
			shapeRenderer.line(x + counter, y, x + counter, y + HEIGHT_INNER_RECTANGLE);
		}
		for (int counter = 1; counter <= (HEIGHT_INNER_RECTANGLE - 1); counter += 1) {
			shapeRenderer.line(x, y + counter, x + WIDTH_INNER_RECTANGLE, y + counter);
		}
	}
	
	private void drawBalls (float x, float y, int numAtomsToDraw, int rectangleWinnerToDraw, int coordX, int coordY) {
		if (gameBoard.isSplittableNode(coordX, coordY, rectangleWinnerToDraw)) {
			if (numAtomsToDraw == 1) {
				instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
			} else if (numAtomsToDraw == 2) {
				if (splittableBreakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/3, y + HEIGHT_RECTANGLE/2, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (float)(0.32 * WIDTH_RECTANGLE), y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				if (splittableBreakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (2 * WIDTH_RECTANGLE)/3, y + HEIGHT_RECTANGLE/2, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (float)(0.68 * WIDTH_RECTANGLE), y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
			} else if (numAtomsToDraw == 3) {
				if (splittableBreakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/3, y + (2 * HEIGHT_RECTANGLE)/5, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (float)(0.32 * WIDTH_RECTANGLE), y + (float)(0.398 * HEIGHT_RECTANGLE), 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				if (splittableBreakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (2 * WIDTH_RECTANGLE)/3, y + (2 * HEIGHT_RECTANGLE)/5, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (float)(0.68 * WIDTH_RECTANGLE), y + (float)(0.398 * HEIGHT_RECTANGLE), 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				if (splittableBreakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + (3 * HEIGHT_RECTANGLE)/5, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + (float)(0.605 * HEIGHT_RECTANGLE), 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
			} else if (numAtomsToDraw == 4) {
				instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/3, y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				instances[rectangleWinnerToDraw].transform.setTranslation(x + (2 * WIDTH_RECTANGLE)/3, y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + (2 * HEIGHT_RECTANGLE)/3, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + (1 * HEIGHT_RECTANGLE)/3, 0);
				modelBatch.render(instances[rectangleWinnerToDraw]);
			}
		} else {
			if (numAtomsToDraw == 1) {
				instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
			} else if (numAtomsToDraw == 2) {
				if (breakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/3, y + HEIGHT_RECTANGLE/2, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (float)(0.325 * WIDTH_RECTANGLE), y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				if (breakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (2 * WIDTH_RECTANGLE)/3, y + HEIGHT_RECTANGLE/2, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (float)(0.675 * WIDTH_RECTANGLE), y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
			} else if (numAtomsToDraw == 3) {
				if (breakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/3, y + (2 * HEIGHT_RECTANGLE)/5, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (float)(0.32 * WIDTH_RECTANGLE), y + (float)(0.393 * HEIGHT_RECTANGLE), 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				if (breakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (2 * WIDTH_RECTANGLE)/3, y + (2 * HEIGHT_RECTANGLE)/5, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + (float)(0.675 * WIDTH_RECTANGLE), y + (float)(0.393 * HEIGHT_RECTANGLE), 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				if (breakingAway < INVERSE_SPEED_OF_BALL_VIBRATION)
					instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + (3 * HEIGHT_RECTANGLE)/5, 0);
				else
					instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + (float)(0.61 * HEIGHT_RECTANGLE), 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
			} else if (numAtomsToDraw == 4) {
				instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/3, y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				instances[rectangleWinnerToDraw].transform.setTranslation(x + (2 * WIDTH_RECTANGLE)/3, y + HEIGHT_RECTANGLE/2, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + (2 * HEIGHT_RECTANGLE)/3, 0);
				modelBatch.render(instances[rectangleWinnerToDraw], environment);
				instances[rectangleWinnerToDraw].transform.setTranslation(x + WIDTH_RECTANGLE/2, y + (1 * HEIGHT_RECTANGLE)/3, 0);
				modelBatch.render(instances[rectangleWinnerToDraw]);
			}
		}
	}
	
	// Normalizes the coordinates of a click to the correct coordinates
	// according to the screen size and the game dimensions.
	private float normalizeClickCoord(float coordVal, float screenVal, float normalizeTo) {
		return ((coordVal*normalizeTo)/screenVal);
	}

	
	private int getCurrentPlyLevel(int difficultyLevel) {
		if (difficultyLevel == 0 || difficultyLevel == 1)
			return difficultyLevel;
		double branchingFactor = gameBoard.getBranchingFactor(currentPlayer);
		
		if (difficultyLevel > 100)		// for DEBUG_CPU purposes
			return (difficultyLevel - 100);
		
		switch(difficultyLevel) {
		case 2:		// combination of 1ply and 3ply
			if (branchingFactor < 0.5)
				return 3;
			else
				return 1;
		case 3:		// combination of 1ply and 2ply
			if (branchingFactor < 0.5)
				return 2;
			else
				return 1;
		case 4:		// purely 3ply
			return 3;
		case 5:		// combination of 2 and 3 ply
			if (branchingFactor < 0.2)
				return 3;
			else
				return 2;
		case 6:		// purely 2ply
			return 2;
		case 7:		// combination of 2 and 4 ply
			if (branchingFactor < 0.4)
				return 4;
			else
				return 2;
		case 8:		// combination of 2 and 4 ply, with more chances for 4ply
			if (branchingFactor < 0.8)
				return 4;
			else
				return 2;
		case 9:		// purely 4ply
			return 4;
		case 10:
			return 4;		// purely 4ply with full search
			
		default:
			return difficultyLevel;				
		}
	}
	
	private void updateStatistics() {
		String key;
		int flag = 0, level;
		if (isCPU[0]) {
			if (currentPlayer == 0)
				key = "lostLevel";
			else
			{
				key = "wonLevel";
				flag = 1;
			}
			key += difficultyLevels[0];
			level = difficultyLevels[0];
		}
		else {
			if (currentPlayer == 1)
				key = "lostLevel";
			else
			{
				key = "wonLevel";
				flag = 1;
			}
			key += difficultyLevels[1];
			level = difficultyLevels[1];
		}
		stats.putInteger(key, stats.getInteger(key, 0)+1);
		
		if (flag==1 && undoUsedFlag == false)
		{
			int overallScore = stats.getInteger("OverallScore", 0) + (level*level) + ((100 - numberOfMovesPlayed)*level*level/20);
			stats.putInteger("OverallScore", overallScore);
			ChainReactionAIGame.googleServices.submitScore(overallScore);
		}
		stats.flush();
	}
	
	private void updateAchievements(int humanPlayer, boolean won) {
		if (!ChainReactionAIGame.googleServices.isSignedIn())
			return;
		else if (undoUsedFlag == true)
			return;
		else {
			// Achievements updated regardless of win or loss
			ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_welcome_to_the_jungle);
			ChainReactionAIGame.googleServices.getIncAchievement(ChainReactionAIGame.achievement_king_of_the_world, 1);
			ChainReactionAIGame.googleServices.getIncAchievement(ChainReactionAIGame.achievement_experienced_professional, 1);
			
			// Achievements updated when the user wins the game 
			if (won) {
				ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_beginners_luck);
				ChainReactionAIGame.googleServices.getIncAchievement(ChainReactionAIGame.achievement_secret_of_the_universe, 1);
				ChainReactionAIGame.googleServices.getIncAchievement(ChainReactionAIGame.achievement_decimus, 1);
				ChainReactionAIGame.googleServices.getIncAchievement(ChainReactionAIGame.achievement_silver_jubilee, 1);
				if (numberOfMovesPlayed < 41)
					ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_quick_mover);
				
				if (NUMBER_OF_PLAYERS == 2) {
					switch (difficultyLevels[(humanPlayer+1)%2]) {
					case 2:
						ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_no_longer_an_amateur);
						break;
					case 3:
						ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_third_times_a_charm);
						break;
					case 4:
						ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_conqueror_quattro);
						break;
					case 5:
						ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_high_five);
						break;
					case 6:
						ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_thats_a_six);
						break;
					case 7:
						ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_lucky_number_seven);
						break;
					case 8:
						ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_i_eight_a_lot);
						break;
					case 9:
						ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_almost_there);
						break;
					case 10:
						ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_you_are_the_champion);
						break;
					}
				}
			}
			else {
				if (numberOfMovesPlayed < 11)
					ChainReactionAIGame.googleServices.getAchievement(ChainReactionAIGame.achievement_sore_loser);
			}
		}
	}
	
	@Override
	public void dispose() {
		// dispose of all the native resources
	}

	// On window resize changes the dimensions for 
	// continued pleasure (if you know what I mean :P)
	@Override
	public void resize(int width, int height) {
		ChainReactionAIGame.WIDTH = width;
		ChainReactionAIGame.HEIGHT = height;
	}

	// Sets game state to 1 thereby Pausing the game.
	@Override
	public void pause() {
		setGameState(1);
	}

	// Sets game state to 0 thereby resuming the game.
	@Override
	public void resume() {
		stats.putInteger("animationSpeed", (int)animationSpeedSlider.getValue());
		stats.flush();
		setGameState(0);
		Gdx.input.setInputProcessor(inputProcessor);
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}
}