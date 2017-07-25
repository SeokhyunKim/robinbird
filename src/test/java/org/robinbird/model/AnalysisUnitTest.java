package org.robinbird.model;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robinbird.model.AnalysisUnit.Language.JAVA8;

/**
 * Created by seokhyun on 6/3/17.
 */
public class AnalysisUnitTest {

	private String rootTestPath;

	@Before
	public void setup() {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		rootTestPath = s + "/src/test/resources/test_directory/AnalysisUnitTest";
	}

	@Test
	public void can_analysis_files() throws IOException {
		String path1 = getTestPath("/can_analysis_files/Test1.java");
		String path2 = getTestPath("/can_analysis_files/Test2.java");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path1));
		au.addPath(Paths.get(path2));
		AnalysisContext ac = au.analysis(null, null);

		assertTrue(ac.getTypes().size() == 2);
		assertTrue(ac.getTypes().get(0).getName().equals("Test1"));
		Class c1 = (Class)ac.getTypes().get(0);
		assertTrue(c1.getClassType() == ClassType.CLASS);
		assertTrue(ac.getTypes().get(1).getName().equals("Test2"));
		Class c2 = (Class)ac.getTypes().get(1);
		assertTrue(c2.getClassType() == ClassType.INTERFACE);
	}

	@Test
	public void can_analysis_folder() throws IOException {
		String path = getTestPath("/can_analysis_folder/test");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(null, null);

		assertTrue(ac.getTypes().size() == 2);
		assertTrue(ac.getTypes().get(0).getName().equals("Test1"));
		Class c1 = (Class)ac.getTypes().get(0);
		assertTrue(c1.getClassType() == ClassType.CLASS);
		assertTrue(ac.getTypes().get(1).getName().equals("Test2"));
		Class c2 = (Class)ac.getTypes().get(1);
		assertTrue(c2.getClassType() == ClassType.INTERFACE);
	}

	@Test
	public void can_read_independent_classes_and_interfaces() {
		String path = getTestPath("/can_read_independent_classes_and_interfaces");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(null, null);

		assertNotNull(ac.getClass("Class1", ClassType.CLASS));
		assertNotNull(ac.getClass("Class2", ClassType.CLASS));
		assertNotNull(ac.getClass("Interface1", ClassType.INTERFACE));
		assertNotNull(ac.getClass("Interface2", ClassType.INTERFACE));
		assertNotNull(ac.getClass("Interface3", ClassType.INTERFACE));
	}

	@Test
	public void can_read_member_variables() {
		// will add later
	}

	@Test
	public void can_read_member_functions_for_class() {
		// will add later
	}

	@Test
	public void can_read_member_functions_for_interface() {
		// will add later
	}

	@Test
	public void relation_test__can_recognize_associated_interface() {
		String path = getTestPath("/relation_test/can_recognize_associated_interface");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(null, null);

		assertNotNull(ac.getClass("Class1", ClassType.CLASS));
		assertNotNull(ac.getClass("Interface1", ClassType.INTERFACE));
	}

	@Test
	public void can_read_package() {
		String path = getTestPath("/can_read_package");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(null, null);

		assertNotNull(ac.getPackage("com.test.module"));
	}

	@Test
	public void enum_test() {
		String path = getTestPath("/can_process_enum_type");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(null, null);
	}

	private String getTestPath(String testPath) {
		return rootTestPath + testPath;
	}


}
