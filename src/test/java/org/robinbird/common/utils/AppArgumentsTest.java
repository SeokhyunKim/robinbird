package org.robinbird.common.utils;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import org.junit.Test;
import org.robinbird.code.presentation.PresentationType;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.robinbird.common.utils.Msgs.Key.ROOT_SOURCE_PATH_NOT_PROVIDED;

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

	@Test
	public void success_to_read_excluded_pattern_with_EXCLUDED() {
		String[] args = new String[]{"-r", "/test", "-ec", "tt+", "-ec", "abc", "--excluded-class", "de*"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getExcludedClassPatterns().get(0).toString().equals("tt+"));
		assertTrue(appArgs.getExcludedClassPatterns().get(1).toString().equals("abc"));
		assertTrue(appArgs.getExcludedClassPatterns().get(2).toString().equals("de*"));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void failed_to_read_excluded_pattern_because_regexp_is_not_given() {
		String[] args = new String[]{"-r", "/test", "--excluded-class"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getExcludedClassPatterns().get(0).toString().equals("tt+"));
	}

	@Test
	public void when_invalid_ArgType_is_given_it_is_ignored() {
		String[] args = new String[]{"-r", "/test", "invalid", "--terminal-class", "tt+"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getTerminalClassPatterns().get(0).toString().equals("tt+"));
	}

	@Test
	public void can_read_presentation_type() {
		String[] args = new String[]{"-r", "/test", "--presentation", "SIMPLE"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getPresentationType().equals(PresentationType.SIMPLE));
	}

	@Test
	public void when_wrong_presentation_type_is_given_use_default_type() {
		String[] args = new String[]{"-r", "/test", "--presentation", "WRONG"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getPresentationType().equals(PresentationType.PLANTUML));
	}

	@Test
	public void when_presentation_type_is_not_given_use_default_type() {
		String[] args = new String[]{"-r", "/test", "--presentation"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getPresentationType().equals(PresentationType.PLANTUML));
	}

	@Test
	public void when_wrong_pattern_is_given_it_is_ignored_with_warn_message() {
		String[] args = new String[]{"-r", "/test", "invalid", "--terminal-class", "+"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		assertTrue(appArgs.getTerminalClassPatterns().size() == 0);
	}

	@Test
	public void test_toString() {
		String[] args = new String[]{"-r", "/test"};
		AppArguments appArgs = AppArguments.parseArguments(args);
		ToStringVerifier.forClass(AppArguments.class).ignore("$jacocoData", "log").containsAllPrivateFields(appArgs);
	}
}
