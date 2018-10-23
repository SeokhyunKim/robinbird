package org.robinbird.main.newrepository.dao;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import lombok.NonNull;
import org.robinbird.main.newrepository.dao.entity.CompositionTypeRelation;
import org.robinbird.main.newrepository.dao.entity.InstanceEntity;
import org.robinbird.main.newrepository.dao.entity.RelationEntity;
import org.robinbird.main.newrepository.dao.entity.TypeEntity;

public class TypeDaoImpl implements TypeDao {

    private final EntityManagerFactory emf; // later, if wants to support multi-threads, need to create em per thread with this.
    private final EntityManager em; // currently running with embedded mode and open this when app starts and close when app ends.

    private final Query loadTypeEntityWithNameQuery;
    private final Query loadTypeEntitiesQuery;
    private final Query loadCompositionTypeRelationQuery;
//    private final Query loadInstanceEntityQuery;
//    private final Query loadRelationEntityQuery;

    public TypeDaoImpl(@NonNull final EntityManagerFactory emf) {
        this.emf = emf;
        this.em = emf.createEntityManager();
        loadTypeEntityWithNameQuery = em.createQuery("select te from TypeEntity te where te.name = :name");
        loadTypeEntitiesQuery = em.createQuery("select te from TypeEntity te where te.id in (:ids)");
        loadCompositionTypeRelationQuery = em.createQuery("select ctr from CompositionTypeRelation ctr where ctr.typeId = :id");
//        loadInstanceEntityQuery = em.createQuery("select ie from InstanceEntity ie where ie.parentTypeId = :parentId");
//        loadRelationEntityQuery = em.createQuery("select re from RelationEntity re where re.parentTypeId = :parentId");
    }

    public Optional<TypeEntity> loadTypeEntity(long id) {
        TypeEntity entity = em.find(TypeEntity.class, id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    public Optional<TypeEntity> loadTypeEntity(String name) {
        List result = loadTypeEntityWithNameQuery.setParameter("name", name).getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of((TypeEntity)result.get(0));
    }

    public List<TypeEntity> loadTypeEntities(List<Long> ids) {
        List result = loadTypeEntitiesQuery.setParameter("ids", ids).getResultList();
        if (result.isEmpty()) {
            return ImmutableList.of();
        }
        Iterator iter = result.iterator();
        List<TypeEntity> entities = new ArrayList<>(result.size());
        while(iter.hasNext()) {
            entities.add((TypeEntity)iter.next());
        }
        return entities;
    }

    public List<Long> loadCompositionTypeIds(long id) {
        List result = loadCompositionTypeRelationQuery.setParameter("id", id).getResultList();
        if (result.isEmpty()) {
            return ImmutableList.of();
        }
        Iterator iter = result.iterator();
        List<Long> ids = new ArrayList<>(result.size());
        while(iter.hasNext()) {
            ids.add(((CompositionTypeRelation)iter.next()).getCompositionTypeId());
        }
        return ids;
    }
    /*
    public List<InstanceEntity> loadInstanceEntities(long parentId) {
        List result = loadInstanceEntityQuery.setParameter("parentId", parentId).getResultList();
        if (result.isEmpty()) {
            return ImmutableList.of();
        }
        Iterator iter = result.iterator();
        List<InstanceEntity> entities = new ArrayList<>(result.size());
        while(iter.hasNext()) {
            entities.add((InstanceEntity)iter.next());
        }
        return entities;
    }

    public List<RelationEntity> loadRelationEntities(long parentId) {
        List result = loadRelationEntityQuery.setParameter("parentId", parentId).getResultList();
        if (result.isEmpty()) {
            return ImmutableList.of();
        }
        Iterator iter = result.iterator();
        List<RelationEntity> entities = new ArrayList<>(result.size());
        while(iter.hasNext()) {
            entities.add((RelationEntity)iter.next());
        }
        return entities;

    }*/

    public TypeEntity saveTypeEntity(TypeEntity te) {
        return transactional(te, em::persist);
    }

/*
    public InstanceEntity saveInstanceEntity(InstanceEntity ie) {
        return saveEntity(ie);
    }

    public RelationEntity saveReleationEntity(RelationEntity re) {
        return saveEntity(re);
    }*/

    private <T> T saveEntity(T entity) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            em.flush();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
        return entity;
    }
    public void deleteTypeEntity(TypeEntity te) {
        transactional(te, em::remove);
    }


    private <T> T transactional(T entity, Consumer<T> consumer) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            consumer.accept(entity);
            em.flush();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
        return entity;
    }

    private void transactionalForCompositionTypeIds(long parentId, List<Long>ids, Consumer<CompositionTypeRelation> consumer) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            for (Long id : ids) {
                CompositionTypeRelation relation =
                        CompositionTypeRelation.builder().typeId(parentId).compositionTypeId(id).build();
                consumer.accept(relation);
            }
            em.flush();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public void saveCompositionTypeIds(long parentId, List<Long> ids) {
        transactionalForCompositionTypeIds(parentId, ids, em::persist);
    }



    public void deleteCompositionTypeIds(long parentId, List<Long> ids) {
        transactionalForCompositionTypeIds(parentId, ids, em::remove);
    }
/*
    public void deleteInstanceEntity(InstanceEntity ie) {

    }

    public void deleteReleationEntity(RelationEntity re) {

    }
    */

}
