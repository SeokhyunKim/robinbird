package org.robinbird.model;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.model.ComponentCasts.toClass;
import static org.robinbird.util.Msgs.Key.LIST_FOR_PACKAGE_NAME_IS_EMPTY;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Package extends Component {

    private static String DELIMITER = ".";

    private Package(@NonNull final String id, @NonNull final String name,
                    @Nullable Map<RelationCategory, List<Relation>> relations,
                    @Nullable Map<String, String> metadata) {
        super(id, name, ComponentCategory.PACKAGE, relations, metadata);
    }

    public void addClass(@NonNull final Class component) {
        final Relation relation = Relation.builder()
                                          .name(component.getName())
                                          .relationCategory(RelationCategory.PACKAGE_MEMBER)
                                          .relatedComponent(component)
                                          .cardinality(Cardinality.ONE)
                                          .owner(this)
                                          .build();
        addRelation(relation);
        // persist immediately
        CurrentRbRepository.persist(this);
    }

    public List<Class> getClasses() {
        return getRelationsList(RelationCategory.PACKAGE_MEMBER)
                       .stream()
                       .map(r -> toClass(r.getRelatedComponent()))
                       .collect(Collectors.toList());
    }

    public static Package create(@NonNull final String id, @NonNull final String name) {
        return new Package(id, name, null, null);
    }

    public static Package create(@NonNull final Component component) {
        Validate.isTrue(component.getComponentCategory() == ComponentCategory.PACKAGE,
                        Msgs.get(Msgs.Key.INTERNAL_ERROR));
        return new Package(component.getId(), component.getName(),
                           component.getRelations(), component.getMetadata());
    }

    public static String createPackageName(@NonNull final List<String> packageNameList) {
        checkState(packageNameList.size()>0, Msgs.get(LIST_FOR_PACKAGE_NAME_IS_EMPTY));
        StringBuffer sb = new StringBuffer();
        sb.append(packageNameList.get(0));
        for (int i=1; i<packageNameList.size(); ++i) {
            sb.append(DELIMITER).append(packageNameList.get(i));
        }
        return sb.toString();
    }


}
