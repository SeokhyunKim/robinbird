package org.robinbird;

import java.lang.reflect.Method;

/**
 * Utility functions for unit tests
 */
public class TestUtils {

	public static Method getAccessiblePrivateMethod(Class cls, String methodName, Class<?>... methodType) throws NoSuchMethodException {
		Method method = cls.getDeclaredMethod(methodName, methodType);
		method.setAccessible(true);
		return method;
	}

}
