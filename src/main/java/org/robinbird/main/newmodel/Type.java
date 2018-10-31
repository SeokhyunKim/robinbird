package org.robinbird.main.newmodel;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.Validate;

@Getter
@ToString
@EqualsAndHashCode(exclude = {"name", "compositionTypes", "instances"})
public class Type {

    @Nullable
    private final Long id; // id is mainly for classes and interfaces. don't give id something like List<Integer>
    @NonNull
    private final TypeCategory category;
    @NonNull
    private final String name;

    private final List<Type> compositionTypes; // mainly for collections which have several related types
    private final List<Instance> instances; // mainly for member variables and functions
    private final List<Relation> relations; // mainly for inheritance and realization

    @Builder
    public Type(final long id, @NonNull final TypeCategory category,
                @NonNull final String name, @Nullable final List<Type> compositionTypes) {
        this.id = id;
        this.category = category;
        this.name = name;
        if (this.category == TypeCategory.PRIMITIVE) {
            instances = null;
            relations = null;
        } else {
            instances = new ArrayList<>();
            relations = new ArrayList<>();
        }
        if (this.category == TypeCategory.COLLECTION || this.category == TypeCategory.FUNCTION) {
            this.compositionTypes = new ArrayList<>();
            if (compositionTypes != null) {
                this.compositionTypes.addAll(compositionTypes);
            }
        } else {
            this.compositionTypes = null;
        }
    }

    public void addInstance(@NonNull final Instance instance) {
        Validate.isTrue(instance != null, "Cannot add instance because instances is null");
        instances.add(instance);
    }

    public void removeInstance(@NonNull final Instance instance) {
        Validate.isTrue(instances != null, "Cannot remove instance because instances is null");
        instances.removeIf(r -> r.equals(instance));
    }

    public List<Relation> getRelations() {
        // todo: this will be derived from list of instances.
        return null;
    }

    public String getSimpleName() {
        // todo: get simple name from name
        return null;
    }

}