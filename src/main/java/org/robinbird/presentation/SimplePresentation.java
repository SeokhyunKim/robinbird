package org.robinbird.presentation;

import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.Member;
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
		Repository<Class> classes = analysisContext.getClasses();
		int i=0;
		while (classes.getRepositable(i) != null) {
			Class classObj = classes.getRepositable(i++);
			System.out.println(classObj.getName());
			for (Map.Entry<String, Member> entry : classObj.getMemberVariables().entrySet()) {
				System.out.println(String.format("\t%s : %s", entry.getKey(), entry.getValue().getType().getName()));
			}
		}
		System.out.println("//----------------------------------------------------");
		System.out.println("// Types");
		Repository<Type> types = analysisContext.getTypes();
		i=0;
		while (types.getRepositable(i) != null) {
			Type type = types.getRepositable(i++);
			System.out.println(type.getName());
		}
	}
}
