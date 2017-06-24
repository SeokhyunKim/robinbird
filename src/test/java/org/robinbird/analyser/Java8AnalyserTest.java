package org.robinbird.analyser;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by seokhyun on 6/23/17.
 */
public class Java8AnalyserTest {

	@Before
	public void setup() {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
	}

	@Test
	public void dummy() {

	}
}
