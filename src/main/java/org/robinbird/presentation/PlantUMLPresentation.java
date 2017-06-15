package org.robinbird.presentation;

import org.robinbird.model.AccessModifier;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.Member;
import org.robinbird.model.Repository;
import org.robinbird.model.Type;

import java.util.Map;

/**
 * Created by seokhyun on 6/9/17.
 */
public class PlantUMLPresentation implements AnalysisContextPersentation {

	public void present(AnalysisContext analysisContext) {
		System.out.println("@startuml");
		Repository<Class> classes = analysisContext.getClasses();
		int i=0;
		while (classes.getRepositable(i) != null) {
			Class classObj = classes.getRepositable(i++);
			// class name, member variables, and member functions
			System.out.println(String.format("class %s {", classObj.getName()));
			printMemberVariables(classObj.getMemberVariables());
			System.out.println("}");
			// inheritance
			if (classObj.getParent() != null) {
				System.out.println(classObj.getParent().getName() + " <|-- " + classObj.getName());
			}
		}
		System.out.println("@enduml");
	}

	private void printMemberVariables(Map<String, Member> mebers) {
		for (Map.Entry<String, Member> entry : mebers.entrySet()) {
			StringBuffer sb = new StringBuffer();
			sb.append("\t").append(convertAccessModifier(entry.getValue().getAccessModifier()))
				.append(" ");
			sb.append(entry.getKey()).append(" : ").append(entry.getValue().getType().getName());
			System.out.println(sb.toString());
		}
	}

	private String convertAccessModifier(AccessModifier accessModifier) {
		switch (accessModifier) {
			case PUBLIC: return "+";
			case PRIVATE: return "-";
			case PROTECTED: return "#";
		}
		return "-";
	}
}
