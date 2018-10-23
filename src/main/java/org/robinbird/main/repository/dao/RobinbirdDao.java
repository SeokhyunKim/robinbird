package org.robinbird.main.repository.dao;

import java.util.List;
import org.robinbird.main.model.RobinbirdObject;


public interface RobinbirdDao {

    RobinbirdObject load(String name);

    RobinbirdObject load(long id);

    default <T> T loadByType(String name) {
        return (T)load(name);
    }

    default <T> T loadByType(int id) {
        return (T)load(id);
    }

    long save(RobinbirdObject newObject);

    List<RobinbirdObject> loadAll();

    int getTotalNumber();

    boolean isExist(String name);
}