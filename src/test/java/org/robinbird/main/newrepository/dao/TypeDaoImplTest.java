package org.robinbird.main.newrepository.dao;

import static org.hamcrest.CoreMatchers.is;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.robinbird.main.newmodel.AccessModifier;
import org.robinbird.main.newmodel.RelationCategory;
import org.robinbird.main.newmodel.TypeCategory;
import org.robinbird.main.newrepository.dao.entity.InstanceEntity;
import org.robinbird.main.newrepository.dao.entity.RelationEntity;
import org.robinbird.main.newrepository.dao.entity.TypeEntity;
import org.robinbird.main.repository.dao.RobinbirdDaoFactory;
import org.robinbird.main.repository.dao.RobinbirdDaoImpl;

public class TypeDaoImplTest {

    private TypeDaoImpl daoImpl =
            (TypeDaoImpl)TypeDaoFactory.createDao("mem:");

    @Test
    public void test_save_load_TypeEntity() {
        TypeEntity entity = new TypeEntity();
        entity.setCategory(TypeCategory.CLASS.name());
        entity.setName("test");
        InstanceEntity ie = new InstanceEntity();
        ie.setTypeId(111L);
        ie.setName("testInstance");
        ie.setAccessModifier(AccessModifier.PUBLIC.name());
        RelationEntity re = new RelationEntity();
        re.setTypeId(222L);
        re.setCategory(RelationCategory.ASSOCIATION.name());
        entity.setInstances(ImmutableList.of(ie));
        entity.setRelations(ImmutableList.of(re));

        final TypeEntity saved = daoImpl.saveTypeEntity(entity);
        final Optional<TypeEntity> loadedOpt = daoImpl.loadTypeEntity(saved.getId());

        Assert.assertTrue(loadedOpt.isPresent());
        Assert.assertThat(saved, is(loadedOpt.get()));

    }



}
