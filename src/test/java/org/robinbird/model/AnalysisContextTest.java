package org.robinbird.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 6/30/17.
 */
public class AnalysisContextTest {

	AnalysisContext analysisContext;

	@Before
	public void setup() {
		analysisContext = new AnalysisContext();
		analysisContext.registerType("test1");
		analysisContext.registerType("test2");
		analysisContext.registerClass("Class1", ClassType.CLASS);
		analysisContext.registerClass("Class2", ClassType.CLASS);
		analysisContext.registerClass("Interface1", ClassType.INTERFACE);
	}

	@Test
	public void registered_type_is_returned_well() {
		assertNotNull(analysisContext.getType("test1"));
		assertNotNull(analysisContext.getType("test2"));
	}

	@Test
	public void return_null_for_unregistered_type() {
		assertNull(analysisContext.getType("unregistered"));
	}

	@Test
	public void registered_class_is_returned_well() {
		assertNotNull(analysisContext.getClass("Class1", ClassType.CLASS));
		assertNotNull(analysisContext.getClass("Class1"));
		assertNull(analysisContext.getClass("Class1", ClassType.INTERFACE));
		assertNotNull(analysisContext.getClass("Interface1", ClassType.INTERFACE));
		assertNotNull(analysisContext.getClass("Interface1"));
		assertNull(analysisContext.getClass("Interface1", ClassType.CLASS));
	}

	@Test
	public void getTypes_returns_list_of_correct_size() {
		assertTrue(analysisContext.getTypes().size() == 5);
	}

	@Test
	public void getClasses_returns_list_of_correct_size() {
		assertTrue(analysisContext.getClasses().size() == 3);
	}
}
