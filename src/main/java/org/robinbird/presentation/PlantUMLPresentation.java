package org.robinbird.presentation;

import org.robinbird.model.AccessModifier;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.Member;
import org.robinbird.model.Package;
import org.robinbird.model.Relation;

import java.util.Map;

/**
 * Created by seokhyun on 6/9/17.
 */
public class PlantUMLPresentation implements AnalysisContextPresentation {

	public String present(AnalysisContext analysisContext) {
		StringAppender sa = new StringAppender();
		sa.appendLine("@startuml");
		for (Package classPackage : analysisContext.getPackages()) {
			sa.appendLine("package " + classPackage.getName() + " {");
			//for (Class classObj : analysisContext.getClasses()) {
			for (Class classObj : classPackage.getClassList()) {
				// class name, member variables, and member functions
				sa.appendLine(String.format("class %s {", classObj.getName()));
				sa.append(printMemberVariables(classObj.getMemberVariables()));
				sa.appendLine("}");
				// inheritance
				if (classObj.getParent() != null) {
					sa.appendLine(removeGenerics(classObj.getParent().getName()) + " <|-- " + removeGenerics(classObj.getName()));
				}
				if (classObj.getInterfaces().size() > 0) {
					for (Class interfaceOfClassObj : classObj.getInterfaces()) {
						sa.appendLine(removeGenerics(interfaceOfClassObj.getName()) + " <|.. " + removeGenerics(classObj.getName()));
					}
				}
			}
			sa.appendLine("}");
		}
		for (Relation r : analysisContext.getRelations()) {
			String firstName = removeGenerics(r.getFirst().getName());
			String secondName = removeGenerics(r.getSecond().getName());
			if (r.getFirstCardinality() == null) {
				sa.appendLine(firstName + " --> " + attachQuotes(r.getSecondCardinality()) + " " + secondName);
			} else if (r.getSecondCardinality() == null) {
				sa.appendLine(firstName + " " + attachQuotes(r.getFirstCardinality()) + " <-- " + secondName);
			} else {
				sa.appendLine(firstName + " " + attachQuotes(r.getFirstCardinality()) + " -- "
									+ attachQuotes(r.getSecondCardinality()) + " " + secondName);
			}
		}
		sa.appendLine("@enduml");
		return sa.toString();
	}

	private String printMemberVariables(Map<String, Member> mebers) {
		StringAppender sa = new StringAppender();
		for (Map.Entry<String, Member> entry : mebers.entrySet()) {

			sa.append("\t").append(convertAccessModifier(entry.getValue().getAccessModifier()))
				.append(" ");
			sa.append(entry.getKey()).append(" : ").appendLine(entry.getValue().getType().getName());
		}
		return sa.toString();
	}

	private String convertAccessModifier(AccessModifier accessModifier) {
		switch (accessModifier) {
			case PUBLIC: return "+";
			case PRIVATE: return "-";
			case PROTECTED: return "#";
		}
		return "-";
	}

	private String attachQuotes(String text) {
		return "\""+text+"\"";
	}

	private String removeGenerics(String name) {
		if (!isGeneric(name)) { return name; }
		return name.substring(0, name.indexOf("<"));
	}

	private boolean isGeneric(String name) {
		return name.contains("<");
	}
}
