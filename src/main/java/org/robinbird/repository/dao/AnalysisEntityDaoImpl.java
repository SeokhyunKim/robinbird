package org.robinbird.repository.dao;

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
import lombok.extern.slf4j.Slf4j;
import org.robinbird.repository.entity.AnalysisEntity;
import org.robinbird.repository.entity.Relation;

@Slf4j
public class AnalysisEntityDaoImpl implements AnalysisEntityDao {

    private final EntityManagerFactory emf; // later, if wants to support multi-threads, need to create em per thread with this.
    private final EntityManager em; // currently running with embedded mode and open this when app starts and close when app ends.

    private final Query loadAnalysisEntityWithIdQuery;
    private final Query loadAnalysisEntityWithNameQuery;
    private final Query loadRelationWithIdQuery;
    private final Query loadRelationsWithAnalysisEntityIdQuery;
    private final Query updateAnalysisEntityQuery;
    private final Query updateRelationQuery;
    private final Query deleteAnalysisEntityQuery;
    private final Query deleteRelationQuery;

    public AnalysisEntityDaoImpl(@NonNull final EntityManagerFactory emf) {
        this.emf = emf;
        this.em = this.emf.createEntityManager();

        loadAnalysisEntityWithIdQuery = em.createQuery("select ae from AnalysisEntity ae where ae.id = :id");
        loadAnalysisEntityWithNameQuery = em.createQuery("select ae from AnalysisEntity ae where ae.name = :name");
        loadRelationWithIdQuery = em.createQuery("select r from Relation r where r.id = :id");
        loadRelationsWithAnalysisEntityIdQuery = em.createQuery("select r from Relation r " +
                                                                        "where r.analysisEntityId = :analysisEntityId");
        updateAnalysisEntityQuery = em.createQuery("update AnalysisEntity set name = :name, category = :category where id = :id");
        updateRelationQuery = em.createQuery("update Relation set analysisEntityId = :analysisEntityId, " +
                                                     "relationType = :relationType, cardinality = :cardinality where id = :id");

        deleteAnalysisEntityQuery = em.createQuery("delete from AnalysisEntity where id = :id");
        deleteRelationQuery = em.createQuery("delete from Relation where id = :id");

        log.info("DaoImpl created.");
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

    private <T, S> Optional<T> loadEntity(@NonNull final Query query, @NonNull final String paramName, final S val) {
        List result = query.setParameter(paramName, val)
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

    public Optional<AnalysisEntity> loadAnalysisEntity(long id) {
        return loadEntity(loadAnalysisEntityWithIdQuery, "id", id);
    }

    public Optional<AnalysisEntity> loadAnalysisEntity(String name) {
        return loadEntity(loadAnalysisEntityWithNameQuery, "name", name);
    }

    public Optional<Relation> loadRelation(long id) {
        return loadEntity(loadRelationWithIdQuery, "id", id);
    }

    public List<Relation> loadRelations(long analysisEntityId) {
        return loadEntities(loadRelationsWithAnalysisEntityIdQuery, "analysisEntityId", analysisEntityId);
    }

    public <T> T save(T entity) {
        return transactional(entity, em::persist);
    }

    public <T> T update(T entity) {
        if (entity instanceof AnalysisEntity) {
            final AnalysisEntity ae = (AnalysisEntity)entity;
            final Optional<AnalysisEntity> analysisEntityOpt = loadAnalysisEntity(ae.getId());
            if (!analysisEntityOpt.isPresent()) {
                return null;
            }
            transactional(updateAnalysisEntityQuery.setParameter("id", ae.getId())
                                                   .setParameter("name", ae.getName())
                                                   .setParameter("category", ae.getCategory()));
            return entity;
        } else if (entity instanceof Relation) {
            final Relation r = (Relation)entity;
            final Optional<Relation> rOpt = loadRelation(r.getId());
            if (!rOpt.isPresent()) {
                return null;
            }
            transactional(updateRelationQuery.setParameter("id", r.getId())
                                             .setParameter("analysisEntityId", r.getAnalysisEntityId())
                                             .setParameter("relationType", r.getRelationType())
                                             .setParameter("cardinality", r.getCardinality()));
            return entity;
        }
        return null;
    }

    public <T> void delete(T entity) {
        if (entity instanceof AnalysisEntity) {
            final AnalysisEntity ae = (AnalysisEntity)entity;
            transactional(deleteAnalysisEntityQuery.setParameter("id", ae.getId()));
        } else if (entity instanceof Relation) {
            final Relation r = (Relation)entity;
            transactional(deleteRelationQuery.setParameter("id", r.getId()));
        }
    }
}
