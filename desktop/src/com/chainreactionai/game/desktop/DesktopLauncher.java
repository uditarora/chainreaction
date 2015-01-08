package com.chainreactionai.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.chainreactionai.game.ChainReactionAIGame;
import com.chainreactionai.game.DesktopGoogleServices;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "ChainReaction";
		config.width = 448;
		config.height = 645;
		new LwjglApplication(new ChainReactionAIGame(new DesktopGoogleServices()), config);
	}
}
