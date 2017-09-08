package org.robinbird.model;

import be.joengenduvel.java.verifiers.ToStringVerifier;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by seokhyun on 9/8/17.
 */
public class CollectionTest {

	@Test
	public void test_toString() {
		Collection c = new Collection("test", Arrays.asList(new Type("int", Type.Kind.PRIMITIVE)));
		ToStringVerifier.forClass(Repositable.class).ignore("$jacocoData").containsAllPrivateFields(c);
	}
}
