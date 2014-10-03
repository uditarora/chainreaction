package com.chainreactionai.game.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.chainreactionai.game.ChainReactionAIGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		
		// Changes in config to disable Accelerometer and Compass so as
		// to conserve battery.
		config.useAccelerometer = false;
		config.useCompass = false;
		
		// Initialize the game
		initialize(new ChainReactionAIGame(), config);
	}
}
