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
@EqualsAndHashCode(exclude = {"name", "members", "relations"})
public class Type {

    private final long id;
    @NonNull
    private TypeCategory category; // there is one case category can be updated. can be registered as CLASS and updated as INTERFACE later.
    @NonNull
    private final String name;
    @Nullable
    private final Type parentType;

    private final List<Instance> members; // mainly for member variables and functions
    private final List<Relation> relations; // mainly for inheritance and realization

    @Builder
    public Type(final long id, @NonNull final TypeCategory category, @NonNull final String name,
                @Nullable final Type parentType,
                @Nullable final List<Instance> members,
                @Nullable final List<Relation> relations) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.parentType = parentType;
        if (this.category == TypeCategory.PRIMITIVE) {
            this.members = null;
            this.relations = null;
        } else {
            this.members = Optional.ofNullable(members).orElse(new ArrayList<>());
            this.relations = Optional.ofNullable(relations).orElse(new ArrayList<>());
        }
    }

    public void updateTypeCategory(@NonNull final TypeCategory category) {
        this.category = category;
    }

    public void addInstance(@NonNull final Instance instance) {
        members.add(instance);
    }

    public void removeInstance(@NonNull final Instance instance) {
        members.removeIf(r -> r.equals(instance));
    }

    public List<Instance> getMembers() {
        return members;
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
                         @NonNull final List<Instance> members,
                         @NonNull final List<Relation> relations) {
        return Type.builder()
                   .id(this.id)
                   .category(this.category)
                   .name(this.name)
                   .members(members)
                   .relations(relations)
                   .build();
    }

    public String getSimpleName() {
        // todo: get simple name from name
        return null;
    }

    public int compareTo(Type another) {
        return this.getName().compareTo(another.getName());
    }

}