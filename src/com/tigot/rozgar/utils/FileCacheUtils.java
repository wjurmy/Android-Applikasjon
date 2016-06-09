package com.tigot.rozgar.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileCacheUtils {

	/**
	 * Check if image was already cached
	 * @param url
	 * @return
	 */
	public static Boolean isExists(String url) {
		return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/rozgar/cache/" + PrimitiveUtils.md5(url) + ".png").exists();
	}

	/**
	 * Retrieve cached bitmap object for specified url
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Bitmap get(String url) throws IOException {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/rozgar/cache/" + PrimitiveUtils.md5(url) + ".png";
		File file = new File(path);
		if (file.exists()) {
			return BitmapFactory.decodeFile(path);
		} 
		throw new IOException("bad file to get in external storage");
	}

	/**
	 * Cache bitmap to external storage
	 * @param url
	 * @param bitmap
	 */
	public static void put(String url, Bitmap bitmap) {
		new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/rozgar").mkdir();
		new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/rozgar/cache").mkdir();
		String withoutExt = Environment.getExternalStorageDirectory().getAbsolutePath() + "/rozgar/cache/" + PrimitiveUtils.md5(url);
		String path = withoutExt + ".png";   
		try {
			FileOutputStream out = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
