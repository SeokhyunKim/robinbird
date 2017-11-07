package org.robinbird.code.model;

import org.junit.Test;
import org.robinbird.TestUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robinbird.code.model.AnalysisUnit.Language.JAVA8;

/**
 * Created by seokhyun on 6/3/17.
 */
public class AnalysisUnitTest {

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
	public void can_recognize_excluded_pattern_file() {
		String path = getTestPath("/can_recognize_excluded_pattern_file");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(null, Arrays.asList(Pattern.compile("Excluded.*")));

		assertNotNull(ac.getClass("Class1"));
		assertNull(ac.getClass("Excluded"));
		assertNull(ac.getClass("ExcludedInterface"));
	}

	@Test
	public void terminal_class_does_not_have_any_member() {
		String path = getTestPath("/terminal_class_does_not_have_any_member");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(Arrays.asList(Pattern.compile("Terminal")), null);

		Class t = ac.getClass("Terminal");
		assertTrue(t.getMemberVariables().size() == 0);
		assertTrue(t.getMemberFunctions().size() == 0);
	}

	@Test
	public void files_with_extensions_other_than_java_are_skipped() {
		String path = getTestPath("/files_with_extensions_other_than_java_are_skipped");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(null, null);

		assertTrue(ac.getClasses().size() == 1);
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

	@Test
	public void inner_classes_are_ignored() {
		String path = getTestPath("/ignore_inner_classes");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(null, null);

		assertTrue(ac.getClasses().size() == 1);
	}

	@Test
	public void can_read_member_functions() {
		String path = getTestPath("/method_test");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		AnalysisContext ac = au.analysis(null, null);

		assertTrue(ac.getClass("TestClass").getMemberFunctions().size() == 4);
		assertTrue(ac.getClass("TestClass2").getMemberFunctions().size() == 1);
	}

	private String getTestPath(String path) {
		return TestUtils.getTestPath("/AnalysisUnitTest"+path);
	}
}
