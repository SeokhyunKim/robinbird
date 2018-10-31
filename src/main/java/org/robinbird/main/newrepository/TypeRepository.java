package org.robinbird.main.newrepository;

import org.robinbird.main.newmodel.Type;
import org.robinbird.main.newmodel.TypeCategory;

public interface TypeRepository {

    Type registerType(TypeCategory category, String name);

    Type getType(long id);

    Type getType(String name);

    void deleteType(long id);

    void deleteType(String name);

    void updateType(Type type);

}
