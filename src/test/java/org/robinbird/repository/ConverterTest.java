package org.robinbird.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.NullPointerTester;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.robinbird.exception.RobinbirdException;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;

public class ConverterTest {

    @Test
    public void test_convert_forComponentEntity_whenValidComponentEntity() {
        ComponentEntity entity = new ComponentEntity();
        entity.setId(1L);
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
        entity.setId(1L);
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
        entity.setId(1L);
        entity.setComponentCategory(ComponentCategory.CLASS.name());
        Converter.convert(entity);
    }

    @Test(expected = RobinbirdException.class)
    public void test_convert_forComponentEntity_throwsException_whenComponentCategoryIsNull() {
        ComponentEntity entity = new ComponentEntity();
        entity.setId(1L);
        entity.setName("test");
        Converter.convert(entity);
    }

    @Test
    public void test_convert_forRelation_whenValidRelation() {
        Component related = mock(Component.class);
        when(related.getId()).thenReturn(2L);
        Component parent = mock(Component.class);
        when(parent.getId()).thenReturn(1L);
        Relation r = Relation.builder()
                             .name("test")
                             .relationCategory(RelationCategory.MEMBER_VARIABLE)
                             .relatedComponent(related)
                             .parent(parent)
                             .id(UUID.randomUUID().toString())
                             .build();
        RelationEntity e = Converter.convert(r);
        Assert.assertThat(e.getParentId(), is(1L));
        Assert.assertNotNull(e.getId());
        Assert.assertThat(e.getCardinality(), is(Cardinality.ONE.name()));
        Assert.assertThat(e.getRelatedComponentId(), is(2L));
        Assert.assertThat(e.getRelationCategory(), is(RelationCategory.MEMBER_VARIABLE.name()));
        Assert.assertThat(e.getName(), is("test"));
    }

    @Test
    public void test_convert_forComponent_whenValidComponent() {
        Component comp = new Component(1L, "test", ComponentCategory.CLASS, null, null);
        final ComponentEntity entity = Converter.convert(comp);
        Assert.assertThat(entity.getId(), is(comp.getId()));
        Assert.assertThat(entity.getName(), is(comp.getName()));
        Assert.assertThat(entity.getComponentCategory(), is(ComponentCategory.CLASS.name()));
        Assert.assertNotNull(entity.getMetadata());
    }

    @Test
    public void test_convert_forRelationEntity_whenValidRelation() {
        Component parent = mock(Component.class);
        when(parent.getId()).thenReturn(1L);
        Component comp = mock(Component.class);
        when(comp.getId()).thenReturn(2L);
        Relation relation = Relation.builder()
                                    .name("test")
                                    .relationCategory(RelationCategory.MEMBER_VARIABLE)
                                    .relatedComponent(comp)
                                    .parent(parent)
                                    .id("test-id")
                                    .build();
        RelationEntity entity = Converter.convert(relation);
        Assert.assertThat(entity.getParentId(), is(1L));
        Assert.assertThat(entity.getId(), is("test-id"));
        Assert.assertThat(entity.getRelationCategory(), is(RelationCategory.MEMBER_VARIABLE.name()));
        Assert.assertThat(entity.getRelatedComponentId(), is(2L));
        Assert.assertThat(entity.getName(), is("test"));
    }

    @Test
    public void test_nulls() {
        NullPointerTester tester = new NullPointerTester();
        Component component = mock(Component.class);
        tester.setDefault(Component.class, component);
        tester.testAllPublicStaticMethods(Converter.class);
    }

}

