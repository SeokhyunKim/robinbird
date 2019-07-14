package org.robinbird.main.newpresentation;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.robinbird.main.oldmodel2.AnalysisContext;
import org.robinbird.main.oldmodel2.Instance;
import org.robinbird.main.oldmodel2.Type;
import org.robinbird.main.oldmodel2.TypeCategory;

@Slf4j
@Getter
@NoArgsConstructor
class TypeRelationsBuilder {
	
	private Map<TypeRelation.Key, TypeRelation> typeRelationMap;
	
	public void update(@NonNull final AnalysisContext ac) {
		log.info("Starting to analyse relations among classes...");
		typeRelationMap = new HashMap<>();
		for (Type classType : ac.getClasses()) {
			log.debug("updating relations for " + classType.getName());
			for (Instance member : classType.getMembers()) {
				Type associated = member.getType();
				String cardinality = "1";
				associated = updateAssociateIfTypeIsCollection(associated, cardinality);
				if (!filterType(associated, ac)) {
					continue;
				}
				// now make a type relatedComponent
				TypeRelation.Key key = TypeRelation.createKey(classType, associated);
				TypeRelation tr = typeRelationMap.get(key);
				if (tr == null) {
					tr = TypeRelation.create(classType, associated, cardinality);
					typeRelationMap.put(key, tr);
				}
				tr.updateCardinality(associated, cardinality);
			}
		}
	}

	private Type updateAssociateIfTypeIsCollection(@NonNull Type associated, @NonNull String cardinality) {
		if (associated.getCategory() != TypeCategory.COLLECTION) {
			return  associated;
		}
		// when seeing a collection member variable, changing associated type to the last type of the collection
		// E.g. for Map<String, AnotherClass>, associated type is AnotherClass
		Type givenAssociated = associated;
		associated = null;//associated.getLastCompositionType();
		if (associated == null) {
			log.error("Collection has zero composition types. collection: {}", givenAssociated);
			return null;
		}
		cardinality = "*";
		return associated;
	}

	private boolean filterType(@NonNull final Type type, @NonNull final AnalysisContext ac) {
		// member function relatedComponent will be added later
		if (type.getCategory() == TypeCategory.FUNCTION) {
			return false;
		}
		// doesn't make relatedComponent for primitive types
		if (type.getCategory() == TypeCategory.PRIMITIVE) {
			return false;
		}
		if (ac.isExcluded(type.getName())) {
			log.debug("type {} is excluded type and skip to create relatedComponent.", type.getName());
			return false;
		}
		return true;
	}
}