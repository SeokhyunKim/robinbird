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
        return createDaoInternal(null, false);
    }

    /**
     * Create ComponentEntityDao working with a given file.
     * @param dbFileName a db file name.
     * @return created ComponentEntityDao working with the given db file.
     */
    public static ComponentEntityDao createDao(@NonNull final String dbFileName, final boolean discardExistingDbFile) {
        return createDaoInternal(dbFileName, discardExistingDbFile);
    }

    private static ComponentEntityDao createDaoInternal(@Nullable final String dbFileName, final boolean discardExistingDbFile) {
        final EntityManagerFactory emf;
        if (dbFileName == null) {
            emf = Persistence.createEntityManagerFactory(PERSISTENT_UNIT);
        } else {
            Map<String, Object> configOverrides = new HashMap<>();
            configOverrides.put("javax.persistence.jdbc.driver", "org.h2.Driver");
            configOverrides.put("javax.persistence.jdbc.url", "jdbc:h2:file:" + dbFileName);
            configOverrides.put("javax.persistence.jdbc.user", "rb");
            configOverrides.put("javax.persistence.jdbc.password", "");
            configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            if (discardExistingDbFile) {
                configOverrides.put("hibernate.hbm2ddl.auto", "create");
            } else {
                configOverrides.put("hibernate.hbm2ddl.auto", "update");
            }
            emf = Persistence.createEntityManagerFactory(PERSISTENT_UNIT, configOverrides);
        }
        return new ComponentEntityDaoImpl(emf);
    }

}