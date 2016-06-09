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

public class DownloadCategoryService extends Service {
	
	@Override
	public void onCreate() { 
		super.onCreate();
		Log.w("DownloadCategoryService", "onCreate");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	 
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.w("DownloadCategoryService", "onStart, " + (intent == null ? "INTENT_NULL" : intent.getDataString()));
	}
	 
	/**
	 * Download category articles and then send intent RECEIVER_ACTION_RELOAD_UI
	 * CategoryFragment and TopCategoriesFragment listen for RECEIVER_ACTION_RELOAD_UI
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final Integer categoryId = intent != null && intent.hasExtra(Consts.INTENT_DOWNLOAD_CATEGORY_ID) ? intent.getIntExtra(Consts.INTENT_DOWNLOAD_CATEGORY_ID, -1) : -1;
		final Long beforeMillis = intent != null && intent.hasExtra(Consts.INTENT_DOWNLOAD_BEFORE) ? intent.getLongExtra(Consts.INTENT_DOWNLOAD_BEFORE, new Date().getTime()) : new Date().getTime();
		final String calllbackAction = intent != null && intent.hasExtra(Consts.INTENT_DOWNLOAD_CALLBACK_ACTION) ? intent.getStringExtra(Consts.INTENT_DOWNLOAD_CALLBACK_ACTION) : Consts.RECEIVER_ACTION_RELOAD_CATEGORY_UI;
		Log.w("DownloadCategoryService", "onStartCommand, [categoryId:" + (intent == null ? "ISNULL" : categoryId) + "]");
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				Log.w("DownloadCategoryService", "onStartCommand, doInBackground()");
				Boolean isNewFeeditemsExists = false; 
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DownloadCategoryService.this);

				Cursor categoriesCursor;
				if (categoryId != -1) {
					Category category = DataProviderFacade.getCategoryById(DownloadCategoryService.this, categoryId);
					isNewFeeditemsExists = DownloadCategoriesUtils.downloadCategory(DownloadCategoryService.this, category.getId(), new Date(beforeMillis));
				} else {
					// Download all channels 
					categoriesCursor = DataProviderFacade.getCategoriesCursor(DownloadCategoryService.this);
					if (categoriesCursor.getCount() > 0) {
						try { 
							while (categoriesCursor.moveToNext()) { 
								Integer id = categoriesCursor.getInt(Consts.COLUMN_INDEX_ID); 
								Integer categoryId = categoriesCursor.getInt(Consts.COLUMN_INDEX_CATEGORY_ID);
								isNewFeeditemsExists = DownloadCategoriesUtils.downloadCategory(DownloadCategoryService.this, id, new Date(beforeMillis)) || isNewFeeditemsExists;
							}
						} finally {
							categoriesCursor.close();
						}
					} else {
						categoriesCursor.close();
					}
				} 
				
				return isNewFeeditemsExists;
			}
			@Override
			protected void onPostExecute(Boolean isNewFeeditemsExists) {
				Log.w("DownloadCategoryService", "onStartCommand, onPostExecute()");
				super.onPostExecute(isNewFeeditemsExists);
				Intent intent = new Intent(calllbackAction); 
				intent.putExtra(Consts.INTENT_DOWNLOADED_CATEGORY_ID, categoryId);
				DownloadCategoryService.this.sendBroadcast(intent);
			}
		}.execute();
		
		return Service.START_NOT_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();
		Log.w("DownloadCategoryService", "onDestroy");
	}
 
}
