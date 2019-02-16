package org.robinbird.main.newpresentation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.robinbird.main.newmodel.AnalysisContext;

/**
 * Created by seokhyun on 6/9/17.
 */
public class PlantUMLPresentation implements AnalysisContextPresentation {

	public String present(AnalysisContext analysisContext) {
		/*
		StringAppender sa = new StringAppender();
		sa.appendLine("@startuml");
		sa.appendLine("left to right direction");
		for (Package classPackage : analysisContext.getPackages()) {
			sa.appendLine("package " + classPackage.getName() + " {");
			for (Class classObj : classPackage.getClassList()) {
				// class name, member variables, and member functions
				sa.appendLine(String.format("class %s {", classObj.getName()));
				sa.append(printMemberVariables(classObj.getMemberVariables()));
				sa.append(printMemberFunctions(classObj.getMemberFunctions().values()));
				sa.appendLine("}");
			}
			sa.appendLine("}");
		}
		// inheritance
		for (Package classPackage : analysisContext.getPackages()) {
			for (Class classObj : classPackage.getClassList()) {
				if (classObj.getParent() != null) {
					sa.appendLine(removeGenerics(classObj.getParent().getName()) + " <|-- " + removeGenerics(classObj.getName()));
				}
			}
		}
		// interfaces
		for (Package classPackage : analysisContext.getPackages()) {
			for (Class classObj : classPackage.getClassList()) {
				if (classObj.getInterfaces().size() > 0) {
					for (Class interfaceOfClassObj : classObj.getInterfaces()) {
						sa.appendLine(removeGenerics(interfaceOfClassObj.getName()) + " <|.. " + removeGenerics(classObj.getName()));
					}
				}
			}
		}
		// relations
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
		*/
		return "not implemented yet";
	}

	/*
	private String printMemberVariables(Map<String, Member> mebers) {
		StringAppender sa = new StringAppender();
		for (Map.Entry<String, Member> entry : mebers.entrySet()) {

			sa.append("\t").append(convertAccessModifier(entry.getValue().getAccessModifier()))
				.append(" ");
			sa.append(entry.getKey()).append(" : ").appendLine(entry.getValue().getType().getName());
		}
		return sa.toString();
	}

	private String printMemberFunctions(Collection<MemberFunction> memberFunctions) {
		StringAppender sa = new StringAppender();
		for (MemberFunction mf : memberFunctions) {
			sa.append("\t").append(convertAccessModifier(mf.getAccessModifier())).append(" ");
			sa.append(mf.getName()).append("(");
			if (mf.getParameters() != null) {
				Iterator<ParameterType> itr = mf.getParameters().iterator();
				while (itr.hasNext()) {
					ParameterType t = itr.next();
					sa.append(t.getType().getName());
					if (itr.hasNext()) {
						sa.append(", ");
					}
				}
			}
			sa.append(") : ").appendLine(mf.getType().getName());
		}
		return sa.toString();
	}

	private String convertAccessModifier(AccessModifier accessModifier) {
		switch (accessModifier) {
			case PUBLIC: return "+";
			case PRIVATE: return "-";
		}
		// protected
		return "#";
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
	*/
}
