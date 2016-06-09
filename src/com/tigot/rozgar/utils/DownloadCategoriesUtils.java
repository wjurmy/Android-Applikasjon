package com.tigot.rozgar.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.provider.DataProviderFacade;
import com.tigot.rozgar.utils.exception.DownloadFailedException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class DownloadCategoriesUtils {

	/**
	 * Download concrete category data from web service
	 * @param context
	 * @param categoryId
	 * @param before
	 * @return
	 */
	public static Boolean downloadCategory(Context context, Integer categoryId, Date before) {
		Log.w("DownloadCategoriesUtils", "downloadCategory()");
		Boolean isNewNewsItemsExists = false;

		List<NewsItem> newNewsItems; 
		try {
			newNewsItems = RozgarUtils.downloadCategory(categoryId, before);
			DataProviderFacade.updateCategoryTimestamp(context, categoryId, new Date());
		} catch (DownloadFailedException e) {
			return false;
		} 
		List<NewsItem> storedNewsItems = DataProviderFacade.getCategoryItems(context, categoryId);
		for (NewsItem NewsItem : newNewsItems) {
			Log.i("DownloadCategoriesUtils", "downloadCategory() [NewsItem:" + NewsItem + "]");
		}
		Log.w("DownloadCategoriesUtils", "downloadCategory() [newNewsItems.size:" + newNewsItems.size() + "] [storedNewsItems.size:" + storedNewsItems.size() + "]");
		while (newNewsItems.removeAll(storedNewsItems));
		Log.w("DownloadCategoriesUtils", "downloadCategory() [newNewsItems.size:" + newNewsItems.size() + "]");
		if (!newNewsItems.isEmpty()) {
			DataProviderFacade.addNewsitems(context, newNewsItems);
			isNewNewsItemsExists = true;
		}
		
		return isNewNewsItemsExists;
	}
	
	/**
	 * Download category images from web service
	 * @param context
	 */
	public static void downloadCategoryImage(Context context) {
		Log.w("DownloadCategoriesUtils", "downloadCategoryImage()");
		Boolean isNewNewsItemsExists = false;

		List<NewsItem> newNewsItems; 
		try {
			Map<Integer, String> category2Images = RozgarUtils.downloadCategoryImages();
			for (Integer categoryId : category2Images.keySet()) {
				DataProviderFacade.updateCategoryImage(context, categoryId, category2Images.get(categoryId));
			}
		} catch (DownloadFailedException e) {
		} 
	}
	
}
