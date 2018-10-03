package org.robinbird.main.model;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 9/8/17.
 */
public class CollectionTest {

	@Test
	public void test_toString() {
		Collection c = new Collection("test", Arrays.asList(new Type("int", Type.Kind.PRIMITIVE)));
		ToStringVerifier.forClass(RobinbirdObject.class).ignore("$jacocoData").containsAllPrivateFields(c);
	}

	@Test
	public void getAssociatedType_returns_last_Type() {
		Collection c = new Collection("test",
			Arrays.asList(new Type("int", Type.Kind.PRIMITIVE), new Type("TestClass", Type.Kind.REFERENCE)));
		assertTrue(c.getAssociatedType().getName() == "TestClass");
	}

	@Test(expected = IllegalStateException.class)
	public void when_Collection_has_no_Types_getAssociateType_throws_an_exception() {
		Collection c =  new Collection("test", new ArrayList<>());
		c.getAssociatedType();
	}
}
