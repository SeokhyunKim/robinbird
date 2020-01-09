package org.robinbird.analyser;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.robinbird.TestUtils;
import org.robinbird.model.AccessLevel;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Array;
import org.robinbird.model.Class;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Container;
import org.robinbird.model.Function;
import org.robinbird.model.Package;
import org.robinbird.model.Varargs;
import org.robinbird.parser.java8.Java8Lexer;
import org.robinbird.parser.java8.Java8Parser;

@RunWith(MockitoJUnitRunner.class)
public class Java8AnalyserTest {

    @Mock
    private AnalysisContext analysisContext;

    private Java8Analyser java8Analyser;

    @Before
    public void setUp() {
        java8Analyser = new Java8Analyser();
        java8Analyser.setAnalysisContext(analysisContext);
        Class c = mock(Class.class);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(c);
        Package p = mock(Package.class);
        when(analysisContext.getCurrentPackage()).thenReturn(p);
    }

    @Test
    public void test_enterPackageDeclaration_worksFine_whenPackageDefined() {
        String code =
                "package test1.test2.test3;\n" +
                "public class Bla {\n" +
                "}";
        Package p = mock(Package.class);
        when(analysisContext.registerPackage(any())).thenReturn(p);
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

        analyseTestCode(code);

        verify(analysisContext, times(1)).registerPackage(captor.capture());
        List<String> packageNameList = (List<String>)captor.getValue();
        Assert.assertThat(packageNameList.get(0), is("test1"));
        Assert.assertThat(packageNameList.get(1), is("test2"));
        Assert.assertThat(packageNameList.get(2), is("test3"));
    }

    @Test
    public void test_exitPackageDeclaration_worksFine_whenPackageDefined() {
        String code =
                "package test1.test2.test3;\n" +
                "public class Bla {\n" +
                "}";
        analyseTestCode(code);
        verify(analysisContext, times(1)).getCurrentPackage();
    }

