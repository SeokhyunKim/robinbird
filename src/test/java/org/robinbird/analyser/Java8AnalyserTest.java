package org.robinbird.analyser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robinbird.TestUtils;
import org.robinbird.exception.ExistingTypeNameException;
import org.robinbird.model.AccessModifier;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.ClassType;
import org.robinbird.model.Collection;
import org.robinbird.model.Package;
import org.robinbird.model.Repository;
import org.robinbird.model.Type;
import org.robinbird.parser.java8.Java8Parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robinbird.analyser.Java8AnalyserMockUtil.*;

/**
 * Created by seokhyun on 6/23/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class Java8AnalyserTest {

	@Mock TerminalNode identifier;
	@Mock Java8Parser.NormalClassDeclarationContext classDeclarationContext;
	//@Mock Java8Parser.NormalInterfaceDeclarationContext interfaceDeclarationContext;
	//@Mock Java8Parser.FieldDeclarationContext fieldDeclarationContext;

	Repository<Type> repository;
	AnalysisContext analysisContext;
	Java8Analyser java8Analyser;

	@Before
	public void setup() {
		repository = new Repository<>();
		analysisContext = new AnalysisContext(repository, new Repository<Package>());
		java8Analyser = new Java8Analyser();
		java8Analyser.setAnalysisContext(analysisContext);

		when(classDeclarationContext.Identifier()).thenReturn(identifier);
		//when(interfaceDeclarationContext.Identifier()).thenReturn(identifier);
		when(identifier.getText()).thenReturn("test");
	}

	@Test
	public void can_return_analysisContext() {
		assertTrue(java8Analyser.getAnalysisContext() == analysisContext);
	}

	@Test
	public void enterNormalClassDeclaration_can_register_new_normal_class() {
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		assertTrue(repository.size() == 1);
		assertNotNull(repository.getRepositable("test"));
		assertTrue(((Class)repository.getRepositable("test")) == analysisContext.getCurrentClass());
		System.out.println(repository.getRepositableList().size());
	}

	@Test
	public void when_already_registered_class_is_found_in_enterNormalClassDeclaration_set_its_type_to_CLASS() {
		analysisContext.registerClass("test", ClassType.INTERFACE);
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		assertTrue(repository.size() == 1);
		assertNotNull(repository.getRepositable("test"));
		assertTrue(((Class)repository.getRepositable("test")).getClassType() == ClassType.CLASS);
	}

	@Test
	public void enterNormalClassDeclaration_can_recognize_multiple_interfaces() {
		Java8Parser.SuperinterfacesContext superinterfacesContext = mockSuperinterfacesContext();
		when(classDeclarationContext.superinterfaces()).thenReturn(superinterfacesContext);
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Class cls = analysisContext.getClass("test");
		assertTrue(repository.size() == 4);
		assertTrue(cls.getInterfaces().size() == 3);
	}

	@Test(expected = ExistingTypeNameException.class)
	public void enterNormalClassDeclaration_failed_to_register_existing_normal_class() {
		repository.register(new Type("test", Type.Kind.REFERENCE));
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
	}

	@Test
	public void enterNormalClassDeclaration_can_get_list_of_generic_parameters() {
		Java8Parser.TypeParametersContext typeParametersContext = mockTypeParametersContext();
		when(classDeclarationContext.typeParameters()).thenReturn(typeParametersContext);
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		assertTrue(repository.size() == 1);
		assertNotNull(repository.getRepositable("test<S, T>"));
		assertTrue(((Class)repository.getRepositable("test<S, T>")) == analysisContext.getCurrentClass());
	}



	@Test
	public void enterNormalClassDeclaration_can_register_new_parent_class_well() {
		Java8Parser.SuperclassContext superclassContext = mockSuperclassContext();
		when(classDeclarationContext.superclass()).thenReturn(superclassContext);
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Class c = (Class)repository.getRepositable("test");

		assertTrue(repository.size() == 2); // test and TestParent
		assertTrue(c.getParent().getName().equals("TestParent"));
	}

	@Test
	public void enterNormalClassDeclaration_can_register_existing_class_as_parent_well() {
		Java8Parser.SuperclassContext superclassContext = mockSuperclassContext();
		when(classDeclarationContext.superclass()).thenReturn(superclassContext);
		repository.register(new Class("TestParent", ClassType.CLASS));
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Class c = (Class)repository.getRepositable("test");

		assertTrue(repository.size() == 2); // test and TestParent
		assertTrue(c.getParent().getName().equals("TestParent"));
		assertTrue(c.getParent().getClassType() == ClassType.CLASS);
	}

	@Test
	public void enterNormalClassDeclaration_can_register_existing_interface_as_parent_well() {
		Java8Parser.SuperclassContext superclassContext = mockSuperclassContext();
		when(classDeclarationContext.superclass()).thenReturn(superclassContext);
		repository.register(new Class("TestParent", ClassType.INTERFACE));
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Class c = (Class)repository.getRepositable("test");

		assertTrue(repository.size() == 2); // test and TestParent
		assertTrue(c.getParent().getName().equals("TestParent"));
		assertTrue(c.getParent().getClassType() == ClassType.INTERFACE);
	}

	@Test
	public void exitNormalClassDeclaration_can_set_current_things_to_null() {
		enterNormalClassDeclaration_can_register_new_normal_class();
		java8Analyser.exitNormalClassDeclaration(classDeclarationContext);
		assertNull(analysisContext.getCurrentClass());
		assertNull(analysisContext.getCurrentPackage());
	}

	@Test
	public void enterFieldDeclaration_can_recognize_a_field_of_class() {
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);

		Java8Parser.FieldDeclarationContext fieldDeclarationContext = mock(Java8Parser.FieldDeclarationContext.class);
		List<Java8Parser.FieldModifierContext> fieldModifierContexts = new ArrayList<>();
		when(fieldDeclarationContext.fieldModifier()).thenReturn(fieldModifierContexts);

		Java8Parser.UnannTypeContext unannTypeContext = mockUnannTypeContextForPrimitive();
		Java8Parser.VariableDeclaratorListContext variableDeclaratorListContext = mockVariableDeclaratorListContext(3);
		when(fieldDeclarationContext.unannType()).thenReturn(unannTypeContext);
		when(fieldDeclarationContext.variableDeclaratorList()).thenReturn(variableDeclaratorListContext);

		java8Analyser.enterFieldDeclaration(fieldDeclarationContext);
		assertTrue(analysisContext.getCurrentClass().getMemberVariables().size() == 3);
	}

	@Test
	public void getAccessModifier_can_return_correctly_with_accessModifierContext_PUBLIC() throws Exception {
		Method getAccessModifier = TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getAccessModifier", List.class);
		Java8Parser.FieldModifierContext mockedFieldModifierContext = mockFieldModifierContext("public");
		List<Java8Parser.FieldModifierContext> mockedFMCList = mockList(mockedFieldModifierContext, 1);
		AccessModifier result = (AccessModifier)getAccessModifier.invoke(java8Analyser, mockedFMCList);
		assertTrue(result == AccessModifier.PUBLIC);
	}

	@Test
	public void when_getAccessModifier_is_called_with_invalid_accessModifierContext_PRIVATE_is_returned() throws Exception {
		Method getAccessModifier = TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getAccessModifier", List.class);
		Java8Parser.FieldModifierContext mockedFieldModifierContext = mockFieldModifierContext("dummy");
		List<Java8Parser.FieldModifierContext> mockedFMCList = mockList(mockedFieldModifierContext, 1);
		AccessModifier result = (AccessModifier)getAccessModifier.invoke(java8Analyser, mockedFMCList);
		assertTrue(result == AccessModifier.PRIVATE);
	}

	@Test(expected = InvocationTargetException.class)
	public void when_invalid_type_context_is_given_then_exception_is_thrown() throws Exception {
		Method getType = TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getType", Java8Parser.UnannTypeContext.class);
		Java8Parser.UnannTypeContext typeContext = Mockito.mock(Java8Parser.UnannTypeContext.class);
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Type type = (Type)getType.invoke(java8Analyser, typeContext);
		java8Analyser.exitNormalClassDeclaration(classDeclarationContext);
	}

	@Test
	public void when_a_primitive_type_is_given_then_getType_can_create_PRIMITIVE_Type() throws Exception {
		Method getType = TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getType", Java8Parser.UnannTypeContext.class);
		Java8Parser.UnannTypeContext typeContext = mockTypeContext(mockPrimitiveTypeContext());
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Type type = (Type)getType.invoke(java8Analyser, typeContext);
		java8Analyser.exitNormalClassDeclaration(classDeclarationContext);
		assertTrue(type.getName().equals("int"));
		assertTrue(type.getKind() == Type.Kind.PRIMITIVE);
	}

	@Test
	public void when_a_primitive_type_class_is_given_then_getType_can_create_PRIMITIVE_Type() throws Exception {
		Method getType = TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getType", Java8Parser.UnannTypeContext.class);
		Java8Parser.UnannTypeContext typeContext = mockTypeContext(mockReferenceTypeContex("Integer"));
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Type type = (Type)getType.invoke(java8Analyser, typeContext);
		java8Analyser.exitNormalClassDeclaration(classDeclarationContext);
		assertTrue(type.getName().equals("Integer"));
		assertTrue(type.getKind() == Type.Kind.PRIMITIVE);
	}

	@Test
	public void when_a_collection_is_given_then_getType_can_create_Collection_Type() throws Exception {
		Method getType = TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getType", Java8Parser.UnannTypeContext.class);
		Java8Parser.UnannReferenceTypeContext root = Mockito.mock(Java8Parser.UnannReferenceTypeContext.class);
		when(root.getText()).thenReturn("List<int>");
		Java8Parser.UnannReferenceTypeContext c1 = Mockito.mock(Java8Parser.UnannReferenceTypeContext.class);
		Java8Parser.UnannReferenceTypeContext c2 = Mockito.mock(Java8Parser.UnannReferenceTypeContext.class);
		when(root.getChild(0)).thenReturn(c1);
		when(root.getChild(1)).thenReturn(c2);
		Java8Parser.UnannClassOrInterfaceTypeContext classOrInterfaceTypeContext = Mockito.mock(Java8Parser.UnannClassOrInterfaceTypeContext.class);
		when(root.unannClassOrInterfaceType()).thenReturn(classOrInterfaceTypeContext);
		Java8Parser.UnannTypeContext typeContext = mockTypeContext(root);
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Type type = (Type)getType.invoke(java8Analyser, typeContext);
		java8Analyser.exitNormalClassDeclaration(classDeclarationContext);
		assertTrue(type instanceof Collection);
	}

	@Test
	public void when_a_class_type_is_given_then_getType_can_create_REFERENCE_Type() throws Exception {
		Method getType = TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getType", Java8Parser.UnannTypeContext.class);
		Java8Parser.UnannTypeContext typeContext = mockTypeContext(mockReferenceTypeContex("TestClass"));
		// case 1: existing class
		analysisContext.registerClass("TestClass", ClassType.CLASS);
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Type type = (Type)getType.invoke(java8Analyser, typeContext);
		java8Analyser.exitNormalClassDeclaration(classDeclarationContext);
		assertTrue(type.getName().equals("TestClass"));
		assertTrue(type.getKind() == Type.Kind.REFERENCE);
		// case 2: new class
		repository.clean();
		analysisContext.registerClass("TestClass", ClassType.CLASS);
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		type = (Type)getType.invoke(java8Analyser, typeContext);
		java8Analyser.exitNormalClassDeclaration(classDeclarationContext);
		assertTrue(type.getName().equals("TestClass"));
		assertTrue(type.getKind() == Type.Kind.REFERENCE);
	}

	@Test
	public void when_excluded_class_is_given_then_it_is_not_registered_in_AnalysisContext() throws Exception {
		Method getType = TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getType", Java8Parser.UnannTypeContext.class);
		Java8Parser.UnannTypeContext typeContext = mockTypeContext(mockReferenceTypeContex("TestClass"));
		analysisContext.setExcludedClassPatterns(Arrays.asList(Pattern.compile("Test.+")));
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Type type = (Type)getType.invoke(java8Analyser, typeContext);
		java8Analyser.exitNormalClassDeclaration(classDeclarationContext);
		assertTrue(type.getName().equals("TestClass"));
		assertTrue(type.getKind() == Type.Kind.REFERENCE);
		assertNull(analysisContext.getClass("TestClass"));
	}

	@Test
	public void when_array_type_is_given_then_getType_can_create_Collection_Type() throws Exception {
		Method getType = TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getType", Java8Parser.UnannTypeContext.class);
		Java8Parser.UnannArrayTypeContext atc = mockArrayTypeContext(mockPrimitiveTypeContext());
		Java8Parser.UnannReferenceTypeContext rtc = Mockito.mock(Java8Parser.UnannReferenceTypeContext.class);
		when(rtc.unannArrayType()).thenReturn(atc);
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Type type = (Type)getType.invoke(java8Analyser, mockTypeContext(rtc));
		java8Analyser.exitNormalClassDeclaration(classDeclarationContext);
		assertTrue(type instanceof Collection);
	}

	@Test
	public void findJava8ParserReferenceTypes_can_find_all_ReferenceTypeContext_from_ParseTree() throws Exception {
		ParseTree tree = mockParseTreeHavingReferenceTypeContext();
		List<Java8Parser.ReferenceTypeContext> refTypes = new ArrayList<>();
		Method findJava8ParserReferenceTypes
			= TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(),
			"findJava8ParserReferenceTypes",
			ParseTree.class, List.class);
		findJava8ParserReferenceTypes.invoke(java8Analyser, tree, refTypes);
		assertTrue(refTypes.size() == 3);
	}

	@Test
	public void getReferenceTypes_can_return_List_of_Type_from_List_of_ReferenceTypeContext() throws Exception {
		List<Java8Parser.ReferenceTypeContext> refTypes = mockListOfReferenceTypeContexts();
		Method getReferenceTypes
			= TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getReferenceTypes", List.class);
		analysisContext.setExcludedClassPatterns(Arrays.asList(Pattern.compile("Excluded.*")));
		List<Type> types = (List<Type>)getReferenceTypes.invoke(java8Analyser, refTypes);
		assertTrue(types.size() == 6);
		assertTrue(analysisContext.getClasses().size() == 2);
	}

	@Test
	public void getArrayType_can_recognize_primitive_type_array() throws Exception {
		Java8Parser.UnannArrayTypeContext atc = mockArrayTypeContext(mockPrimitiveTypeContext());
		Method getArrayType
			= TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getArrayType", Java8Parser.UnannArrayTypeContext.class);
		Collection collection = (Collection)getArrayType.invoke(java8Analyser, atc);
		assertTrue(collection.getTypes().get(0).getName().equals("int"));
	}

	@Test
	public void getArrayType_can_recognize_primitive_class_array() throws Exception {
		Java8Parser.UnannArrayTypeContext atc
			= mockArrayTypeContext(mockClassOrInterfaceTypeContext("Integer"));
		Method getArrayType
			= TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getArrayType", Java8Parser.UnannArrayTypeContext.class);
		Collection collection = (Collection)getArrayType.invoke(java8Analyser, atc);
		assertTrue(collection.getTypes().get(0).getName().equals("Integer"));
	}

	@Test
	public void getArrayType_can_recognize_class_array() throws Exception {
		Java8Parser.UnannArrayTypeContext atc
			= mockArrayTypeContext(mockClassOrInterfaceTypeContext("TestClass"));
		Method getArrayType
			= TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getArrayType", Java8Parser.UnannArrayTypeContext.class);
		Collection collection = (Collection)getArrayType.invoke(java8Analyser, atc);
		assertTrue(collection.getTypes().get(0).getName().equals("TestClass"));
		assertTrue(analysisContext.getClasses().size() == 1);
	}

	@Test
	public void getArrayType_can_filter_out_excluded_pattern() throws Exception {
		Java8Parser.UnannArrayTypeContext atc
			= mockArrayTypeContext(mockClassOrInterfaceTypeContext("Excluded"));
		Method getArrayType
			= TestUtils.getAccessiblePrivateMethod(java8Analyser.getClass(), "getArrayType", Java8Parser.UnannArrayTypeContext.class);
		analysisContext.setExcludedClassPatterns(Arrays.asList(Pattern.compile("Excluded")));
		Collection collection = (Collection)getArrayType.invoke(java8Analyser, atc);
		assertTrue(collection.getTypes().get(0).getName().equals("Excluded"));
		assertTrue(analysisContext.getClasses().size() == 0);
	}


}
