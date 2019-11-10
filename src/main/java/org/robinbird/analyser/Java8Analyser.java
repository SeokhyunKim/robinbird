package org.robinbird.analyser;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.model.ModelConstants.METHOD_PARAMETER_SEPERATOR;
import static org.robinbird.util.Msgs.Key.CURRENT_CLASS_IS_NULL_WHILE_WALKING_THROUGH_PARSE_TREE;
import static org.robinbird.util.Msgs.Key.INTERNAL_ERROR;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.robinbird.exception.RobinbirdException;
import org.robinbird.model.AccessLevel;
import org.robinbird.model.Analyser;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.Collection;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Function;
import org.robinbird.model.ModelConstants;
import org.robinbird.model.Package;
import org.robinbird.model.Varargs;
import org.robinbird.parser.java8.Java8BaseListener;
import org.robinbird.parser.java8.Java8Parser;
import org.robinbird.util.Msgs;

@Slf4j
public class Java8Analyser extends Java8BaseListener implements Analyser {

    private enum State { NONE, PARSING_CLASS, PARSING_INTERFACE, EXCLUDED_TYPE, INNER_CLASS};

    private AnalysisContext analysisContext;
    State state = State.NONE;

    public void setAnalysisContext(AnalysisContext analysisContext) { this.analysisContext = analysisContext; }

    public AnalysisContext getAnalysisContext() { return analysisContext; }

