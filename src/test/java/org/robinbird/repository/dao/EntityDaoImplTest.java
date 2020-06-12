package org.robinbird.repository.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections4.SetUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.robinbird.model.Cardinality;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;


public class EntityDaoImplTest {

    private static EntityDao entityDao;

    private String name;

    @BeforeClass
    public static void dbSetup() {
        EntityDaoImplTest.entityDao = EntityDaoH2Factory.createDao();
    }

    @Before
    public void setUp() {
        name = UUID.randomUUID().toString();
    }

    @After
    public void tearDown() {
        entityDao.deleteAll();
    }

    @Test
    public void test_save() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName(name);
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = entityDao.save(componentEntity);

        Assert.assertThat(savedComponentEntity.getName(), is(name));
        Assert.assertThat(savedComponentEntity.getComponentCategory(), is("CLASS"));

        final RelationEntity relationEntity = new RelationEntity();
        relationEntity.setOwnerId(savedComponentEntity.getId());
        relationEntity.setId(UUID.randomUUID().toString());
        relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        relationEntity.setName("test");
        relationEntity.setRelatedComponentId("3");
        relationEntity.setCardinality(Cardinality.ONE.toString());
        relationEntity.setMetadata("{}");

        final RelationEntity savedRelationEntity = entityDao.save(relationEntity);

        Assert.assertThat(savedRelationEntity.getOwnerId(), is(relationEntity.getOwnerId()));
        Assert.assertThat(savedRelationEntity.getId(), is(relationEntity.getId()));
        Assert.assertThat(savedRelationEntity.getRelationCategory(), is("MEMBER_VARIABLE"));
        Assert.assertThat(savedRelationEntity.getName(), is("test"));
        Assert.assertThat(savedRelationEntity.getRelatedComponentId(), is("3"));
        Assert.assertThat(savedRelationEntity.getCardinality(), is(Cardinality.ONE.name()));
        Assert.assertThat(savedRelationEntity.getMetadata(), is("{}"));
    }

    @Test
    public void test_loadComponentEntity_withId() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName(name);
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = entityDao.save(componentEntity);
        final ComponentEntity loadedComponentEntity = entityDao.loadComponentEntityById(savedComponentEntity.getId()).get();

        Assert.assertThat(savedComponentEntity, equalTo(loadedComponentEntity));
    }

    @Test
    public void test_loadComponentEntities_withName() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName(name);
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = entityDao.save(componentEntity);
        final ComponentEntity loadedComponentEntity = entityDao.loadComponentEntityByName(savedComponentEntity.getName()).get(0);

        Assert.assertThat(savedComponentEntity, equalTo(loadedComponentEntity));
    }

    @Test
    public void test_loadComponentEntities_withComponentCategory() {
        final ComponentEntity entity1 = new ComponentEntity();
        final ComponentEntity entity2 = new ComponentEntity();
        entity1.setName(name);
        entity1.setComponentCategory(ComponentCategory.CLASS.name());
        entity2.setName(name + "-test");
        entity2.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity saved1 = entityDao.save(entity1);
        final ComponentEntity saved2 = entityDao.save(entity2);

        Set<ComponentEntity> entities = new HashSet<>(entityDao.loadComponentEntities(ComponentCategory.CLASS.name()));
        Assert.assertThat(entities, is(ImmutableSet.of(saved1, saved2)));
    }

    @Test
    public void test_loadRelationEntity_withId() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName(name);
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = entityDao.save(componentEntity);

        final RelationEntity relationEntity = new RelationEntity();
        relationEntity.setCardinality(Cardinality.ONE.toString());
        relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        relationEntity.setOwnerId(savedComponentEntity.getId());
        relationEntity.setRelatedComponentId("3");
        final RelationEntity savedRelationEntity = entityDao.save(relationEntity);

        final RelationEntity loadedRelationEntity =
                entityDao.loadRelationEntity(savedRelationEntity.getOwnerId(),
                                             savedRelationEntity.getId()).get();

        Assert.assertThat(savedRelationEntity, equalTo(loadedRelationEntity));
    }

    @Test
    public void test_loadRelationEntities_withParentId() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName(name);
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = entityDao.save(componentEntity);

        for (int i=0; i<3; ++i) {
            final RelationEntity relationEntity = new RelationEntity();
            relationEntity.setCardinality(Cardinality.MULTIPLE.toString());
            relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
            relationEntity.setRelatedComponentId(Integer.toString(i + 1));
            relationEntity.setOwnerId(savedComponentEntity.getId());
            entityDao.save(relationEntity);
        }

        final List<RelationEntity> relationEntities = entityDao.loadRelationEntities(savedComponentEntity.getId());
        final Set<String> relationIds = relationEntities.stream().map(RelationEntity::getRelatedComponentId).collect(Collectors.toSet());

        Assert.assertTrue(SetUtils.isEqualSet(relationIds, ImmutableSet.of("1", "2", "3")));
    }

    @Test
    public void test_update() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName(name);
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = entityDao.save(componentEntity);

        savedComponentEntity.setComponentCategory(ComponentCategory.INTERFACE.name());
        savedComponentEntity.setName("updated");
        final ComponentEntity updated = entityDao.update(savedComponentEntity);

        Assert.assertThat(savedComponentEntity.getId(), equalTo(updated.getId()));
        Assert.assertThat(updated.getComponentCategory(), equalTo("INTERFACE"));
        Assert.assertThat(updated.getName(), equalTo("updated"));

        final RelationEntity relationEntity = new RelationEntity();
        relationEntity.setCardinality(Cardinality.ONE.toString());
        relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        relationEntity.setOwnerId(savedComponentEntity.getId());
        relationEntity.setRelatedComponentId("3");
        final RelationEntity savedRelationEntity = entityDao.save(relationEntity);

        savedRelationEntity.setRelationCategory(RelationCategory.MEMBER_FUNCTION.name());
        savedRelationEntity.setCardinality(Cardinality.MULTIPLE.toString());
        final RelationEntity updatedRelationEntity = entityDao.update(savedRelationEntity);

        Assert.assertThat(updatedRelationEntity.getId(), equalTo(updatedRelationEntity.getId()));
        Assert.assertThat(updatedRelationEntity.getOwnerId(), equalTo(savedComponentEntity.getId()));
        Assert.assertThat(updatedRelationEntity.getCardinality(), is(Cardinality.MULTIPLE.toString()));
        Assert.assertThat(updatedRelationEntity.getRelationCategory(), equalTo(RelationCategory.MEMBER_FUNCTION.name()));
        Assert.assertThat(updatedRelationEntity.getRelatedComponentId(), is("3"));
    }

    @Test
    public void test_delete() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName(name);
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = entityDao.save(componentEntity);

        final RelationEntity relationEntity = new RelationEntity();
        relationEntity.setCardinality(Cardinality.ONE.toString());
        relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        relationEntity.setOwnerId(savedComponentEntity.getId());
        relationEntity.setRelatedComponentId("3");
        final RelationEntity savedRelationEntity = entityDao.save(relationEntity);

        entityDao.delete(savedComponentEntity);
        entityDao.delete(savedRelationEntity);

        final Optional<ComponentEntity> analysisEntityOpt =
                entityDao.loadComponentEntityById(savedComponentEntity.getId());
        final Optional<RelationEntity> relationEntityOpt =
                entityDao.loadRelationEntity(savedRelationEntity.getOwnerId(), savedRelationEntity.getId());

        Assert.assertFalse(analysisEntityOpt.isPresent());
        Assert.assertFalse(relationEntityOpt.isPresent());
    }

}
