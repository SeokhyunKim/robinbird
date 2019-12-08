package org.robinbird.clustering;

import java.util.List;
import org.robinbird.model.Component;
import org.robinbird.model.Relation;

public interface RelationsSelector {

    List<Relation> getRelations(Component component);

}
