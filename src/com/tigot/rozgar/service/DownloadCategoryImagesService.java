package com.tigot.rozgar.service;

import java.util.Date;
import java.util.List;

import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.data.bean.Category;
import com.tigot.rozgar.provider.DataProviderFacade;
import com.tigot.rozgar.utils.DownloadCategoriesUtils;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class DownloadCategoryImagesService extends Service {
	
	@Override
	public void onCreate() { 
		super.onCreate();
		Log.w("DownloadCategoryImagesService", "onCreate");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	 
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.w("DownloadCategoryImagesService", "onStart, " + (intent == null ? "INTENT_NULL" : intent.getDataString()));
	}
	 
	/**
	 * Download category images and then send intent RECEIVER_ACTION_RELOAD_CATEGORY_IMAGES
	 * NavigationCategoryFragment listens for RECEIVER_ACTION_RELOAD_UI
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.w("DownloadCategoryImagesService", "onStartCommand,");
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				Log.w("DownloadCategoryImagesService", "onStartCommand, doInBackground()");
				Boolean isNewFeeditemsExists = false; 
				
				List<Category> categories = DataProviderFacade.getCategories(DownloadCategoryImagesService.this);
				for (Category category : categories) {
					Log.w("DownloadCategoryImagesService", "onStartCommand, doInBackground() [catId:" + category.getCategoryId() + "] [image:" + category.getImage() + "]");
					if (category.getImage() == null) {
						DownloadCategoriesUtils.downloadCategoryImage(DownloadCategoryImagesService.this);
						return true;
					}
				}
				return false;
			}
			@Override
			protected void onPostExecute(Boolean isUpdated) {
				Log.w("DownloadCategoryImagesService", "onStartCommand, onPostExecute()");
				super.onPostExecute(isUpdated);
				if (isUpdated) { 
					Intent intent = new Intent(Consts.RECEIVER_ACTION_RELOAD_CATEGORY_IMAGES);
					DownloadCategoryImagesService.this.sendBroadcast(intent);
				}
			}
		}.execute();
		
		return Service.START_NOT_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();
		Log.w("DownloadCategoryImagesService", "onDestroy");
	}
 
}