    @Test
    public void test_enterNormalClassDeclaration_excludeClassAndChangeState_forExcludedClass() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "}";
        when(analysisContext.isExcluded("Bla")).thenReturn(true);
        Class c = mock(Class.class);
        when(analysisContext.getCurrent()).thenReturn(c);
        analyseTestCode(code);
        String state = TestUtils.getMemberVariable(java8Analyser, "state").toString();
        Assert.assertThat(state, is("EXCLUDED_TYPE"));
    }

    @Test
    public void test_enterNormalClassDeclaration_skipParsing_forInnerClass() {
        String code =
                "package test1.test2.test3;\n"
                        + "public class Inner {\n"
                        + "}";
        Class currentMock = mock(Class.class);
        when(analysisContext.getCurrent()).thenReturn(currentMock);
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ComponentCategory> captor2 = ArgumentCaptor.forClass(ComponentCategory.class);
        analyseTestCode(code);
        verify(analysisContext, times(1)).registerClass(captor1.capture(), captor2.capture());

        Assert.assertThat(captor1.getValue(), is("Inner"));
        Assert.assertThat(captor2.getValue(), is(ComponentCategory.CLASS));
        String state = TestUtils.getMemberVariable(java8Analyser, "state").toString();
        Assert.assertThat(state, is("INNER_CLASS"));
    }

    @Test
    public void test_enterNormalClassDeclaration_worksFine_whenSimpleClassWithoutTemplateInheritance() {
        String code =
                "package test1.test2.test3;\n" +
                "public class Bla {\n" +
                "}";
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ComponentCategory> captor2 = ArgumentCaptor.forClass(ComponentCategory.class);
        analyseTestCode(code);

        verify(analysisContext, times(1)).registerClass(captor1.capture(), captor2.capture());

        Assert.assertThat(captor1.getValue(), is("Bla"));
        Assert.assertThat(captor2.getValue(), is(ComponentCategory.CLASS));
    }

    @Test
    public void test_enterNormalClassDeclaration_worksFine_withTemplateClass() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla<S, T> {\n" +
                        "}";
        Class mockClass = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any(ComponentCategory.class))).thenReturn(mockClass);
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ComponentCategory> captor2 = ArgumentCaptor.forClass(ComponentCategory.class);
        ArgumentCaptor<List> captor3 = ArgumentCaptor.forClass(List.class);

        analyseTestCode(code);

        verify(analysisContext, times(1)).registerClass(captor1.capture(), captor2.capture());
        verify(mockClass, times(1)).setTemplateVariables(captor3.capture());

        Assert.assertThat(captor1.getValue(), is("Bla"));
        Assert.assertThat(captor2.getValue(), is(ComponentCategory.TEMPLATE_CLASS));
        List<String> templateParams = (List<String>)captor3.getValue();
        Assert.assertThat(templateParams.get(0), is("S"));
        Assert.assertThat(templateParams.get(1), is("T"));
    }

    @Test
    public void test_enterNormalClassDeclaration_worksFine_withNotRegisteredParentClass() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla extends Parent {\n" +
                        "}";

        Class newClassMock = mock(Class.class);
        when(analysisContext.registerClass("Bla", ComponentCategory.CLASS)).thenReturn(newClassMock);
        Class parentMock = mock(Class.class);
        when(analysisContext.registerClass("Parent", ComponentCategory.CLASS)).thenReturn(parentMock);

        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> captor2 = ArgumentCaptor.forClass(Class.class);
        analyseTestCode(code);
        verify(analysisContext, times(1)).getClass(captor1.capture());
        verify(newClassMock, times(1)).setParent(captor2.capture());

        Assert.assertThat(captor1.getValue(), is("Parent"));
        Assert.assertThat(captor2.getValue(), is(parentMock));
    }

    @Test
    public void test_enterNormalClassDeclaration_worksFine_withRegisteredParentClass() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla extends Parent {\n" +
                        "}";

        Class newClassMock = mock(Class.class);
        when(analysisContext.registerClass("Bla", ComponentCategory.CLASS)).thenReturn(newClassMock);
        Class parentMock = mock(Class.class);
        when(analysisContext.getClass("Parent")).thenReturn(parentMock);

        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class> captor2 = ArgumentCaptor.forClass(Class.class);
        analyseTestCode(code);
        verify(analysisContext, times(1)).getClass(captor1.capture());
        verify(newClassMock, times(1)).setParent(captor2.capture());

        Assert.assertThat(captor1.getValue(), is("Parent"));
        Assert.assertThat(captor2.getValue(), is(parentMock));
    }

    @Test
    public void test_enterNormalClassDeclaration_worksFine_withMultipleInterfaces() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla implements I1, I2, I3 {\n" +
                        "}";

        Class newClassMock = mock(Class.class);
        when(analysisContext.registerClass("Bla", ComponentCategory.CLASS)).thenReturn(newClassMock);
        Class I1 = mock(Class.class);
        Class I2 = mock(Class.class);
        Class I3 = mock(Class.class);
        when(analysisContext.getClass("I1")).thenReturn(I1);
        when(analysisContext.registerClass("I2", ComponentCategory.INTERFACE)).thenReturn(I2);
        when(analysisContext.registerClass("I3", ComponentCategory.INTERFACE)).thenReturn(I3);

        ArgumentCaptor<Class> captor = ArgumentCaptor.forClass(Class.class);
        analyseTestCode(code);
        verify(newClassMock, times(3)).addInterface(captor.capture());

        Assert.assertThat(Sets.newHashSet(captor.getAllValues()), is(Sets.newHashSet(I1, I2, I3)));
    }

    @Test
    public void test_enterNormalInterfaceDeclaration_excludeInterfaceAndChangeState_forExcludedInterface() {
        String code =
                "package test1.test2.test3;\n" +
                        "public interface Bla {\n" +
                        "}";
        when(analysisContext.isExcluded("Bla")).thenReturn(true);
        Class c = mock(Class.class);
        when(analysisContext.getCurrent()).thenReturn(c);
        analyseTestCode(code);
        String state = TestUtils.getMemberVariable(java8Analyser, "state").toString();
        Assert.assertThat(state, is("EXCLUDED_TYPE"));
    }

    @Test
    public void test_enterNormalInterfaceDeclaration_skipParsing_forInnerInterface() {
        String code =
                "package test1.test2.test3;\n"
                        + "public interface Inner {\n"
                        + "}";
        Class currentMock = mock(Class.class);
        when(analysisContext.getCurrent()).thenReturn(currentMock);
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ComponentCategory> captor2 = ArgumentCaptor.forClass(ComponentCategory.class);
        analyseTestCode(code);
        verify(analysisContext, times(1)).registerClass(captor1.capture(), captor2.capture());

        Assert.assertThat(captor1.getValue(), is("Inner"));
        Assert.assertThat(captor2.getValue(), is(ComponentCategory.INTERFACE));
        String state = TestUtils.getMemberVariable(java8Analyser, "state").toString();
        Assert.assertThat(state, is("INNER_CLASS"));
    }

    @Test
    public void test_enterNormalInterfaceDeclaration_worksFine_withOrdinaryInterface() {
        String code =
                "package test1.test2.test3;\n" +
                        "public interface Bla {\n" +
                        "}";
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ComponentCategory> captor2 = ArgumentCaptor.forClass(ComponentCategory.class);
        analyseTestCode(code);
        verify(analysisContext, times(1)).registerClass(captor1.capture(), captor2.capture());

        Assert.assertThat(captor1.getValue(), is("Bla"));
        Assert.assertThat(captor2.getValue(), is(ComponentCategory.INTERFACE));
    }

    @Test
    public void test_enterFieldDeclaration_doNothing_whenFilteringOutStatus() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   private int a, b;\n" +
                        "}";
        TestUtils.setValueToMember(java8Analyser, "state", Java8Analyser.State.EXCLUDED_TYPE);
        Class c = mock(Class.class);
        when(analysisContext.getCurrent()).thenReturn(c);
        analyseTestCode(code);
        verify(c, times(0)).addMemberVariable(any(), anyString(), any());
    }

    @Test
    public void test_enterFieldDeclaration_worksFine_withPrimatePrimitiveVariables() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   private int a, b;\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Component comp = mock(Component.class);
        when(analysisContext.registerPrimitiveType("int")).thenReturn(comp);
        ArgumentCaptor<Component> captor1 = ArgumentCaptor.forClass(Component.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccessLevel> captor3 = ArgumentCaptor.forClass(AccessLevel.class);
        analyseTestCode(code);
        verify(current, times(2)).addMemberVariable(captor1.capture(), captor2.capture(), captor3.capture());

        Assert.assertThat(captor1.getValue(), is(comp));
        Assert.assertThat(Sets.newHashSet(captor2.getAllValues()), is(Sets.newHashSet("a", "b")));
        Assert.assertThat(captor3.getValue(), is(AccessLevel.PRIVATE));
    }

    @Test
    public void test_enterFieldDeclaration_worksFine_withPrimatePrimitiveClassVariables() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   private Double a, b;\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Component comp = mock(Component.class);
        when(analysisContext.registerPrimitiveType("Double")).thenReturn(comp);
        ArgumentCaptor<Component> captor1 = ArgumentCaptor.forClass(Component.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccessLevel> captor3 = ArgumentCaptor.forClass(AccessLevel.class);
        analyseTestCode(code);
        verify(current, times(2)).addMemberVariable(captor1.capture(), captor2.capture(), captor3.capture());

        Assert.assertThat(captor1.getValue(), is(comp));
        Assert.assertThat(Sets.newHashSet(captor2.getAllValues()), is(Sets.newHashSet("a", "b")));
        Assert.assertThat(captor3.getValue(), is(AccessLevel.PRIVATE));
    }

    @Test
    public void test_enterFieldDeclaration_worksFine_withClassVariables() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   public GeneralClass a, b;\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Component comp = mock(Component.class);
        when(analysisContext.register("GeneralClass")).thenReturn(comp);
        ArgumentCaptor<Component> captor1 = ArgumentCaptor.forClass(Component.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccessLevel> captor3 = ArgumentCaptor.forClass(AccessLevel.class);
        analyseTestCode(code);
        verify(current, times(2)).addMemberVariable(captor1.capture(), captor2.capture(), captor3.capture());

        Assert.assertThat(captor1.getValue(), is(comp));
        Assert.assertThat(Sets.newHashSet(captor2.getAllValues()), is(Sets.newHashSet("a", "b")));
        Assert.assertThat(captor3.getValue(), is(AccessLevel.PUBLIC));
    }

    @Test
    public void test_enterFieldDeclaration_worksFine_withMapVariable() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   protected Map<Long, Map<GeneralClass, List<String>>> map;\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Component longComp = mock(Component.class);
        Component generalClassComp = mock(Component.class);
        Component stringComp = mock(Component.class);
        when(analysisContext.registerPrimitiveType("Long")).thenReturn(longComp);
        when(analysisContext.register("GeneralClass")).thenReturn(generalClassComp);
        when(analysisContext.registerPrimitiveType("String")).thenReturn(stringComp);
        Container container = mock(Container.class);
        when(analysisContext.registerContainer(eq("Map"), any())).thenReturn(container);

        ArgumentCaptor<Component> captor1 = ArgumentCaptor.forClass(Component.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccessLevel> captor3 = ArgumentCaptor.forClass(AccessLevel.class);
        analyseTestCode(code);
        verify(current, times(1)).addMemberVariable(captor1.capture(), captor2.capture(), captor3.capture());

        Assert.assertThat(captor1.getValue(), is(container));
        Assert.assertThat(Sets.newHashSet(captor2.getAllValues()), is(Sets.newHashSet("map")));
        Assert.assertThat(captor3.getValue(), is(AccessLevel.PROTECTED));
    }

    @Test
    public void test_enterFieldDeclaration_worksFine_withPrimitiveTypeArrayVariable() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   private int[] intAry;\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Array intAry = mock(Array.class);
        when(analysisContext.registerPrimitiveTypeArray("int")).thenReturn(intAry);

        ArgumentCaptor<Component> captor1 = ArgumentCaptor.forClass(Component.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccessLevel> captor3 = ArgumentCaptor.forClass(AccessLevel.class);
        analyseTestCode(code);
        verify(current, times(1)).addMemberVariable(captor1.capture(), captor2.capture(), captor3.capture());

        Assert.assertThat(captor1.getValue(), is(intAry));
        Assert.assertThat(Sets.newHashSet(captor2.getAllValues()), is(Sets.newHashSet("intAry")));
        Assert.assertThat(captor3.getValue(), is(AccessLevel.PRIVATE));
    }

    @Test
    public void test_enterFieldDeclaration_worksFine_withPrimitiveClassArrayVariable() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   private Float[] floatAry;\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Array floatAry = mock(Array.class);
        when(analysisContext.registerPrimitiveTypeArray("Float")).thenReturn(floatAry);

        ArgumentCaptor<Component> captor1 = ArgumentCaptor.forClass(Component.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccessLevel> captor3 = ArgumentCaptor.forClass(AccessLevel.class);
        analyseTestCode(code);
        verify(current, times(1)).addMemberVariable(captor1.capture(), captor2.capture(), captor3.capture());

        Assert.assertThat(captor1.getValue(), is(floatAry));
        Assert.assertThat(Sets.newHashSet(captor2.getAllValues()), is(Sets.newHashSet("floatAry")));
        Assert.assertThat(captor3.getValue(), is(AccessLevel.PRIVATE));
    }

    @Test
    public void test_enterFieldDeclaration_worksFine_withGeneralClassArrayVariable() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   private GeneralClass[] generalClassAry;\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Array generalClassAry = mock(Array.class);
        when(analysisContext.registerReferenceTypeArray("GeneralClass")).thenReturn(generalClassAry);

        ArgumentCaptor<Component> captor1 = ArgumentCaptor.forClass(Component.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccessLevel> captor3 = ArgumentCaptor.forClass(AccessLevel.class);
        analyseTestCode(code);
        verify(current, times(1)).addMemberVariable(captor1.capture(), captor2.capture(), captor3.capture());

        Assert.assertThat(captor1.getValue(), is(generalClassAry));
        Assert.assertThat(Sets.newHashSet(captor2.getAllValues()), is(Sets.newHashSet("generalClassAry")));
        Assert.assertThat(captor3.getValue(), is(AccessLevel.PRIVATE));
    }

    @Test
    public void test_enterMethodDeclaration_doNothing_whenFilteringOutStatus() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   void testFunc() {}\n" +
                        "}";
        TestUtils.setValueToMember(java8Analyser, "state", Java8Analyser.State.EXCLUDED_TYPE);
        Class c = mock(Class.class);
        when(analysisContext.getCurrent()).thenReturn(c);
        analyseTestCode(code);
        verify(c, times(0)).addMemberFunction(any(), anyString(), any());
    }

    @Test
    public void test_enterMethodDeclaration_worksFine_withZeroParametersAndVoidReturn() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   private void testFunc() {}\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Function functionMock = mock(Function.class);
        when(analysisContext.registerFunction(anyString(), any(), any())).thenReturn(functionMock);
        analyseTestCode(code);
        verify(analysisContext, times(1)).registerFunction(anyString(), any(), any());
        verify(current, times(1)).addMemberFunction(functionMock, "testFunc", AccessLevel.PRIVATE);
    }

    @Test
    public void test_enterMethodDeclaration_worksFine_withMultipleParametersAndVoidReturn() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   protected void testFunc(Integer a, GeneralClass b, char c) {}\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Component integerMock = mock(Component.class);
        when(integerMock.getName()).thenReturn("Integer");
        Component generalClassMock = mock(Component.class);
        when(generalClassMock.getName()).thenReturn("GeneralClass");
        Component charMock = mock(Component.class);
        when(charMock.getName()).thenReturn("char");
        when(analysisContext.registerPrimitiveType("Integer")).thenReturn(integerMock);
        when(analysisContext.register("GeneralClass")).thenReturn(generalClassMock);
        when(analysisContext.registerPrimitiveType("char")).thenReturn(charMock);
        Function functionMock = mock(Function.class);
        when(analysisContext.registerFunction(anyString(), any(), any())).thenReturn(functionMock);
        analyseTestCode(code);
        verify(analysisContext, times(1)).registerFunction(anyString(), any(), any());
        verify(current, times(1)).addMemberFunction(functionMock, "testFunc", AccessLevel.PROTECTED);
    }

    @Test
    public void test_enterMethodDeclaration_worksFine_withVarArgsArrayAndVoidReturn() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   public void testFunc(Integer a, char[] b, Object... c) {}\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Component integerMock = mock(Component.class);
        when(integerMock.getName()).thenReturn("Integer");
        Array charMock = mock(Array.class);
        when(charMock.getName()).thenReturn("char");
        Component objectMock = mock(Component.class);
        Varargs objectVarargsMock = mock(Varargs.class);
        when(analysisContext.registerPrimitiveType("Integer")).thenReturn(integerMock);
        when(analysisContext.registerPrimitiveTypeArray("char")).thenReturn(charMock);
        when(analysisContext.register("Object")).thenReturn(objectMock);
        when(analysisContext.registerVarargs(objectMock)).thenReturn(objectVarargsMock);
        Function functionMock = mock(Function.class);
        when(analysisContext.registerFunction(anyString(), any(), any())).thenReturn(functionMock);
        analyseTestCode(code);
        verify(analysisContext, times(1)).registerFunction(anyString(), any(), any());
        verify(current, times(1)).addMemberFunction(functionMock, "testFunc", AccessLevel.PUBLIC);
    }

    @Test
    public void test_enterMethodDeclaration_worksFine_withNoParamAndGeneralClassReturn() {
        String code =
                "package test1.test2.test3;\n" +
                        "public class Bla {\n" +
                        "   public GeneralClass testFunc() {}\n" +
                        "}";
        Class current = mock(Class.class);
        when(analysisContext.registerClass(anyString(), any())).thenReturn(current);
        when(analysisContext.getCurrent()).thenReturn(null).thenReturn(current);
        Class generalClassMock = mock(Class.class);
        when(analysisContext.register("GeneralClass")).thenReturn(generalClassMock);
        Function functionMock = mock(Function.class);
        when(analysisContext.registerFunction(anyString(), any(), any())).thenReturn(functionMock);
        analyseTestCode(code);
        verify(analysisContext, times(1)).registerFunction(anyString(), any(), any());
        verify(current, times(1)).addMemberFunction(functionMock, "testFunc", AccessLevel.PUBLIC);
    }

    private void analyseTestCode(String code) {
        ANTLRInputStream input = new ANTLRInputStream(code);
        Java8Lexer lexer = new Java8Lexer(input);
        Java8Parser parser = new Java8Parser(new CommonTokenStream(lexer));
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker.DEFAULT.walk(this.java8Analyser, tree);
    }
}
