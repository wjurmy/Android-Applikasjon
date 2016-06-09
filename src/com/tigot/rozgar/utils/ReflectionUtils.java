package com.tigot.rozgar.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.Log;

public class ReflectionUtils {

	public static Boolean simpleEquals(Object value1, Object value2) {
		if (value1 == null && value2 != null) {
			return false;
		} else if (value1 != null && value2 == null) {
			return false;
		} else if (value1 == null && value2 == null) {
			return true;
		} else {
			return value1.equals(value2);
		}
	}
	
	public static Object getField(Class class1, String fieldName) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = class1.getDeclaredField(fieldName);
		field.setAccessible(true);
		Object value1 = field.get(null);
		return value1;
	}
	
	public static Method getMethod(Class class1, String methodName, Class... args) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return class1.getMethod(methodName, args);
	}
	
	public static Method getMethod(Object object1, String methodName, Class... args) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return getMethod(object1.getClass(), methodName, args);
	}
	
	public static Object invokeMethod(Class class1, String methodName) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method method = class1.getMethod(methodName);
		Object value1 = method.invoke(null);
		return value1;
	}
	
	public static Object invokeMethod(Object object1, String methodName) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class class1 = object1.getClass();
		Method method = class1.getMethod(methodName);
		Object value1 = method.invoke(object1);
		return value1;
	}
	
	public static Object invokeClassMethod(Method method, Object... argValues) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Object value1 = method.invoke(null, argValues);
		return value1;
	}
	
	public static Object invokeMethod(Object object1, Method method, Object... argValues) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Object value1 = method.invoke(object1, argValues);
		return value1;
	}
	
	public static Object getField(Object object1, String fieldName) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class aClass = object1.getClass();
		Field field = aClass.getField(fieldName);
		field.setAccessible(true);
		Object value1 = field.get(object1);
		return value1;
	}
	
	public static Boolean fieldEquals(Object object1, Object object2, String fieldName) {
		if (object1 == null && object2 != null) {
			return false;
		} else if (object1 != null && object2 == null) {
			return false;
		} else if (object1 == null && object2 == null) {
			return true;
		} else {
			// Aggregated objects are not null
			try {
				Class aClass = object1.getClass();
				Field field = aClass.getField(fieldName);
				Object value1 = field.get(object1);
				Object value2 = field.get(object2);
				return simpleEquals(value1, value2);
			} catch (SecurityException e) {
				return false;
			} catch (NoSuchFieldException e) {
				return false;
			} catch (IllegalArgumentException e) {
				return false;
			} catch (IllegalAccessException e) {
				return false;
			}
		}
	}
	
}
