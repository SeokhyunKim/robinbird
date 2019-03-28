package org.robinbird.main.newanalyser;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.main.util.Msgs.Key.CURRENT_CLASS_IS_NULL_WHILE_WALKING_THROUGH_PARSE_TREE;
import static org.robinbird.main.util.Msgs.Key.FAILED_TO_FIND_MEMBER_TYPE;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.robinbird.main.model.MemberFunction;
import org.robinbird.main.newmodel.AccessModifier;
import org.robinbird.main.newmodel.Analyser;
import org.robinbird.main.newmodel.AnalysisContext;
import org.robinbird.main.newmodel.Instance;
import org.robinbird.main.newmodel.Relation;
import org.robinbird.main.newmodel.RelationCategory;
import org.robinbird.main.newmodel.Type;
import org.robinbird.main.newmodel.TypeCategory;
import org.robinbird.main.util.Msgs;
import org.robinbird.parser.java8.Java8BaseListener;
import org.robinbird.parser.java8.Java8Parser;

/**
 * Based on ANTLR generated Java8BaseListener, building AnalysisContext from java8 source codes
 *
 * TODO: currently, same class name in different packages will make problems. Need to fix this.
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
		Type p = analysisContext.registerPackage(packageNameList);
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
			analysisContext.pushCurrentClass(analysisContext.registerType(className, TypeCategory.CLASS));
			return;
		}
		state = State.PARSING_CLASS;
		final Type c = analysisContext.registerType(className, TypeCategory.CLASS);
		if (ctx.superclass() != null) {
			Java8Parser.ClassTypeContext classTypeContext = ctx.superclass().classType();
			Type parent = analysisContext.registerType(classTypeContext.getText(), TypeCategory.CLASS);
			c.addRelation(Relation.create(RelationCategory.INHERITANCE, parent));
		}
		if (ctx.superinterfaces() != null) {
			Java8Parser.SuperinterfacesContext superinterfacesContext = ctx.superinterfaces();
			if (superinterfacesContext.interfaceTypeList() != null) {
				for (Java8Parser.InterfaceTypeContext interfaceTypeContext : superinterfacesContext.interfaceTypeList().interfaceType()) {
					Type newInterface = analysisContext.registerType(interfaceTypeContext.getText(), TypeCategory.INTERFACE);
					c.addRelation(Relation.create(RelationCategory.REALIZATION, newInterface));
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
			analysisContext.pushCurrentClass(analysisContext.registerType(interfaceName, TypeCategory.INTERFACE));
			return;
		}
		state = State.PARSING_INTERFACE;
		Type c = analysisContext.registerType(interfaceName, TypeCategory.INTERFACE);
		analysisContext.pushCurrentClass(c);
	}

	@Override
	public void exitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
		analysisContext.popCurrentClass();
		analysisContext.setCurrentPackage(null);
	}

	private boolean isFiltered() {
		if (analysisContext.isParsingEnum()) { return true; }
		if (state == State.EXCLUDED_TYPE || state == State.INNER_CLASS) { return true; }
		checkState(analysisContext.getCurrentClass() != null, Msgs.get(CURRENT_CLASS_IS_NULL_WHILE_WALKING_THROUGH_PARSE_TREE));
		if (analysisContext.isCurrentClassTerminal()) { return true; }
		return false;
	}

	@Override
	public void enterFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
		if (isFiltered()) { return; }

		AccessModifier accessModifier = getAccessModifier(ctx.fieldModifier());
		Type type = getType(ctx.unannType());
		List<String> vars = getVariableList(ctx.variableDeclaratorList());

		for (String var : vars) {
			Instance newMember = Instance.builder()
										 .type(type)
										 .name(var)
										 .accessModifier(accessModifier)
										 .build();
			analysisContext.getCurrentClass().addInstance(newMember);
		}
	}

	@Override
	public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
		if (isFiltered()) { return; }
/*
		List<Type> params = getMethodParameterList(ctx.methodHeader().methodDeclarator());
		String methodName = ctx.methodHeader().methodDeclarator().Identifier().getText();
		long parentId = analysisContext.getCurrentClass().getId();
		String signature = MemberFunction.createMethodSignature(methodName, params);
		MemberFunction mf = analysisContext.getCurrentClass().getMemberFunction(signature);
		if (mf == null) {
			AccessModifier accessModifier = getMethodAccessModifier(ctx.methodModifier());
			Type returnType;
			if (ctx.methodHeader().result().unannType() != null) {
				returnType = getType(ctx.methodHeader().result().unannType());
			} else {
				checkState(ctx.methodHeader().result().getText().equals("void"), Msgs.get(CURRENT_CLASS_IS_NULL_WHILE_WALKING_THROUGH_PARSE_TREE));
				returnType = new Type("void", Type.Kind.PRIMITIVE);
			}
			mf = new MemberFunction(accessModifier, returnType, methodName, params);
			analysisContext.getCurrentClass().addMemberFunction(mf);
		}*/
	}



	private List<Type> getMethodParameterList(Java8Parser.MethodDeclaratorContext ctx) {
		if (ctx.formalParameterList() != null) {
			List<Type> paramList = new ArrayList<>();
			Java8Parser.FormalParametersContext fpc = ctx.formalParameterList().formalParameters();
			Java8Parser.LastFormalParameterContext lfpc = ctx.formalParameterList().lastFormalParameter();
			if (fpc != null) {
				for (Java8Parser.FormalParameterContext c : fpc.formalParameter()) {
					paramList.add(getType(c.unannType()));
				}
			}
			if (lfpc != null) {
				if (lfpc.formalParameter() != null) {
					paramList.add(getType(lfpc.formalParameter().unannType()));
				}
				// varargs case
				else {
					paramList.add(getType(lfpc.unannType()));
				}
			}
			return paramList;
		}
		return null;
	}

	@Override public void enterMethodBody(Java8Parser.MethodBodyContext ctx) {
		//System.out.println("enterMethodBody:\n" + ctx.getText());
	}

	@Override public void enterMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
		//System.out.println("enterMethodInvocation:\n" + ctx.getText());
	}

	@Override public void enterMethodReference(Java8Parser.MethodReferenceContext ctx) {
		//System.out.println("enterMethodReference:\n" + ctx.getText());
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
				accessModifier = AccessModifier.fromName(modifierContext.getText());
			} catch (IllegalArgumentException e) {
				continue; // there can be string other than public/private/protected like static
			}
		}
		return accessModifier;
	}

	private AccessModifier getMethodAccessModifier(List<Java8Parser.MethodModifierContext> methodModifierContexts) {
		AccessModifier accessModifier = AccessModifier.PRIVATE;
		for (Java8Parser.MethodModifierContext mmc : methodModifierContexts) {
			try {
				accessModifier = AccessModifier.fromName(mmc.getText());
			} catch (IllegalArgumentException e) {
				continue; // there can be string other than public/private/protected like static
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
		return analysisContext.registerType(primitiveTypeContext.getText(), TypeCategory.PRIMITIVE);
	}

	private Type getReferenceType(Java8Parser.UnannReferenceTypeContext referenceTypeContext) {
		Type type = null;
		String typeText = referenceTypeContext.getText();
		if (isPrimitiveClass(typeText)) // For example, types like String, Integer, and etc are just processed as primitive
		{
			type = Type.builder()
					   .name(typeText)
					   .category(TypeCategory.PRIMITIVE)
					   .build();
		}
		else if (isCollection(typeText))
		{
			List<Java8Parser.ReferenceTypeContext> java8RefTypes = new ArrayList<>();
			findJava8ParserReferenceTypes(referenceTypeContext, java8RefTypes);
			List<Type> refTypes = getReferenceTypes(java8RefTypes);
			type = analysisContext.registerCollection(typeText, refTypes);
		}
		else
		{
			type = analysisContext.registerType(typeText, TypeCategory.CLASS);
		}
		return type;
	}

	private Type getArrayType(Java8Parser.UnannArrayTypeContext arrayTypeContext) {
		Type aryBaseType = null;
		if (arrayTypeContext.unannClassOrInterfaceType() != null) {
			String classOrInterfaceTypeText = arrayTypeContext.unannClassOrInterfaceType().getText();
			if (isPrimitiveClass(classOrInterfaceTypeText)) {
				aryBaseType = analysisContext.registerType(classOrInterfaceTypeText, TypeCategory.PRIMITIVE);
			} else {
				aryBaseType = analysisContext.registerType(classOrInterfaceTypeText, TypeCategory.CLASS);
			}
		} else if (arrayTypeContext.unannPrimitiveType() != null) {
			aryBaseType = analysisContext.registerType(arrayTypeContext.unannPrimitiveType().getText(), TypeCategory.PRIMITIVE);
		}
		checkState(aryBaseType != null, Msgs.get(FAILED_TO_FIND_MEMBER_TYPE, arrayTypeContext.getText()));
		List<Type> types = new ArrayList<>();
		types.add(aryBaseType);
		return analysisContext.registerCollection(arrayTypeContext.getText(), types);
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

	// convert the list of Java8Parser.ReferenceTypeContext like "List<A>" to "A", "Map<B, C>" to "B, C", and etc
	// by removing specific collection type.
	// and register reference types
	private List<Type> getReferenceTypes(List<Java8Parser.ReferenceTypeContext> referenceTypeContexts) {
		List<Type> refTypes = new ArrayList<>();
		for (Java8Parser.ReferenceTypeContext context : referenceTypeContexts) {
			if (isCollection(context.getText())) {
				continue;
			}
			final TypeCategory category;
			if (isPrimitiveClass(context.getText())) {
				category = TypeCategory.PRIMITIVE;
			} else {
				category = TypeCategory.CLASS;
			}
			refTypes.add(analysisContext.registerType(context.getText(), category));
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
