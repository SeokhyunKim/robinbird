package org.robinbird.utils;

import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.robinbird.utils.Msgs.Key.ROOT_SOURCE_PATH_NOT_PROVIDED;

/**
 * Created by seokhyun on 5/26/17.
 */
public class AppArgumentsTest {
	@Test(expected = NullPointerException.class)
	public void throws_null_pointer_exception_when_no_root_path_is_given() throws IOException {
		String[] args = new String[]{};
		AppArguments.parseArguments(args);
	}

	@Test(expected = IllegalStateException.class)
	public void throws_illegal_state_exception_when_no_root_path_is_given_with_ROOT1() {
		String[] args = new String[]{"-r"};
		try {
			AppArguments.parseArguments(args);
		} catch (Exception e) {
			assertTrue(e.getMessage().equals(Msgs.get(ROOT_SOURCE_PATH_NOT_PROVIDED)));
			throw e;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void throws_illegal_state_exception_when_no_root_path_is_given_with_ROOT2() {
		String[] args = new String[]{"--root"};
		try {
			AppArguments.parseArguments(args);
		} catch (Exception e) {
			assertTrue(e.getMessage().equals(Msgs.get(ROOT_SOURCE_PATH_NOT_PROVIDED)));
			throw e;
		}
	}

	@Test
	public void success_to_read_source_root_path_with_ROOT1() {
		String[] args = new String[]{"-r", "/test"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getSourceRootPath().equals("/test"));
	}

	@Test
	public void success_to_read_source_root_path_with_ROOT2() {
		String[] args = new String[]{"--root", "/test"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getSourceRootPath().equals("/test"));
	}

	@Test
	public void success_to_read_terminal_pattern_with_TERMINAL() {
		String[] args = new String[]{"-r", "/test", "-tc", "tt+", "-tc", "abc", "--terminal-class", "de*"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getTerminalClassPatterns().get(0).toString().equals("tt+"));
		assertTrue(appArgs.getTerminalClassPatterns().get(1).toString().equals("abc"));
		assertTrue(appArgs.getTerminalClassPatterns().get(2).toString().equals("de*"));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void failed_to_read_terminal_pattern_because_regexp_is_not_given() {
		String[] args = new String[]{"-r", "/test", "--terminal-class"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getTerminalClassPatterns().get(0).toString().equals("tt+"));
	}
}
