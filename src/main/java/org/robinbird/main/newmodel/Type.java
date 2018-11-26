package org.robinbird.main.newmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(exclude = {"name", "compositionTypes", "instances"})
public class Type {

    @NonNull
    private final Long id;
    @NonNull
    private final TypeCategory category;
    @NonNull
    private final String name;

    private final List<Type> compositionTypes; // mainly for collections which have several related types
    private final List<Instance> instances; // mainly for member variables and functions
    private final List<Relation> relations; // mainly for inheritance and realization

    @Builder
    public Type(final long id, @NonNull final TypeCategory category, @NonNull final String name,
                @Nullable final List<Type> compositionTypes,
                @Nullable final List<Instance> instances,
                @Nullable final List<Relation> relations) {
        this.id = id;
        this.category = category;
        this.name = name;
        if (this.category == TypeCategory.PRIMITIVE) {
            this.compositionTypes = null;
            this.instances = null;
            this.relations = null;
        } else {
            this.compositionTypes = Optional.ofNullable(compositionTypes).orElse(new ArrayList<>());
            this.instances = Optional.ofNullable(instances).orElse(new ArrayList<>());
            this.relations = Optional.ofNullable(relations).orElse(new ArrayList<>());
        }
    }

    public void addInstance(@NonNull final Instance instance) {
        instances.add(instance);
    }

    public void removeInstance(@NonNull final Instance instance) {
        instances.removeIf(r -> r.equals(instance));
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public void addRelation(@NonNull final Relation relation) {
        relations.add(relation);
    }

    public void removeRelation(@NonNull final Relation relation) {
        relations.removeIf(r -> r.equals(relation));
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public Type populate(@NonNull final List<Type> compositionTypes,
                         @NonNull final List<Instance> instances,
                         @NonNull final List<Relation> relations) {
        return Type.builder()
                   .id(this.id)
                   .category(this.category)
                   .name(this.name)
                   .compositionTypes(compositionTypes)
                   .instances(instances)
                   .relations(relations)
                   .build();
    }

    public String getSimpleName() {
        // todo: get simple name from name
        return null;
    }

}