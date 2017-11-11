package org.robinbird.common.model;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by seokhyun on 8/28/17.
 */
public class RepositableTest {

	@Test
	public void test_equals_and_hashcode() {
		EqualsVerifier.forClass(Repositable.class).withIgnoredFields("id").verify();

	}

	@Test
	public void test_toString() {
		Repositable r = new Repositable("test");
		ToStringVerifier.forClass(Repositable.class).ignore("$jacocoData").containsAllPrivateFields(r);
	}
}
