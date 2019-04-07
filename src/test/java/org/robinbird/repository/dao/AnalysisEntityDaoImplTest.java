package org.robinbird.repository.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.SetUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.robinbird.model.AnalysisEntityCategory;
import org.robinbird.model.RelationType;
import org.robinbird.repository.entity.AnalysisEntity;
import org.robinbird.repository.entity.Relation;


public class AnalysisEntityDaoImplTest {

    private static AnalysisEntityDao analysisEntityDao;

    @BeforeClass
    public static void dbSetup() {
        AnalysisEntityDaoImplTest.analysisEntityDao = AnalysisEntityDaoH2Factory.createDao();
    }

    @Test
    public void test_save() {
        final AnalysisEntity analysisEntity = new AnalysisEntity();
        analysisEntity.setName("test");
        analysisEntity.setCategory(AnalysisEntityCategory.CLASS.name());
        final AnalysisEntity savedAnalysisEntity = analysisEntityDao.save(analysisEntity);

        Assert.assertThat(savedAnalysisEntity.getName(), is("test"));
        Assert.assertThat(savedAnalysisEntity.getCategory(), is("CLASS"));

        final Relation relation = new Relation();
        relation.setCardinality(1);
        relation.setRelationType(RelationType.MEMBER_VARIABLE.name());
        relation.setAnalysisEntityId(savedAnalysisEntity.getId());
        final Relation savedRelation = analysisEntityDao.save(relation);

        Assert.assertThat(savedRelation.getAnalysisEntityId(), is(savedAnalysisEntity.getId()));
        Assert.assertThat(savedRelation.getRelationType(), is("MEMBER_VARIABLE"));
        Assert.assertThat(savedRelation.getCardinality(), is(1));
    }

    @Test
    public void test_loadAnalysisEntity_withId() {
        final AnalysisEntity analysisEntity = new AnalysisEntity();
        analysisEntity.setName("test");
        analysisEntity.setCategory(AnalysisEntityCategory.CLASS.name());
        final AnalysisEntity savedAnalysisEntity = analysisEntityDao.save(analysisEntity);
        final AnalysisEntity loadedAnalysisEntity = analysisEntityDao.loadAnalysisEntity(savedAnalysisEntity.getId()).get();

        Assert.assertThat(savedAnalysisEntity, equalTo(loadedAnalysisEntity));
    }

    @Test
    public void test_loadAnalysisEntity_withName() {
        final AnalysisEntity analysisEntity = new AnalysisEntity();
        analysisEntity.setName("test-name");
        analysisEntity.setCategory(AnalysisEntityCategory.CLASS.name());
        final AnalysisEntity savedAnalysisEntity = analysisEntityDao.save(analysisEntity);
        final AnalysisEntity loadedAnalysisEntity = analysisEntityDao.loadAnalysisEntity(savedAnalysisEntity.getName()).get();

        Assert.assertThat(savedAnalysisEntity, equalTo(loadedAnalysisEntity));
    }

    @Test
    public void test_loadRelation_withId() {
        final AnalysisEntity analysisEntity = new AnalysisEntity();
        analysisEntity.setName("test");
        analysisEntity.setCategory(AnalysisEntityCategory.CLASS.name());
        final AnalysisEntity savedAnalysisEntity = analysisEntityDao.save(analysisEntity);

        final Relation relation = new Relation();
        relation.setCardinality(1);
        relation.setRelationType(RelationType.MEMBER_VARIABLE.name());
        relation.setAnalysisEntityId(savedAnalysisEntity.getId());
        final Relation savedRelation = analysisEntityDao.save(relation);

        final Relation loadedRelation = analysisEntityDao.loadRelation(savedRelation.getId()).get();

        Assert.assertThat(savedRelation, equalTo(loadedRelation));
    }

    @Test
    public void test_loadRelations_withAnalysisEntityId() {
        final AnalysisEntity analysisEntity = new AnalysisEntity();
        analysisEntity.setName("test");
        analysisEntity.setCategory(AnalysisEntityCategory.CLASS.name());
        final AnalysisEntity savedAnalysisEntity = analysisEntityDao.save(analysisEntity);

        for (int i=0; i<3; ++i) {
            final Relation relation = new Relation();
            relation.setCardinality(i);
            relation.setRelationType(RelationType.MEMBER_VARIABLE.name());
            relation.setAnalysisEntityId(savedAnalysisEntity.getId());
            analysisEntityDao.save(relation);
        }

        final List<Relation> relations = analysisEntityDao.loadRelations(savedAnalysisEntity.getId());
        final Set<Integer> cardinalities = relations.stream().map(Relation::getCardinality).collect(Collectors.toSet());

        Assert.assertTrue(SetUtils.isEqualSet(cardinalities, ImmutableSet.of(0, 1, 2)));
    }

    @Test
    public void test_update() {
        final AnalysisEntity analysisEntity = new AnalysisEntity();
        analysisEntity.setName("test");
        analysisEntity.setCategory(AnalysisEntityCategory.CLASS.name());
        final AnalysisEntity savedAnalysisEntity = analysisEntityDao.save(analysisEntity);

        savedAnalysisEntity.setCategory(AnalysisEntityCategory.INTERFACE.name());
        savedAnalysisEntity.setName("updated");
        final AnalysisEntity updated = analysisEntityDao.update(savedAnalysisEntity);

        Assert.assertThat(savedAnalysisEntity.getId(), equalTo(updated.getId()));
        Assert.assertThat(updated.getCategory(), equalTo("INTERFACE"));
        Assert.assertThat(updated.getName(), equalTo("updated"));

        final Relation relation = new Relation();
        relation.setCardinality(1);
        relation.setRelationType(RelationType.MEMBER_VARIABLE.name());
        relation.setAnalysisEntityId(savedAnalysisEntity.getId());
        final Relation savedRelation = analysisEntityDao.save(relation);

        savedRelation.setRelationType(RelationType.MEMBER_FUNCTION.name());
        savedRelation.setCardinality(3);
        final Relation updatedRelation = analysisEntityDao.update(savedRelation);

        Assert.assertThat(updatedRelation.getId(), equalTo(updatedRelation.getId()));
        Assert.assertThat(updatedRelation.getAnalysisEntityId(), equalTo(savedAnalysisEntity.getId()));
        Assert.assertThat(updatedRelation.getCardinality(), is(3));
        Assert.assertThat(updatedRelation.getRelationType(), equalTo(RelationType.MEMBER_FUNCTION.name()));
    }

    @Test
    public void test_delete() {
        final AnalysisEntity analysisEntity = new AnalysisEntity();
        analysisEntity.setName("test");
        analysisEntity.setCategory(AnalysisEntityCategory.CLASS.name());
        final AnalysisEntity savedAnalysisEntity = analysisEntityDao.save(analysisEntity);

        final Relation relation = new Relation();
        relation.setCardinality(1);
        relation.setRelationType(RelationType.MEMBER_VARIABLE.name());
        relation.setAnalysisEntityId(savedAnalysisEntity.getId());
        final Relation savedRelation = analysisEntityDao.save(relation);

        analysisEntityDao.delete(savedAnalysisEntity);
        analysisEntityDao.delete(savedRelation);

        final Optional<AnalysisEntity> analysisEntityOpt =
                analysisEntityDao.loadAnalysisEntity(savedAnalysisEntity.getId());
        final Optional<Relation> relationEntityOpt =
                analysisEntityDao.loadRelation(savedRelation.getId());

        Assert.assertFalse(analysisEntityOpt.isPresent());
        Assert.assertFalse(relationEntityOpt.isPresent());
    }
}
