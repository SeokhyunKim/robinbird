package org.robinbird.main.presentation;

import org.robinbird.main.model.AccessModifier;
import org.robinbird.main.model.AnalysisContext;
import org.robinbird.main.model.Class;
import org.robinbird.main.model.ClassType;
import org.robinbird.main.model.Member;
import org.robinbird.main.model.Package;
import org.robinbird.main.repository.Repository;

import java.util.Arrays;

/**
 * Created by seokhyun on 10/27/17.
 */
public class PresentationTestUtils {

	public static AnalysisContext createTestAnalysisContext() {
		AnalysisContext analysisContext = new AnalysisContext(new Repository<>(), new Repository<>());
		Package pkg = analysisContext.registerPackage(Arrays.asList("com", "test", "pkg"));
		analysisContext.setCurrentPackage(pkg);
		Class parentA = analysisContext.registerClass("ParentOfA<T>", ClassType.CLASS);
		Class classA = analysisContext.registerClass("ClassA", ClassType.CLASS);
		Class classB = analysisContext.registerClass("ClassB", ClassType.CLASS);
		Class interfaceC = analysisContext.registerClass("InterfaceC", ClassType.INTERFACE);
		Class classD = analysisContext.registerClass("ClassD", ClassType.CLASS);
		analysisContext.pushCurrentClass(parentA); // register to the test package
		analysisContext.popCurrentClass();
		analysisContext.pushCurrentClass(classA);
		classA.setParent(parentA);
		classA.addMember(new Member(AccessModifier.PRIVATE, classB, "m1"));
		analysisContext.popCurrentClass();
		analysisContext.pushCurrentClass(classB);
		classB.addMember(new Member(AccessModifier.PUBLIC, classA, "m2"));
		classB.addMember(new Member(AccessModifier.PROTECTED, classD, "mm"));
		classB.addInterface(interfaceC);
		analysisContext.popCurrentClass();
		analysisContext.pushCurrentClass(interfaceC);
		analysisContext.popCurrentClass();
		analysisContext.pushCurrentClass(classD);
		classD.addMember(new Member(AccessModifier.PROTECTED, classA, "m3"));
		analysisContext.popCurrentClass();
		analysisContext.update();
		return analysisContext;
	}
}
