package org.robinbird.code.presentation;

import org.junit.Before;
import org.junit.Test;
import org.robinbird.code.model.AnalysisContext;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 11/8/17.
 */
public class AbstractedClassesPresentationTest {

	private AnalysisContext analysisContext;

	@Before
	public void setUp() {
		analysisContext = PresentationTestUtils.createTestAnalysisContext();
	}

	@Test
	public void testPresent() {
		AbstractedClassesPresentation presentation = new AbstractedClassesPresentation(CLUSTERING_METHOD.HIERARCHICAL_CUSTERING, 1);
		String present = presentation.present(analysisContext);
//		assertTrue(present.contains("graph"));
//		assertTrue(present.contains("node"));
//		assertTrue(present.contains("id com.test.pkg.ParentOfA<T>"));
//		assertTrue(present.contains("id com.test.pkg.ClassD"));
//		assertTrue(present.contains("edge"));
//		assertTrue(present.contains("source com.test.pkg.ClassA"));
//		assertTrue(present.contains("target com.test.pkg.ClassB"));
		System.out.println(present);
	}
}
