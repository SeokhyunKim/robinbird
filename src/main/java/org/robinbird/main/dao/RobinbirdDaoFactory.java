package org.robinbird.main.dao;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RobinbirdDaoFactory {

    private static final String PERSISTENT_UNIT = "entity-manager";

    /**
     * Create RobinbirdDao.
     * Currently, dao is working based on h2 database embeded mode.
     *
     * @param dbFileName embaded database fine name. If this is null, create unnamed private in-memory database for one connection.
     * @return created RobinbirdDao with given db file.
     */
    public static RobinbirdDao createDao(@NonNull String dbFileName) {
        final EntityManagerFactory emf;
        if (dbFileName == null) {
            emf = Persistence.createEntityManagerFactory(PERSISTENT_UNIT);
        } else {
            Map<String, Object> configOverrides = new HashMap<>();
            configOverrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + dbFileName);
            emf = Persistence.createEntityManagerFactory(PERSISTENT_UNIT, configOverrides);
        }
        return new RobinbirdDaoImpl(emf);
    }

    public static RobinbirdDao createDao() {
        return new RobinbirdDaoImpl(Persistence.createEntityManagerFactory(PERSISTENT_UNIT));
    }
}
