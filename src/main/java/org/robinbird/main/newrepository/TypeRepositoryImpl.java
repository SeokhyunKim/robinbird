package org.robinbird.main.newrepository;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.robinbird.main.newmodel.Type;
import org.robinbird.main.newmodel.TypeCategory;
import org.robinbird.main.newrepository.dao.TypeDao;

@AllArgsConstructor
public class TypeRepositoryImpl implements TypeRepository {

    @NonNull
    private final TypeDao dao;

    public Type registerType(TypeCategory category, String name) {
        return null;

    }

    public Type getType(long id) {
        return null;

    }

    public Type getType(String name) {
        return null;

    }

    public void deleteType(long id) {

    }

    public void deleteType(String name) {

    }

    public void updateType(Type type) {
        
    }
}
