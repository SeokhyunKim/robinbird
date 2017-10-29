package org.robinbird.presentation;

import org.junit.Before;
import org.junit.Test;
import org.robinbird.model.AccessModifier;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.ClassType;
import org.robinbird.model.Member;
import org.robinbird.model.Package;
import org.robinbird.model.Repository;
import org.robinbird.model.Type;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 10/27/17.
 */
public class PlantUMLPresentationTest {

	private AnalysisContext analysisContext;

	@Before
	public void setUp() {
		analysisContext = PresentationTestUtils.createTestAnalysisContext();
	}

	@Test
	public void testPresent() {
		PlantUMLPresentation presentation = new PlantUMLPresentation();
		String present = presentation.present(analysisContext);
		assertTrue(present.contains("@startuml"));
		assertTrue(present.contains("@enduml"));
		assertTrue(present.contains("class ParentOfA<T>"));
		assertTrue(present.contains("class ClassA"));
		assertTrue(present.contains("class ClassB"));
		assertTrue(present.contains("class InterfaceC"));
		assertTrue(present.contains("class ClassD"));
		assertTrue(present.contains("- m1 : ClassB"));
		assertTrue(present.contains("+ m2 : ClassA"));
		assertTrue(present.contains("# m3 : ClassA"));
		System.out.println(present);
	}


}
