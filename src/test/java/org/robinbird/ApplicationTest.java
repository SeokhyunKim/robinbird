package org.robinbird;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;
import static org.robinbird.TestUtils.getTestPath;

/**
 * Created by seokhyun on 10/27/17.
 */
public class ApplicationTest {

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
	}

	@Test
	public void testApplication_with_SIMPLE() {
		ByteArrayOutputStream testOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(testOut));
		String[] args = new String[] { "-r", getTestPath("/ApplicationTest"), "-p", "SIMPLE" };
		Application.main(args);
		String output = testOut.toString();
		System.out.println(output);
		assertTrue(output.contains("Packages"));
		assertTrue(output.contains("org.robinbird.test"));
		assertTrue(output.contains("Classes"));
		assertTrue(output.contains("a : int"));
		assertTrue(output.contains("b : float"));
		assertTrue(output.contains("Types"));
		assertTrue(output.contains("SimpleClass"));
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
}
