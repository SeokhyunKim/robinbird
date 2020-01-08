package org.robinbird;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.robinbird.exception.RobinbirdException;

/**
 * Utility functions for unit tests
 */
@Slf4j
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

	public static Method getMethod(@NonNull final Class<?> classObj, @NonNull final String methodName, Class<?>... paramTypes) {
		try {
			return classObj.getMethod(methodName, paramTypes);
		} catch (final NoSuchMethodException e) {
			throw new RobinbirdException(e);
		}
	}

	public static Object callMethod(@Nullable final Object object, @NonNull final Method method, Object... args) {
		try {
			method.setAccessible(true);
			return method.invoke(object, args);
		} catch (final IllegalAccessException | InvocationTargetException e) {
			throw new RobinbirdException("Failed to call a method " + method.getName(), e);
		}
	}

	public static Object getMemberVariable(@NonNull final Object object, @NonNull final String fieldName) {
		try {
			final Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (final NoSuchFieldException | IllegalAccessException e) {
			throw new RobinbirdException("Failed to get a field " + fieldName, e);
		}
	}

	public static Object getStaticVariable(@NonNull final Class<?> clz, @NonNull final String fieldName) {
		try {
			final Field field = clz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(null);
		} catch (final NoSuchFieldException | IllegalAccessException e) {
			throw new RobinbirdException("Failed to get a field " + fieldName, e);
		}
	}

	public static void setValueToMember(Object instance, String variableName, Object newValue) {
		try {
			Field field = instance.getClass().getDeclaredField(variableName);
			field.setAccessible(true);
			field.set(instance, newValue);
		} catch (Exception e) {
			throw new RobinbirdException(e);
		}
	}

	public static void setValueToStaticMember(Class cls, String variableName, Object newValue) {
		try {
			Field field = cls.getDeclaredField(variableName);
			field.setAccessible(true);
			field.set(null, newValue);
		} catch (Exception e) {
			throw new RobinbirdException(e);
		}
	}





}
