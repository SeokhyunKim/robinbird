package org.robinbird.main.repository.dao;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.robinbird.main.repository.dao.entity.ClassEntity;
import org.robinbird.main.repository.dao.entity.EntityConverter;
import org.robinbird.main.repository.dao.entity.RobinbirdEntity;
import org.robinbird.main.repository.dao.entity.TypeEntity;
import org.robinbird.main.model.Class;
import org.robinbird.main.model.ModelConverter;
import org.robinbird.main.model.RobinbirdObject;
import org.robinbird.main.model.Type;

@Slf4j
public class RobinbirdDaoImpl implements RobinbirdDao {

    private final EntityManagerFactory emf; // later, if wants to support multi-threads, need to create em per thread with this.
    private final EntityManager em; // currently running with embedded mode and open this when app starts and close when app ends.

    public RobinbirdDaoImpl(@NonNull final EntityManagerFactory emf) {
        this.emf = emf;
        this.em = emf.createEntityManager();
    }

    public RobinbirdObject load(String name) {
        List result = em.createQuery("select rb from RobinbirdEntity rb where rb.name = :param")
                        .setParameter("param", name)
                        .getResultList();
        if (result.isEmpty()) {
            return null;
        }
        RobinbirdEntity entity = (RobinbirdEntity)result.get(0);
        if (entity.getType().equals(TypeEntity.class.getSimpleName())) {
            TypeEntity typeEntity = em.find(TypeEntity.class, entity.getId());
            return EntityConverter.convert(typeEntity);
        }
        return null;
    }

    public RobinbirdObject load(long id) {
        RobinbirdEntity robinbird = em.find(RobinbirdEntity.class, id);
        if (robinbird.getType().equals(TypeEntity.class.getSimpleName())) {
            TypeEntity entity = em.find(TypeEntity.class, robinbird.getId());
            return EntityConverter.convert(entity);
        }
        return null;
    }

    public long save(RobinbirdObject newObject) {
        EntityTransaction tx = null;
        long id = 0;
        try {
            tx = em.getTransaction();
            tx.begin();
            if (newObject instanceof Type) {
                TypeEntity entity = ModelConverter.convert((Type) newObject);
                em.persist(entity);
                id = entity.getId();
            } else if (newObject instanceof Class) {
                ClassEntity entity = ModelConverter.convert((Class) newObject);
                em.persist(entity);
                id = entity.getId();
            }
            em.flush();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null  && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
        return id;
    }

    public List<RobinbirdObject> loadAll() {
        List<RobinbirdEntity> result = em.createQuery("select rb from RobinbirdEntity  rb").getResultList();
        return result.stream().map(EntityConverter::convert).collect(Collectors.toList());
    }

    public int getTotalNumber() {
        return em.createQuery("select rb from RobinbirdEntity  rb").getResultList().size();
    }

    public boolean isExist(String name) {
        List result = em.createQuery("select rb from RobinbirdEntity rb where rb.name = :param")
                        .setParameter("param", name)
                        .getResultList();
        return !result.isEmpty();
    }
}
