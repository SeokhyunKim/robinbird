package org.robinbird.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.robinbird.model.AnalysisUnit.Language.JAVA8;

/**
 * Created by seokhyun on 6/3/17.
 */
public class AnalysisUnitTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private static final String CONTENT1 = "public class Test1 {}";
	private static final String CONTENT2 = "public interface Test2 {}";

	@Test
	public void can_analysis_files() throws IOException {
		String path1 = writeStringToFile(CONTENT1, tempFolder.getRoot().getAbsolutePath(), "Test1.java");
		String path2 = writeStringToFile(CONTENT2, tempFolder.getRoot().getAbsolutePath(), "Test2.java");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(path1));
		au.addPath(Paths.get(path2));
		au.analysis();

		assertTrue(au.getClasses().size() == 2);
		assertTrue(au.getClasses().getRepositable(0).getName().equals("Test1"));
		assertTrue(au.getClasses().getRepositable(0).getClassType() == ClassType.CLASS);
		assertTrue(au.getClasses().getRepositable(1).getName().equals("Test2"));
		assertTrue(au.getClasses().getRepositable(1).getClassType() == ClassType.INTERFACE);
	}

	@Test
	public void can_analysis_folder() throws IOException {
		File testFolder = tempFolder.newFolder("test");
		String path1 = writeStringToFile(CONTENT1, testFolder.getAbsolutePath(), "Test1.java");
		String path2 = writeStringToFile(CONTENT2, testFolder.getAbsolutePath(), "Test2.java");

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(testFolder.getAbsolutePath()));
		au.analysis();

		assertTrue(au.getClasses().size() == 2);
		assertTrue(au.getClasses().getRepositable(0).getName().equals("Test1"));
		assertTrue(au.getClasses().getRepositable(0).getClassType() == ClassType.CLASS);
		assertTrue(au.getClasses().getRepositable(1).getName().equals("Test2"));
		assertTrue(au.getClasses().getRepositable(1).getClassType() == ClassType.INTERFACE);
	}

	private String writeStringToFile(String content, String root, String fileName) throws IOException {
		String path = root + "/" + fileName;
		FileWriter fw = new FileWriter(path);
		fw.write(content);
		fw.flush();
		return path;
	}


}
