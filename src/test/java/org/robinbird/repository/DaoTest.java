package org.robinbird.repository;

import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robinbird.model.AnalysisEntityCategory;
import org.robinbird.model.RelationType;
import org.robinbird.repository.dao.AnalysisEntityRepository;
import org.robinbird.repository.dao.DaoConfiguration;
import org.robinbird.repository.dao.RelationRepository;
import org.robinbird.repository.entity.AnalysisEntity;
import org.robinbird.repository.entity.Relation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DaoTestConfiguration.class})
public class DaoTest {

    @Inject
    private AnalysisEntityRepository analysisEntityRepository;

    @Inject
    private RelationRepository relationRepository;


    @Test
    public void test() {
        final AnalysisEntity analysisEntity = new AnalysisEntity();
        analysisEntity.setId(1L);
        analysisEntity.setName("test");
        analysisEntity.setCategory(AnalysisEntityCategory.CLASS.name());
        final AnalysisEntity savedAnalysisEntity = analysisEntityRepository.save(analysisEntity);
//        final Relation relation = new Relation();
//        final RelationKey relationKey = new RelationKey(savedAnalysisEntity.getId(), 1L);
//        relation.setKey(relationKey);
//        relation.setCardinality(1);
//        relation.setRelationType(RelationType.MEMBER_VARIABLE.name());
//        final Relation savedRelation = relationRepository.save(relation);

        System.out.println(savedAnalysisEntity.toString());
        //System.out.println(savedRelation.toString());

    }
}
