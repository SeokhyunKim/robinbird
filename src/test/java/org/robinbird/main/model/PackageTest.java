package org.robinbird.main.model;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 9/8/17.
 */
public class PackageTest {

	@Test
	public void test_toString() {
		Package p = new Package(Arrays.asList("org", "robinbird", "test"));
		ToStringVerifier.forClass(RobinbirdObject.class).ignore("$jacocoData").containsAllPrivateFields(p);
	}

	@Test(expected = IllegalStateException.class)
	public void when_empty_list_is_given_to_Package_IllegalStateException_is_thrown() {
		new Package(new ArrayList<String>());
	}

	@Test
	public void test_adding_and_getting_class_list() {
		Package p = new Package(Arrays.asList("org", "robinbird", "test"));
		p.addClass(new Class("test"));
		assertTrue(p.getClassList().size() == 1);
	}
}
