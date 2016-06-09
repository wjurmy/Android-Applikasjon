package com.tigot.rozgar.utils;

import java.text.DecimalFormat;

import android.os.Debug;
import android.util.Log;

public class PerformanceUtils {

	public static void logHeap(Class clazz) {
		Double allocated = new Double(Debug.getNativeHeapAllocatedSize()) / new Double((1048576));
		Double available = new Double(Debug.getNativeHeapSize() / 1048576.0);
		Double free = new Double(Debug.getNativeHeapFreeSize() / 1048576.0);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		Log.d("PerformanceUtils", "debug. =================================");
		Log.d("PerformanceUtils", "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free)
				+ "MB free) in [" + clazz.getName().replaceAll("com.closr.", "") + "]");
		Log.d("PerformanceUtils",
				"debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory() / 1048576)) + "MB of "
						+ df.format(new Double(Runtime.getRuntime().maxMemory() / 1048576)) + "MB ("
						+ df.format(new Double(Runtime.getRuntime().freeMemory() / 1048576)) + "MB free)");
		System.gc();
		System.gc();

	}

}
