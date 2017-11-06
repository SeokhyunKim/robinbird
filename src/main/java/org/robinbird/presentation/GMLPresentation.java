package org.robinbird.presentation;

import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.Package;
import org.robinbird.model.Relation;

/**
 * Created by seokhyun on 10/31/17.
 */
public class GMLPresentation implements AnalysisContextPresentation {

	public String present(AnalysisContext analysisContext) {
		StringAppender sa = new StringAppender();
		sa.appendLine("graph");
		sa.appendLine("[");
		for (Package classPackage : analysisContext.getPackages()) {
			for (Class classObj : classPackage.getClassList()) {
				appendNodeString(sa, classObj.getFullName(), classObj.getName());
			}
		}
		// inheritance
		for (Package classPackage : analysisContext.getPackages()) {
			for (Class classObj : classPackage.getClassList()) {
				if (classObj.getParent() != null) {
					appendEdgeString(sa, classObj.getFullName(), classObj.getParent().getFullName());
				}
			}
		}
		// interfaces
		for (Package classPackage : analysisContext.getPackages()) {
			for (Class classObj : classPackage.getClassList()) {
				if (classObj.getInterfaces().size() > 0) {
					for (Class interfaceOfClassObj : classObj.getInterfaces()) {
						appendEdgeString(sa, removeGenerics(classObj.getFullName()), removeGenerics(interfaceOfClassObj.getFullName()));
					}
				}
			}
		}
		// relations
		for (Relation r : analysisContext.getRelations()) {
			if (!(r.getFirst() instanceof Class) || !(r.getSecond() instanceof Class)) {
				continue;
			}
			String firstId = removeGenerics(((Class)r.getFirst()).getFullName());
			String secondId = removeGenerics(((Class)r.getSecond()).getFullName());
			appendEdgeString(sa, firstId, secondId);
		}
		sa.appendLine("]");
		return sa.toString();
	}

	private void appendNodeString(final StringAppender sa, final String id, final String label) {
		sa.appendLine("node");
		sa.appendLine("[");
		sa.appendLine("\tid " + id);
		sa.appendLine("\tlabel \"" + label + "\"");
		sa.appendLine("]");
	}

	private void appendEdgeString(final StringAppender sa, final String source, final String target) {
		sa.appendLine("edge");
		sa.appendLine("[");
		sa.appendLine("source " + source);
		sa.appendLine("target " + target);
		sa.appendLine("]");
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