    @Override public void enterEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
        analysisContext.setParsingEnum(true);
    }

    @Override public void exitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
        analysisContext.setParsingEnum(false);
    }

    @Override
    public void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        final List<String> packageNameList = new ArrayList<>();
        for (TerminalNode identifier : ctx.Identifier()) {
            packageNameList.add(identifier.getText());
        }
        final Package p = analysisContext.registerPackage(packageNameList);
        analysisContext.setCurrentPackage(p);
    }

    @Override
    public void exitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        analysisContext.getCurrentPackage().persist();
    }

    @Override
    public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        final List<String> templateParams = getTemplateClassParameters(ctx.typeParameters());
        final String className = ctx.Identifier().getText();
        final ComponentCategory category = templateParams.isEmpty() ? ComponentCategory.CLASS : ComponentCategory.TEMPLATE_CLASS;
        if (analysisContext.isExcluded(className)) {
            state = State.EXCLUDED_TYPE;
            return;
        }
        if (analysisContext.getCurrent() != null) {
            state = State.INNER_CLASS;
            final Class newClass = analysisContext.registerClass(className, category);
            if (category == ComponentCategory.TEMPLATE_CLASS && !templateParams.isEmpty()) {
                newClass.setTemplateVariables(templateParams);
            }
            analysisContext.pushCurrent(newClass);
            return;
        }
        state = State.PARSING_CLASS;
        final Class newClass = analysisContext.registerClass(className, category);
        if (category == ComponentCategory.TEMPLATE_CLASS) {
            newClass.setTemplateVariables(templateParams);
        }
        if (ctx.superclass() != null) {
            Java8Parser.ClassTypeContext classTypeContext = ctx.superclass().classType();
            Class parent = analysisContext.getClass(classTypeContext.getText());
            if (parent == null) {
                // parent class is not defined yet. so, category is not certain.
                // category will be updated when the parent is parsed.
                parent = analysisContext.registerClass(classTypeContext.getText(), ComponentCategory.CLASS);
            }
            newClass.setParent(parent);
        }
        if (ctx.superinterfaces() != null) {
            Java8Parser.SuperinterfacesContext superinterfacesContext = ctx.superinterfaces();
            if (superinterfacesContext.interfaceTypeList() != null) {
                for (Java8Parser.InterfaceTypeContext interfaceTypeContext : superinterfacesContext.interfaceTypeList().interfaceType()) {
                    Class implementingInterface = analysisContext.getClass(interfaceTypeContext.getText());
                    if (implementingInterface == null) {
                        implementingInterface = analysisContext.registerClass(interfaceTypeContext.getText(), ComponentCategory.INTERFACE);
                    }
                    newClass.addInterface(implementingInterface);
                }
            }
        }
        analysisContext.pushCurrent(newClass);
    }

    @Override
    public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        analysisContext.getCurrent().persist();
        analysisContext.popCurrent();
        analysisContext.setCurrentPackage(null);
    }

    @Override
    public void enterNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        final String interfaceName = ctx.Identifier().getText();
        if (analysisContext.isExcluded(interfaceName)) {
            state = State.EXCLUDED_TYPE;
            return;
        }
        if (analysisContext.getCurrent() != null) {
            state = State.INNER_CLASS;
            final Class newInterface = analysisContext.registerClass(interfaceName, ComponentCategory.INTERFACE);
            analysisContext.pushCurrent(newInterface);
            return;
        }
        state = State.PARSING_INTERFACE;
        Class newInterface =analysisContext.registerClass(interfaceName, ComponentCategory.INTERFACE);
        analysisContext.pushCurrent(newInterface);
    }

    @Override
    public void exitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        analysisContext.getCurrent().persist();
        analysisContext.popCurrent();
        analysisContext.setCurrentPackage(null);
    }

    @Override
    public void enterFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
        if (isFiltered()) {
            return;
        }
        final AccessLevel accessLevel = getFieldAccessLevel(ctx.fieldModifier());
        final List<String> varNames = getVariableList(ctx.variableDeclaratorList());
        final Component type = getType(ctx.unannType());
        addMemberVariable(accessLevel, type, varNames);
    }

    private Component getType(Java8Parser.UnannTypeContext ctx) {
        final Optional<Component> primitiveTypeOpt = getPrimitiveType(ctx);
        if (primitiveTypeOpt.isPresent()) {
            return primitiveTypeOpt.get();
        }
        final Optional<Component> referenceTypeOpt = getReferenceType(ctx);
        if (referenceTypeOpt.isPresent()) {
            return referenceTypeOpt.get();
        }
        final Optional<Component> collectionOrArrayOpt = getCollectionOrArrayType(ctx);
        if (collectionOrArrayOpt.isPresent()) {
            return collectionOrArrayOpt.get();
        }
        throw new RobinbirdException(Msgs.get(INTERNAL_ERROR));
    }

    private Optional<Component> getPrimitiveType(Java8Parser.UnannTypeContext ctx) {
        if (ctx.unannPrimitiveType() != null) {
            final String typeName = ctx.unannPrimitiveType().getText();
            final Component primitiveComponent = analysisContext.registerPrimitiveType(typeName);
            return Optional.of(primitiveComponent);
        } else if (ctx.unannReferenceType() != null) {
            Java8Parser.UnannReferenceTypeContext referenceTypeContext = ctx.unannReferenceType();
            if (referenceTypeContext.unannClassOrInterfaceType() != null) {
                final String typeName = referenceTypeContext.unannClassOrInterfaceType().getText();
                if (isPrimitiveType(typeName)) {
                    final Component primitiveComponent = analysisContext.registerPrimitiveType(typeName);
                    return Optional.of(primitiveComponent);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<Component> getReferenceType(Java8Parser.UnannTypeContext ctx) {
        if (ctx.unannReferenceType() == null || ctx.unannReferenceType().unannClassOrInterfaceType() == null) {
            return Optional.empty();
        }
        Java8Parser.UnannClassOrInterfaceTypeContext classOrInterfaceTypeContext = ctx.unannReferenceType().unannClassOrInterfaceType();
        final String typeName = classOrInterfaceTypeContext.getText();
        if (getCollectionTypeName(typeName) != null) {
            return Optional.empty();
        }
        return Optional.of(analysisContext.register(typeName));
    }

    private Optional<Component> getCollectionOrArrayType(Java8Parser.UnannTypeContext ctx) {
        if (ctx.unannReferenceType() == null) {
            return Optional.empty();
        }
        Java8Parser.UnannReferenceTypeContext referenceTypeContext = ctx.unannReferenceType();
        if (referenceTypeContext.unannClassOrInterfaceType() != null) {
            final String typeName = referenceTypeContext.unannClassOrInterfaceType().getText();
            if (getCollectionTypeName(typeName) != null) {
                final List<Java8Parser.ReferenceTypeContext> java8RefTypes = findJava8ParserReferenceTypes(referenceTypeContext);
                final List<Component> types = getReferenceComponents(java8RefTypes);
                final String collectionTypeName = getCollectionTypeName(typeName);
                final Collection collection = analysisContext.registerCollection(collectionTypeName, types);
                return Optional.of(collection);
            }
            return Optional.empty();
        } else if (referenceTypeContext.unannArrayType() != null) {
            final Java8Parser.UnannArrayTypeContext arrayTypeContext = referenceTypeContext.unannArrayType();
            if (arrayTypeContext.unannClassOrInterfaceType() != null) {
                final String classOrInterfaceTypText = arrayTypeContext.unannClassOrInterfaceType().getText();
                if (isPrimitiveType(classOrInterfaceTypText)) {
                    return Optional.of(analysisContext.registerPrimitiveTypeArray(classOrInterfaceTypText));
                } else {
                    return Optional.of(analysisContext.registerReferenceTypeArray(classOrInterfaceTypText));
                }
            } else if (arrayTypeContext.unannPrimitiveType() != null) {
                final String typeText = arrayTypeContext.unannPrimitiveType().getText();
                return Optional.of(analysisContext.registerPrimitiveTypeArray(typeText));
            }
        }
        return Optional.empty();
    }

    private void addMemberVariable(@NonNull final AccessLevel accessLevel,
                                   @NonNull final Component component,
                                   @NonNull final List<String> varNames) {
        varNames.forEach(var -> analysisContext.getCurrent().addMemberVariable(component, var, accessLevel));
    }

    @Override
    public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        if (isFiltered()) {
            return;
        }
        final List<Component> params = getMethodParameterList(ctx.methodHeader().methodDeclarator());
        final String methodName = ctx.methodHeader().methodDeclarator().Identifier().getText();
        log.debug("methodName={}, params={}", methodName, params);
        final String signature = createMethodSignature(methodName, params);
        AccessLevel accessLevel = getMethodAccessLevel(ctx.methodModifier());
        final Component returnType;
        if (ctx.methodHeader().result().unannType() != null) {
            returnType = getType(ctx.methodHeader().result().unannType());
        } else {
            returnType = analysisContext.registerPrimitiveType(ModelConstants.VOID);
        }
        final Function memberFunction = analysisContext.registerFunction(signature, params, returnType);
        analysisContext.getCurrent().addMemberFunction(memberFunction, methodName, accessLevel);
    }

    private List<Component> getMethodParameterList(Java8Parser.MethodDeclaratorContext ctx) {
        if (ctx.formalParameterList() != null) {
            List<Component> paramList = new ArrayList<>();
            Java8Parser.FormalParametersContext fpc = ctx.formalParameterList().formalParameters();
            Java8Parser.LastFormalParameterContext lfpc = ctx.formalParameterList().lastFormalParameter();
            if (fpc != null) {
                for (Java8Parser.FormalParameterContext c : fpc.formalParameter()) {
                    final Component type = getType(c.unannType());
                    paramList.add(type);
                }
            }
            if (lfpc != null) {
                if (lfpc.formalParameter() != null) {
                    final Component type = getType(lfpc.formalParameter().unannType());
                    paramList.add(type);
                }
                // varargs case
                else {
                    final Component baseType = getType(lfpc.unannType());
                    final Varargs varargs = analysisContext.registerVarargs(baseType);
                    paramList.add(varargs);
                }
            }
            return paramList;
        }
        return null;
    }

    @Override
    public void enterMethodBody(Java8Parser.MethodBodyContext ctx) {
        log.debug("enterMethodBody: {}", ctx.getText());
    }

    @Override
    public void enterMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        log.debug("enterMethodInvocation: {}", ctx.getText());
    }

    @Override
    public void enterMethodReference(Java8Parser.MethodReferenceContext ctx) {
        log.debug("enterMethodReference: {}", ctx.getText());
    }

    private List<String> getTemplateClassParameters(Java8Parser.TypeParametersContext ctx) {
        if (ctx == null) {
            return Lists.newArrayList();
        }

        final List<String> templateParams = new ArrayList<>();
        for (final Java8Parser.TypeParameterContext tpCtx : ctx.typeParameterList().typeParameter()) {
            templateParams.add(tpCtx.Identifier().getText());
        }
        return templateParams;
    }

    private boolean isFiltered() {
        if (analysisContext.isParsingEnum() || state == State.EXCLUDED_TYPE || state == State.INNER_CLASS) {
            return true;
        }
        checkState(analysisContext.getCurrent() != null,
                   Msgs.get(CURRENT_CLASS_IS_NULL_WHILE_WALKING_THROUGH_PARSE_TREE));
        if (analysisContext.isCurrentTerminal()) {
            return true;
        }
        return false;
    }

    private AccessLevel getFieldAccessLevel(List<Java8Parser.FieldModifierContext> fieldModifierContexts) {
        AccessLevel accessLevel = AccessLevel.PRIVATE;
        for (Java8Parser.FieldModifierContext modifierContext : fieldModifierContexts) {
            // from a modifier list, there would be only one access level
            if (AccessLevel.isAccessLevelString(modifierContext.getText())) {
                accessLevel = AccessLevel.fromString(modifierContext.getText());
            }
        }
        return accessLevel;
    }

    private AccessLevel getMethodAccessLevel(List<Java8Parser.MethodModifierContext> methodModifierContexts) {
        AccessLevel accessLevel = AccessLevel.PRIVATE;
        for (Java8Parser.MethodModifierContext mmc : methodModifierContexts) {
            // from a modifier list, there would be only one access level
            if (AccessLevel.isAccessLevelString(mmc.getText())) {
                accessLevel = AccessLevel.fromString(mmc.getText());
            }
        }
        return accessLevel;
    }

    private List<Java8Parser.ReferenceTypeContext> findJava8ParserReferenceTypes(ParseTree node) {
        List<Java8Parser.ReferenceTypeContext> refTypes = new LinkedList<>();
        if (node instanceof Java8Parser.ReferenceTypeContext) {
            refTypes.add((Java8Parser.ReferenceTypeContext)node);
        }
        int i=0;
        while (node.getChild(i) != null) {
            List<Java8Parser.ReferenceTypeContext> childRefTypes =
                    findJava8ParserReferenceTypes(node.getChild(i++));
            refTypes.addAll(childRefTypes);
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

    // convert the list of Java8Parser.ReferenceTypeContext like "List<A>, A, Map<B, C>, B, C" to "A, B, C" by removing specific collection implementations.
    // and register reference types
    private List<Component> getReferenceComponents(List<Java8Parser.ReferenceTypeContext> referenceTypeContexts) {
        List<Component> refComponents = new ArrayList<>();
        for (Java8Parser.ReferenceTypeContext context : referenceTypeContexts) {
            if (getCollectionTypeName(context.getText()) != null) {
                continue;
            }
            if (isPrimitiveType(context.getText())) {
                refComponents.add(analysisContext.registerPrimitiveType(context.getText()));
            } else {
                refComponents.add(analysisContext.register(context.getText()));
            }
        }
        return refComponents;
    }

    private boolean isPrimitiveType(String text) {
        String[] types = { "Byte", "Short", "Integer", "Long", "Character", "Float", "Double", "Boolean", "String",
                           "int", "char", "double", "boolean"};
        for (String type : types) {
            if (text.startsWith(type)) { return true; }
        }
        return false;
    }

    private String getCollectionTypeName(String text) {
        String[] collections = { "List", "LinkedList", "ArrayList",
                                 "Set", "TreeSet", "HashSet", "LinkedHashSet",
                                 "Map", "HashMap", "TreeMap", "ConcurrentMap"};
        for (String collection : collections) {
            if (text.startsWith(collection)) { return collection; }
        }
        return null;
    }

    private String createMethodSignature(@NonNull final String methodName, @Nullable final List<Component> params) {
        log.debug("methodName={}, params={}", methodName, params);
        StringBuilder stringBuilder = new StringBuilder(methodName);
        if (params != null) {
            for (Component t : params) {
                stringBuilder.append(METHOD_PARAMETER_SEPERATOR);
                stringBuilder.append(t.getName());
            }
        }
        return stringBuilder.toString();
    }

}
