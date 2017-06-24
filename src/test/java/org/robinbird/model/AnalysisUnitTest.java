package org.robinbird.model;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		au.analysis();

		assertTrue(au.getTypes().size() == 2);
		assertTrue(au.getTypes().getRepositable(0).getName().equals("Test1"));
		Class c1 = (Class)au.getTypes().getRepositable(0);
		assertTrue(c1.getClassType() == ClassType.CLASS);
		assertTrue(au.getTypes().getRepositable(1).getName().equals("Test2"));
		Class c2 = (Class)au.getTypes().getRepositable(1);
		assertTrue(c2.getClassType() == ClassType.INTERFACE);
	}

	@Test
	public void can_analysis_folder() throws IOException {
		String path = getTestPath("/can_analysis_folder/test");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path));
		au.analysis();

		assertTrue(au.getTypes().size() == 2);
		assertTrue(au.getTypes().getRepositable(0).getName().equals("Test1"));
		Class c1 = (Class)au.getTypes().getRepositable(0);
		assertTrue(c1.getClassType() == ClassType.CLASS);
		assertTrue(au.getTypes().getRepositable(1).getName().equals("Test2"));
		Class c2 = (Class)au.getTypes().getRepositable(1);
		assertTrue(c2.getClassType() == ClassType.INTERFACE);
	}

	private String getTestPath(String testPath) {
		return rootTestPath + testPath;
	}


}
