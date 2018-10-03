package org.robinbird.main.model;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 7/1/17.
 */
public class RelationTest {

	@Test
	public void create_relation_with_lexicographical_order_of_type_names() {
		Relation r1 = Relation.create(new Type("A_should_be_1st", Type.Kind.REFERENCE), new Type("B_should_be_2nd", Type.Kind.REFERENCE));
		Relation r2 = Relation.create(new Type("B_should_be_2nd", Type.Kind.REFERENCE), new Type("A_should_be_1st", Type.Kind.REFERENCE));
		assertTrue(r1.getFirst().getName() == "A_should_be_1st");
		assertTrue(r1.getSecond().getName() == "B_should_be_2nd");
		assertTrue(r2.getFirst().getName() == "A_should_be_1st");
		assertTrue(r2.getSecond().getName() == "B_should_be_2nd");
	}

	@Test
	public void create_Key_fns_make_Keys_based_on_alphabetical_order_of_names() {
		Relation.Key k1 = Relation.createKey(new Type("BType", Type.Kind.REFERENCE), new Type("AType", Type.Kind.REFERENCE));
		Relation.Key k2= Relation.createKey("AType", "BType");
		assertTrue(k1.equals(k2));
	}

	@Test
	public void create_and_createKey_make_same_Keys() {
		Relation r = Relation.create(new Type("BType", Type.Kind.REFERENCE), new Type("AType", Type.Kind.REFERENCE));
		Relation.Key k1 = Relation.createKey("AType", "BType");
		Relation.Key k2 = Relation.createKey("BType", "AType");
		assertTrue(r.getKey().equals(k1));
		assertTrue(r.getKey().equals(k2));
	}

	@Test
	public void can_set_cardinalities() {
		Relation r = Relation.create(new Type("BType", Type.Kind.REFERENCE), new Type("AType", Type.Kind.REFERENCE));
		r.setFirstCardinality("1");
		r.setSecondCardinality("1");
		assertTrue("1".equals(r.getFirstCardinality()));
		assertTrue("1".equals(r.getSecondCardinality()));
	}

	@Test
	public void check_equals_and_hashcode() {
		EqualsVerifier.forClass(Relation.Key.class).verify();
	}

	@Test
	public void check_toString() {
		Relation r = Relation.create(new Type("abc", Type.Kind.PRIMITIVE), new Type("def", Type.Kind.REFERENCE));
		ToStringVerifier.forClass(Relation.class).ignore("$jacocoData").containsAllPrivateFields(r);
		Relation.Key k = Relation.createKey("first", "second");
		ToStringVerifier.forClass(Relation.Key.class).ignore("$jacocoData").containsAllPrivateFields(k);
	}

	@Test(expected = IllegalStateException.class)
	public void create_relation_with_1st_null_param() {
		Relation.create(null, new Type("test", Type.Kind.REFERENCE));
	}

	@Test(expected = IllegalStateException.class)
	public void create_relation_with_2nd_null_param() {
		Relation.create(new Type("test", Type.Kind.REFERENCE), null);
	}

	@Test(expected = IllegalStateException.class)
	public void create_key_with_1st_null_param() {
		Relation.createKey(null, new Type("test", Type.Kind.REFERENCE));
	}

	@Test(expected = IllegalStateException.class)
	public void create_key_with_2nd_null_param() {
		Relation.createKey(new Type("test", Type.Kind.REFERENCE), null);
	}

	@Test(expected = IllegalStateException.class)
	public void create_key_with_1st_null_string() {
		Relation.createKey(null, "test");
	}

	@Test(expected = IllegalStateException.class)
	public void create_key_with_2nd_null_string() {
		Relation.createKey("test", null);
	}
}
