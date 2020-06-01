package org.robinbird.repository.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;

@Slf4j
public class EntityDaoImpl implements EntityDao {

    private final EntityManagerFactory emf; // later, if wants to support multi-threads, need to create em per thread with this.
    private final EntityManager em; // currently running with embedded mode and open this when app starts and close when app ends.

    private final Query loadComponentEntityWithIdQuery;
    private final Query loadComponentEntityWithNameAndOwnerIdQuery;
    private final Query loadComponentEntityWithNameQuery;
    private final Query loadComponentEntitiesWithComponentCategory;
    private final Query loadRelationEntityWithParentIdAndIdQuery;
    private final Query loadRelationEntitiesWithParentIdQuery;
    private final Query updateComponentEntityQuery;
    private final Query updateRelationEntityQuery;
    private final Query deleteComponentEntityQuery;
    private final Query deleteRelationEntityQuery;
    private final Query countComponentEntities;

    public EntityDaoImpl(@NonNull final EntityManagerFactory emf) {
        this.emf = emf;
        this.em = this.emf.createEntityManager();

        loadComponentEntityWithIdQuery = em.createQuery("select ce from ComponentEntity ce where ce.id = :id");
        loadComponentEntityWithNameAndOwnerIdQuery = em.createQuery("select ce from ComponentEntity ce " +
                                                                            "where ce.name = :name and ce.ownerId = :ownerId");
        loadComponentEntityWithNameQuery = em.createQuery("select ce from ComponentEntity ce where ce.name = :name");
        loadComponentEntitiesWithComponentCategory = em.createQuery("select ce from ComponentEntity ce where " +
                                                                        "ce.componentCategory = :componentCategory");
        loadRelationEntityWithParentIdAndIdQuery = em.createQuery("select r from RelationEntity r where r.ownerId = :parentId and r.id = :id");
        loadRelationEntitiesWithParentIdQuery = em.createQuery("select r from RelationEntity r where r.ownerId = :parentId");

        updateComponentEntityQuery = em.createQuery("update ComponentEntity " +
                                                            "set name = :name, " +
                                                            "componentCategory = :componentCategory, " +
                                                            "metadata = :metadata " +
                                                            "where id = :id");
        updateRelationEntityQuery = em.createQuery("update RelationEntity " +
                                                           "set relationCategory = :relationCategory," +
                                                           "name = :name," +
                                                           "relatedComponentId = :relationId," +
                                                           "cardinality = :cardinality," +
                                                           "metadata = :metadata" +
                                                           " where ownerId = :parentId and id = :id");
        deleteComponentEntityQuery = em.createQuery("delete from ComponentEntity where id = :id");
        deleteRelationEntityQuery = em.createQuery("delete from RelationEntity where ownerId = :parentId and id = :id");
        countComponentEntities = em.createQuery("select count(ce) from ComponentEntity ce");

        log.info("DaoImpl created.");
    }

    private <T> void transactional(@NonNull final T entity, @NonNull final Consumer<T> consumer) {
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
    }

    private <T, S> Optional<T> loadEntity(@NonNull final Query query, @NonNull final String paramName, final S val) {
        List result = query.setParameter(paramName, val)
                           .getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of((T)result.get(0));
    }

    private <T, S1, S2> Optional<T> loadEntity(@NonNull final Query query,
                                               @NonNull final String paramName1, final S1 val1,
                                               @NonNull final String paramName2, final S2 val2) {
        List result = query.setParameter(paramName1, val1)
                           .setParameter(paramName2, val2)
                           .getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of((T)result.get(0));
    }

