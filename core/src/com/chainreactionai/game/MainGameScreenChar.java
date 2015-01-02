/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
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
	final private int WIDTH_PAUSE_BUTTON = 55;
	final private int PAD_BOTTOM_PAUSE_BUTTON = 10;
	final private int PAD_TOP_PAUSE_BUTTON = 10;
	final private int THREE_D_EFFECT_DISTANCE_FOR_GRID = 24;
	final private int WIDTH_SCREEN = 448;
	final private float WIDTH_RECTANGLE = ((float)(WIDTH_SCREEN)/GRID_SIZE_X);
	final private float HEIGHT_RECTANGLE = WIDTH_RECTANGLE;
	final private float WIDTH_INNER_RECTANGLE = WIDTH_RECTANGLE - (((float)(THREE_D_EFFECT_DISTANCE_FOR_GRID))/GRID_SIZE_X);
	final private float HEIGHT_INNER_RECTANGLE = ((float)((HEIGHT_RECTANGLE * GRID_SIZE_Y) - THREE_D_EFFECT_DISTANCE_FOR_GRID))/GRID_SIZE_Y;
	final private int HEIGHT_SCREEN = (int)((GRID_SIZE_Y * HEIGHT_RECTANGLE) + PAD_BOTTOM_PAUSE_BUTTON + HEIGHT_PAUSE_BUTTON + PAD_TOP_PAUSE_BUTTON) + 1;
	final private int HEIGHT_PAUSE_MENU_BUTTONS = 60;
	final private int WIDTH_PAUSE_MENU_BUTTONS = 150;
	final private int MAX_NUM_PLAYERS = ChainReactionAIGame.MAX_NUMBER_PLAYERS;
	final private int INVERSE_CHANCES_OF_NEW_BALLS = 40;
	final private int MAX_Z_DIST_OF_NEW_BALLS = 400;
	final private int MIN_Z_DIST_OF_NEW_BALLS = 200;
	final private int MAX_SPEED_OF_BALLS = 2;
	final private int MIN_SPEED_OF_BALLS = 1;
	final private int MAX_NUMBER_OF_BALLS_AT_A_MOMENT = 3;
	private int numBalls;
	private int INVERSE_SPEED_OF_BALL_VIBRATION = 28;
	private int NUMBER_OF_PLAYERS, breakingAway, splittableBreakingAway;
	private Texture pauseButtonImg = new Texture("pauseButton.jpg");
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
	private Skin skin = new Skin(Gdx.files.internal("data/Holo-dark-mdpi.json"), new TextureAtlas(Gdx.files.internal("data/Holo-dark-mdpi.atlas")));
	private TextButton resumeButton, exitButton, newGameButton, mainMenuButton;
	private Position highlightPos = new Position(-1, -1);
	private GameSolverChar solver;
	private long prevTime, newTime;
	private FileHandle handle = Gdx.files.external("data/myfile.txt");
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private Color[] colors;
	private TextButtonStyle resumeButtonStyler;
	// Trying 3D Graphics
	private Model[] models;
	private ModelInstance[] instances;
	private ModelBatch modelBatch;
	private PerspectiveCamera cam;
	private Environment environment;
	private ArrayList<Integer> startZPosition, distNow, xVal, yVal, color, speed;
	private boolean animationInit = false;
	private Random rand;
	// Stats to be stored
	private Preferences stats;
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

	private void create() {
		batch = new SpriteBatch();
		// Initializing stuff.
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
		colors[2] = Color.MAROON;
		colors[3] = Color.ORANGE;
		colors[4] = Color.PURPLE;
		colors[5] = Color.GREEN;
		rand = new Random();
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
		resumeButton = new TextButton(new String("Resume"), skin);
		newGameButton = new TextButton(new String("New Game"), skin);
		mainMenuButton = new TextButton(new String("Main Menu"), skin);
		exitButton = new TextButton("Exit", skin);
		handle.writeString("--------------------------------------------------------------------------\r\n", true);
		
		// Populating the Pause menu with the buttons.
		resumeButtonStyler = new TextButtonStyle(resumeButton.getStyle());
		resumeButtonStyler.font.setScale((1+(heightUpscaleFactor-1)/2));
		resumeButton.setStyle(resumeButtonStyler);
		table.add(resumeButton).size(WIDTH_PAUSE_MENU_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_PAUSE_MENU_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(2).row();
		newGameButton.setStyle(resumeButtonStyler);
		table.add(newGameButton).size(WIDTH_PAUSE_MENU_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_PAUSE_MENU_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(2).row();
		mainMenuButton.setStyle(resumeButtonStyler);
		table.add(mainMenuButton).size(WIDTH_PAUSE_MENU_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_PAUSE_MENU_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(2).row();
		exitButton.setStyle(resumeButtonStyler);
		table.add(exitButton).size(WIDTH_PAUSE_MENU_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_PAUSE_MENU_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(2).row();
		table.setFillParent(true);
		
		//Attaching click handlers to the pause menu buttons.
		resumeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				resume();
			}
		});
		newGameButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new NumPlayersScreen(myGame, xVal, yVal, color, startZPosition, distNow, speed, numBalls));
			}
		});
		mainMenuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new MainMenuScreen(myGame, xVal, yVal, color, startZPosition, distNow, speed, numBalls));
			}
		});
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
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
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
		
		// If game is not paused
		if (gameState == 0) {
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
							Thread.sleep(100);
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
							handle.writeString("MainPlayer: " + currentPlayer + " with percentageMovesSearched: " + percentageMovesSearched + " numMovesPlayed: " + numberOfMovesPlayed + " and Time taken : " + ((newTime - prevTime)/1000) +"\r\n", true);
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
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// Animates board to show new board with next level of BFS
				// calls done. Returns whether the BFS is complete or not.
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
						
						// Update statistics
						if (NUMBER_OF_PLAYERS == 2 && (isCPU[0] == true || isCPU[1] == true) && !(isCPU[0] && isCPU[1])) {
							String key;
							if (isCPU[0]) {
								if (currentPlayer == 0)
									key = "lostLevel";
								else
									key = "wonLevel";
								key += difficultyLevels[0];
							}
							else {
								if (currentPlayer == 1)
									key = "lostLevel";
								else
									key = "wonLevel";
								key += difficultyLevels[1];
							}
							stats.putInteger(key, stats.getInteger(key, 0)+1);
							stats.flush();
						}
						
						myGame.setScreen(new GameEndScreen(myGame, currentPlayer, numberOfMovesPlayed));
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
			batch.draw(pauseButtonImg, 0, ((GRID_SIZE_Y * HEIGHT_RECTANGLE) + PAD_BOTTOM_PAUSE_BUTTON));
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
			if (animationInit) {
				modelBatch.begin(cam);
				createAnimation();
				drawAnimation();
				modelBatch.end();
			}
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
				gameBoard.changeBoard2(clickCoordX, clickCoordY,
						currentPlayer);
				highlightPos.coordX = clickCoordX;
				highlightPos.coordY = clickCoordY;
				if (DEBUG)
					System.out.println("Hightlight set " + clickCoordX + " " + clickCoordY);
				moveCompleted = false;
				numberOfMovesPlayed += 1;
				percentageMovesSearched += incrementValForPercentageMovesSearched;
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
		if (coordX > WIDTH_PAUSE_BUTTON * widthUpscaleFactor || coordX < 0) {
			return;
		}
		if (DEBUG)
			Gdx.app.log("Pause Button Debug", "coordX: " + coordX + " coordY: " + coordY + " coordX is smaller than: " + WIDTH_PAUSE_BUTTON * widthUpscaleFactor);
		pause();
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
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock2.x, tempBlock2.y, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y);
			shapeRenderer.line(tempBlock2.x, tempBlock2.y, tempBlock2.x, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
			shapeRenderer.line(tempBlock2.x, tempBlock2.y + HEIGHT_INNER_RECTANGLE, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
			shapeRenderer.line(tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y, tempBlock2.x + WIDTH_INNER_RECTANGLE, tempBlock2.y + HEIGHT_INNER_RECTANGLE);
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
		System.out.println("Cleared " + startZPosition.size());
		xVal.clear();
		yVal.clear();
		color.clear();
		startZPosition.clear();
		distNow.clear();
		speed.clear();
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