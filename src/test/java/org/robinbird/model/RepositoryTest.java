package org.robinbird.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 6/2/17.
 */
public class RepositoryTest {

	@Test
	public void create_repositables_with_no_issue() {
		Repositable r = new Repositable("test");
		assertTrue(r.getName().equals("test"));
		assertTrue(r.getId() == 0);

		Type t =  new Type("test");
		assertTrue(t.getName().equals("test"));
		assertTrue(t.getId() == 0);

		Collection c = new Collection("test", t);
		assertTrue(t.getName().equals("test"));
		assertTrue(c.getType() == t);

		Map m = new Map("test", t, c);
		assertTrue(m.getName().equals("test"));
		assertTrue(m.getKey() == t);
		assertTrue(m.getValue() == c);

		Class cl = new Class("test");
		assertTrue(cl.getClassType() == ClassType.CLASS);
	}
}
