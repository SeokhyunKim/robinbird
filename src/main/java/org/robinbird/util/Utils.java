package org.robinbird.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

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

	public static <S, T> Map<S, List<T>> deepCopyMap(@NonNull final Map<S, List<T>> map) {
		final Map<S, List<T>> copiedMap = new HashMap<>();
		map.forEach((k, ary) -> copiedMap.put(k, new ArrayList<>(ary)));
		return copiedMap;
	}

}
