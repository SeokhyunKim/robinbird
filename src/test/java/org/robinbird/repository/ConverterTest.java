package org.robinbird.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robinbird.util.JsonObjectMapper.OBJECT_MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.NullPointerTester;
import java.io.IOException;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.robinbird.JsonProcessingExceptionForTest;
import org.robinbird.TestUtils;
import org.robinbird.exception.RobinbirdException;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;

public class ConverterTest {

    @Before
    public void setUp() {
        TestUtils.setValueToStaticMember(Converter.class, "objectMapper", OBJECT_MAPPER);
    }

    @Test
    public void test_convert_forComponentEntity_whenValidComponentEntity() {
        ComponentEntity entity = new ComponentEntity();
        entity.setId("1");
        entity.setName("test");
        entity.setComponentCategory(ComponentCategory.CLASS.name());
        entity.setMetadata("{\"key\":\"value\"}");

        Component comp = Converter.convert(entity);
        Assert.assertThat(comp.getId(), is(entity.getId()));
        Assert.assertThat(comp.getName(), is(entity.getName()));
        Assert.assertThat(comp.getComponentCategory(), is(ComponentCategory.CLASS));
        Assert.assertThat(comp.getMetadata(), equalTo(ImmutableMap.of("key", "value")));
    }

    @Test
    public void test_convert_forComponentEntity_hasEmptyMetadataMap_whenNullMetadataIsGiven() {
        ComponentEntity entity = new ComponentEntity();
        entity.setId("1");
        entity.setName("test");
        entity.setComponentCategory(ComponentCategory.CLASS.name());

        Component comp = Converter.convert(entity);
        Assert.assertThat(comp.getId(), is(entity.getId()));
        Assert.assertThat(comp.getName(), is(entity.getName()));
        Assert.assertThat(comp.getComponentCategory(), is(ComponentCategory.CLASS));
        Assert.assertThat(comp.getMetadata(), equalTo(ImmutableMap.of()));
    }

    @Test(expected = RobinbirdException.class)
    public void test_convert_forComponentEntity_throwsException_whenNameIsNull() {
        ComponentEntity entity = new ComponentEntity();
        entity.setId("1");
        entity.setComponentCategory(ComponentCategory.CLASS.name());
        Converter.convert(entity);
    }

    @Test(expected = RobinbirdException.class)
    public void test_convert_forComponentEntity_throwsException_whenComponentCategoryIsNull() {
        ComponentEntity entity = new ComponentEntity();
        entity.setId("1");
        entity.setName("test");
        Converter.convert(entity);
    }

    @Test(expected = RobinbirdException.class)
    public void test_convert_forComponentEntity_throwsException_whenMetadataFromatIsWrong() {
        ComponentEntity entity = new ComponentEntity();
        entity.setMetadata("wrong");
        Converter.convert(entity);
    }

    @Test
    public void test_convert_forComponent_whenValidComponent() {
        Component comp = new Component("1", "test", ComponentCategory.CLASS, null, null);
        final ComponentEntity entity = Converter.convert(comp);
        Assert.assertThat(entity.getId(), is(comp.getId()));
        Assert.assertThat(entity.getName(), is(comp.getName()));
        Assert.assertThat(entity.getComponentCategory(), is(ComponentCategory.CLASS.name()));
        Assert.assertNotNull(entity.getMetadata());
    }

    @Test(expected = RobinbirdException.class)
    public void test_convert_forComponent_throwsException_whenFailedToCreateJsonFromMetadata() throws JsonProcessingException {
        Component comp = new Component("1", "test", ComponentCategory.CLASS, null, null);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(ArgumentMatchers.any())).thenThrow(new JsonProcessingExceptionForTest());
        TestUtils.setValueToStaticMember(Converter.class, "objectMapper", objectMapper);
        Converter.convert(comp);
    }

    @Test
    public void test_convert_forRelation_whenValidRelation() {
        Component related = mock(Component.class);
        when(related.getId()).thenReturn("2");
        Component parent = mock(Component.class);
        when(parent.getId()).thenReturn("1");
        Relation r = Relation.builder()
                             .name("test")
                             .relationCategory(RelationCategory.MEMBER_VARIABLE)
                             .relatedComponent(related)
                             .parent(parent)
                             .id(UUID.randomUUID().toString())
                             .build();
        RelationEntity e = Converter.convert(r);
        Assert.assertThat(e.getParentId(), is("1"));
        Assert.assertNotNull(e.getId());
        Assert.assertThat(e.getCardinality(), is(Cardinality.ONE.name()));
        Assert.assertThat(e.getRelatedComponentId(), is("2"));
        Assert.assertThat(e.getRelationCategory(), is(RelationCategory.MEMBER_VARIABLE.name()));
        Assert.assertThat(e.getName(), is("test"));
    }

    @Test(expected = RobinbirdException.class)
    public void test_convert_forRelation_whenFailedToCreateJsonFromMetadata() throws JsonProcessingException {
        Component related = mock(Component.class);
        when(related.getId()).thenReturn("2");
        Component parent = mock(Component.class);
        when(parent.getId()).thenReturn("1");
        Relation r = Relation.builder()
                             .name("test")
                             .relationCategory(RelationCategory.MEMBER_VARIABLE)
                             .relatedComponent(related)
                             .parent(parent)
                             .id(UUID.randomUUID().toString())
                             .build();
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(ArgumentMatchers.any())).thenThrow(new JsonProcessingExceptionForTest());
        TestUtils.setValueToStaticMember(Converter.class, "objectMapper", objectMapper);
        Converter.convert(r);
    }

    @Test
    public void test_convert_forRelationEntity_whenValidRelation() {
        RelationEntity e = new RelationEntity();
        e.setId("1");
        e.setName("test");
        e.setMetadata("{\"key\":\"value\"}");
        e.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        e.setCardinality(Cardinality.ONE.name());
        Component related = mock(Component.class);
        Component parent = mock(Component.class);

        Relation r = Converter.convert(e, related, parent);
        Assert.assertThat(r.getParent(), is(parent));
        Assert.assertThat(r.getRelatedComponent(), is(related));
        Assert.assertThat(r.getId(), is("1"));
        Assert.assertThat(r.getName(), is("test"));
        Assert.assertThat(r.getCardinality(), is(Cardinality.ONE));
        Assert.assertThat(r.getRelationCategory(), is(RelationCategory.MEMBER_VARIABLE));
        Assert.assertThat(r.getMetadata(), is(ImmutableMap.of("key", "value")));
    }

    @Test(expected = RobinbirdException.class)
    public void test_convert_forRelationEntity_whenFailedToCreateJsonFromMetadata() throws IOException {
        RelationEntity e = new RelationEntity();
        e.setId("1");
        e.setName("test");
        e.setMetadata("{\"key\":\"value\"}");
        e.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        e.setCardinality(Cardinality.ONE.name());
        Component related = mock(Component.class);
        Component parent = mock(Component.class);

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(new JsonProcessingExceptionForTest());
        TestUtils.setValueToStaticMember(Converter.class, "objectMapper", objectMapper);
        Converter.convert(e, related, parent);
    }

    @Test
    public void test_nulls() {
        NullPointerTester tester = new NullPointerTester();
        Component component = mock(Component.class);
        tester.setDefault(Component.class, component);
        tester.testAllPublicStaticMethods(Converter.class);
    }


}

