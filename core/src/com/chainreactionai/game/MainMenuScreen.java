/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Kartik Parnami
 * 
 */
public class MainMenuScreen implements Screen {
	SpriteBatch batch;
	private ChainReactionAIGame myGame;
	final private int WIDTH_SCREEN = 448;
	final private int HEIGHT_SCREEN = 645;
	final private int HEIGHT_MAIN_MENU_BUTTONS = 60;
	final private int WIDTH_MAIN_MENU_BUTTONS = 275;
	final private int WIDTH_LOGO = 293;
	final private int HEIGHT_LOGO = 138;
	private float widthUpscaleFactor;
	private Stage stage = new Stage();
	private Table table = new Table();
	private ImageButton buttonPlay, buttonRules, buttonTutorial, buttonLeaderboard, buttonAchievements, logo;
	private PerspectiveCamera cam;
	private Image img;
	
	public MainMenuScreen(ChainReactionAIGame game) {
		ChainReactionAIGame.currentScreen = 0;
		myGame = game;
		create();
	}
	
	private void create() {
		batch = new SpriteBatch();
		// The elements are displayed in the order you add them.
		// The first appear on top, the last at the bottom.
		// Up-scale Factors are used to get proper sized buttons
		// upscaled or downscaled according to the Screen Dimensions
		widthUpscaleFactor = ((float)(ChainReactionAIGame.WIDTH))/WIDTH_SCREEN;
		// Trying 3D graphics
		cam = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float camZ = ((float)1440*720/1240)*((float)Gdx.graphics.getHeight()/Gdx.graphics.getWidth());
		cam.position.set(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, camZ);
	    cam.lookAt(WIDTH_SCREEN/2, HEIGHT_SCREEN/2, 0);
	    cam.near = 1f;
	    cam.far = 4000f;
	    cam.update();
	    // Adds the title and buttons to the Table.
        if (!ChainReactionAIGame.GRAYED_OUT)
        	img = new Image(ChainReactionAIGame.texture);
        else
        	img = new Image(ChainReactionAIGame.textureGray);
        //title.setFontScale((1+(heightUpscaleFactor-1)/2));
		//table.add(title).padBottom(40).row();
        logo = new ImageButton(ChainReactionAIGame.logoDraw);
        logo.getImageCell().expand().fill();
        table.add(logo).size(WIDTH_LOGO*widthUpscaleFactor, HEIGHT_LOGO*widthUpscaleFactor).padBottom(20).row();
		buttonPlay = new ImageButton(ChainReactionAIGame.playButtonDraw, ChainReactionAIGame.playPressedButtonDraw);
		buttonPlay.getImageCell().expand().fill();
		table.add(buttonPlay).size(WIDTH_MAIN_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_MAIN_MENU_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		if (!ChainReactionAIGame.GRAYED_OUT)
			buttonRules = new ImageButton(ChainReactionAIGame.rulesButtonDraw, ChainReactionAIGame.rulesPressedButtonDraw);
		else
			buttonRules = new ImageButton(ChainReactionAIGame.rulesGrayButtonDraw);
		buttonRules.getImageCell().expand().fill();
		table.add(buttonRules).size(WIDTH_MAIN_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_MAIN_MENU_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		if (!ChainReactionAIGame.GRAYED_OUT)
			buttonTutorial = new ImageButton(ChainReactionAIGame.tutorialButtonDraw, ChainReactionAIGame.tutorialPressedButtonDraw);
		else
			buttonTutorial = new ImageButton(ChainReactionAIGame.tutorialGrayButtonDraw);
		buttonTutorial.getImageCell().expand().fill();
		table.add(buttonTutorial).size(WIDTH_MAIN_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_MAIN_MENU_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		if (!ChainReactionAIGame.GRAYED_OUT)
			buttonLeaderboard = new ImageButton(ChainReactionAIGame.leaderboardButtonDraw, ChainReactionAIGame.leaderboardPressedButtonDraw);
		else
			buttonLeaderboard = new ImageButton(ChainReactionAIGame.leaderboardGrayButtonDraw);
		buttonLeaderboard.getImageCell().expand().fill();
		table.add(buttonLeaderboard).size(WIDTH_MAIN_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_MAIN_MENU_BUTTONS*widthUpscaleFactor).padBottom(10).row();
		if (!ChainReactionAIGame.GRAYED_OUT)
			buttonAchievements = new ImageButton(ChainReactionAIGame.achievementsButtonDraw, ChainReactionAIGame.achievementsPressedButtonDraw);
		else
			buttonAchievements = new ImageButton(ChainReactionAIGame.achievementsGrayButtonDraw);
		
		buttonAchievements.getImageCell().expand().fill();
		table.add(buttonAchievements).size(WIDTH_MAIN_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_MAIN_MENU_BUTTONS*widthUpscaleFactor).row();
		table.setFillParent(true);
		// Adding the table to stage.
		img.setFillParent(true);
		stage.addActor(img);
		stage.addActor(table);
		// Attaching ClickListeners to the Play and Exit buttons.
		if (!ChainReactionAIGame.GRAYED_OUT) {
			buttonPlay.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new NumPlayersScreen(myGame));
				}
			});
		} else {
			buttonPlay.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new TutorialTextScreen(myGame, 0, 0, 2));
				}
			});
		}
		if(!ChainReactionAIGame.GRAYED_OUT) {
			buttonRules.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					myGame.setScreen(new GameRulesScreen(myGame, 1));
				}
			});
		}
		if(!ChainReactionAIGame.GRAYED_OUT) {
			buttonTutorial.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					ChainReactionAIGame.GRAYED_OUT = true;
					myGame.setScreen(new TutorialTextScreen(myGame, 1));
				}
			});
		}
		if(!ChainReactionAIGame.GRAYED_OUT) {
			buttonLeaderboard.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					ChainReactionAIGame.googleServices.showScores();
				}
			});
		}
		if(!ChainReactionAIGame.GRAYED_OUT) {
			buttonAchievements.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					ChainReactionAIGame.googleServices.showAchievement();
				}
			});
		}
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(((float)(15)/255), ((float)(15)/255), ((float)(15)/255), 1);
		stage.act(delta);
		stage.draw();
		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
			if (ChainReactionAIGame.GRAYED_OUT == false)
				Gdx.app.exit();
			else
				myGame.setScreen(new TutorialTextScreen(myGame, 1));
		}
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
	}
}
