package org.robinbird.analyser;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.robinbird.exception.ExistingTypeNameException;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.ClassType;
import org.robinbird.model.Repository;
import org.robinbird.model.Type;
import org.robinbird.parser.java8.Java8Parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by seokhyun on 6/23/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class Java8AnalyserTest {

	@Mock TerminalNode identifier;
	@Mock Java8Parser.NormalClassDeclarationContext classDeclarationContext;
	@Mock Java8Parser.NormalInterfaceDeclarationContext interfaceDeclarationContext;
	@Mock Java8Parser.FieldDeclarationContext fieldDeclarationContext;

	Repository<Type> repository;
	AnalysisContext analysisContext;
	Java8Analyser java8Analyser;

	@Before
	public void setup() {
		repository = new Repository<>();
		analysisContext = new AnalysisContext(repository);
		java8Analyser = new Java8Analyser();
		java8Analyser.setAnalysisContext(analysisContext);

		when(classDeclarationContext.Identifier()).thenReturn(identifier);
		when(interfaceDeclarationContext.Identifier()).thenReturn(identifier);
		when(identifier.getText()).thenReturn("test");
	}

	@Test
	public void can_register_new_normal_class() {
		repository.clean();
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		assertTrue(repository.size() == 1);
		assertNotNull(repository.getRepositable("test"));
		assertTrue(((Class)repository.getRepositable("test")) == analysisContext.getCurrentClass());
	}

	@Test(expected = ExistingTypeNameException.class)
	public void failed_to_register_existing_normal_class() {
		repository.clean();
		repository.register(new Type("test"));
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
	}

	@Test
	public void can_get_list_of_generic_parameters() {
		Java8Parser.TypeParametersContext typeParametersContext = mockTypeParametersContext();
		when(classDeclarationContext.typeParameters()).thenReturn(typeParametersContext);
		repository.clean();
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		assertTrue(repository.size() == 1);
		assertNotNull(repository.getRepositable("test<S, T>"));
		assertTrue(((Class)repository.getRepositable("test<S, T>")) == analysisContext.getCurrentClass());
	}

	private Java8Parser.TypeParametersContext mockTypeParametersContext() {
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

	@Test
	public void can_register_new_parent_class_well() {
		Java8Parser.SuperclassContext superclassContext = mockSuperclassContext();
		when(classDeclarationContext.superclass()).thenReturn(superclassContext);
		repository.clean();
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Class c = (Class)repository.getRepositable("test");

		assertTrue(repository.size() == 2); // test and TestParent
		assertTrue(c.getParent().getName().equals("TestParent"));
	}

	@Test
	public void can_register_existing_class_as_parent_well() {
		Java8Parser.SuperclassContext superclassContext = mockSuperclassContext();
		when(classDeclarationContext.superclass()).thenReturn(superclassContext);
		repository.clean();
		repository.register(new Class("TestParent", ClassType.CLASS));
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Class c = (Class)repository.getRepositable("test");

		assertTrue(repository.size() == 2); // test and TestParent
		assertTrue(c.getParent().getName().equals("TestParent"));
		assertTrue(c.getParent().getClassType() == ClassType.CLASS);
	}

	@Test
	public void can_register_existing_interface_as_parent_well() {
		Java8Parser.SuperclassContext superclassContext = mockSuperclassContext();
		when(classDeclarationContext.superclass()).thenReturn(superclassContext);
		repository.clean();
		repository.register(new Class("TestParent", ClassType.INTERFACE));
		java8Analyser.enterNormalClassDeclaration(classDeclarationContext);
		Class c = (Class)repository.getRepositable("test");

		assertTrue(repository.size() == 2); // test and TestParent
		assertTrue(c.getParent().getName().equals("TestParent"));
		assertTrue(c.getParent().getClassType() == ClassType.INTERFACE);
	}

	private Java8Parser.SuperclassContext mockSuperclassContext() {
		Java8Parser.SuperclassContext superclassContext = mock(Java8Parser.SuperclassContext.class);
		Java8Parser.ClassTypeContext classTypeContext = mock(Java8Parser.ClassTypeContext.class);

		when(superclassContext.classType()).thenReturn(classTypeContext);
		when(classTypeContext.getText()).thenReturn("TestParent");

		return superclassContext;
	}
}
