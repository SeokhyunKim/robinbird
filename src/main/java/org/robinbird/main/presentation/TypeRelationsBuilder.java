package org.robinbird.main.presentation;

import java.util.HashMap;
import java.util.Map;

import org.robinbird.main.newmodel.AnalysisContext;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class TypeRelationsBuilder {
	
	private Map<TypeRelation.Key, TypeRelation> typeRelationsMap;
	
	public void update(@NonNull final AnalysisContext ac) {
		log.info("Starting to analyse relations among classes...");
		typeRelationsMap = new HashMap<>();
		
		
	}
}