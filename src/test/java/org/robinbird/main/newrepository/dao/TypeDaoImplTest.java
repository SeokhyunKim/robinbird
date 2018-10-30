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

    private TypeDaoImpl daoImpl =
            (TypeDaoImpl)TypeDaoFactory.createDao("mem:");

    @Test
    public void test_save_load_remove_TypeEntity() {
        TypeEntity entity = new TypeEntity();
        entity.setCategory(TypeCategory.CLASS.name());
        entity.setName("test");

        final TypeEntity saved = daoImpl.saveTypeEntity(entity);
        final Optional<TypeEntity> loadedOpt = daoImpl.loadTypeEntity(saved.getId());

        Assert.assertTrue(loadedOpt.isPresent());
        Assert.assertThat(saved, is(loadedOpt.get()));

        daoImpl.removeTypeEntity(loadedOpt.get());
        final Optional<TypeEntity> loadedAgain = daoImpl.loadTypeEntity(saved.getId());

        Assert.assertFalse(loadedAgain.isPresent());
    }

    @Test
    public void test_loadTypeEntity_by_name() {
        final String name1 = UUID.randomUUID().toString();
        final String name2 = UUID.randomUUID().toString();

        final TypeEntity entity1 = new TypeEntity();
        entity1.setCategory(TypeCategory.CLASS.name());
        entity1.setName(name1);
        daoImpl.saveTypeEntity(entity1);

        final TypeEntity entity2 = new TypeEntity();
        entity2.setCategory(TypeCategory.CLASS.name());
        entity2.setName(name2);
        daoImpl.saveTypeEntity(entity2);

        final Optional<TypeEntity> loadedOpt1 = daoImpl.loadTypeEntity(name1);
        final Optional<TypeEntity> loadedOpt2 = daoImpl.loadTypeEntity(name2);

        Assert.assertThat(loadedOpt1.get(), is(entity1));
        Assert.assertThat(loadedOpt2.get(), is(entity2));
    }

    @Test
    public void test_load_save_remove_CompositionTypeEntity() {
        final RandomDataGenerator generator = new RandomDataGenerator();

        final CompositionTypeEntity entity1 = new CompositionTypeEntity();
        long typeId = generator.nextLong(1L, 1000L);
        entity1.setTypeId(typeId);
        entity1.setCompositionTypeId(1L);
        daoImpl.saveCompositionTypeEntity(entity1);

        final CompositionTypeEntity entity2 = new CompositionTypeEntity();
        entity2.setTypeId(typeId);
        entity2.setCompositionTypeId(2L);
        daoImpl.saveCompositionTypeEntity(entity2);


        final List<CompositionTypeEntity> compTypes = daoImpl.loadCompositionTypeEntities(typeId);

        Assert.assertThat(compTypes.size(), is(2));
        Set<Long> compIds = compTypes.stream().map(CompositionTypeEntity::getCompositionTypeId).collect(Collectors.toSet());
        Assert.assertTrue(compIds.contains(1L));
        Assert.assertTrue(compIds.contains(2L));

        daoImpl.removeCompositionTypeEntities(typeId);
        final List<CompositionTypeEntity> removedTypes = daoImpl.loadCompositionTypeEntities(typeId);
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
        daoImpl.saveInstanceEntity(entity1);

        final InstanceEntity entity2 = new InstanceEntity();
        entity2.setParentTypeId(parentTypeId);
        entity2.setTypeId(2L);
        entity2.setAccessModifier(AccessModifier.PUBLIC.name());
        entity2.setName("entity2");
        daoImpl.saveInstanceEntity(entity2);

        final List<InstanceEntity> loadedEntities = daoImpl.loadInstanceEntities(parentTypeId);
        Assert.assertThat(loadedEntities.size(), is(2));
        final Set<Long> typeIds = loadedEntities.stream().map(InstanceEntity::getTypeId).collect(Collectors.toSet());
        Assert.assertTrue(typeIds.contains(1L));
        Assert.assertTrue(typeIds.contains(2L));

        daoImpl.removeInstanceEntities(parentTypeId);
        Assert.assertTrue(daoImpl.loadInstanceEntities(parentTypeId).isEmpty());
    }

    @Test
    public void test_load_save_remove_RelationEntity() {
        final long parentTypeId = new RandomDataGenerator().nextLong(1L, 1000L);

        final RelationEntity entity1 = new RelationEntity();
        entity1.setParentTypeId(parentTypeId);
        entity1.setTypeId(1L);
        entity1.setCategory(RelationCategory.ASSOCIATION.name());
        daoImpl.saveRelationEntity(entity1);

        final RelationEntity entity2 = new RelationEntity();
        entity2.setParentTypeId(parentTypeId);
        entity2.setTypeId(2L);
        entity2.setCategory(RelationCategory.ASSOCIATION.name());
        daoImpl.saveRelationEntity(entity2);

        final List<RelationEntity> loadedEntities = daoImpl.loadRelationEntities(parentTypeId);
        Assert.assertThat(loadedEntities.size(), is(2));
        final Set<Long> typeIds = loadedEntities.stream().map(RelationEntity::getTypeId).collect(Collectors.toSet());
        Assert.assertTrue(typeIds.contains(1L));
        Assert.assertTrue(typeIds.contains(2L));

        daoImpl.removeRelationEntities(parentTypeId);
        Assert.assertTrue(daoImpl.loadRelationEntities(parentTypeId).isEmpty());
    }
}
