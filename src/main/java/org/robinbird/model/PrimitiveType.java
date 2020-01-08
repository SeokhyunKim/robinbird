package org.robinbird.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class PrimitiveType extends Component {

    @Builder
    public PrimitiveType(@NonNull final String id, @NonNull final String name) {
        super(id, name, ComponentCategory.PRIMITIVE_TYPE, null, null);
    }

}
