package org.robinbird.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

		List<Type> types = new ArrayList<>();
		Collection c = new Collection("test", types);
		assertTrue(t.getName().equals("test"));
		assertTrue(c.getTypes() == types);

		Class cl = new Class("test");
		assertTrue(cl.getClassType() == ClassType.CLASS);
		assertTrue(cl.getMemberFunctions() != null);
		assertTrue(cl.getMemberVariables() != null);
	}
}
