package org.robinbird.model;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = PRIVATE)
public class ComponentCasts {

    public static Class toClass(@NonNull final Component component) {
        return Class.builder()
                    .id(component.getId())
                    .name(component.getName())
                    .category(component.getComponentCategory())
                    .relations(component.getRelations())
                    .metadata(component.getMetadata())
                    .build();
    }
}
