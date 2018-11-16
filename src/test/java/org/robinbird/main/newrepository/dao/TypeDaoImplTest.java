package org.robinbird.main.newrepository.dao;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.robinbird.main.newmodel.AccessModifier;
import org.robinbird.main.newmodel.RelationCategory;
import org.robinbird.main.newmodel.TypeCategory;
import org.robinbird.main.newrepository.dao.entity.CompositionTypeEntity;
import org.robinbird.main.newrepository.dao.entity.InstanceEntity;
import org.robinbird.main.newrepository.dao.entity.RelationEntity;
import org.robinbird.main.newrepository.dao.entity.TypeEntity;

public class TypeDaoImplTest {

    private TypeDao dao = TypeDaoFactory.createDao("mem:");

    @Test
    public void test_save_load_remove_TypeEntity() {
        TypeEntity entity = new TypeEntity();
        entity.setCategory(TypeCategory.CLASS.name());
        entity.setName("test");

        final TypeEntity saved = dao.saveTypeEntity(entity);
        final Optional<TypeEntity> loadedOpt = dao.loadTypeEntity(saved.getId());

        Assert.assertTrue(loadedOpt.isPresent());
        Assert.assertThat(saved, is(loadedOpt.get()));

        dao.removeTypeEntity(loadedOpt.get());
        final Optional<TypeEntity> loadedAgain = dao.loadTypeEntity(saved.getId());

        Assert.assertFalse(loadedAgain.isPresent());
    }

    @Test
    public void test_loadTypeEntity_by_name() {
        final String name1 = UUID.randomUUID().toString();
        final String name2 = UUID.randomUUID().toString();

        final TypeEntity entity1 = new TypeEntity();
        entity1.setCategory(TypeCategory.CLASS.name());
        entity1.setName(name1);
        dao.saveTypeEntity(entity1);

        final TypeEntity entity2 = new TypeEntity();
        entity2.setCategory(TypeCategory.CLASS.name());
        entity2.setName(name2);
        dao.saveTypeEntity(entity2);

        final Optional<TypeEntity> loadedOpt1 = dao.loadTypeEntity(name1);
        final Optional<TypeEntity> loadedOpt2 = dao.loadTypeEntity(name2);

        Assert.assertThat(loadedOpt1.get(), is(entity1));
        Assert.assertThat(loadedOpt2.get(), is(entity2));
    }

    @Test
    public void test_updateTypeEntity() {
        final TypeEntity entity = new TypeEntity();
        entity.setCategory(TypeCategory.CLASS.name());
        entity.setName("test1");
        dao.saveTypeEntity(entity);

        final Optional<TypeEntity> loaded = dao.loadTypeEntity(entity.getId());
        Assert.assertThat(loaded.get(), is(entity));

        entity.setCategory("updated");
        dao.updateTypeEntity(entity);
        final Optional<TypeEntity> updated = dao.loadTypeEntity(entity.getId());
        Assert.assertThat(updated.get(), is(entity));
    }

    @Test
    public void test_load_save_remove_CompositionTypeEntity() {
        final RandomDataGenerator generator = new RandomDataGenerator();

        final CompositionTypeEntity entity1 = new CompositionTypeEntity();
        long typeId = generator.nextLong(1L, 1000L);
        entity1.setTypeId(typeId);
        entity1.setCompositionTypeId(1L);
        dao.saveCompositionTypeEntity(entity1);

        final CompositionTypeEntity entity2 = new CompositionTypeEntity();
        entity2.setTypeId(typeId);
        entity2.setCompositionTypeId(2L);
        dao.saveCompositionTypeEntity(entity2);


        final List<CompositionTypeEntity> compTypes = dao.loadCompositionTypeEntities(typeId);

        Assert.assertThat(compTypes.size(), is(2));
        Set<Long> compIds = compTypes.stream().map(CompositionTypeEntity::getCompositionTypeId).collect(Collectors.toSet());
        Assert.assertTrue(compIds.contains(1L));
        Assert.assertTrue(compIds.contains(2L));

        dao.removeCompositionTypeEntities(typeId);
        final List<CompositionTypeEntity> removedTypes = dao.loadCompositionTypeEntities(typeId);
        Assert.assertTrue(removedTypes.isEmpty());
    }

    @Test
    public void test_load_save_remove_InstanceEntity() {
        final long parentTypeId = new RandomDataGenerator().nextLong(1L, 1000L);

        final InstanceEntity entity1 = new InstanceEntity();
        entity1.setParentTypeId(parentTypeId);
        entity1.setTypeId(1L);
        entity1.setAccessModifier(AccessModifier.PUBLIC.name());
        entity1.setName("entity1");
        dao.saveInstanceEntity(entity1);

        final InstanceEntity entity2 = new InstanceEntity();
        entity2.setParentTypeId(parentTypeId);
        entity2.setTypeId(2L);
        entity2.setAccessModifier(AccessModifier.PUBLIC.name());
        entity2.setName("entity2");
        dao.saveInstanceEntity(entity2);

        final List<InstanceEntity> loadedEntities = dao.loadInstanceEntities(parentTypeId);
        Assert.assertThat(loadedEntities.size(), is(2));
        final Set<Long> typeIds = loadedEntities.stream().map(InstanceEntity::getTypeId).collect(Collectors.toSet());
        Assert.assertTrue(typeIds.contains(1L));
        Assert.assertTrue(typeIds.contains(2L));

        dao.removeInstanceEntities(parentTypeId);
        Assert.assertTrue(dao.loadInstanceEntities(parentTypeId).isEmpty());
    }

