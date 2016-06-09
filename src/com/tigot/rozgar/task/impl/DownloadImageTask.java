package com.tigot.rozgar.task.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;

import com.tigot.rozgar.app.RozgarApplication;
import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.task.bean.TaskPriority;
import com.tigot.rozgar.task.bean.TaskRunnable;
import com.tigot.rozgar.utils.DisplayUtils;
import com.tigot.rozgar.utils.FileCacheUtils;
import com.tigot.rozgar.utils.ImageUtils;
import com.tigot.rozgar.utils.PerformanceUtils;
import com.tigot.rozgar.utils.bean.AdjustLayoutRunnable;
import com.tigot.rozgar.utils.bean.Holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class DownloadImageTask implements TaskRunnable {

	private Boolean isStopNotified = false;
	private Boolean isCompleted = false;
	private Activity activity;
	private Handler handler;
	private ImageView imageView;
	private String urlPath;
	private Integer progressSize;
	private Integer photoScaleFactor; 
	private AdjustLayoutRunnable adjustLayoutRunnable;
	
	public DownloadImageTask(Activity activity, Handler handler, ImageView imageView, String urlPath, Integer progressSize, Integer photoScaleFactor, AdjustLayoutRunnable adjustLayoutRunnable) {
		this.activity = activity;
		this.handler = handler;
		this.imageView = imageView; 
		this.urlPath = urlPath;
		this.progressSize = progressSize;
		this.photoScaleFactor = photoScaleFactor;
		this.adjustLayoutRunnable = adjustLayoutRunnable;
	}
	
	@Override
	public void run() {
		if (isStopNotified) {
			isCompleted = true; 
			return;
		}
		try {
			downloadImpl(imageView, urlPath, progressSize, photoScaleFactor);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		isCompleted = true; 
	}

	@Override
	public TaskPriority getPriority() {
		return TaskPriority.MEDIUM;
	}

	@Override
	public void onStopNotification() {
		Log.w("DownloadImageTask", "stop(), [stopped:" + Thread.currentThread().isAlive() + "]");
		isStopNotified = true;
	}
 
	@Override
	public Boolean isCompleted() {
		return isCompleted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((urlPath == null) ? 0 : urlPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownloadImageTask other = (DownloadImageTask) obj;
		if (urlPath == null) {
			if (other.urlPath != null)
				return false;
		} else if (!urlPath.equals(other.urlPath))
			return false;
		return true;
	}

	/**
	 * Download image from internet, store on SDcard, show progress image during downloading.
	 * @param aImageView
	 * @param aUrlPath
	 * @param aProgressSize
	 * @param aScaleFactor
	 * @throws IOException
	 */
	private void downloadImpl(final ImageView aImageView, final String aUrlPath, final Integer aProgressSize, final Integer aScaleFactor) throws IOException {
		Log.w("DownloadImageTask", "downloadImpl(), [aUrlPath:" + aUrlPath + "]");
		if (aUrlPath == null) {
			return; 
		}
		/**
		 * if image is already downloaded then show it and return
		 */
		if (FileCacheUtils.isExists(aUrlPath)) {
			final Holder<Bitmap> bitmap = new Holder<Bitmap>(FileCacheUtils.get(aUrlPath));
			if (bitmap.getValue() != null) {
				ImageUtils.fixImageViewLayout(handler, aImageView, bitmap.getValue());
				return;
			}
		}
		/**
		 * download image with URLConnection. Every 5 percent progress image showd be updated
		 */
		URL url = new URL(aUrlPath);
		URLConnection ucon = url.openConnection();
		InputStream is = ucon.getInputStream();
		int imageSize = ucon.getContentLength();
		BufferedInputStream bis = new BufferedInputStream(is, 16 * 1024);
		ByteArrayBuffer baf = new ByteArrayBuffer(imageSize);//8 * 1024
		Log.w("DownloadImageTask", "run(), [baf.size:" + imageSize + "]");
		
		Log.w("DownloadImageTask", "run(), [maxMemory:" + Runtime.getRuntime().maxMemory() + "]");
		Log.w("DownloadImageTask", "run(), [freeMemory:" + Runtime.getRuntime().freeMemory() + "]");
		PerformanceUtils.logHeap(this.getClass());
		
		final Bitmap bitmap0 = ImageUtils.getProgress(0, ImageUtils.convertDip2Pixel(activity, aProgressSize));
		handler.post(new Runnable() {
			@Override
			public void run() { 
				aImageView.setScaleType(ScaleType.CENTER);
				aImageView.setImageBitmap(bitmap0);
				handler.postDelayed(new Runnable() {  
					@Override
					public void run() {
						aImageView.setVisibility(View.VISIBLE);
					}
				}, 500);
			}
		});

		int count = 0;
		byte data[] = new byte[16 * 1024]; 
		int lastPercent = 0;
		while ((count = bis.read(data)) != -1) {
			baf.append(data, 0, count); 
			int curPercent = (int) ((baf.length() / (float) imageSize) * 100);
			if (curPercent - lastPercent > 5) {
				final Bitmap bitmap = ImageUtils.getProgress(curPercent, ImageUtils.convertDip2Pixel(activity, aProgressSize));
				lastPercent = curPercent;
				handler.post(new Runnable() {  
					@Override
					public void run() {
						aImageView.setImageBitmap(bitmap);
					} 
				});
			}
		}

		/**
		 * decode downloaded raw image. Finally show image instead of progress image
		 */
		final Bitmap bm = BitmapFactory.decodeByteArray(baf.buffer(), 0, baf.length());
		DisplayMetrics dm = activity.getResources().getDisplayMetrics(); 
		bm.setDensity(dm.densityDpi);
		FileCacheUtils.put(aUrlPath, bm);
		adjustLayoutRunnable.setBm(bm);
		handler.post(adjustLayoutRunnable);
	}
	
}
