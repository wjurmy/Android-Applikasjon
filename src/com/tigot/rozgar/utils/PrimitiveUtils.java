package com.tigot.rozgar.utils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class PrimitiveUtils {

	public static String toHexValue(ByteBuffer buffer) {
		StringBuffer result = new StringBuffer(buffer.remaining() * 2);
		while (buffer.hasRemaining()) {
			String value = Integer.toHexString(buffer.get() & 0xff);
			if (value.length() == 1)
				result.append("0"); //ensure 2 digit
			result.append(value);
		}
		return result.toString();
	}

	/**
	 * create md5 for byte array
	 * @param s
	 * @return
	 */
	public static byte[] md5(byte[] b) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			return digest.digest(b);
		} catch (NoSuchAlgorithmException e) {
			Log.e("PrimitiveUtils", "md5", e);
			return null;  
		}
	}

	/**
	 * create md5 for string
	 * @param s
	 * @return
	 */
	public static String md5(String s) {
		byte[] result = md5(s.getBytes());
		return PrimitiveUtils.toHexValue(ByteBuffer.wrap(result));
	}

}
