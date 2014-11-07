package com.chainreactionai.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.chainreactionai.game.ChainReactionAIGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "ChainReaction";
		config.width = 452;
		config.height = 480;
		new LwjglApplication(new ChainReactionAIGame(), config);
	}
}
