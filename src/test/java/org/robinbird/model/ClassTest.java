package org.robinbird.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 7/1/17.
 */
public class ClassTest {

	@Test
	public void test_to_string() {
		Class c = new Class("test");
		c.addInterface(new Class("Interface1", ClassType.INTERFACE));
		String c_str = c.toString();
		c_str.contains("id=0, name=test");
		c_str.contains("classType=CLASS");
		c_str.contains("parent=null");
		c_str.contains("interfaces=[Class");
	}

	/* TO DO: don't know whether deleting this one is good or not
	@Test(expected = IllegalStateException.class)
	public void throws_IllegalStateException_when_noninterface_Class_is_added_as_interface() {
		Class c = new Class("test");
		c.addInterface(new Class("Class1", ClassType.CLASS));
	}
	*/

	@Test
	public void test_addMember_and_addMemberFunction() {
		Class c = new Class("test");
		Member m = new Member(AccessModifier.PUBLIC, new Type("TestType", Type.Kind.DEFINED), "testMember");
		MemberFunction f = new MemberFunction(AccessModifier.PUBLIC, new Type("TestType", Type.Kind.DEFINED), "testFn");
		c.addMember(m);
		c.addMemberFunction(f);
		assertTrue(c.getMemberVariables().size() == 1);
		assertTrue(c.getMemberFunctions().size() == 1);
	}

	@Test(expected = IllegalStateException.class)
	public void when_member_name_is_empty_IllegalStateException_is_thrown() {
		Class c = new Class("test");
		Member m = new Member(AccessModifier.PUBLIC, new Type("TestType", Type.Kind.DEFINED), "");
		c.addMember(m);
	}

	@Test(expected = IllegalStateException.class)
	public void when_memger_function_name_is_empty_IllegalStateException_is_thrown() {
		Class c = new Class("test");
		MemberFunction f = new MemberFunction(AccessModifier.PUBLIC, new Type("TestType", Type.Kind.DEFINED), "");
		c.addMemberFunction(f);
	}
}
