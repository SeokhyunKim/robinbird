package org.robinbird.common.utils;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.robinbird.common.utils.Msgs.Key.ALREADY_EXISTING_TYPE_NAME;
import static org.robinbird.common.utils.Msgs.Key.ROOT_SOURCE_PATH_NOT_PROVIDED;

/**
 * Created by seokhyun on 9/8/17.
 */
public class MsgsTest {

	@Test
	public void when_cause_exception_is_provided_it_should_be_included_in_the_msg() {
		String msg = Msgs.get(ROOT_SOURCE_PATH_NOT_PROVIDED, new Exception());
		assertTrue(msg.contains("Exception"));

	}

	@Test
	public void when_cause_exception_and_msg_are_provided_those_should_be_included_in_the_msg() {
		String msg = Msgs.get(ALREADY_EXISTING_TYPE_NAME, new Exception(), "test");
		assertTrue(msg.contains("Exception"));
		assertTrue(msg.contains("test"));

	}
}
