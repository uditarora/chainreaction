/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
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
	final private int GRID_SIZE = 7;
	final private float WIDTH_RECTANGLE = ((float)(440)/GRID_SIZE);
	final private float HEIGHT_RECTANGLE = ((float)(440)/GRID_SIZE);
	final private int WIDTH_SCREEN = 452;
	final private int HEIGHT_SCREEN = 480;
	final private int HEIGHT_PAUSE_BUTTON = 27;
	final private int WIDTH_PAUSE_BUTTON = 55;
	final private int HEIGHT_PAUSE_MENU_BUTTONS = 60;
	final private int WIDTH_PAUSE_MENU_BUTTONS = 150;
	final private int MAX_NUM_PLAYERS = 6;
	private int INVERSE_SPEED_OF_BALL_VIBRATION = 28;
	private int NUMBER_OF_PLAYERS, breakingAway, splittableBreakingAway;
	private Texture pauseButtonImg = new Texture("pauseButton.jpg");
	private Array<Rectangle> rectangularGrid;
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
	private TextButton resumeButton, exitButton, newGameButton;
	private Position highlightPos = new Position(-1, -1);
	private GameSolverChar solver;
	private long prevTime, newTime;
	private FileHandle handle = Gdx.files.external("data/myfile.txt");
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private Color[] colors;
	private TextButtonStyle resumeButtonStyler;
	// Trying 3D Graphics
	public Model[] models;
	public ModelInstance[] instances;
	public ModelBatch modelBatch;
	public PerspectiveCamera cam;
	public Environment environment;
	// All debug printing should go under this flag.
	final private boolean DEBUG = true;
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
		
		//Simulating with only CPU Players for testing
		if (DEBUG_CPU) {
			for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
				isCPU[i] = true;
				difficultyLevels[i] = 104;
			}
		}
		else {
			if (DEBUG)
				System.out.println(CPU.size());
			
			for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
				isCPU[i] = CPU.get(i);
				if (isCPU[i]) {
					difficultyLevels[i] = difficultyLevelList.get(i);
					System.out.println("isCPU[" + i + "] = " + isCPU[i] + " with difficulty Level = " + difficultyLevels[i]);
				} else {
					System.out.println("isCPU[" + i + "] = " + isCPU[i]);
				}
			}
		}
		setGameState(0);
		create();
	}

	private void create() {
		batch = new SpriteBatch();
		// Initializing stuff.
		rectangularGrid = new Array<Rectangle>();
		gameBoard = new GameBoardChar(GRID_SIZE, NUMBER_OF_PLAYERS);
		inputProcessor = new MyInputProcessor(myGame);
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
		//percentageMovesSearched = 1;
		incrementValForPercentageMovesSearched = 1/(double)(3*maxPlyLevel*maxPlyLevel);
		resumeButton = new TextButton(new String("Resume"), skin);
		newGameButton = new TextButton(new String("New Game"), skin);
		exitButton = new TextButton("Exit", skin);
		handle.writeString("--------------------------------------------------------------------------\r\n", true);
		
		// Populating the Pause menu with the buttons.
		resumeButtonStyler = new TextButtonStyle(resumeButton.getStyle());
		resumeButtonStyler.font.setScale((1+(heightUpscaleFactor-1)/2));
		resumeButton.setStyle(resumeButtonStyler);
		table.add(resumeButton).size(WIDTH_PAUSE_MENU_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_PAUSE_MENU_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(2).row();
		newGameButton.setStyle(resumeButtonStyler);
		table.add(newGameButton).size(WIDTH_PAUSE_MENU_BUTTONS*(1+(widthUpscaleFactor-1)/2), HEIGHT_PAUSE_MENU_BUTTONS*(1+(heightUpscaleFactor-1)/2)).padBottom(2).row();
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
				shiftToNewGameScreen();
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
		Gdx.app.log("size", "CamZ: "+camZ);
		cam.position.set(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, camZ);
		
	    cam.lookAt(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, 0);
	    cam.near = 1f;
	    cam.far = 3000f;
	    cam.update();
		
		ModelBuilder modelBuilder = new ModelBuilder();
		models = new Model[MAX_NUM_PLAYERS];
		instances = new ModelInstance[MAX_NUM_PLAYERS];
		for (int i = 0; i < MAX_NUM_PLAYERS; i += 1) {
			models[i] = modelBuilder.createSphere(20f, 20f, 20f, 30, 30, new Material(ColorAttribute.createDiffuse(colors[i])), Usage.Position | Usage.Normal | Usage.TextureCoordinates);
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
		for (int i = 0; i < GRID_SIZE; i += 1) {
			for (int j = 0; j < GRID_SIZE; j += 1) {
				Rectangle tempBlock = new Rectangle();
				tempBlock.x = (float) (i * WIDTH_RECTANGLE) + 4;
				tempBlock.y = (float) (j * HEIGHT_RECTANGLE) + 4;
				tempBlock.height = (float) (HEIGHT_RECTANGLE);
				tempBlock.width = (float) (WIDTH_RECTANGLE);
				System.out.println(tempBlock.x + " " + tempBlock.y);
				rectangularGrid.add(tempBlock);
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
//			heuristicNumbers[0] = 12;
//			heuristicNumbers[1] = 12;
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
						System.out.println("Player " + currentPlayer
								+ " has won the game!");
						myGame.setScreen(new GameEndScreen(myGame, currentPlayer, numberOfMovesPlayed));
					}
					currentPlayer = (currentPlayer + 1) % NUMBER_OF_PLAYERS;
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
			batch.draw(pauseButtonImg, 0, (GRID_SIZE*HEIGHT_RECTANGLE + 10));
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (isBackButtonPressed) {
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
		float coordX = inputProcessor.getXCoord(), coordY = inputProcessor.getYCoord(), distOfPauseButtonFromTop, distOfGridFromBottom, distOfGridFromTop, heightOfGrid, modHeightUpscaleFactor;
		distOfPauseButtonFromTop = (ChainReactionAIGame.HEIGHT - (ChainReactionAIGame.WIDTH + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor)))/2;
		distOfGridFromBottom = distOfPauseButtonFromTop;
		distOfGridFromTop = distOfPauseButtonFromTop + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor);
		if (coordY < distOfGridFromTop || coordY > ChainReactionAIGame.HEIGHT - distOfGridFromBottom) {
			return;
		}
		heightOfGrid = ChainReactionAIGame.HEIGHT - distOfGridFromBottom - distOfGridFromTop;
		modHeightUpscaleFactor = heightOfGrid/GRID_SIZE;
		clickOnEdge = false;
		clickCoordX = (int)((coordX/widthUpscaleFactor)/(WIDTH_RECTANGLE + 1.5));
		// Try to find clickOnEdge in X - coordinate
		clickCoordY = (int) (((coordY - distOfGridFromTop))/modHeightUpscaleFactor);
		clickCoordY = GRID_SIZE - clickCoordY - 1;
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
		distOfPauseButtonFromTop = (ChainReactionAIGame.HEIGHT - (ChainReactionAIGame.WIDTH + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor)))/2;
		distOfGridFromTop = distOfPauseButtonFromTop + (HEIGHT_PAUSE_BUTTON * heightUpscaleFactor);
		if (coordY > distOfGridFromTop || coordY < distOfPauseButtonFromTop) {
			return;
		}
		if (coordX > WIDTH_PAUSE_BUTTON * widthUpscaleFactor || coordX < 0) {
			return;
		}
		Gdx.app.log("Pause Button Debug", "coordX: " + coordX + " coordY: " + coordY + " coordX is smaller than: " + WIDTH_PAUSE_BUTTON * widthUpscaleFactor);
		pause();
	}
	
	// Function to draw the game board using the three
	// arrays.
	private void drawGameBoard() {
		Iterator<Rectangle> iter = rectangularGrid.iterator();
		int i, j, count = 0;
		// Goes through all the rectangles pre-fed into the 
		// rectangularGrid and draws them to the board.
		while (iter.hasNext()) {
			Rectangle tempBlock = iter.next();
			i = count / GRID_SIZE;
			j = count % GRID_SIZE;
			shapeRenderer.set(ShapeType.Line);
			shapeRenderer.setColor(colors[currentPlayer]);
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y);
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x + 4, tempBlock.y + 4, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + 4);
			shapeRenderer.line(tempBlock.x + 4, tempBlock.y + 4, tempBlock.x + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			shapeRenderer.line(tempBlock.x + 4, tempBlock.y + HEIGHT_RECTANGLE + 4, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + 4, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock.x + 4, tempBlock.y + 4);
			shapeRenderer.line(tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE, tempBlock.x + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + 4);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			// Checks if a given rectangle has to be highlighted indicating a move.
			if (highlightPos.coordX == i && highlightPos.coordY == j) {
				drawHighlight(tempBlock.x, tempBlock.y);
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
		for (int counter = 1; counter <=3; counter += 1) {
			shapeRenderer.line(x + counter, y + counter, x + WIDTH_RECTANGLE + counter, y + counter);
			shapeRenderer.line(x + counter, y + counter, x + counter, y + HEIGHT_RECTANGLE + counter);
			shapeRenderer.line(x + WIDTH_RECTANGLE + counter, y + counter, x + WIDTH_RECTANGLE + counter, y + HEIGHT_RECTANGLE + counter);
			shapeRenderer.line(x + counter, y + HEIGHT_RECTANGLE + counter, x + WIDTH_RECTANGLE + counter, y + HEIGHT_RECTANGLE + counter);
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

	// This function shifts the Screen to the NumPlayersScreen.
	private void shiftToNewGameScreen() {
		myGame.setScreen(new NumPlayersScreen(myGame));
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