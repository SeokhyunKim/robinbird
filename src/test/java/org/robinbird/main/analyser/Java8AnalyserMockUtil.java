package org.robinbird.main.analyser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.ArrayUtils;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robinbird.parser.java8.Java8Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by seokhyun on 9/11/17.
 */
public class Java8AnalyserMockUtil {

	/**
	 * Mocking Java8Parser.SuperinterfacesContext containing list of 3 Java8Parser.InterfaceTypeContext
	 * @return mocked Java8Parser.SuperinterfacesContext
	 */
	public static Java8Parser.SuperinterfacesContext mockSuperinterfacesContext() {
		Java8Parser.SuperinterfacesContext superinterfacesContext = Mockito.mock(Java8Parser.SuperinterfacesContext.class);
		Java8Parser.InterfaceTypeListContext interfaceTypeListContext = Mockito.mock(Java8Parser.InterfaceTypeListContext.class);
		List<Java8Parser.InterfaceTypeContext> interfaceTypeContexts = new ArrayList<>();
		when(superinterfacesContext.interfaceTypeList()).thenReturn(interfaceTypeListContext);
		when(interfaceTypeListContext.interfaceType()).thenReturn(interfaceTypeContexts);

		String testInterfaces[] = {"TestInterface1", "TestInterface2", "TestInterface3"};
		for (int i=0; i<testInterfaces.length; ++i) {
			Java8Parser.InterfaceTypeContext interfaceTypeContext = Mockito.mock(Java8Parser.InterfaceTypeContext.class);
			when(interfaceTypeContext.getText()).thenReturn(testInterfaces[i]);
			interfaceTypeContexts.add(interfaceTypeContext);
		}

		return superinterfacesContext;
	}

	/**
	 * Mocking Java8Parser.TypeParametersContext which has list of 2 generic type texts
	 * @return mocked Java8Parser.TypeParametersContext
	 */
	public static Java8Parser.TypeParametersContext mockTypeParametersContext() {
		Java8Parser.TypeParametersContext typeParametersContext = mock(Java8Parser.TypeParametersContext.class);
		Java8Parser.TypeParameterListContext typeParameterListContext = mock(Java8Parser.TypeParameterListContext.class);
		Java8Parser.TypeParameterContext generic1 = mock(Java8Parser.TypeParameterContext.class);
		Java8Parser.TypeParameterContext generic2 = mock(Java8Parser.TypeParameterContext.class);
		TerminalNode generic1Identifier = mock(TerminalNode.class);
		TerminalNode generic2Identifier = mock(TerminalNode.class);

		when(typeParametersContext.typeParameterList()).thenReturn(typeParameterListContext);
		when(typeParameterListContext.typeParameter(0)).thenReturn(generic1);
		when(typeParameterListContext.typeParameter(1)).thenReturn(generic2);
		when(generic1.Identifier()).thenReturn(generic1Identifier);
		when(generic2.Identifier()).thenReturn(generic2Identifier);
		when(generic1Identifier.getText()).thenReturn("S");
		when(generic2Identifier.getText()).thenReturn("T");

		return typeParametersContext;
	}

	/**
	 * Mocking Java8Parser.SuperclassContext having arbitrary superclass named TestParent
	 * @return mocked Java8Parser.SuperclassContext
	 */
	public static Java8Parser.SuperclassContext mockSuperclassContext() {
		Java8Parser.SuperclassContext superclassContext = mock(Java8Parser.SuperclassContext.class);
		Java8Parser.ClassTypeContext classTypeContext = mock(Java8Parser.ClassTypeContext.class);

		when(superclassContext.classType()).thenReturn(classTypeContext);
		when(classTypeContext.getText()).thenReturn("TestParent");

		return superclassContext;
	}

	/**
	 * Mocking Java8Parser.UnannTypeContext having primitive type 'int'
	 * @return mocked Java8Parser.UnannTypeContext
	 */
	public static Java8Parser.UnannTypeContext mockUnannTypeContextForPrimitive() {
		Java8Parser.UnannTypeContext unannTypeContext = mock(Java8Parser.UnannTypeContext.class);
		Java8Parser.UnannPrimitiveTypeContext unannPrimitiveTypeContext = mock(Java8Parser.UnannPrimitiveTypeContext.class);
		when(unannTypeContext.unannPrimitiveType()).thenReturn(unannPrimitiveTypeContext);
		when(unannPrimitiveTypeContext.getText()).thenReturn("int");
		return unannTypeContext;
	}

