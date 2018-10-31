package org.robinbird.main.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 7/1/17.
 */
public class AccessModifierTest {
	@Test
	public void can_get_correct_descriptions() {
		assertTrue(AccessModifier.PUBLIC.getDescription().equals("public"));
		assertTrue(AccessModifier.PRIVATE.getDescription().equals("private"));
		assertTrue(AccessModifier.PROTECTED.getDescription().equals("protected"));
	}

	@Test
	public void fromDescription_works_well() {
		assertTrue(AccessModifier.fromDescription("public") == AccessModifier.PUBLIC);
		assertTrue(AccessModifier.fromDescription("private") == AccessModifier.PRIVATE);
		assertTrue(AccessModifier.fromDescription("protected") == AccessModifier.PROTECTED);
	}
}
