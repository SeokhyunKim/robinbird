package org.robinbird.analyser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.robinbird.model.AccessModifier;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.AnalysisContextApplier;
import org.robinbird.model.Class;
import org.robinbird.model.ClassType;
import org.robinbird.model.Collection;
import org.robinbird.model.Member;
import org.robinbird.model.Type;
import org.robinbird.parser.java8.Java8BaseListener;
import org.robinbird.parser.java8.Java8Parser;
import org.robinbird.utils.Msgs;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.utils.Msgs.Key.*;

/**
 * Created by seokhyun on 5/26/17.
 */
public class Java8Analyser extends Java8BaseListener implements AnalysisContextApplier {

	private AnalysisContext analysisContext;

	public void setAnalysisContext(AnalysisContext analysisContext) { this.analysisContext = analysisContext; }
	public AnalysisContext getAnalysisContext() { return analysisContext; }

	@Override
	public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
		String className = ctx.Identifier().getText() + getTemplateClassParameters(ctx.typeParameters());
		Class c = analysisContext.getClass( className, ClassType.CLASS);
		if (ctx.superclass() != null) {
			Java8Parser.ClassTypeContext classTypeContext = ctx.superclass().classType();
			Class parent = analysisContext.getClass(classTypeContext.getText(), ClassType.CLASS);
			c.setParent(parent);
		}
		analysisContext.setCurrentClass(c);
	}

	@Override
	public void enterNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
		analysisContext.getClass(ctx.Identifier().getText(), ClassType.INTERFACE); // registering interface. getClass registers new one.
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

	private String getTemplateClassParameters(Java8Parser.TypeParametersContext ctx) {
		if (ctx == null) { return ""; }
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		int i = 0;
		Java8Parser.TypeParameterContext tpCtx = ctx.typeParameterList().typeParameter(i);
		while (tpCtx != null) {
			sb.append(tpCtx.Identifier().getText());
			tpCtx = ctx.typeParameterList().typeParameter(++i);
			if (tpCtx != null) {
				sb.append(", ");
			}
		}
		sb.append(">");
		return sb.toString();
	}

	private AccessModifier getAccessModifier(List<Java8Parser.FieldModifierContext> fieldModifierContexts) {
		AccessModifier accessModifier = AccessModifier.PRIVATE;
		for (Java8Parser.FieldModifierContext modifierContext : fieldModifierContexts) {
			try {
				accessModifier = AccessModifier.fromDescription(modifierContext.getText());
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
					type = new Type(typeText, Type.Kind.PRIMITIVE);
				}
				else if (isCollection(typeText))
				{
					List<Java8Parser.ReferenceTypeContext> java8RefTypes = new ArrayList<>();
					findJava8ParserReferenceTypes(referenceTypeContext, java8RefTypes);
					List<Type> refTypes = getReferenceTypes(java8RefTypes);
					type = new Collection(typeText, refTypes);
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
		String[] collections = { "List", "LinkedList", "ArrayList", "Set", "TreeSet", "HashSet", "LinkedHashSet", "Map", "HashMap", "TreeMap"};
		for (String collection : collections) {
			if (text.startsWith(collection)) { return true; }
		}
		return false;
	}

	private void findJava8ParserReferenceTypes(ParseTree node, List<Java8Parser.ReferenceTypeContext> refTypes) {
		if (node instanceof Java8Parser.ReferenceTypeContext) {
			refTypes.add((Java8Parser.ReferenceTypeContext)node);
		}
		int i=0;
		while (node.getChild(i) != null) {
			findJava8ParserReferenceTypes(node.getChild(i++), refTypes);
		}
	}

	// convert the list of Java8Parser.ReferenceTypeContext like "List<A>, A, Map<B, C>, B, C" to "A, B, C" by removing collections and maps.
	// and register reference types
	private List<Type> getReferenceTypes(List<Java8Parser.ReferenceTypeContext> referenceTypeContexts) {
		List<Type> refTypes = new ArrayList<>();
		for (Java8Parser.ReferenceTypeContext context : referenceTypeContexts) {
			if (isCollection(context.getText()) || isPrimitive(context.getText())) {
				continue;
			}
			refTypes.add(analysisContext.getType(context.getText()));
		}
		return refTypes;
	}

	private List<String> getVariableList(Java8Parser.VariableDeclaratorListContext variableDeclaratorListContext) {
		List<String> vars = new ArrayList<>();
		for (Java8Parser.VariableDeclaratorContext context : variableDeclaratorListContext.variableDeclarator()) {
			vars.add(context.variableDeclaratorId().Identifier().getText());
		}
		return vars;
	}

}
