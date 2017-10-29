package org.robinbird.presentation;

import org.robinbird.model.AccessModifier;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.ClassType;
import org.robinbird.model.Member;
import org.robinbird.model.Package;
import org.robinbird.model.Repository;
import org.robinbird.model.Type;

import java.util.Arrays;

/**
 * Created by seokhyun on 10/27/17.
 */
public class PresentationTestUtils {

	public static AnalysisContext createTestAnalysisContext() {
		AnalysisContext analysisContext = new AnalysisContext(new Repository<Type>(), new Repository<Package>());
		Package pkg = analysisContext.registerPackage(Arrays.asList("com", "test", "pkg"));
		analysisContext.setCurrentPackage(pkg);
		Class parentA = analysisContext.registerClass("ParentOfA<T>", ClassType.CLASS);
		Class classA = analysisContext.registerClass("ClassA", ClassType.CLASS);
		Class classB = analysisContext.registerClass("ClassB", ClassType.CLASS);
		Class interfaceC = analysisContext.registerClass("InterfaceC", ClassType.INTERFACE);
		Class classD = analysisContext.registerClass("ClassD", ClassType.CLASS);
		analysisContext.setCurrentClass(parentA);
		analysisContext.setCurrentClass(classA);
		classA.setParent(parentA);
		classA.addMember(new Member(AccessModifier.PRIVATE, classB, "m1"));
		analysisContext.setCurrentClass(classB);
		classB.addMember(new Member(AccessModifier.PUBLIC, classA, "m2"));
		classB.addMember(new Member(AccessModifier.PROTECTED, classD, "mm"));
		classB.addInterface(interfaceC);
		analysisContext.setCurrentClass(interfaceC);
		analysisContext.setCurrentClass(classD);
		classD.addMember(new Member(AccessModifier.PROTECTED, classA, "m3"));
		analysisContext.update();
		return analysisContext;
	}
}
