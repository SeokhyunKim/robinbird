package org.robinbird.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import com.google.common.collect.ImmutableSet;
import com.google.common.testing.NullPointerTester;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.dao.EntityDao;
import org.robinbird.repository.dao.EntityDaoH2Factory;

public class RbRepositoryTest {

    private static EntityDao entityDao;
    private RbRepository repository = new RbRepository(entityDao);

    @BeforeClass
    public static void dbSetup() {
        RbRepositoryTest.entityDao = EntityDaoH2Factory.createDao();
    }

    @After
    public void tearDown() {
        entityDao.deleteAll();
    }

    @Test
    public void test_getComponent_returnEmpty_whenNotExistingComponent() {
        final Optional<Component> compOpt = repository.getComponent(UUID.randomUUID().toString());
        Assert.assertTrue(!compOpt.isPresent());
    }

    @Test
    public void test_registerComponent_and_getComponent() {
        final Component au = repository.registerComponent("Test", ComponentCategory.CLASS);
        final Optional<Component> compOpt = repository.getComponent("Test");
        Assert.assertTrue(compOpt.isPresent());
        Assert.assertThat(au, equalTo(compOpt.get()));
    }

    @Test
    public void test_registerComponents_and_getComponents() {
        final Component comp1 = repository.registerComponent("Test1", ComponentCategory.CLASS);
        final Component comp2 = repository.registerComponent("Test2", ComponentCategory.CLASS);
        final Set<Component> savedComps = new HashSet<>(repository.getComponents(ComponentCategory.CLASS));
        Assert.assertThat(savedComps, is(ImmutableSet.of(comp1, comp2)));
    }

    @Test
    public void test_registerComponent_returnsExistingComponent_whenRegisterWithSameNameAndCategory() {
        final Component comp1 = repository.registerComponent("Test1", ComponentCategory.CLASS);
        final Component existing = repository.registerComponent("Test1", ComponentCategory.CLASS);
        Assert.assertThat(comp1, is(existing));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_registerComponent_throwsException_whenRegisterWithSameNameButDifferentCategory() {
        repository.registerComponent("Test1", ComponentCategory.CLASS);
        repository.registerComponent("Test1", ComponentCategory.INTERFACE);
    }

    @Test
    public void test_getRelations() {
        final Component au = repository.registerComponent("Test2", ComponentCategory.CLASS);
        final Component r1 = repository.registerComponent("Relation1", ComponentCategory.CLASS);
        final Component r2 = repository.registerComponent("Relation2", ComponentCategory.CLASS);
        final Component r3 = repository.registerComponent("Relation3", ComponentCategory.CLASS);
        au.addRelation(Relation.builder()
                               .owner(au)
                               .cardinality(Cardinality.ONE)
                               .relatedComponent(r1)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        au.addRelation(Relation.builder()
                               .owner(au)
                               .cardinality(Cardinality.MULTIPLE)
                               .relatedComponent(r2)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        au.addRelation(Relation.builder()
                               .owner(au)
                               .cardinality(Cardinality.MULTIPLE)
                               .relatedComponent(r3)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        repository.updateComponent(au);
        final List<Relation> relations = repository.getRelations(au);
        final Set<Cardinality> cardinalities = relations.stream()
                                                        .map(Relation::getCardinality)
                                                        .collect(Collectors.toSet());
        final Set<String> names = relations.stream()
                                           .map(r -> r.getRelatedComponent().getName())
                                           .collect(Collectors.toSet());
        Assert.assertThat(cardinalities, is(ImmutableSet.of(Cardinality.ONE, Cardinality.MULTIPLE)));
        Assert.assertThat(names, is(ImmutableSet.of("Relation1", "Relation2", "Relation3")));
    }

    @Test
    public void test_updateComponent() {
        final Component au = repository.registerComponent("Test3", ComponentCategory.CLASS);
        final Component r1 = repository.registerComponent("Relation1", ComponentCategory.CLASS);
        final Component r2 = repository.registerComponent("Relation2", ComponentCategory.CLASS);
        au.addRelation(Relation.builder()
                               .owner(au)
                               .cardinality(Cardinality.ONE)
                               .relatedComponent(r1)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        au.addRelation(Relation.builder()
                               .owner(au)
                               .cardinality(Cardinality.MULTIPLE)
                               .relatedComponent(r2)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        repository.updateComponent(au);
        final Set<String> names = au.getRelations().stream()
                                    .map(r -> r.getRelatedComponent().getName())
                                    .collect(Collectors.toSet());
        Assert.assertThat(names, is(ImmutableSet.of("Relation1", "Relation2")));

        au.deleteRelation(Relation.builder()
                                  .owner(au)
                                  .cardinality(Cardinality.ONE)
                                  .relatedComponent(r1)
                                  .relationCategory(RelationCategory.MEMBER_VARIABLE)
                                  .build());

        final Component r3 = repository.registerComponent("Relation3", ComponentCategory.CLASS);
        au.addRelation(Relation.builder()
                               .owner(au)
                               .cardinality(Cardinality.MULTIPLE)
                               .relatedComponent(r3)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        repository.updateComponent(au);
        final Set<String> names2 = au.getRelations().stream()
                                     .map(r -> r.getRelatedComponent().getName())
                                     .collect(Collectors.toSet());
        Assert.assertThat(names2, is(ImmutableSet.of("Relation2", "Relation3")));
    }

    @Test
    public void test_updateComponentWithoutChangingRelations() {
        final Component au = repository.registerComponent("Test3", ComponentCategory.CLASS);
        final Component r3 = repository.registerComponent("Relation3", ComponentCategory.CLASS);
        au.addRelation(Relation.builder()
                               .owner(au)
                               .cardinality(Cardinality.MULTIPLE)
                               .relatedComponent(r3)
                               .relationCategory(RelationCategory.MEMBER_VARIABLE)
                               .build());
        repository.updateComponentWithoutChangingRelations(au);
        List<Relation> relationsInDb = repository.getRelations(au);
        Assert.assertTrue(relationsInDb.isEmpty());
    }

    @Test
    public void test_nulls() {
        NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicConstructors(RbRepository.class);
        tester.testAllPublicInstanceMethods(repository);
    }

}
