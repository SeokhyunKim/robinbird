package org.robinbird;

import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.robinbird.TestUtils.getTestPath;

@Ignore
public class ApplicationTest {

	@Test
	public void dbTest() {
		Application.main(null);
	}

	@Test
	public void testApplication_with_PLANTUML() {
		ByteArrayOutputStream testOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(testOut));
		String[] args = new String[] { "-r", getTestPath("/ApplicationTest")};
		Application.main(args);
		String output = testOut.toString();
		assertTrue(output.contains("@startuml"));
		assertTrue(output.contains("@enduml"));
		assertTrue(output.contains("package org.robinbird.test"));
		assertTrue(output.contains("class SimpleClass"));
		assertTrue(output.contains("- a : int"));
		assertTrue(output.contains("+ b : float"));
		assertTrue(output.contains("+ fn1(int) : int"));
		assertTrue(output.contains("# fn2(int) : int"));
		assertTrue(output.contains("- fn3(int) : SimpleClass"));
	}

	@Test
	public void testApplication_with_SIMPLE() {
		ByteArrayOutputStream testOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(testOut));
		String[] args = new String[] { "-r", getTestPath("/ApplicationTest"), "-p", "SIMPLE" };
		Application.main(args);
		String output = testOut.toString();
		assertTrue(output.contains("Packages"));
		assertTrue(output.contains("org.robinbird.test"));
		assertTrue(output.contains("Classes"));
		assertTrue(output.contains("a : int"));
		assertTrue(output.contains("b : float"));
		assertTrue(output.contains("Types"));
		assertTrue(output.contains("SimpleClass"));
		assertTrue(output.contains(""));
		assertTrue(output.contains(""));
		assertTrue(output.contains(""));
	}

	@Test
	public void testApplication_with_GML() {
		ByteArrayOutputStream testOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(testOut));
		String[] args = new String[] { "-r", getTestPath("/ApplicationTest"), "-p", "GML" };
		Application.main(args);
		String output = testOut.toString();
		assertTrue(output.contains("graph"));
		assertTrue(output.contains("node"));
		assertTrue(output.contains("id org.robinbird.test.SimpleClass"));
		assertTrue(output.contains("edge"));
		assertTrue(output.contains("source org.robinbird.test.SimpleClass"));
		assertTrue(output.contains("target org.robinbird.test.TestClass"));
	}

	@Ignore
	@Test
	public void infiniteCompilingCaseOfANTLR() {
		String[] args = new String[] { "-r", ppp("BlockManager.java")};
		Application.main(args);
	}

	private String ppp(String ss) {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		return s + "/src/test/resources/test_antlr/"+ss;
	}
}
