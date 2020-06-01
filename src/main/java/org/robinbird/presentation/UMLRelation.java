package org.robinbird.presentation;

import static org.robinbird.util.Msgs.Key.INTERNAL_ERROR;
import static org.robinbird.util.Msgs.Key.TRIED_TO_ADD_WRONG_COMPONENT;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.robinbird.exception.RobinbirdException;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.util.Msgs;

@Slf4j
@Getter
@ToString
@EqualsAndHashCode
public class UMLRelation {

    @Value
    public static class Key {
        @NonNull
        private final String first;
        @NonNull
        private final String second;

        boolean has(@NonNull final String keyName) {
            return first.equals(keyName) || second.equals(keyName);
        }
    }

    private final Key key;
    private final Map<String, Component> relationMap = Maps.newHashMap();

    public UMLRelation(@NonNull final Component first, @NonNull final Component second) {
        this.key = createKey(first, second);
        this.relationMap.put(first.getName(), first);
        this.relationMap.put(second.getName(), second);
    }

    public void addRelation(@NonNull final Component component) {
        Validate.isTrue(this.key.has(component.getName()), Msgs.get(TRIED_TO_ADD_WRONG_COMPONENT, component.getName()));
        this.relationMap.put(component.getName(), component);
    }

    public Component getFirst() {
        return this.relationMap.get(this.key.first);
    }

    public Component getSecond() {
        return this.relationMap.get(this.key.second);
    }

    public Optional<Cardinality> getCardinalityFromFirstToSecond() {
        return getCardinality(getFirst(), getSecond());
    }

    public Optional<Cardinality> getCardinalityFromSecondToFirst() {
        return getCardinality(getSecond(), getFirst());
    }

    private Optional<Cardinality> getCardinality(@NonNull final Component comp1, @NonNull final Component comp2) {
        final List<Relation> comp1MemberVars = comp1.getRelationsList(RelationCategory.MEMBER_VARIABLE);
        final Set<Cardinality> cardinalitySet = comp1MemberVars.stream()
                                                               .filter(r -> r.getRelatedComponent().equals(comp2))
                                                               .map(Relation::getCardinality)
                                                               .collect(Collectors.toSet());
        if (cardinalitySet.contains(Cardinality.MULTIPLE)) {
            return Optional.of(Cardinality.MULTIPLE);
        } else if (cardinalitySet.contains(Cardinality.ONE)) {
            return Optional.of(Cardinality.ONE);
        } else if (cardinalitySet.isEmpty()) {
            return Optional.empty();
        }
        throw new RobinbirdException(Msgs.get(INTERNAL_ERROR));
    }

    public static UMLRelation.Key createKey(@NonNull final Component first, @NonNull final Component second) {
        if (first.getName().compareTo(second.getName()) <= 0) {
            return new Key(first.getName(), second.getName());
        }
        return new Key(second.getName(), first.getName());
    }
}
