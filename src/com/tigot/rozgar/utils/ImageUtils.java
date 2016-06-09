package com.tigot.rozgar.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.tigot.rozgar.app.RozgarApplication;
import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.task.TaskManager;
import com.tigot.rozgar.task.impl.DownloadImageTask;

public class ImageUtils {

	public static int convertDip2Pixel(Context context, int dips) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, context.getResources().getDisplayMetrics());
	}

	public static float convertPixel2Dp(Context context, float px) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	/**
	 * create round progress image
	 * 
	 * @param percent
	 *            to show on progress image
	 * @param wh
	 * @return
	 */
	public static Bitmap getProgress(int percent, int wh) {
		int degree = (int) (percent * 3.6);
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		Bitmap bmp = Bitmap.createBitmap(wh, wh, conf); // this creates a
														// MUTABLE bitmap
		Canvas canvas = new Canvas(bmp);
		final RectF rect = new RectF();
		rect.set(0, 0, wh, wh);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		canvas.drawArc(rect, 0, 360, true, paint);
		paint.setColor(0xFF4444FF);
		canvas.drawArc(rect, 0, degree, true, paint);
		paint.setColor(Color.WHITE);
		rect.set(10, 10, wh - 10, wh - 10);
		canvas.drawArc(rect, 0, 360, true, paint);
		return bmp;
	}

	public static void fixImageViewLayout(Handler handler, final ImageView aImageView, Bitmap bitmap) {
		final Integer articleWidthPx = Math.min(ImageUtils.convertDip2Pixel(RozgarApplication.getAppContext(), Consts.ARTICLE_WIDTH_DP), DisplayUtils.getDisplayWidth()/3);
		final Integer width = bitmap.getWidth() * articleWidthPx / Math.max(bitmap.getHeight(), bitmap.getWidth());
		final Integer height = bitmap.getHeight() * articleWidthPx / Math.max(bitmap.getHeight(), bitmap.getWidth());
		final Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		if (handler == null) {
			RelativeLayout.LayoutParams lp = (LayoutParams) aImageView.getLayoutParams();
			lp.width = width;
			lp.height = height;
			aImageView.setLayoutParams(lp);
			
			aImageView.setImageBitmap(newBitmap); 
			aImageView.setTag(Boolean.TRUE);
			aImageView.setVisibility(View.VISIBLE);
		} else {
			handler.post(new Runnable() { 
				@Override
				public void run() {
					RelativeLayout.LayoutParams lp = (LayoutParams) aImageView.getLayoutParams();
					lp.width = width;
					lp.height = height;
					aImageView.setLayoutParams(lp);
					
					aImageView.setImageBitmap(newBitmap); 
					aImageView.setTag(Boolean.TRUE);
					aImageView.setVisibility(View.VISIBLE);
				}
			});
		}
	}
	
	
}
