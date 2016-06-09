package com.tigot.rozgar.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Display;
import android.view.WindowManager;

import com.tigot.rozgar.app.RozgarApplication;

public class DisplayUtils {

	public static Boolean isLandscape() {
		return RozgarApplication.getAppContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
	
	public static Boolean isTablet() {
		WindowManager window = (WindowManager) RozgarApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = window.getDefaultDisplay();
		return ImageUtils.convertPixel2Dp(RozgarApplication.getAppContext(), display.getWidth()) >= 590;
	}
	
	public static Integer getDisplayWidth() {
		WindowManager window = (WindowManager) RozgarApplication.getAppContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = window.getDefaultDisplay();
		return display.getWidth();
	}


}
