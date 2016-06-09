package com.tigot.rozgar.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class RozgarApplication extends Application {

	private static Context context;

	public void onCreate() {
		super.onCreate();
		RozgarApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return RozgarApplication.context;
	}

}
