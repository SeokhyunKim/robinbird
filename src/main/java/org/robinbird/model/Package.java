package org.robinbird.model;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.util.Msgs.Key.FOUND_COMPONENT_OF_DIFFERENT_TYPE;
import static org.robinbird.util.Msgs.Key.LIST_FOR_PACKAGE_NAME_IS_EMPTY;
import static org.robinbird.util.Msgs.Key.WRONG_COMPONENT_CATEGORY;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Builder;
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

    @Builder
    private Package(final long id, @NonNull final String name, @Nullable final List<Relation> relations) {
        super(id, name, ComponentCategory.PACKAGE, relations, null);
    }

    public void addClass(@NonNull final Class component) {
        final Relation relation = Relation.builder()
                                          .name(component.getName())
                                          .relationCategory(RelationCategory.PACKAGE_MEMBER)
                                          .relatedComponent(component)
                                          .cardinality(Cardinality.ONE)
                                          .parent(this)
                                          .build();
        addRelation(relation);
        // persist immediately
        persist();
    }

    public List<Class> getClasses() {
        return getRelations(RelationCategory.PACKAGE_MEMBER)
                       .stream()
                       .map(r -> {
                           final Component component = r.getRelatedComponent();
                           return Class.builder()
                                       .id(component.getId())
                                       .name(component.getName())
                                       .category(component.getComponentCategory())
                                       .relations(component.getRelations())
                                       .metadata(component.getMetadata())
                                       .build();
                       })
                       .collect(Collectors.toList());
    }

    public static Package create(@NonNull final Component component) {
        Validate.isTrue(component.getComponentCategory() == ComponentCategory.PACKAGE,
                        Msgs.get(FOUND_COMPONENT_OF_DIFFERENT_TYPE, component.getName(), component.getComponentCategory().name()));
        return Package.builder()
                      .id(component.getId())
                      .name(component.getName())
                      .relations(component.getRelations())
                      .build();
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
