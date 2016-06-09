package com.tigot.rozgar.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.utils.exception.DownloadFailedException;

import android.os.Bundle;
import android.util.Log;

public class RozgarUtils {

//	private static DateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

	public static List<NewsItem> downloadCategory(Integer categoryId, Date before) throws DownloadFailedException {
		List<NewsItem> newsitems = new LinkedList<NewsItem>();
		String url = String.format(Consts.URL_CATEGORY, categoryId, Consts.DATE_FORMAT.format(before));
		url = url.replaceAll("\\s", "&nbsp;");
		JSONArray jsonArray = getJSONfromURL(url);
		for (int i = 0;i < jsonArray.length(); i++) { 
			try {
			JSONObject jsonobject = jsonArray.getJSONObject(i);
				// Retrive JSON Objects 
				String title = jsonobject.getString("title"); 
				String author = jsonobject.getString("author"); 
				Date created = Consts.DATE_FORMAT.parse(jsonobject.getString("created"));
				String imageArticle = jsonobject.getString("image");	
				String imageCaption = jsonobject.getString("image_caption");
				String abstractTitle = jsonobject.getString("abstract");
				String body = jsonobject.getString("body"); 
			// Set the JSON Objects into the array
				newsitems.add(new NewsItem(title, author, categoryId, imageArticle, created, abstractTitle, imageCaption, body));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newsitems;
	}

	public static Map<Integer, String> downloadCategoryImages() throws DownloadFailedException {
		Map<Integer, String> category2Images = new HashMap<Integer, String>();
//		String url = String.format(Consts.URL_CATEGORY, categoryId, Consts.DATE_FORMAT.format(before));
//		url = url.replaceAll("\\s", "&nbsp;");
		JSONArray jsonArray = getJSONfromURL(Consts.URL_CATEGORY_IMAGES);
		for (int i = 0;i < jsonArray.length(); i++) { 
			try {
			JSONObject jsonobject = jsonArray.getJSONObject(i);
				// Retrive JSON Objects 
				Integer categoryId = Integer.parseInt(jsonobject.getString("id")); 
				String image = jsonobject.getString("image"); 
				Log.w("RozgarUtils", "downloadCategoryImages(), [categoryId:" + categoryId + "], [image:" + image + "]");
				category2Images.put(categoryId, image);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return category2Images;
	}

	public static JSONArray getJSONfromURL(String url) throws DownloadFailedException {
		InputStream is = null;
		String result = "";
		JSONArray jArray = null;

		// Download JSON data from URL
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
			throw new DownloadFailedException();
		}

		// Convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8); // iso-8859-1

			/*
			 * //GSON GsonBuilder gsonBuilder = new GsonBuilder();
			 * gsonBuilder.setDateFormat("M/d/yy hh:mm a"); Gson gson =
			 * gsonBuilder.create(); List<Article> articles = new
			 * ArrayList<Article>(); articles =
			 * Arrays.asList(gson.fromJson(reader, Article[].class));
			 */
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
			throw new DownloadFailedException();
		}
		Log.w("log_tag", "RozgarUtils.getJSONfromURL() [result:" + result.length() + "], [start:" + (result.length() > 20 ? result.substring(0, 20) : result) + "]"
				+ ", [end:" + (result.length() > 20 ? result.substring(result.length() - 20, result.length()) : result) + "]");

		try {

			jArray = new JSONArray(result);
		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
			throw new DownloadFailedException();
		}

		return jArray;
	}

}
