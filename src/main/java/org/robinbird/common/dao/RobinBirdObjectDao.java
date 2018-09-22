package org.robinbird.common.dao;

import java.util.List;


public interface RobinBirdObjectDao {

    RobinBirdObject load(String name);

    default <T> T loadByType(String name) {
        return (T)load(name);
    }

    RobinBirdObject load(int id);

    default <T> T loadByType(int id) {
        return (T)load(id);
    }

    void save(RobinBirdObject newObject);

    List<RobinBirdObject> loadAll();

    int getTotalNumber();

    boolean isExist(String name);
}