package org.robinbird.main.model;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 8/23/17.
 */
public class MemberTest {

	@Test
	public void test_Constructor() {
		Member m = createTestMember();
		assertTrue(m.getAccessModifier() == AccessModifier.PUBLIC);
		assertTrue(m.getType().equals(new Type("test", Type.Kind.PRIMITIVE)));
		assertTrue(m.getName() == "test");
	}

	@Test(expected = NullPointerException.class)
	public void failed_with_null_modifier() {
		new Member(null, new Type("test", Type.Kind.PRIMITIVE), "test");
	}

	@Test(expected = NullPointerException.class)
	public void failed_with_null_type() {
		new Member(AccessModifier.PUBLIC, null, "test");
	}

	@Test(expected = NullPointerException.class)
	public void failed_with_null_name() {
		new Member(AccessModifier.PUBLIC, new Type("test", Type.Kind.PRIMITIVE), null);
	}

	@Test
	public void test_equals_and_hashCode() {
		EqualsVerifier.forClass(Member.class)
						.withIgnoredAnnotations(NonNull.class)
						.withRedefinedSubclass(MemberFunction.class).verify();
	}

	@Test
	public void test_toString() {
		Member m = createTestMember();
		ToStringVerifier.forClass(Member.class).ignore("$jacocoData").containsAllPrivateFields(m);
	}

	private Member createTestMember() {
		return new Member(AccessModifier.PUBLIC, new Type("test", Type.Kind.PRIMITIVE), "test");
	}
}
