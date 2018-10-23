package org.robinbird.main.presentation;

import org.junit.Before;
import org.junit.Test;
import org.robinbird.main.model.AnalysisContext;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 10/27/17.
 */
public class SimplePresentationTest {

	private AnalysisContext analysisContext;

	@Before
	public void setUp() {
		analysisContext = PresentationTestUtils.createTestAnalysisContext();
	}

	@Test
	public void testPresent() {
		SimplePresentation presentation = new SimplePresentation();
		String present = presentation.present(analysisContext);
		assertTrue(present.contains("// Packages"));
		assertTrue(present.contains("com.test.pkg"));
		assertTrue(present.contains("ParentOfA<T>"));
		assertTrue(present.contains("ClassA"));
		assertTrue(present.contains("ClassB"));
		assertTrue(present.contains("InterfaceC"));
		assertTrue(present.contains("ClassD"));
		assertTrue(present.contains("m1 : ClassB"));
		assertTrue(present.contains("m2 : ClassA"));
		assertTrue(present.contains("m3 : ClassA"));
		assertTrue(present.contains("// TypeCategory"));
		System.out.println(present);
	}
}
