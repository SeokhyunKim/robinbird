package org.robinbird.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.dao.ComponentEntityDao;
import org.robinbird.repository.dao.ComponentEntityDaoH2Factory;

public class ComponentRepositoryTest {

    private static ComponentEntityDao componentEntityDao;
    private ComponentRepository repository = new ComponentRepository(componentEntityDao);

    @BeforeClass
    public static void dbSetup() {
        ComponentRepositoryTest.componentEntityDao = ComponentEntityDaoH2Factory.createDao();
    }

    @Test
    public void test_registerComponent_and_getComponent() {
        final Component au = repository.registerComponent("Test", ComponentCategory.CLASS);
        final Optional<Component> compOpt = repository.getComponent("Test");
        Assert.assertTrue(compOpt.isPresent());
        Assert.assertThat(au, equalTo(compOpt.get()));
    }

    @Test
    public void test_getRelations() {
        final Component au = repository.registerComponent("Test2", ComponentCategory.CLASS);
        final Component r1 = repository.registerComponent("Relation1", ComponentCategory.CLASS);
        final Component r2 = repository.registerComponent("Relation2", ComponentCategory.CLASS);
        final Component r3 = repository.registerComponent("Relation3", ComponentCategory.CLASS);
        au.addRelation(Relation.builder()
                               .parent(au)
                               .cardinality(Cardinality.ONE)
                               .relatedComponent(r1)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        au.addRelation(Relation.builder()
                               .parent(au)
                               .cardinality(Cardinality.MULTIPLE)
                               .relatedComponent(r2)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        au.addRelation(Relation.builder()
                               .parent(au)
                               .cardinality(Cardinality.MULTIPLE)
                               .relatedComponent(r3)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        repository.updateComponent(au);
        final List<Relation> relations = repository.getRelations(au);
        final Set<String> cardinalities = relations.stream()
                                                    .map(r -> r.getCardinality().toString())
                                                    .collect(Collectors.toSet());
        final Set<String> names = relations.stream()
                                           .map(r -> r.getRelatedComponent().getName())
                                           .collect(Collectors.toSet());
        Assert.assertThat(cardinalities, is(ImmutableSet.of("1", "n")));
        Assert.assertThat(names, is(ImmutableSet.of("Relation1", "Relation2", "Relation3")));
    }

    @Test
    public void test_updateAnalysisUnit() {
        final Component au = repository.registerComponent("Test3", ComponentCategory.CLASS);
        final Component r1 = repository.registerComponent("Relation1", ComponentCategory.CLASS);
        final Component r2 = repository.registerComponent("Relation2", ComponentCategory.CLASS);
        au.addRelation(Relation.builder()
                               .parent(au)
                               .cardinality(Cardinality.ONE)
                               .relatedComponent(r1)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        au.addRelation(Relation.builder()
                               .parent(au)
                               .cardinality(Cardinality.MULTIPLE)
                               .relatedComponent(r2)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        repository.updateComponent(au);
        final Set<String> names = au.getRelations().stream()
                                    .map(r -> r.getRelatedComponent().getName())
                                    .collect(Collectors.toSet());
        Assert.assertThat(names, is(ImmutableSet.of("Relation1", "Relation2")));

        au.getRelations().forEach(au::deleteRelation);
        final Component r3 = repository.registerComponent("Relation3", ComponentCategory.CLASS);
        au.addRelation(Relation.builder()
                               .parent(au)
                               .cardinality(Cardinality.MULTIPLE)
                               .relatedComponent(r3)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        repository.updateComponent(au);
        final Set<String> names2 = au.getRelations().stream()
                                     .map(r -> r.getRelatedComponent().getName())
                                     .collect(Collectors.toSet());
        Assert.assertThat(names2, is(ImmutableSet.of("Relation3")));
    }

}
