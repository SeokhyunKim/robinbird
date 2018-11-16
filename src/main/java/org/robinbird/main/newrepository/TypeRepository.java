package org.robinbird.main.newrepository;

import java.util.Optional;
import org.robinbird.main.newmodel.Type;
import org.robinbird.main.newmodel.TypeCategory;

public interface TypeRepository {

    Type registerType(TypeCategory category, String name);

    Optional<Type> getType(long id);

    Optional<Type> getType(String name);

    void deleteType(long id);

    void deleteType(String name);

    Type populateType(Type type);

    void updateType(Type type);

}