	/**
	 * Mocking Java8Parser.VariableDeclaratorListContext having arbitrary number of variable names list
	 * @param numVariableContext number of variables wanted to be included in the list
	 * @return mocked Java8Parser.VariableDeclaratorListContext
	 */
	public static Java8Parser.VariableDeclaratorListContext mockVariableDeclaratorListContext(int numVariableContext) {
		Java8Parser.VariableDeclaratorListContext variableDeclaratorListContext
			= mock(Java8Parser.VariableDeclaratorListContext.class);
		List<Java8Parser.VariableDeclaratorContext> variableDeclaratorContexts
			= new ArrayList<>();
		when(variableDeclaratorListContext.variableDeclarator()).thenReturn(variableDeclaratorContexts);

		String[] vars = ArrayUtils.toArray("a", "b", "c", "d", "e");
		for (int i=0; i<numVariableContext; ++i) {
			Java8Parser.VariableDeclaratorContext variableDeclaratorContext = mock(Java8Parser.VariableDeclaratorContext.class);
			Java8Parser.VariableDeclaratorIdContext variableDeclaratorIdContext = mock(Java8Parser.VariableDeclaratorIdContext.class);
			TerminalNode varName = mock(TerminalNode.class);
			when(variableDeclaratorContext.variableDeclaratorId()).thenReturn(variableDeclaratorIdContext);
			when(variableDeclaratorIdContext.Identifier()).thenReturn(varName);
			when(varName.getText()).thenReturn(vars[i%5]);

			variableDeclaratorContexts.add(variableDeclaratorContext);
		}

		return variableDeclaratorListContext;
	}

	/**
	 * Mocking Java8Parser.FieldModifierContext returning a given text when getText() is called
	 * @param text a text returned for getText() calls
	 * @return mocked Java8Parser.FieldModifierContext
	 */
	public static Java8Parser.FieldModifierContext mockFieldModifierContext(String text) {
		Java8Parser.FieldModifierContext mockFieldModifierContext = Mockito.mock(Java8Parser.FieldModifierContext.class);
		when(mockFieldModifierContext.getText()).thenReturn(text);
		return mockFieldModifierContext;
	}

