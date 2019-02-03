/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
	final private int MAX_CORNER_SPLIT_WAIT_TIME = 50;
	final private int MAX_MIDDLE_SPLIT_WAIT_TIME = 50;
	final private int MAX_EDGE_SPLIT_WAIT_TIME = 50;
	final private int MAX_WIN_ADJOINING_RECT_SPLIT_WAIT_TIME = 50;
	final private int MAX_RECURSIVELY_BETWEEN_SPLIT_WAIT_TIME = 50;
	final private int MAX_RECURSIVELY_SPLIT_WAIT_TIME = 100;
	private int cornerSplitWaitTime, middleSplitWaitTime, edgeSplitWaitTime, winAdjoiningRectAfterSplitWaitTime, recursivelySplitWaitTime;
	private int textChoice;
	private Stage stage = new Stage();
	private Table table = new Table(), container = new Table();
	private float heightUpscaleFactor, widthUpscaleFactor;
	private Label rulesOne, rulesTwo, rulesThree, rulesFour, rulesFive, rulesFiveFollowUp, rulesHeading;
	private int currentImage;
	private ScrollPane scroll;
	// Trying 3D Graphics
	private PerspectiveCamera cam;
	private Drawable cornerBeforeSplitButtonDraw, cornerAfterSplitButtonDraw, middleBeforeSplitButtonDraw, middleAfterSplitButtonDraw,
					 edgeBeforeSplitButtonDraw, edgeAfterSplitButtonDraw, recursivelyBeforeSplitButtonDraw, recursivelyAfterSplitButtonDraw,
					 winAdjoiningRectBeforeSplitButtonDraw, winAdjoiningRectAfterSplitButtonDraw, recursivelyBetweenSplitButtonDraw;
	// Trying ImageButton
	private ImageButton mainMenuButtonImg = new ImageButton(ChainReactionAIGame.mainMenuButtonDraw, ChainReactionAIGame.mainMenuPressedButtonDraw),
			nextButtonImg = new ImageButton(ChainReactionAIGame.nextButtonDraw, ChainReactionAIGame.nextPressedButtonDraw),
			tutorialButtonImg = new ImageButton(ChainReactionAIGame.tutorialButtonDraw, ChainReactionAIGame.tutorialPressedButtonDraw);
	private ImageButton cornerSplitButton, middleSplitButton, edgeSplitButton, recursivelySplitButton, winAdjoiningRectSplitButton;
	private Image img = new Image(ChainReactionAIGame.texture);
	
	// Constructor
	public GameRulesScreen(ChainReactionAIGame game, int textChoice) {
		myGame = game;
		this.textChoice = textChoice;
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
		// Trying 3D graphics
		cam = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float camZ = ((float)1440*720/1240)*((float)Gdx.graphics.getHeight()/Gdx.graphics.getWidth());
		cam.position.set(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, camZ);
	    cam.lookAt(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, 0);
	    cam.near = 1f;
	    cam.far = 4000f;
	    cam.update();
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
				+ "or one occupied by atoms of their own colour.\n"
				+ "You can undo your last move during the game.\n"
				+ "But you won't unlock any achievements and your\n"
				+ "score won't be updated in the leaderboards.\n\n"
				+ "Let's walk you through an interactive tutorial to learn the rules "
				+ "of the game. Click on the cells pointed at by the arrows "
				+ "to understand the way that the game works.\n", ChainReactionAIGame.skin);
		rulesTwo = new Label("Critical mass is 2 for the cells in the "
				+ "corners ie. the atoms will split in two available "
				+ "horizontal and vertical directions as shown in the image below.", ChainReactionAIGame.skin);
		rulesThree = new Label("Critical mass is 3 for the cells along "
				+ "the edges ie. the atoms will split and one atom each "
				+ "will be placed in the 3 available horizontal and vertical "
				+ "directions as shown in the image below.", ChainReactionAIGame.skin);
		rulesFour = new Label("Similarly, critical mass is 4 for the rest of "
				+ "the cells of the grid as shown in the image below. ", ChainReactionAIGame.skin);
		rulesFive = new Label("When your atom reaches a given cell on splitting, you "
				+ "occupy that cell and your new atom gets added to the previously "
				+ "existing atoms present in that cell. Click on "
				+ "the two images below to see it in action.", ChainReactionAIGame.skin);
		rulesFiveFollowUp = new Label("If you are not familiar with our interface as of "
				+ "yet, check out the tutorial by clicking the button underneath.", ChainReactionAIGame.skin);
		rulesOne.setFontScale((float)heightUpscaleFactor/2);
		rulesOne.setWrap(true);
		rulesTwo.setFontScale((float)heightUpscaleFactor/2);
		rulesTwo.setWrap(true);
		rulesThree.setFontScale((float)heightUpscaleFactor/2);
		rulesThree.setWrap(true);
		rulesFour.setFontScale((float)heightUpscaleFactor/2);
		rulesFour.setWrap(true);
		rulesFive.setFontScale((float)heightUpscaleFactor/2);
		rulesFive.setWrap(true);
		rulesFiveFollowUp.setFontScale((float)heightUpscaleFactor/2);
		rulesFiveFollowUp.setWrap(true);
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
			table.add(rulesFiveFollowUp).padLeft(10).padRight(10).padBottom(10).width(420*widthUpscaleFactor).row();
		}
		nextButtonImg.getImageCell().expand().fill();
		mainMenuButtonImg.getImageCell().expand().fill();
		if (textChoice < 5)
			table.add(nextButtonImg).size(WIDTH_RULES_SCREEN_BUTTONS*widthUpscaleFactor, HEIGHT_RULES_SCREEN_BUTTONS*widthUpscaleFactor).padBottom(15).row();
		if (textChoice == 5)
			table.add(tutorialButtonImg).size(WIDTH_RULES_SCREEN_BUTTONS*widthUpscaleFactor, HEIGHT_RULES_SCREEN_BUTTONS*widthUpscaleFactor).padBottom(2).row();
			table.add(mainMenuButtonImg).size(WIDTH_RULES_SCREEN_BUTTONS*widthUpscaleFactor, HEIGHT_RULES_SCREEN_BUTTONS*widthUpscaleFactor).padBottom(2).row();
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
		mainMenuButtonImg.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new MainMenuScreen(myGame));
			}
		});
		tutorialButtonImg.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ChainReactionAIGame.GRAYED_OUT = true;
				myGame.setScreen(new TutorialTextScreen(myGame, 1));
			}
		});
		nextButtonImg.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				myGame.setScreen(new GameRulesScreen(myGame, textChoice+1));
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
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
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
			if (textChoice == 1) {
				myGame.setScreen(new MainMenuScreen(myGame));
			} else if (textChoice == 2) {
				myGame.setScreen(new GameRulesScreen(myGame, 1));
			} else if (textChoice == 3) {
				myGame.setScreen(new GameRulesScreen(myGame, 2));
			} else if (textChoice == 4) {
				myGame.setScreen(new GameRulesScreen(myGame, 3));
			} else if (textChoice == 5) {
				myGame.setScreen(new GameRulesScreen(myGame, 4));
			}
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
