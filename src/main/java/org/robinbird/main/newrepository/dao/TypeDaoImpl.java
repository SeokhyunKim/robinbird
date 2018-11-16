package org.robinbird.main.newrepository.dao;

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
import org.robinbird.main.newrepository.dao.entity.CompositionTypeEntity;
import org.robinbird.main.newrepository.dao.entity.InstanceEntity;
import org.robinbird.main.newrepository.dao.entity.RelationEntity;
import org.robinbird.main.newrepository.dao.entity.TypeEntity;

public class TypeDaoImpl implements TypeDao {

    private final EntityManagerFactory emf; // later, if wants to support multi-threads, need to create em per thread with this.
    private final EntityManager em; // currently running with embedded mode and open this when app starts and close when app ends.

    private final Query loadTypeEntityWithNameQuery;
    private final Query updateTypeEntityQuery;
    private final Query loadCompositionTypeEntitiesQuery;
    private final Query deleteCompositionTypeEntityQuery;
    private final Query loadInstanceEntityQuery;
    private final Query loadInstanceEntitiesQuery;
    private final Query updateInstanceEntityQuery;
    private final Query deleteInstanceEntitiesQuery;
    private final Query loadRelationEntityQuery;
    private final Query loadRelationEntitiesQuery;
    private final Query updateRelationEntity;
    private final Query deleteRelationEntitiesQuery;

    public TypeDaoImpl(@NonNull final EntityManagerFactory emf) {
        this.emf = emf;
        this.em = emf.createEntityManager();
        loadTypeEntityWithNameQuery = em.createQuery("select te from TypeEntity te where te.name = :name");
        updateTypeEntityQuery = em.createQuery("update TypeEntity set name = :name, category = :category where id = :id");
        loadCompositionTypeEntitiesQuery = em.createQuery("select cte from CompositionTypeEntity cte where cte.typeId = :typeId");
        deleteCompositionTypeEntityQuery = em.createQuery("delete from CompositionTypeEntity cte where cte.typeId = :typeId");
        loadInstanceEntityQuery = em.createQuery("select ie from InstanceEntity ie where ie.parentTypeId = :parentTypeId and ie.typeId = :typeId and ie.name = :name");
        loadInstanceEntitiesQuery = em.createQuery("select ie from InstanceEntity ie where ie.parentTypeId = :parentTypeId");
        updateInstanceEntityQuery = em.createQuery("update InstanceEntity set accessModifier = :accessModifier where parentTypeId = :parentTypeId and typeId = :typeId and name = :name");
        deleteInstanceEntitiesQuery = em.createQuery("delete from InstanceEntity ie where ie.parentTypeId = :parentTypeId");
        loadRelationEntityQuery = em.createQuery("select re from RelationEntity re where re.parentTypeId = :parentTypeId and re.typeId = :typeId");
        loadRelationEntitiesQuery = em.createQuery("select re from RelationEntity re where re.parentTypeId = :parentTypeId");
        updateRelationEntity = em.createQuery("update RelationEntity set category = :category where parentTypeId = :parentTypeId and typeId = :typeId");
        deleteRelationEntitiesQuery = em.createQuery("delete from RelationEntity re where re.parentTypeId = :parentTypeId");
    }

