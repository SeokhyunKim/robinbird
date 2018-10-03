package org.robinbird.main.model;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.robinbird.main.model.RobinbirdObject;

/**
 * Created by seokhyun on 8/28/17.
 */
public class RobinbirdObjectTest {

	@Test
	public void test_equals_and_hashcode() {
		EqualsVerifier.forClass(RobinbirdObject.class).withIgnoredFields("id").verify();

	}

	@Test
	public void test_toString() {
		RobinbirdObject r = new RobinbirdObject("test");
		ToStringVerifier.forClass(RobinbirdObject.class).ignore("$jacocoData").containsAllPrivateFields(r);
	}
}
