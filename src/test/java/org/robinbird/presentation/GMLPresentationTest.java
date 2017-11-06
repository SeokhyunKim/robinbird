package org.robinbird.presentation;

import org.junit.Before;
import org.junit.Test;
import org.robinbird.model.AnalysisContext;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 11/6/17.
 */
public class GMLPresentationTest {

	private AnalysisContext analysisContext;

	@Before
	public void setUp() {
		analysisContext = PresentationTestUtils.createTestAnalysisContext();
	}

	@Test
	public void testPresent() {
		GMLPresentation presentation = new GMLPresentation();
		String present = presentation.present(analysisContext);
		assertTrue(present.contains("graph"));
		assertTrue(present.contains("node"));
		assertTrue(present.contains("id com.test.pkg.ParentOfA<T>"));
		assertTrue(present.contains("id com.test.pkg.ClassD"));
		assertTrue(present.contains("edge"));
		assertTrue(present.contains("source com.test.pkg.ClassA"));
		assertTrue(present.contains("target com.test.pkg.ClassB"));
		System.out.println(present);
	}
}
