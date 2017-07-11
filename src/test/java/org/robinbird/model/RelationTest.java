package org.robinbird.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 7/1/17.
 */
public class RelationTest {

	@Test
	public void create_relation_with_lexicographical_order_of_type_names() {
		Relation r1 = Relation.create(new Type("A_should_be_1st", Type.Kind.DEFINED), new Type("B_should_be_2nd", Type.Kind.DEFINED));
		Relation r2 = Relation.create(new Type("B_should_be_2nd", Type.Kind.DEFINED), new Type("A_should_be_1st", Type.Kind.DEFINED));
		assertTrue(r1.getFirst().getName() == "A_should_be_1st");
		assertTrue(r1.getSecond().getName() == "B_should_be_2nd");
		assertTrue(r2.getFirst().getName() == "A_should_be_1st");
		assertTrue(r2.getSecond().getName() == "B_should_be_2nd");
	}

	@Test
	public void create_Key_fns_make_Keys_based_on_alphabetical_order_of_names() {
		Relation.Key k1 = Relation.createKey(new Type("BType", Type.Kind.DEFINED), new Type("AType", Type.Kind.DEFINED));
		Relation.Key k2= Relation.createKey("AType", "BType");
		assertTrue(k1.equals(k2));
	}

	@Test
	public void create_and_createKey_make_same_Keys() {
		Relation r = Relation.create(new Type("BType", Type.Kind.DEFINED), new Type("AType", Type.Kind.DEFINED));
		Relation.Key k = Relation.createKey("AType", "BType");
		assertTrue(r.getKey().equals(k));
	}
}
