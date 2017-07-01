package org.robinbird.model;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 7/1/17.
 */
public class MemberFunctionTest {

	@Test
	public void test_create_member_function_without_arguments() {
		MemberFunction f = new MemberFunction(AccessModifier.PUBLIC, new Type("String", Type.Kind.DEFINED), "test1");
		assertTrue(f.getAccessModifier() == AccessModifier.PUBLIC);
		assertTrue(f.getName().equals("test1"));
		assertTrue(f.getType().getName() == "String");
		assertTrue(f.getType().getKind() == Type.Kind.DEFINED);
		assertTrue(f.getName() == "test1");
	}

	@Test
	public void test_create_member_function_with_arguments() {
		MemberFunction f = new MemberFunction(	AccessModifier.PUBLIC,
												new Type("String", Type.Kind.DEFINED),
												"test1",
												Arrays.asList(new Type("Integer"), new Class("Class1")));
		assertTrue( f.getArguments().get(0).getName() == "Integer");
		assertTrue( f.getArguments().get(1).getName() == "Class1");
	}
}