	/**
	 * Create a mocking list whose iterator returns a given item num times
	 * @param item Returns item when next() is called for an iterator
	 * @param num hasNext() returns true num times
	 * @param <T> Type of an item for the mocked list
	 * @return mocked list of T
	 */
	public static <T> List<T> mockList(T item, int num) {
		List<T> mockList = Mockito.mock(List.class);
		Iterator<T> mockItr = Mockito.mock(Iterator.class);
		when(mockList.iterator()).thenReturn(mockItr);
		when(mockItr.hasNext()).thenAnswer(new Answer<Boolean>() {
			private int numCalled = 0;
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if (numCalled++ < num) {
					return true;
				}
				return false;
			}
		});
		when(mockItr.next()).thenReturn(item);
		return mockList;
	}

	/**
	 * Mock Java8Parser.UnannTypeContext with given UnannPrimitiveTypeContext
	 * @param primitiveTypeContext primitive type context
	 * @return UnannTypeContext returning primitive type context
	 */
	public static Java8Parser.UnannTypeContext mockTypeContext(Java8Parser.UnannPrimitiveTypeContext primitiveTypeContext) {
		Java8Parser.UnannTypeContext typeContext = Mockito.mock(Java8Parser.UnannTypeContext.class);
		when(typeContext.unannPrimitiveType()).thenReturn(primitiveTypeContext);
		return typeContext;
	}

	/**
	 * Mock Java8Parser.UnannTypeContext with given UnannReferenceTypeContext
	 * @param referenceTypeContext reference type context
	 * @return UnannTypeContext returning reference type context
	 */
	public static Java8Parser.UnannTypeContext mockTypeContext(Java8Parser.UnannReferenceTypeContext referenceTypeContext) {
		Java8Parser.UnannTypeContext typeContext = Mockito.mock(Java8Parser.UnannTypeContext.class);
		when(typeContext.unannReferenceType()).thenReturn(referenceTypeContext);
		return typeContext;
	}

	/**
	 * Mock Java8Parser.UnannArrayTypeContext with given UnannPrimitiveTypeContext
	 * @param primitiveTypeContext primitive type context
	 * @return UnannArrayTypeContext returning primitive type context
	 */
	public static Java8Parser.UnannArrayTypeContext mockArrayTypeContext(Java8Parser.UnannPrimitiveTypeContext primitiveTypeContext) {
		Java8Parser.UnannArrayTypeContext aryContext = Mockito.mock(Java8Parser.UnannArrayTypeContext.class);
		when(aryContext.unannPrimitiveType()).thenReturn(primitiveTypeContext);
		String aryText = primitiveTypeContext.getText() + "[]";
		when(aryContext.getText()).thenReturn(aryText);
		return aryContext;
	}

	/**
	 * Mock Java8Parser.UnannArrayTypeContext with given UnannClassOrInterfaceTypeContext
	 * @param classOrInterfaceTypeContext class or interface type context
	 * @return UnannArrayTypeContext returning class or interface type context
	 */
	public static Java8Parser.UnannArrayTypeContext mockArrayTypeContext(Java8Parser.UnannClassOrInterfaceTypeContext classOrInterfaceTypeContext) {
		Java8Parser.UnannArrayTypeContext aryContext = Mockito.mock(Java8Parser.UnannArrayTypeContext.class);
		when(aryContext.unannClassOrInterfaceType()).thenReturn(classOrInterfaceTypeContext);
		String aryText = classOrInterfaceTypeContext.getText() + "[]";
		when(aryContext.getText()).thenReturn(aryText);
		return aryContext;
	}

	/**
	 * Mock primitive type context for "int"
	 * @return mocked primitive type context
	 */
	public static Java8Parser.UnannPrimitiveTypeContext mockPrimitiveTypeContext() {
		Java8Parser.UnannPrimitiveTypeContext primitiveTypeContext = Mockito.mock(Java8Parser.UnannPrimitiveTypeContext.class);
		when(primitiveTypeContext.getText()).thenReturn("int");
		return primitiveTypeContext;
	}

	/**
	 * Mock reference type context which returns typeText
	 * @param typeText string for type text of returned mock
	 * @return mocked UnannReferenceTypeContext
	 */
	public static Java8Parser.UnannReferenceTypeContext mockReferenceTypeContex(String typeText) {
		Java8Parser.UnannReferenceTypeContext referenceTypeContext = Mockito.mock(Java8Parser.UnannReferenceTypeContext.class);
		when(referenceTypeContext.getText()).thenReturn(typeText);
		Java8Parser.UnannClassOrInterfaceTypeContext classOrInterfaceTypeContext = Mockito.mock(Java8Parser.UnannClassOrInterfaceTypeContext.class);
		when(referenceTypeContext.unannClassOrInterfaceType()).thenReturn(classOrInterfaceTypeContext);
		return referenceTypeContext;
	}

	/**
	 * Mock ClassOrInterface type context which returns typeText
	 * @param typeText string for type text of returned mock
	 * @return mocked UnannClassOrInterfaceTypeContext
	 */
	public static Java8Parser.UnannClassOrInterfaceTypeContext mockClassOrInterfaceTypeContext(String typeText) {
		Java8Parser.UnannClassOrInterfaceTypeContext classOrInterfaceTypeContext = Mockito.mock(Java8Parser.UnannClassOrInterfaceTypeContext.class);
		when(classOrInterfaceTypeContext.getText()).thenReturn(typeText);
		return classOrInterfaceTypeContext;
	}

	/**
	 * Mock ParseTree having 3 Java8Parser.ReferenceTypeContext
	 * @return mocked ParseTree having 3 Java8Parser.ReferenceTypeContext
	 */
	public static ParseTree mockParseTreeHavingReferenceTypeContext() {
		ParserRuleContext root = new ParserRuleContext();
		ParserRuleContext c1 = new Java8Parser.ReferenceTypeContext(root, 1);
		ParserRuleContext c2 = new ParserRuleContext(root, 1);
		root.addChild(c1); root.addChild(c2);
		ParserRuleContext c11 = new ParserRuleContext(c1, 1);
		ParserRuleContext c12 = new Java8Parser.ReferenceTypeContext(c1, 1);
		c1.addChild(c11); c1.addChild(c12);
		ParserRuleContext c21 = new Java8Parser.ReferenceTypeContext(c2, 1);
		c2.addChild(c21);
		return root;
	}

	/**
	 * Mock list of Java8Parser.ReferenceTypeContext:
	 * { List<Integer>, Integer, TestClass1, Excluded1, TestClass2, TestClass1, Excluded2 }.
	 * @return mocked list of Java8Parser.ReferenceTypeContext
	 */
	public static List<Java8Parser.ReferenceTypeContext> mockListOfReferenceTypeContexts() {
		List<Java8Parser.ReferenceTypeContext> refTypes = new ArrayList<>();
		List<String> texts
			= Arrays.asList("List<Integer>", "Integer", "TestClass1", "Excluded1", "TestClass2", "TestClass1", "Excluded2");
		for (String s : texts) {
			Java8Parser.ReferenceTypeContext rtc = Mockito.mock(Java8Parser.ReferenceTypeContext.class);
			when(rtc.getText()).thenReturn(s);
			refTypes.add(rtc);
		}
		return refTypes;
	}

}
