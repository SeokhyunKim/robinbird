package org.robinbird.analyser;

import net.bytebuddy.implementation.bytecode.Throw;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.ArrayUtils;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robinbird.parser.java8.Java8Parser;

import java.util.ArrayList;
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
	 * Mock Java8Parser.UnannTypeContext for primitive and reference types
	 * @param whichType can be primitive, reference_primitiveClass, reference_collection, reference_array, and reference
	 * @return mocked UnannTypeContext
	 */
	/*public static Java8Parser.UnannTypeContext mockTypeContext(String whichType) {
		Java8Parser.UnannTypeContext typeContext = Mockito.mock(Java8Parser.UnannTypeContext.class);
		if (whichType == "primitive") {
			Java8Parser.UnannPrimitiveTypeContext primitiveTypeContext = mockPrimitiveTypeContext();
			when(typeContext.unannPrimitiveType()).thenReturn(primitiveTypeContext);
		} else if (whichType.contains("reference")) {
			Java8Parser.UnannReferenceTypeContext referenceTypeContext = Mockito.mock(Java8Parser.UnannReferenceTypeContext.class);
			when(typeContext.unannReferenceType()).thenReturn(referenceTypeContext);
			Java8Parser.UnannClassOrInterfaceTypeContext classOrInterfaceTypeContext = Mockito.mock(Java8Parser.UnannClassOrInterfaceTypeContext.class);
			Java8Parser.UnannArrayTypeContext arrayTypeContext = Mockito.mock(Java8Parser.UnannArrayTypeContext.class);
			if (whichType.contains("primitiveClass")) {
				when(referenceTypeContext.unannClassOrInterfaceType()).thenReturn(classOrInterfaceTypeContext);
				when(referenceTypeContext.getText()).thenReturn("String");
			} else if (whichType.contains("collection")) {
				when(referenceTypeContext.unannClassOrInterfaceType()).thenReturn(classOrInterfaceTypeContext);
				when(referenceTypeContext.getText()).thenReturn("List<List<String>>");
				// need to check...
			} else if (whichType.contains("array")) {
				when(referenceTypeContext.unannArrayType()).thenReturn(arrayTypeContext);
				if (whichType.contains("primitiveArray")) {

				} else {

				}
			} else {

			}
		}
		return typeContext;
	}*/

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
	 * @return mo
	 */
	public static Java8Parser.UnannReferenceTypeContext mockReferenceTypeContex(String typeText) {
		Java8Parser.UnannReferenceTypeContext referenceTypeContext = Mockito.mock(Java8Parser.UnannReferenceTypeContext.class);
		when(referenceTypeContext.getText()).thenReturn(typeText);
		Java8Parser.UnannClassOrInterfaceTypeContext classOrInterfaceTypeContext = Mockito.mock(Java8Parser.UnannClassOrInterfaceTypeContext.class);
		when(referenceTypeContext.unannClassOrInterfaceType()).thenReturn(classOrInterfaceTypeContext);
		return referenceTypeContext;
	}

}
