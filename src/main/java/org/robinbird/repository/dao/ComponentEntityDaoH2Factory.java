package org.robinbird.repository.dao;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.NonNull;

public class ComponentEntityDaoH2Factory {

    private static final String PERSISTENT_UNIT = "entity-manager";

    /**
     * Create ComponentEntityDao working in-memory.
     * @return created in-memory ComponentEntityDao.
     */
    public static ComponentEntityDao createDao() {
        return createDaoInternal(null);
    }

    /**
     * Create ComponentEntityDao working with a given file.
     * @param dbFileName a db file name.
     * @return created ComponentEntityDao working with the given db file.
     */
    public static ComponentEntityDao createDao(@NonNull final String dbFileName) {
        return createDaoInternal(dbFileName);
    }

    private static ComponentEntityDao createDaoInternal(@Nullable String dbFileName) {
        final EntityManagerFactory emf;
        if (dbFileName == null) {
            emf = Persistence.createEntityManagerFactory(PERSISTENT_UNIT);
        } else {
            Map<String, Object> configOverrides = new HashMap<>();
            configOverrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + dbFileName);
            emf = Persistence.createEntityManagerFactory(PERSISTENT_UNIT, configOverrides);
        }
        return new ComponentEntityDaoImpl(emf);
    }

}