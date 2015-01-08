package com.chainreactionai.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.chainreactionai.game.ChainReactionAIGame;
import com.chainreactionai.game.DesktopGoogleServices;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(440, 480);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new ChainReactionAIGame(new DesktopGoogleServices());
        }
}