package org.robinbird.analyser;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.robinbird.model.AccessModifier;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Analyser;
import org.robinbird.model.Class;
import org.robinbird.model.ClassType;
import org.robinbird.model.Collection;
import org.robinbird.model.Member;
import org.robinbird.model.Type;
import org.robinbird.model.Package;
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
@Slf4j
public class Java8Analyser extends Java8BaseListener implements Analyser {

	private AnalysisContext analysisContext;

	public void setAnalysisContext(AnalysisContext analysisContext) { this.analysisContext = analysisContext; }
	public AnalysisContext getAnalysisContext() { return analysisContext; }

	private enum State { NONE, PARSING_CLASS, PARSING_INTERFACE, EXCLUDED_TYPE, INNER_CLASS};
	State state = State.NONE;

	@Override
	public void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
		List<String> packageNameList = new ArrayList<>();
		for (TerminalNode identifier : ctx.Identifier()) {
			packageNameList.add(identifier.getText());
		}
		Package p = analysisContext.registerPackage(packageNameList);
		analysisContext.setCurrentPackage(p);
	}

	@Override public void enterEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
		analysisContext.setParsingEnum(true);
	}

	@Override public void exitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
		analysisContext.setParsingEnum(false);
	}

	@Override
	public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
		String className = ctx.Identifier().getText() + getTemplateClassParameters(ctx.typeParameters());
		if (analysisContext.isExcluded(className)) {
			state = State.EXCLUDED_TYPE;
			return;
		}
		if (analysisContext.getCurrentClass() != null) {
			state = State.INNER_CLASS;
			analysisContext.pushCurrentClass(new Class(className, ClassType.CLASS));
			return;
		}
		state = State.PARSING_CLASS;
		Class c = analysisContext.getClass(className);
		if (c != null) {
			c.setClassType(ClassType.CLASS);
		} else {
			c = analysisContext.registerClass(className, ClassType.CLASS);
		}
		if (ctx.superclass() != null) {
			Java8Parser.ClassTypeContext classTypeContext = ctx.superclass().classType();
			Class parent = analysisContext.getClass(classTypeContext.getText());
			if (parent == null) {
				parent = analysisContext.registerClass(classTypeContext.getText(), ClassType.CLASS);
			}
			c.setParent(parent);
		}
		// TO DO : write UTs
		if (ctx.superinterfaces() != null) {
			Java8Parser.SuperinterfacesContext superinterfacesContext = ctx.superinterfaces();
			if (superinterfacesContext.interfaceTypeList() != null) {
				for (Java8Parser.InterfaceTypeContext interfaceTypeContext : superinterfacesContext.interfaceTypeList().interfaceType()) {
					Class newInterface = analysisContext.getClass(interfaceTypeContext.getText());
					if (newInterface == null) {
						newInterface = analysisContext.registerClass(interfaceTypeContext.getText(), ClassType.INTERFACE);
					}
					c.addInterface(newInterface);
				}

			}
		}
		analysisContext.pushCurrentClass(c);
	}

	@Override
	public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
		analysisContext.popCurrentClass();
		analysisContext.setCurrentPackage(null);
	}

	@Override
	public void enterNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
		String interfaceName = ctx.Identifier().getText();
		if (analysisContext.isExcluded(interfaceName)) {
			state = State.EXCLUDED_TYPE;
			return;
		}
		if (analysisContext.getCurrentClass() != null) {
			state = State.INNER_CLASS;
			analysisContext.pushCurrentClass(new Class(interfaceName, ClassType.INTERFACE));
			return;
		}
		state = State.PARSING_INTERFACE;
		Class c = analysisContext.getClass(interfaceName);
		if (c != null) {
			c.setClassType(ClassType.INTERFACE);
		} else {
			c = analysisContext.registerClass(interfaceName, ClassType.INTERFACE);
		}
		analysisContext.pushCurrentClass(c);
	}

	@Override
	public void exitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
		analysisContext.popCurrentClass();
		analysisContext.setCurrentPackage(null);
	}

	@Override
	public void enterFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
		if (analysisContext.isParsingEnum()) { return; }
		if (state == State.EXCLUDED_TYPE || state == State.INNER_CLASS) { return; }
		checkState(analysisContext.getCurrentClass() != null, Msgs.get(CURRENT_CLASS_IS_NULL_WHILE_WALKING_THROUGH_PARSE_TREE));
		if (analysisContext.isCurrentClassTerminal()) { return; }

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
			type = getPrimitiveType(unannTypeContext.unannPrimitiveType());
		}
		// reference type field
		else if (unannTypeContext.unannReferenceType() != null) {
			Java8Parser.UnannReferenceTypeContext referenceTypeContext = unannTypeContext.unannReferenceType();
			if (referenceTypeContext.unannClassOrInterfaceType() != null) {
				type = getReferenceType(referenceTypeContext);
			} else if (referenceTypeContext.unannArrayType() != null) {
				type = getArrayType(referenceTypeContext.unannArrayType());
			}
		}
		checkState(type != null, Msgs.get(FAILED_TO_FIND_MEMBER_TYPE, analysisContext.getCurrentClass().getName()));
		return type;
	}

	private Type getPrimitiveType(Java8Parser.UnannPrimitiveTypeContext primitiveTypeContext) {
		return new Type(primitiveTypeContext.getText(), Type.Kind.PRIMITIVE);

	}

	private Type getReferenceType(Java8Parser.UnannReferenceTypeContext referenceTypeContext) {
		Type type = null;
		String typeText = referenceTypeContext.getText();
		if (isPrimitiveClass(typeText)) // For example, types like String, Integer, and etc are just processed as primitive
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
			if (type == null) {
				if (analysisContext.isExcluded(typeText)) {
					type = new Class(typeText, ClassType.CLASS);
				} else {
					// For newly found reference type, just set as CLASS. Can be changed to INTERFACE later.
					type = analysisContext.registerClass(typeText, ClassType.CLASS);
				}
			}
		}
		return type;
	}

	private Type getArrayType(Java8Parser.UnannArrayTypeContext arrayTypeContext) {
		Type aryBaseType = null;
		if (arrayTypeContext.unannClassOrInterfaceType() != null) {
			String classOrInterfaceTypeText = arrayTypeContext.unannClassOrInterfaceType().getText();
			if (isPrimitiveClass(classOrInterfaceTypeText)) {
				aryBaseType = new Type(classOrInterfaceTypeText, Type.Kind.PRIMITIVE);
			} else {
				aryBaseType = analysisContext.getClass(classOrInterfaceTypeText);
				if (aryBaseType == null) {
					if (analysisContext.isExcluded(classOrInterfaceTypeText)) {
						aryBaseType = new Class(classOrInterfaceTypeText, ClassType.CLASS);
					} else {
						aryBaseType = analysisContext.registerClass(classOrInterfaceTypeText, ClassType.CLASS);
					}
				}
			}
		} else if (arrayTypeContext.unannPrimitiveType() != null) {
			aryBaseType = new Type(arrayTypeContext.unannPrimitiveType().getText(), Type.Kind.PRIMITIVE);
		}
		checkState(aryBaseType != null, Msgs.get(FAILED_TO_FIND_MEMBER_TYPE, arrayTypeContext.getText()));
		List<Type> types = new ArrayList<>();
		types.add(aryBaseType);
		return new Collection(arrayTypeContext.getText(), types);
	}



	private boolean isPrimitiveClass(String text) {
		String[] types = { "Byte", "Short", "Integer", "Long", "Character", "Float", "Double", "Boolean", "String"};
		for (String type : types) {
			if (text.startsWith(type)) { return true; }
		}
		return false;
	}

	private boolean isCollection(String text) {
		String[] collections = { "List", "LinkedList", "ArrayList", "Set", "TreeSet", "HashSet", "LinkedHashSet", "Map", "HashMap", "TreeMap", "ConcurrentMap"};
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

	// convert the list of Java8Parser.ReferenceTypeContext like "List<A>, A, Map<B, C>, B, C" to "A, B, C" by removing specific collection implementations.
	// and register reference types
	private List<Type> getReferenceTypes(List<Java8Parser.ReferenceTypeContext> referenceTypeContexts) {
		List<Type> refTypes = new ArrayList<>();
		for (Java8Parser.ReferenceTypeContext context : referenceTypeContexts) {
			if (isCollection(context.getText())) {
				continue;
			}
			if (isPrimitiveClass(context.getText())) {
				refTypes.add(new Type(context.getText(), Type.Kind.PRIMITIVE));
			} else {
				Type t = analysisContext.getType(context.getText());
				if (t == null) {
					if (analysisContext.isExcluded(context.getText())) {
						t = new Class(context.getText(), ClassType.CLASS);
					} else {
						t = analysisContext.registerClass(context.getText(), ClassType.CLASS);
					}
				}
				refTypes.add(t);
			}
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
