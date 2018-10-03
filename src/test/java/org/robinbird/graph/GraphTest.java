package org.robinbird.graph;

import org.junit.Before;
import org.junit.Test;
import org.robinbird.main.model.AccessModifier;
import org.robinbird.main.model.Class;
import org.robinbird.main.model.Member;
import org.robinbird.main.model.Type;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 11/11/17.
 */
public class GraphTest {

	private List<Class> classes;
	private Graph g;

	@Before
	public void setup() {
		Class c1 = new Class("Class1");
		Class c2 = new Class("Class2");
		Class c3 = new Class("Class3");
		c2.setParent(c1);
		c2.addInterface(c3);
		c1.addMember(new Member(AccessModifier.PUBLIC, c3, "test"));
		c2.addMember(new Member(AccessModifier.PRIVATE, new Type("int", Type.Kind.PRIMITIVE), "test"));
		classes = Arrays.asList(c1, c2, c3);
		g = Graph.createGraphFromClasses(classes);
	}

	@Test
	public void test_graph_creation() {
		assertTrue(g.getNodes().size() == 3);
		assertTrue(g.getNodes().stream().mapToInt(n -> n.getEdges().size()).sum() == 6);
	}

	@Test
	public void getNode_can_return_correct_node() {
		assertTrue(g.getNode("Class1").getName() == "Class1");
	}

	@Test(expected = NullPointerException.class)
	public void when_null_is_given_to_getNode_then_exception_is_thrown() {
		g.getNode(null);
	}

	@Test(expected = NullPointerException.class)
	public void when_null_is_given_to_createNode_then_exception_is_thrown() {
		g.createNode(null);
	}
}
