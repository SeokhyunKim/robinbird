package org.robinbird.main.repository.dao.entity;

import static javax.persistence.FetchType.EAGER;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class PackageEntity extends RobinbirdEntity {

    @OneToMany(fetch = EAGER)
    private List<ClassEntity> classEntities;

}
