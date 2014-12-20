/**
 * 
 */
package com.chainreactionai.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Kartik Parnami
 * 
 */
public class MyInputProcessor implements InputProcessor {

	private float clickCoordX, clickCoordY;
	private boolean isTouchDown;
	private ChainReactionAIGame myGame;
	
	public MyInputProcessor(ChainReactionAIGame game) {
		myGame = game;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		clickCoordX = x;
		clickCoordY = y;
		isTouchDown = true;
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public boolean isTouchedDown() {
		return isTouchDown;
	}

	public void unsetTouchDown() {
		isTouchDown = false;
	}

	public float getXCoord() {
		return clickCoordX;
	}

	public float getYCoord() {
		return clickCoordY;
	}

}
