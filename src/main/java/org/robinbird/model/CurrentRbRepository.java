package org.robinbird.model;

import java.util.List;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.robinbird.repository.RbRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrentRbRepository {

    private static RbRepository rbRepository;

    public static void setRbRepository(@Nullable final RbRepository rbRepository) {
        CurrentRbRepository.rbRepository = rbRepository;
    }

    public static void persist(@NonNull Component component) {
        rbRepository.updateComponent(component);
    }

    public static List<Relation> getRelations(@NonNull final Component component) {
        return rbRepository.getRelations(component);
    }

}
