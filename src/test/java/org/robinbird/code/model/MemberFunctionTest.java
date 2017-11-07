package org.robinbird.code.model;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 7/1/17.
 */
public class MemberFunctionTest {

	@Test
	public void test_create_member_function_without_arguments() {
		MemberFunction f = new MemberFunction(AccessModifier.PUBLIC, new Type("String", Type.Kind.REFERENCE), "test1");
		assertTrue(f.getAccessModifier() == AccessModifier.PUBLIC);
		assertTrue(f.getName().equals("test1"));
		assertTrue(f.getType().getName() == "String");
		assertTrue(f.getType().getKind() == Type.Kind.REFERENCE);
		assertTrue(f.getName() == "test1");
	}

	@Test
	public void test_create_member_function_with_arguments() {
		MemberFunction f = createTestMemberFunction();
		assertTrue( f.getArguments().get(0).getName() == "Integer");
		assertTrue( f.getArguments().get(1).getName() == "Class1");
	}

	@Test
	public void test_equals_and_hashCode() {
		class UTMemberFunction extends MemberFunction {
			public UTMemberFunction(AccessModifier accessModifier, Type type, String name, List<Type> arguments) {
				super(accessModifier, type, name, arguments);
			}
			@Override
			protected boolean canEqual(Object other) {
				return false;
			}
		}
		EqualsVerifier.forClass(MemberFunction.class)
			.withRedefinedSuperclass()
			.withRedefinedSubclass(UTMemberFunction.class).verify();
	}

	@Test
	public void test_toString() {
		MemberFunction f = createTestMemberFunction();
		ToStringVerifier.forClass(MemberFunction.class).ignore("$jacocoData").containsAllPrivateFields(f);
	}

	private MemberFunction createTestMemberFunction() {
		return new MemberFunction(	AccessModifier.PUBLIC,
			new Type("String", Type.Kind.REFERENCE),
			"test1",
			Arrays.asList(new Type("Integer", Type.Kind.PRIMITIVE), new Class("Class1")));
	}
}
