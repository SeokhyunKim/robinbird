package org.robinbird.model;

public enum RelationCategory {
    // RelationTypes for class type
    PARENT_CLASS,
    IMPLEMENTING_INTERFACE,
    MEMBER_VARIABLE,
    MEMBER_FUNCTION,
    // package relationships
    PACKAGE_MEMBER,
    PARENT_PACKAGE,
    // a template type used in a collection or Optional
    TEMPLATE_TYPE,
    // a base type of an array
    ARRAY_BASE_TYPE,
    // a base type of a varargs
    VARARGS_BASE_TYPE,
    // for a function
    FUNCTION_PARAMETER,
    FUNCTION_RETURN_TYPE,
    // for clustering
    CLUSTER_MEMBER

}
