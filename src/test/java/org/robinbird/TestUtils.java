package org.robinbird;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility functions for unit tests
 */
public class TestUtils {

	private static String rootTestPath;
	static {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		if (s.contains(".idea/modules")) {
			s = s.replace(".idea/modules", "");
		}
		rootTestPath = s + "/src/test/resources/test_directory";
	}

	public static String getTestPath(String testPath) {
		return rootTestPath + testPath;
	}

	public static Method getAccessiblePrivateMethod(Class cls, String methodName, Class<?>... methodType) throws NoSuchMethodException {
		Method method = cls.getDeclaredMethod(methodName, methodType);
		method.setAccessible(true);
		return method;
	}

}
