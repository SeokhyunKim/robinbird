package org.robinbird.model;

public enum RelationCategory {
    OWNER_COMPONENT,
    // RelationTypes for class type
    PARENT_CLASS,
    IMPLEMENTING_INTERFACE,
    MEMBER_VARIABLE,
    MEMBER_FUNCTION,
    // package relationships
    PACKAGE_MEMBER,
    PARENT_PACKAGE,
    // a template type used by a class or interface
    TEMPLATE_TYPE,
    // a base type of a container and an array
    CONTAINER_BASE_TYPE,
    ARRAY_BASE_TYPE,
    // a base type of a varargs
    VARARGS_BASE_TYPE,
    // for a function
    FUNCTION_PARAMETER,
    FUNCTION_RETURN_TYPE,
    // for clustering
    CLUSTER_MEMBER

}
