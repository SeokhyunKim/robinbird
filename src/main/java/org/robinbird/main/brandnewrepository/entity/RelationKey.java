package org.robinbird.main.brandnewrepository.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class RelationKey implements Serializable {

    @Column(nullable = false)
    private int analysisEntityId;

    @Column(nullable = false)
    private int relationId;

}
