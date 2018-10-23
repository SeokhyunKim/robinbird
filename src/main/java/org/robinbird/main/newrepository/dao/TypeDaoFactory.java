package org.robinbird.main.newrepository.dao;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.NonNull;
import org.robinbird.main.repository.dao.RobinbirdDaoImpl;

public class TypeDaoFactory {

    private static final String PERSISTENT_UNIT = "entity-manager";

    /**
     * Create TypeDao.
     * Currently, dao is working based on h2 database embeded mode.
     *
     * @param dbFileName embaded database fine name. If this is null, create unnamed private in-memory database for one connection.
     * @return created RobinbirdDao with given db file.
     */
    public static TypeDao createDao(@NonNull String dbFileName) {
        final EntityManagerFactory emf;
        if (dbFileName == null) {
            emf = Persistence.createEntityManagerFactory(PERSISTENT_UNIT);
        } else {
            Map<String, Object> configOverrides = new HashMap<>();
            configOverrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + dbFileName);
            emf = Persistence.createEntityManagerFactory(PERSISTENT_UNIT, configOverrides);
        }
        return new TypeDaoImpl(emf);
    }

    public static TypeDao createDao() {
        return new TypeDaoImpl(Persistence.createEntityManagerFactory(PERSISTENT_UNIT));
    }
}
