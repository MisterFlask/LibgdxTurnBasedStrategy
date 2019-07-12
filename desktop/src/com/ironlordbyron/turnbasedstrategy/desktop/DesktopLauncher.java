package com.ironlordbyron.turnbasedstrategy.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ironlordbyron.turnbasedstrategy.entrypoints.GdxGameMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		try {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.width = 1200;
			config.height = 900;
			config.x = 0;
			config.y = 0;
			new LwjglApplication(new GdxGameMain(), config);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
