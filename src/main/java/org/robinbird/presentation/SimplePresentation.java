package org.robinbird.presentation;

import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.Member;
import org.robinbird.model.Relation;
import org.robinbird.model.Repository;
import org.robinbird.model.Type;

import java.util.Map;

/**
 * Created by seokhyun on 6/7/17.
 */
public class SimplePresentation implements AnalysisContextPersentation {

	public void present(AnalysisContext analysisContext) {
		System.out.println("//----------------------------------------------------");
		System.out.println("// Classes");
		for (Class classObj : analysisContext.getClasses()) {
			System.out.println(classObj.getName());
			for (Map.Entry<String, Member> entry : classObj.getMemberVariables().entrySet()) {
				System.out.println(String.format("\t%s : %s", entry.getKey(), entry.getValue().getType().getName()));
			}
		}
		System.out.println("//----------------------------------------------------");
		System.out.println("// Types");
		for (Type type : analysisContext.getTypes()) {
			System.out.println(type.getName());
		}
		System.out.println("//----------------------------------------------------");
		System.out.println("// Relations");
		for (Relation r : analysisContext.getRelations()) {
			System.out.println(String.format("%s (%s) --- (%s) %s", r.getFirst().getName(), (r.getFirstCardinality() != null ? r.getFirstCardinality() : "null"),
				(r.getSecondCardinality() != null ? r.getSecondCardinality() : "null"), r.getSecond().getName()));
		}
	}
}
