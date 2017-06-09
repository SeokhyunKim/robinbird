package org.robinbird.listener;

import org.antlr.v4.runtime.tree.ParseTree;
import org.robinbird.model.AccessModifier;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.AnalysisContextApplier;
import org.robinbird.model.Class;
import org.robinbird.model.ClassType;
import org.robinbird.model.Collection;
import org.robinbird.model.Map;
import org.robinbird.model.Member;
import org.robinbird.model.Type;
import org.robinbird.parser.java8.Java8Parser;
import org.robinbird.utils.Msgs;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.utils.Msgs.Key.*;

/**
 * Created by seokhyun on 5/26/17.
 */
public class Java8Listener extends org.robinbird.parser.java8.Java8BaseListener implements AnalysisContextApplier {

	private AnalysisContext analysisContext;

	public void setAnalysisContext(AnalysisContext analysisContext) { this.analysisContext = analysisContext; }
	public AnalysisContext getAnalysisContext() { return analysisContext; }

	@Override
	public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
		Class c = analysisContext.getClass(ctx.Identifier().getText(), ClassType.CLASS);
	}

	@Override
	public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
	}

	@Override
	public void enterNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
		Class c = analysisContext.getClass(ctx.Identifier().getText(), ClassType.INTERFACE);
	}

	@Override
	public void enterFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
		checkState(analysisContext.getCurrentClass() != null, Msgs.get(CURRENT_CLASS_IS_NULL_WHILE_WALKING_THROUGH_PARSE_TREE));

		AccessModifier accessModifier = getAccessModifier(ctx.fieldModifier());
		Type type = getType(ctx.unannType());
		List<String> vars = getVariableList(ctx.variableDeclaratorList());

		for (String var : vars) {
			analysisContext.getCurrentClass().addMember(new Member(accessModifier, type, var));
		}
	}

	private AccessModifier getAccessModifier(List<Java8Parser.FieldModifierContext> fieldModifierContexts) {
		AccessModifier accessModifier = AccessModifier.PRIVATE;
		for (Java8Parser.FieldModifierContext modifierContext : fieldModifierContexts) {
			try {
				accessModifier = AccessModifier.valueOf(modifierContext.getText());
			} catch (IllegalArgumentException e) {
				continue;
			}
		}
		return accessModifier;
	}

	private Type getType(Java8Parser.UnannTypeContext unannTypeContext) {
		Type type = null;

		// primitive type field
		if (unannTypeContext.unannPrimitiveType() != null) {
			type = new Type(unannTypeContext.unannPrimitiveType().getText(), Type.Kind.PRIMITIVE);
		}
		// reference type field
		else if (unannTypeContext.unannReferenceType() != null) {
			Java8Parser.UnannReferenceTypeContext referenceTypeContext = unannTypeContext.unannReferenceType();

			if (referenceTypeContext.unannClassOrInterfaceType() != null) {
				String typeText = referenceTypeContext.getText();
				if (isPrimitive(typeText))
				{
					type = new Type(unannTypeContext.unannPrimitiveType().getText(), Type.Kind.PRIMITIVE);
				}
				else if (isCollection(typeText))
				{
					List<Java8Parser.ReferenceTypeContext> refTypes = new ArrayList<>();
					findReferenceTypes(referenceTypeContext, refTypes);
					type = new Collection(typeText, analysisContext.getType(refTypes.get(0).getText()));
				}
				else if (isMap(typeText))
				{
					List<Java8Parser.ReferenceTypeContext> refTypes = new ArrayList<>();
					findReferenceTypes(referenceTypeContext, refTypes);
					type = new Map(typeText, analysisContext.getType(refTypes.get(0).getText()), analysisContext.getType(refTypes.get(1).getText()));
				}
				else
				{
					type = analysisContext.getType(typeText);
				}
			}
			// array type field
			else if (referenceTypeContext.unannArrayType() != null) {
				Java8Parser.UnannArrayTypeContext arrayTypeContext = referenceTypeContext.unannArrayType();
				if (arrayTypeContext.unannClassOrInterfaceType() != null) {
					if (isPrimitive(arrayTypeContext.unannClassOrInterfaceType().getText())) {
						type = new Type(unannTypeContext.unannPrimitiveType().getText(), Type.Kind.PRIMITIVE);
					} else {
						type = analysisContext.getType(arrayTypeContext.unannClassOrInterfaceType().getText());
					}
				} else if (arrayTypeContext.unannPrimitiveType() != null) {
					type = new Type(arrayTypeContext.unannPrimitiveType().getText(), Type.Kind.PRIMITIVE);
				}
			}
		}
		checkState(type != null, Msgs.get(FAILED_TO_FIND_MEMBER_TYPE, analysisContext.getCurrentClass().getName()));
		return type;
	}

	private boolean isPrimitive(String text) {
		String[] types = { "Byte", "Short", "Integer", "Long", "Character", "Float", "Double", "Boolean"};
		for (String type : types) {
			if (text.startsWith(type)) { return true; }
		}
		return false;
	}

	private boolean isCollection(String text) {
		String[] collections = { "List", "LinkedList", "ArrayList", "Set", "TreeSet", "HashSet", "LinkedHashSet"};
		for (String collection : collections) {
			if (text.startsWith(collection)) { return true; }
		}
		return false;
	}

	private boolean isMap(String text) {
		String[] maps = { "Map", "HashMap", "TreeMap"};
		for (String map : maps) {
			if (text.startsWith(map)) { return true; }
		}
		return false;
	}

	private void findReferenceTypes(ParseTree node, List<Java8Parser.ReferenceTypeContext> refTypes) {
		if (node instanceof Java8Parser.ReferenceTypeContext) {
			refTypes.add((Java8Parser.ReferenceTypeContext)node);
		}
		int i=0;
		while (node.getChild(i) != null) {
			findReferenceTypes(node.getChild(i++), refTypes);
		}
	}

	private List<String> getVariableList(Java8Parser.VariableDeclaratorListContext variableDeclaratorListContext) {
		List<String> vars = new ArrayList<>();
		for (Java8Parser.VariableDeclaratorContext context : variableDeclaratorListContext.variableDeclarator()) {
			vars.add(context.variableDeclaratorId().Identifier().getText());
		}
		return vars;
	}

}
