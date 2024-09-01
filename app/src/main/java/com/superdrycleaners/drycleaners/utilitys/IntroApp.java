package com.superdrycleaners.drycleaners.utilitys;

import android.app.Application;

/**
 *
 */

public class IntroApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Globals.init(this);
	}
}
