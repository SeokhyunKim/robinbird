package org.robinbird.util;

import org.robinbird.main.presentation.StringAppender;

/**
 * Created by seokhyun on 12/15/17.
 */
public class Utils {

	public static String printMemoryInfo() {
		StringAppender sa = new StringAppender();
		sa.appendLine("Memory Info in MBs:");
		Runtime runtime = Runtime.getRuntime();
		long mb = 1024 * 1024;
		sa.appendLine("- Total Memory: " + runtime.totalMemory()/mb);
		sa.appendLine("- Max Memory: " + runtime.maxMemory()/mb);
		sa.appendLine("- Free Memory: " + runtime.freeMemory()/mb);
		return sa.toString();
	}

}