    private <T> T transactional(@NonNull final T entity, @NonNull final Consumer<T> consumer) {
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

    private void transactional(@NonNull final Query query) {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            query.executeUpdate();
            em.flush();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    private <T> List<T> loadEntities(@NonNull final Query query, @NonNull final String paramName, final long id) {
        List result = query.setParameter(paramName, id)
                           .getResultList();
        List<T> results = new ArrayList<>();
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            results.add((T)iter.next());
        }
        return results;
    }

    @Override
    public Optional<TypeEntity> loadTypeEntity(final long id) {
        TypeEntity entity = em.find(TypeEntity.class, id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<TypeEntity> loadTypeEntity(@NonNull final String name) {
        List result = loadTypeEntityWithNameQuery.setParameter("name", name)
                                                 .getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of((TypeEntity)result.get(0));
    }

    @Override
    public TypeEntity saveTypeEntity(@NonNull final TypeEntity te) {
        return transactional(te, em::persist);
    }

    @Override
    public TypeEntity updateTypeEntity(TypeEntity te) {
        Optional<TypeEntity> loaded = loadTypeEntity(te.getId());
        if (!loaded.isPresent()) {
            return null;
        }
        transactional(updateTypeEntityQuery.setParameter("name", te.getName())
                                           .setParameter("category", te.getCategory())
                                           .setParameter("id", te.getId()));
        return te;
    }

    @Override
    public void removeTypeEntity(@NonNull final TypeEntity te) {
        transactional(te, em::remove);
    }

    @Override
    public void removeTypeEntity(long id) {
        Optional<TypeEntity> teOpt = loadTypeEntity(id);
        teOpt.ifPresent(te -> transactional(te, em::remove));
    }

    @Override
    public List<CompositionTypeEntity> loadCompositionTypeEntities(final long typeId) {
        return loadEntities(loadCompositionTypeEntitiesQuery, "typeId", typeId);
    }

    @Override
    public CompositionTypeEntity saveCompositionTypeEntity(@NonNull final CompositionTypeEntity compositionTypeEntity) {
        return transactional(compositionTypeEntity, em::persist);
    }

    @Override
    public void removeCompositionTypeEntities(final long typeId) {
        transactional(deleteCompositionTypeEntityQuery.setParameter("typeId", typeId));
    }

    @Override
    public List<InstanceEntity> loadInstanceEntities(final long parentTypeId) {
        return loadEntities(loadInstanceEntitiesQuery, "parentTypeId", parentTypeId);
    }

    @Override
    public InstanceEntity saveInstanceEntity(@NonNull final InstanceEntity instanceEntity) {
        return transactional(instanceEntity, em::persist);
    }

    @Override
    public InstanceEntity updateInstanceEntity(InstanceEntity ie) {
        List result = loadInstanceEntityQuery.setParameter("parentTypeId", ie.getParentTypeId())
                                             .setParameter("typeId", ie.getTypeId())
                                             .setParameter("name", ie.getName())
                                             .getResultList();
        if (result.isEmpty()) {
            return null;
        }
        InstanceEntity loaded = (InstanceEntity)result.get(0);
        transactional(updateInstanceEntityQuery.setParameter("accessModifier", ie.getAccessModifier())
                                               .setParameter("parentTypeId", loaded.getParentTypeId())
                                               .setParameter("typeId", loaded.getTypeId())
                                               .setParameter("name", loaded.getName()));
        return ie;
    }

    @Override
    public void removeInstanceEntity(InstanceEntity instanceEntity) {
        transactional(instanceEntity, em::remove);
    }

    @Override
    public void removeInstanceEntities(long parentTypeId) {
        transactional(deleteInstanceEntitiesQuery.setParameter("parentTypeId", parentTypeId));
    }

    @Override
    public List<RelationEntity> loadRelationEntities(long parentTypeId) {
        return loadEntities(loadRelationEntitiesQuery, "parentTypeId", parentTypeId);
    }

    @Override
    public RelationEntity saveRelationEntity(RelationEntity relationEntity) {
        return transactional(relationEntity, em::persist);
    }

    @Override
    public RelationEntity updateRelationEntity(RelationEntity re) {
        List result = loadRelationEntityQuery.setParameter("parentTypeId", re.getParentTypeId())
                                             .setParameter("typeId", re.getTypeId())
                                             .getResultList();
        if (result.isEmpty()) {
            return null;
        }
        RelationEntity loaded = (RelationEntity)result.get(0);
        transactional(updateRelationEntity.setParameter("parentTypeId", loaded.getParentTypeId())
                                          .setParameter("typeId", loaded.getTypeId())
                                          .setParameter("category", re.getCategory()));
        return re;
    }

    @Override
    public void removeRelationEntity(RelationEntity relationEntity) {
        transactional(relationEntity, em::remove);
    }

    @Override
    public void removeRelationEntities(long parentTypeId) {
        transactional(deleteRelationEntitiesQuery.setParameter("parentTypeId", parentTypeId));
    }

}