    private <T, S> List<T> loadEntities(@NonNull final Query query, @NonNull final String paramName, final S val) {
        List result = query.setParameter(paramName, val)
                           .getResultList();
        List<T> results = new ArrayList<>();
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            results.add((T)iter.next());
        }
        return results;
    }

    @Override
    public Optional<ComponentEntity> loadComponentEntityById(final String id) {
        return loadEntity(loadComponentEntityWithIdQuery, "id", id);
    }

    @Override
    public Optional<ComponentEntity> loadComponentEntityByNameAndOwnerId(@NonNull final String name, @NonNull final String ownerId) {
        return loadEntity(loadComponentEntityWithNameAndOwnerIdQuery, "name", name, "ownerId", ownerId);
    }

    @Override
    public List<ComponentEntity> loadComponentEntityByName(String name) {
        return loadEntities(loadComponentEntityWithNameQuery, "name", name);
    }

    @Override
    public List<ComponentEntity> loadComponentEntities(@NonNull final String componentCategory) {
        return loadEntities(loadComponentEntitiesWithComponentCategory, "componentCategory", componentCategory);
    }

    @Override
    public Optional<RelationEntity> loadRelationEntity(final String parentId, final String id) {
        return loadEntity(loadRelationEntityWithParentIdAndIdQuery, "parentId", parentId, "id", id);
    }

    @Override
    public List<RelationEntity> loadRelationEntities(final String parentId) {
        return loadEntities(loadRelationEntitiesWithParentIdQuery, "parentId", parentId);
    }

    @Override
    public int getNumComponentEntities() {
        final List resultList = countComponentEntities.getResultList();
        if (resultList.isEmpty()) {
            return 0;
        }
        return ((Long)resultList.iterator().next()).intValue();
    }

    @Override
    public <T> T save(@NonNull final T entity) {
        if (entity instanceof RelationEntity) {
            final RelationEntity relationEntity = (RelationEntity) entity;
            if (relationEntity.getId() == null) {
                relationEntity.setId(UUID.randomUUID().toString());
            }
        }
        transactional(entity, em::persist);
        return entity;
    }

    @Override
    public <T> T update(@NonNull final T entity) {
        if (entity instanceof ComponentEntity) {
            final ComponentEntity ae = (ComponentEntity)entity;
            final Optional<ComponentEntity> analysisEntityOpt = loadComponentEntityById(ae.getId());
            if (!analysisEntityOpt.isPresent()) {
                return null;
            }
            transactional(updateComponentEntityQuery.setParameter("id", ae.getId())
                                                    .setParameter("name", ae.getName())
                                                    .setParameter("componentCategory", ae.getComponentCategory())
                                                    .setParameter("metadata", ae.getMetadata()), Query::executeUpdate);
            return entity;
        } else if (entity instanceof RelationEntity) {
            final RelationEntity r = (RelationEntity)entity;
            final Optional<RelationEntity> rOpt = loadRelationEntity(r.getOwnerId(), r.getId());
            if (!rOpt.isPresent()) {
                return null;
            }
            transactional(updateRelationEntityQuery.setParameter("ownerId", r.getOwnerId())
                                                   .setParameter("id", r.getId())
                                                   .setParameter("relationCategory", r.getRelationCategory())
                                                   .setParameter("name", r.getName())
                                                   .setParameter("relationId", r.getRelatedComponentId())
                                                   .setParameter("cardinality", r.getCardinality())
                                                   .setParameter("metadata", r.getMetadata()), Query::executeUpdate);
            return entity;
        }
        return null;
    }

    @Override
    public <T> void delete(@NonNull final T entity) {
        if (entity instanceof ComponentEntity) {
            final ComponentEntity ae = (ComponentEntity)entity;
            transactional(deleteComponentEntityQuery.setParameter("id", ae.getId()), Query::executeUpdate);
        } else if (entity instanceof RelationEntity) {
            final RelationEntity r = (RelationEntity)entity;
            transactional(deleteRelationEntityQuery.setParameter("ownerId", r.getOwnerId())
                                                   .setParameter("id", r.getId()), Query::executeUpdate);
        }
    }

    @Override
    public void deleteAll() {
        final Query deleteAllComponentEntities = em.createQuery("delete from ComponentEntity");
        final Query deleteAllRelationEntities = em.createQuery("delete from RelationEntity");

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            deleteAllComponentEntities.executeUpdate();
            deleteAllRelationEntities.executeUpdate();
            em.flush();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public void close() {
        em.close();
        emf.close();
    }
}
