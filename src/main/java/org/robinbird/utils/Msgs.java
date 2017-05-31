package org.robinbird.utils;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by seokhyun on 5/26/17.
 */
public class Msgs {

	public enum Key {
		ROOT_SOURCE_PATH_NOT_PROVIDED,
		IOEXCEPTION_WHILE_READING_SOURCE_CODES
	}

	private static final Map<Key, String> msgMap = ImmutableMap.of(
		Key.ROOT_SOURCE_PATH_NOT_PROVIDED, "Root source path should be provided.",
		Key.IOEXCEPTION_WHILE_READING_SOURCE_CODES, "(Unchecked)IOException is thrown while reading source codes."
	);

	public static String get(Key k) {
		return msgMap.get(k);
	}


}
