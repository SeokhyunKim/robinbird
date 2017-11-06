package org.robinbird.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by seokhyun on 6/30/17.
 */
public class AnalysisContextTest {

	AnalysisContext analysisContext;

	@Before
	public void setUp() {
		this.analysisContext = createTestAnalysisContext();
	}

	private AnalysisContext createTestAnalysisContext() {
		AnalysisContext analysisContext = new AnalysisContext();
		analysisContext.registerType("test1");
		analysisContext.registerType("test2");
		analysisContext.registerClass("Class1", ClassType.CLASS);
		analysisContext.registerClass("Class2", ClassType.CLASS);
		analysisContext.registerClass("Interface1", ClassType.INTERFACE);
		return analysisContext;
	}

	@Test
	public void registered_types_are_returned_well() {
		assertNotNull(analysisContext.getType("test1"));
		assertNotNull(analysisContext.getType("test2"));
	}

	@Test
	public void return_null_for_unregistered_type() {
		assertNull(analysisContext.getType("unregistered"));
	}

	@Test
	public void can_return_already_registered_type() {
		assertTrue(analysisContext.getType("test1") == analysisContext.registerType("test1"));
	}

	@Test
	public void registered_classes_are_returned_well() {
		assertNotNull(analysisContext.getClass("Class1", ClassType.CLASS));
		assertNotNull(analysisContext.getClass("Class1"));
		assertNull(analysisContext.getClass("Class1", ClassType.INTERFACE));
		assertNotNull(analysisContext.getClass("Interface1", ClassType.INTERFACE));
		assertNotNull(analysisContext.getClass("Interface1"));
		assertNull(analysisContext.getClass("Interface1", ClassType.CLASS));
	}

	@Test
	public void can_return_already_registered_class() {
		assertTrue(analysisContext.getClass("Class1") == analysisContext.registerClass("Class1", ClassType.CLASS));
	}

	@Test
	public void getTypes_returns_list_of_correct_size() {
		assertTrue(analysisContext.getTypes().size() == 5);
	}

	@Test
	public void getClasses_returns_list_of_correct_size() {
		assertTrue(analysisContext.getClasses().size() == 3);
	}

	@Test
	public void test_isTerminal() {
		AnalysisContext analysisContext = createTestAnalysisContext();
		analysisContext.setTerminalClassPatterns(Arrays.asList(Pattern.compile("ABC")));
		assertTrue(analysisContext.isTerminal("ABC"));
		assertFalse(analysisContext.isTerminal("DEF"));
		analysisContext.setTerminalClassPatterns(null);
		assertFalse(analysisContext.isTerminal("ABC"));
	}

	@Test
	public void test_isCurrentClassTerminal() {
		AnalysisContext analysisContext = createTestAnalysisContext();
		analysisContext.pushCurrentClass(analysisContext.getClass("Class1"));
		analysisContext.setTerminalClassPatterns(Arrays.asList(Pattern.compile("Class\\d+")));
		assertTrue(analysisContext.isCurrentClassTerminal());
		analysisContext.popCurrentClass();

		analysisContext.pushCurrentClass(null);
		analysisContext.setTerminalClassPatterns(null);
		assertFalse(analysisContext.isCurrentClassTerminal());
	}

	@Test
	public void test_isExcluded() {
		AnalysisContext analysisContext = createTestAnalysisContext();
		analysisContext.setExcludedClassPatterns(Arrays.asList(Pattern.compile("ABC")));
		assertTrue(analysisContext.isExcluded("ABC"));
		assertFalse(analysisContext.isExcluded("DEF"));

		analysisContext.setExcludedClassPatterns(null);
		assertFalse(analysisContext.isExcluded("ABC"));
	}

	@Test
	public void test_isCurrentClassExcluded() {
		AnalysisContext analysisContext = createTestAnalysisContext();
		analysisContext.pushCurrentClass(analysisContext.getClass("Class1"));
		analysisContext.setExcludedClassPatterns(Arrays.asList(Pattern.compile("Class\\d+")));
		assertTrue(analysisContext.isCurrentClassExcluded());
		analysisContext.popCurrentClass();

		analysisContext.pushCurrentClass(null);
		analysisContext.setExcludedClassPatterns(null);
		assertFalse(analysisContext.isCurrentClassExcluded());
	}

	@Test(expected = IllegalStateException.class)
	public void if_getRelations_is_called_before_update_then_throw_IllegalStateException() {
		analysisContext.getRelations();
	}

	@Test
	public void returns_relations_after_calling_update() {
		AnalysisContext analysisContext = new AnalysisContext();
		analysisContext.update();
		assertNotNull(analysisContext.getRelations());
	}

	@Test
	public void update_do_not_consider_terminal_class() {
		AnalysisContext analysisContext = createTestAnalysisContext();
		Class c = analysisContext.getClass("Class1");
		c.addMember(new Member(AccessModifier.PUBLIC, analysisContext.getClass("Class2"), "test"));
		c = analysisContext.getClass("Class2");
		c.addMember(new Member(AccessModifier.PUBLIC, analysisContext.getClass("Class1"), "test"));

		analysisContext.setTerminalClassPatterns(Arrays.asList(Pattern.compile("Class1")));
		analysisContext.update();
		Set<Relation> relations = analysisContext.getRelations();
		assertTrue(relations.size() == 1);
	}

	@Test
	public void when_a_member_variable_is_collection_cardinality_and_related_type_are_extracted_correctly() {
		AnalysisContext analysisContext = createTestAnalysisContext();
		Class c = analysisContext.getClass("Class1");
		c.addMember(new Member(AccessModifier.PUBLIC, new Collection("collectionTest", Arrays.asList(analysisContext.getClass("Class2"))), "collectionTest"));
		analysisContext.update();
		Set<Relation> relations = analysisContext.getRelations();
		assertTrue(relations.size() == 1);
		Iterator<Relation> itr = relations.iterator();
		assertTrue(itr.next().getSecondCardinality().equals("*"));
	}

	@Test(expected = IllegalStateException.class)
	public void when_Collection_has_no_type_IllegalStateException_is_thrown() {
		AnalysisContext analysisContext = createTestAnalysisContext();
		Class c = analysisContext.getClass("Class1");
		c.addMember(new Member(AccessModifier.PUBLIC, new Collection("collectionTest", new ArrayList<Type>()), "collectionTest"));
		analysisContext.update();
	}

	@Test
	public void when_associated_type_isExcluded_relation_creation_is_skipped() {
		AnalysisContext analysisContext = createTestAnalysisContext();
		Class c = analysisContext.getClass("Class1");
		c.addMember(new Member(AccessModifier.PUBLIC, new Collection("collectionTest", Arrays.asList(analysisContext.getClass("Class2"))), "collectionTest"));
		analysisContext.setExcludedClassPatterns(Arrays.asList(Pattern.compile("Class2")));
		analysisContext.update();
		Set<Relation> relations = analysisContext.getRelations();
		assertTrue(relations.size() == 0);
	}

	@Test
	public void test_registerPackage() {
		AnalysisContext analysisContext = createTestAnalysisContext();
		analysisContext.registerPackage(Arrays.asList("org", "robinbird", "test1"));
		analysisContext.registerPackage(Arrays.asList("org", "robinbird", "test1"));
		analysisContext.registerPackage(Arrays.asList("org", "robinbird", "test2"));

		assertTrue(analysisContext.getPackage("org.robinbird.test1") != null);
		assertTrue(analysisContext.getPackages().size() == 2);
	}
}
