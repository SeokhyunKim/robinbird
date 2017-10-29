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
	public void testApplication() {
		ByteArrayOutputStream testOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(testOut));
		String[] args = new String[]{"-r", getTestPath("/ApplicationTest")};
		Application.main(args);
		String output = testOut.toString();
		assertTrue(output.contains("@startuml"));
		assertTrue(output.contains("@enduml"));
		assertTrue(output.contains("package org.robinbird.test"));
		assertTrue(output.contains("class SimpleClass"));
		assertTrue(output.contains("- a : int"));
		assertTrue(output.contains("+ b : float"));
	}
}
