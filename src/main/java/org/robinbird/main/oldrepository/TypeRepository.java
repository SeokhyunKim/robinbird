package org.robinbird.main.oldrepository;

import java.util.List;
import java.util.Optional;
import org.robinbird.main.oldmodel2.Instance;
import org.robinbird.main.oldmodel2.Relation;
import org.robinbird.main.oldmodel2.Type;
import org.robinbird.main.oldmodel2.TypeCategory;

public interface TypeRepository {

    Type registerType(TypeCategory category, String name);

    Optional<Type> getType(long id);

    Optional<Type> getType(String name);

    void deleteType(long id);

    void deleteType(String name);

    Type populateType(Type type);

    void updateType(Type type);

    void addInstance(Type parentType, Instance instance);

    void addRelation(Type type, Relation relation);

    List<Type> getAllTypes();

    List<Type> getTypes(TypeCategory category);

}
