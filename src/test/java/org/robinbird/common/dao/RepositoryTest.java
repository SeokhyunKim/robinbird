package org.robinbird.common.dao;

import org.junit.Test;
import org.robinbird.code.model.Class;
import org.robinbird.code.model.ClassType;
import org.robinbird.code.model.Collection;
import org.robinbird.code.model.Type;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by seokhyun on 6/2/17.
 */
public class RepositoryTest {

	@Test
	public void create_repositables_with_no_issue() {
		RobinBirdObject r = new RobinBirdObject("test");
		assertTrue(r.getName().equals("test"));
		assertTrue(r.getId() == 0);

		Type t =  new Type("test", Type.Kind.REFERENCE);
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

	@Test
	public void can_find_repositable_with_name() {
		Repository<RobinBirdObject> repo = new Repository<>();
		repo.register(new RobinBirdObject("test3"));
		repo.register(new RobinBirdObject("test2"));
		repo.register(new RobinBirdObject("test1"));
		assertNotNull(repo.getRepositable("test1"));
		assertNotNull(repo.getRepositable("test2"));
		assertNotNull(repo.getRepositable("test3"));
	}

	@Test
	public void can_return_already_existing_repositable() {
		Repository<RobinBirdObject> repo = new Repository<>();
		RobinBirdObject r1 = repo.register(new RobinBirdObject("test1"));
		RobinBirdObject r2 = repo.register(new RobinBirdObject("test1"));
		assertTrue(r1 == r2);
	}

	@Test
	public void return_null_for_not_existing_repositable() {
		Repository<RobinBirdObject> repo = new Repository<>();
		assertNull(repo.getRepositable("test"));
		repo.register(new RobinBirdObject("test"));
		assertNull(repo.getRepositable("wrong_name"));
	}

	@Test
	public void return_null_with_wrong_indicies() {
		Repository<RobinBirdObject> repo = new Repository<>();
		repo.register(new RobinBirdObject("test"));
		assertNull(repo.getRepositable(-1));
		assertNull(repo.getRepositable(1));
	}

	@Test
	public void saved_in_the_order_of_insertions() {
		Repository<RobinBirdObject> repo = new Repository<>();
		repo.register(new RobinBirdObject("test3"));
		repo.register(new RobinBirdObject("test2"));
		repo.register(new RobinBirdObject("test1"));
		assertTrue(repo.getRepositable(0).getName().equals("test3"));
		assertTrue(repo.getRepositable(1).getName().equals("test2"));
		assertTrue(repo.getRepositable(2).getName().equals("test1"));
	}

	@Test
	public void cleaned_well() {
		Repository<RobinBirdObject> repo = new Repository<>();
		repo.register(new RobinBirdObject("test3"));
		repo.register(new RobinBirdObject("test2"));
		repo.register(new RobinBirdObject("test1"));
		repo.clean();
		assertTrue(repo.getRepositableList().size() == 0);
	}

	@Test
	public void isExisting_working_well() {
		Repository<RobinBirdObject> repo = new Repository<>();
		repo.register(new RobinBirdObject("test3"));
		repo.register(new RobinBirdObject("test2"));
		assertFalse(repo.isExisting("test1"));
		assertTrue(repo.isExisting("test2"));
		assertTrue(repo.isExisting("test3"));
	}
}