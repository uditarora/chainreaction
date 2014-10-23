package com.chainreactionai.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.chainreactionai.game.ChainReactionAIGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "ChainReaction";
		config.width = 440;
		config.height = 650;
		new LwjglApplication(new ChainReactionAIGame(), config);
	}
}
