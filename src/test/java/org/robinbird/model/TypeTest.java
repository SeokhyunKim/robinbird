package org.robinbird.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 7/1/17.
 */
public class TypeTest {

	@Test(expected = NullPointerException.class)
	public void cannot_create_Type_with_null_name() {
		new Type(null, Type.Kind.DEFINED);
	}

	@Test
	public void test_Kind_checking_functions() {
		Type t1 = new Type("type", Type.Kind.PRIMITIVE);
		assertTrue(t1.isPrimitiveType());
		Type t2 = new Type("type", Type.Kind.DEFINED);
		assertTrue(t2.isDefinedType());
	}

}
