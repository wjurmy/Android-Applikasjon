package com.tigot.rozgar.utils.bean;

import android.graphics.Bitmap;

public abstract class AdjustLayoutRunnable implements Runnable {

	private Bitmap bm;
	
	public AdjustLayoutRunnable() {
	}

	public Bitmap getBm() {
		return bm;
	}

	public void setBm(Bitmap bm) {
		this.bm = bm;
	}
	
}
