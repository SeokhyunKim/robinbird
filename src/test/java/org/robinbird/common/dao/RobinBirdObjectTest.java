package org.robinbird.common.dao;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

/**
 * Created by seokhyun on 8/28/17.
 */
public class RobinBirdObjectTest {

	@Test
	public void test_equals_and_hashcode() {
		EqualsVerifier.forClass(RobinBirdObject.class).withIgnoredFields("id").verify();

	}

	@Test
	public void test_toString() {
		RobinBirdObject r = new RobinBirdObject("test");
		ToStringVerifier.forClass(RobinBirdObject.class).ignore("$jacocoData").containsAllPrivateFields(r);
	}
}