    @Test
    public void test_updateInstanceEntity() {
        final long parentTypeId = new RandomDataGenerator().nextLong(1L, 1000L);

        final InstanceEntity entity1 = new InstanceEntity();
        entity1.setParentTypeId(parentTypeId);
        entity1.setTypeId(1L);
        entity1.setAccessModifier(AccessModifier.PUBLIC.name());
        entity1.setName("entity1");
        dao.saveInstanceEntity(entity1);

        entity1.setAccessModifier(AccessModifier.PRIVATE.name());
        final InstanceEntity updated = dao.updateInstanceEntity(entity1);

        Assert.assertThat(updated.getParentTypeId(), is(parentTypeId));
        Assert.assertThat(updated.getTypeId(), is(1L));
        Assert.assertThat(updated.getName(), is("entity1"));
        Assert.assertThat(updated.getAccessModifier(), is("PRIVATE"));
    }

    @Test
    public void test_removeInstanceEntity() {
        final InstanceEntity entity1 = new InstanceEntity();
        entity1.setParentTypeId(new RandomDataGenerator().nextLong(1L, 1000L));
        entity1.setTypeId(1L);
        entity1.setAccessModifier(AccessModifier.PUBLIC.name());
        entity1.setName("entity1");
        dao.saveInstanceEntity(entity1);

        final List<InstanceEntity> loaded = dao.loadInstanceEntities(entity1.getParentTypeId());
        Assert.assertThat(loaded.size(), is(1));
        Assert.assertThat(loaded.iterator().next().getName(), is("entity1"));

        dao.removeInstanceEntity(entity1);
        Assert.assertTrue(dao.loadInstanceEntities(entity1.getParentTypeId()).isEmpty());
    }

    @Test
    public void test_load_save_remove_RelationEntity() {
        final long parentTypeId = new RandomDataGenerator().nextLong(1L, 1000L);

        final RelationEntity entity1 = new RelationEntity();
        entity1.setParentTypeId(parentTypeId);
        entity1.setTypeId(1L);
        entity1.setCategory(RelationCategory.ASSOCIATION.name());
        dao.saveRelationEntity(entity1);

        final RelationEntity entity2 = new RelationEntity();
        entity2.setParentTypeId(parentTypeId);
        entity2.setTypeId(2L);
        entity2.setCategory(RelationCategory.ASSOCIATION.name());
        dao.saveRelationEntity(entity2);

        final List<RelationEntity> loadedEntities = dao.loadRelationEntities(parentTypeId);
        Assert.assertThat(loadedEntities.size(), is(2));
        final Set<Long> typeIds = loadedEntities.stream().map(RelationEntity::getTypeId).collect(Collectors.toSet());
        Assert.assertTrue(typeIds.contains(1L));
        Assert.assertTrue(typeIds.contains(2L));

        dao.removeRelationEntities(parentTypeId);
        Assert.assertTrue(dao.loadRelationEntities(parentTypeId).isEmpty());
    }

    @Test
    public void test_updateRelationEntity() {
        final RelationEntity entity = new RelationEntity();
        entity.setParentTypeId(new RandomDataGenerator().nextLong(1L, 1000L));
        entity.setTypeId(new RandomDataGenerator().nextLong(1L, 1000L));
        entity.setCategory(TypeCategory.CLASS.name());
        dao.saveRelationEntity(entity);

        final List<RelationEntity> loaded = dao.loadRelationEntities(entity.getParentTypeId());
        Assert.assertThat(loaded.size(), is(1));
        Assert.assertThat(loaded.get(0), is(entity));

        entity.setCategory("updated");
        dao.updateRelationEntity(entity);
        final List<RelationEntity> updated = dao.loadRelationEntities(entity.getParentTypeId());
        Assert.assertThat(updated.size(), is(1));
        Assert.assertThat(updated.get(0), is(entity));
    }

    @Test
    public void test_removeRelationEntity() {
        final RelationEntity entity1 = new RelationEntity();
        entity1.setParentTypeId(new RandomDataGenerator().nextLong(1L, 1000L));
        entity1.setTypeId(1L);
        entity1.setCategory(RelationCategory.ASSOCIATION.name());
        dao.saveRelationEntity(entity1);

        final List<RelationEntity> loaded = dao.loadRelationEntities(entity1.getParentTypeId());
        Assert.assertThat(loaded.size(), is(1));
        Assert.assertThat(loaded.iterator().next().getCategory(), is(RelationCategory.ASSOCIATION.name()));

        dao.removeRelationEntity(entity1);
        Assert.assertTrue(dao.loadRelationEntities(entity1.getParentTypeId()).isEmpty());
    }
}
