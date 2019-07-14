package org.robinbird.repository.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections4.SetUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.robinbird.model.Cardinality;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;


public class ComponentEntityDaoImplTest {

    private static ComponentEntityDao componentEntityDao;

    @BeforeClass
    public static void dbSetup() {
        ComponentEntityDaoImplTest.componentEntityDao = ComponentEntityDaoH2Factory.createDao();
    }


    @Test
    public void test_save() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName("test");
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = componentEntityDao.save(componentEntity);

        Assert.assertThat(savedComponentEntity.getName(), is("test"));
        Assert.assertThat(savedComponentEntity.getComponentCategory(), is("CLASS"));

        final RelationEntity relationEntity = new RelationEntity();
        relationEntity.setParentId(savedComponentEntity.getId());
        relationEntity.setId(UUID.randomUUID().toString());
        relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        relationEntity.setName("test");
        relationEntity.setRelationId(3L);
        relationEntity.setCardinality(Cardinality.ONE.toString());
        relationEntity.setMetadata("{}");

        final RelationEntity savedRelationEntity = componentEntityDao.save(relationEntity);

        Assert.assertThat(savedRelationEntity.getParentId(), is(relationEntity.getParentId()));
        Assert.assertThat(savedRelationEntity.getId(), is(relationEntity.getId()));
        Assert.assertThat(savedRelationEntity.getRelationCategory(), is("MEMBER_VARIABLE"));
        Assert.assertThat(savedRelationEntity.getName(), is("test"));
        Assert.assertThat(savedRelationEntity.getRelationId(), is(3L));
        Assert.assertThat(savedRelationEntity.getCardinality(), is("1"));
        Assert.assertThat(savedRelationEntity.getMetadata(), is("{}"));
    }

    @Test
    public void test_loadAnalysisEntity_withId() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName("test");
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = componentEntityDao.save(componentEntity);
        final ComponentEntity loadedComponentEntity = componentEntityDao.loadComponentEntity(savedComponentEntity.getId()).get();

        Assert.assertThat(savedComponentEntity, equalTo(loadedComponentEntity));
    }

    @Test
    public void test_loadAnalysisEntity_withName() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName("test-name");
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = componentEntityDao.save(componentEntity);
        final ComponentEntity loadedComponentEntity = componentEntityDao.loadComponentEntity(savedComponentEntity.getName()).get();

        Assert.assertThat(savedComponentEntity, equalTo(loadedComponentEntity));
    }

    @Test
    public void test_loadRelation_withId() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName("test");
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = componentEntityDao.save(componentEntity);

        final RelationEntity relationEntity = new RelationEntity();
        relationEntity.setCardinality(Cardinality.ONE.toString());
        relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        relationEntity.setParentId(savedComponentEntity.getId());
        relationEntity.setRelationId(3L);
        final RelationEntity savedRelationEntity = componentEntityDao.save(relationEntity);

        final RelationEntity loadedRelationEntity =
                componentEntityDao.loadRelationEntity(savedRelationEntity.getParentId(),
                                                      savedRelationEntity.getId()) .get();

        Assert.assertThat(savedRelationEntity, equalTo(loadedRelationEntity));
    }

    @Test
    public void test_loadRelations_withParentId() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName("test");
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = componentEntityDao.save(componentEntity);

        for (int i=0; i<3; ++i) {
            final RelationEntity relationEntity = new RelationEntity();
            relationEntity.setCardinality(Cardinality.MULTIPLE.toString());
            relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
            relationEntity.setRelationId(i + 1L);
            relationEntity.setParentId(savedComponentEntity.getId());
            componentEntityDao.save(relationEntity);
        }

        final List<RelationEntity> relationEntities = componentEntityDao.loadRelationEntities(savedComponentEntity.getId());
        final Set<Long> relationIds = relationEntities.stream().map(RelationEntity::getRelationId).collect(Collectors.toSet());

        Assert.assertTrue(SetUtils.isEqualSet(relationIds, ImmutableSet.of(1L, 2L, 3L)));
    }

    @Test
    public void test_update() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName("test");
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = componentEntityDao.save(componentEntity);

        savedComponentEntity.setComponentCategory(ComponentCategory.INTERFACE.name());
        savedComponentEntity.setName("updated");
        final ComponentEntity updated = componentEntityDao.update(savedComponentEntity);

        Assert.assertThat(savedComponentEntity.getId(), equalTo(updated.getId()));
        Assert.assertThat(updated.getComponentCategory(), equalTo("INTERFACE"));
        Assert.assertThat(updated.getName(), equalTo("updated"));

        final RelationEntity relationEntity = new RelationEntity();
        relationEntity.setCardinality(Cardinality.ONE.toString());
        relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        relationEntity.setParentId(savedComponentEntity.getId());
        relationEntity.setRelationId(3L);
        final RelationEntity savedRelationEntity = componentEntityDao.save(relationEntity);

        savedRelationEntity.setRelationCategory(RelationCategory.MEMBER_FUNCTION.name());
        savedRelationEntity.setCardinality(Cardinality.MULTIPLE.toString());
        final RelationEntity updatedRelationEntity = componentEntityDao.update(savedRelationEntity);

        Assert.assertThat(updatedRelationEntity.getId(), equalTo(updatedRelationEntity.getId()));
        Assert.assertThat(updatedRelationEntity.getParentId(), equalTo(savedComponentEntity.getId()));
        Assert.assertThat(updatedRelationEntity.getCardinality(), is(Cardinality.MULTIPLE.toString()));
        Assert.assertThat(updatedRelationEntity.getRelationCategory(), equalTo(RelationCategory.MEMBER_FUNCTION.name()));
        Assert.assertThat(updatedRelationEntity.getRelationId(), is(3L));
    }

    @Test
    public void test_delete() {
        final ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setName("test");
        componentEntity.setComponentCategory(ComponentCategory.CLASS.name());
        final ComponentEntity savedComponentEntity = componentEntityDao.save(componentEntity);

        final RelationEntity relationEntity = new RelationEntity();
        relationEntity.setCardinality(Cardinality.ONE.toString());
        relationEntity.setRelationCategory(RelationCategory.MEMBER_VARIABLE.name());
        relationEntity.setParentId(savedComponentEntity.getId());
        relationEntity.setRelationId(3L);
        final RelationEntity savedRelationEntity = componentEntityDao.save(relationEntity);

        componentEntityDao.delete(savedComponentEntity);
        componentEntityDao.delete(savedRelationEntity);

        final Optional<ComponentEntity> analysisEntityOpt =
                componentEntityDao.loadComponentEntity(savedComponentEntity.getId());
        final Optional<RelationEntity> relationEntityOpt =
                componentEntityDao.loadRelationEntity(savedRelationEntity.getParentId(), savedRelationEntity.getId());

        Assert.assertFalse(analysisEntityOpt.isPresent());
        Assert.assertFalse(relationEntityOpt.isPresent());
    }

}
