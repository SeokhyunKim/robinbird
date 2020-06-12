package org.robinbird.util;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by seokhyun on 5/26/17.
 */
public class Msgs {

	public enum Key {
		ROOT_SOURCE_PATH_NOT_PROVIDED,
		IOEXCEPTION_WHILE_READING_SOURCE_CODES,
		ALREADY_EXISTING_TYPE_NAME,
		CURRENT_CLASS_IS_NULL_WHILE_WALKING_THROUGH_PARSE_TREE,
		FAILED_TO_FIND_MEMBER_TYPE,
		PRESENTATION_OPTION_IS_NOT_VALID,
		NULL_POINTER_ENCOUNTERED,
		WRONG_REGEXP_FOR_TERMINAL,
		WRONG_REGEXP_FOR_EXCLUSION,
		REGEXP_FOR_TERMINAL_IS_NOT_GIVEN,
		REGEXP_FOR_EXCLUSION_IS_NOT_GIVEN,
		LIST_FOR_PACKAGE_NAME_IS_EMPTY,
		CANNOT_CREATE_GRAPH_NODE_FROM_TYPE,
		FAILED_TO_GET_ASSOCIATED_TYPE_FROM_COLLECTION,
		FAILED_TO_FIND_AGGCLUSTER_NODE,
		SCORE_FOR_CLUSTERING_IS_NOT_GIVEN,
		CLUSTERING_TYPE_IS_NOT_GIVEN,
		INVALID_COMPONENT_CATEGORY,
		JSON_PROCESSING_ISSUE,
		CURRENT_COMPONENT_IS_NOT_CLASS,
		FOUND_COMPONENT_OF_DIFFERENT_TYPE,
		WRONG_COMPONENT_CATEGORY,
		TRIED_TO_ADD_WRONG_COMPONENT,
		TRIED_TO_CREATE_NEW_PERSISTED_RELATION_WITH_ALREADY_STORED,
		MULTIPLE_INDEPENDENT_COMPONENTS_OF_SAME_NAME,
		COMPONENT_ENTITY_WITH_NULL_CATEGORY,
		INTERNAL_ERROR
	}

	private static final Map<Key, String> msgMap;
	static  {
		msgMap = new ImmutableMap.Builder<Key, String>()
			.put(Key.ROOT_SOURCE_PATH_NOT_PROVIDED, "Root source path should be provided.")
			.put(Key.IOEXCEPTION_WHILE_READING_SOURCE_CODES, "(Unchecked)IOException is thrown while reading source codes.")
			.put(Key.ALREADY_EXISTING_TYPE_NAME, "Already existing type name is given: %s.")
			.put(Key.CURRENT_CLASS_IS_NULL_WHILE_WALKING_THROUGH_PARSE_TREE, "Current class is null while parsing class.")
			.put(Key.FAILED_TO_FIND_MEMBER_TYPE, "Failed to find member type for %s.")
			.put(Key.PRESENTATION_OPTION_IS_NOT_VALID, "Invalid presentation option: %s.")
			.put(Key.NULL_POINTER_ENCOUNTERED, "Null pointer encountered in %s.")
			.put(Key.WRONG_REGEXP_FOR_TERMINAL, "Wrong regular expression pattern is given for terminal: %s.")
			.put(Key.WRONG_REGEXP_FOR_EXCLUSION, "Wrong regular expression pattern is given for exclusion: %s.")
			.put(Key.REGEXP_FOR_TERMINAL_IS_NOT_GIVEN, "Regular expression pattern for a terminal is not given.")
			.put(Key.REGEXP_FOR_EXCLUSION_IS_NOT_GIVEN, "Regular expression pattern for exclusion is not given.")
			.put(Key.LIST_FOR_PACKAGE_NAME_IS_EMPTY, "List for defining package name is empty.")
			.put(Key.CANNOT_CREATE_GRAPH_NODE_FROM_TYPE, "Cannot create graph node from Type %s.")
			.put(Key.FAILED_TO_GET_ASSOCIATED_TYPE_FROM_COLLECTION, "Failed to get associated type from Container %s.")
			.put(Key.FAILED_TO_FIND_AGGCLUSTER_NODE, "Failed to find agglomerative clustering node %s.")
			.put(Key.SCORE_FOR_CLUSTERING_IS_NOT_GIVEN, "Score for controling clustering method was not given.")
			.put(Key.CLUSTERING_TYPE_IS_NOT_GIVEN, "Clustering type was not given.")
			.put(Key.INVALID_COMPONENT_CATEGORY, "Invalid ComponentCategory %s is used.")
			.put(Key.JSON_PROCESSING_ISSUE, "Exception caught while processing json serialization/deserialization for %s.")
			.put(Key.INTERNAL_ERROR, "Unexpected internal error occurred.")
			.put(Key.CURRENT_COMPONENT_IS_NOT_CLASS, "In AnalysisContext, current component is not Class. Check this: %s.")
			.put(Key.FOUND_COMPONENT_OF_DIFFERENT_TYPE, "While trying to register a component, found another component " +
														"with different type. Name: %s, Category: %s.")
			.put(Key.WRONG_COMPONENT_CATEGORY, "Wrong ComponentCategory is used. componentCategory: %s.")
			.put(Key.TRIED_TO_ADD_WRONG_COMPONENT, "Tried to add wrong component. Component: %s.")
			.put(Key.TRIED_TO_CREATE_NEW_PERSISTED_RELATION_WITH_ALREADY_STORED,
				 "Tried to create a new relation with already persisted one. Relation: %s.")
			.put(Key.MULTIPLE_INDEPENDENT_COMPONENTS_OF_SAME_NAME, "Found multiple independent components of same name.")
			.put(Key.COMPONENT_ENTITY_WITH_NULL_CATEGORY, "ComponentEntity with null category: %s.")
			.build();
	}

	public static String get(Key k) {
		return msgMap.get(k);
	}

	public static String getAndAddMessage(Key k, String msg) {
		return msgMap.get(k) + ": " + msg;
	}

	public static String get(Key k, String msg) {
		return String.format(msgMap.get(k), msg);
	}

	public static String get(Key k, String msg1, String msg2) {
		return String.format(msgMap.get(k), msg1, msg2);
	}

	public static String get(Key k, Exception e) {
		return msgMap.get(k) + getNewLineStackTrace(e);
	}

	public static String get(Key k, Exception e, String msg) {
		return get(k, msg) + getNewLineStackTrace(e);
	}

	private static String getNewLineStackTrace(Exception e) {
		return "\n" + Throwables.getStackTraceAsString(e);
	}
}
