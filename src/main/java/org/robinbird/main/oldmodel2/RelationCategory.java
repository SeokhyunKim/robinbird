package org.robinbird.main.oldmodel2;

public enum RelationCategory {
    // General relatedComponent type. Using this for testing and etc.
    RELATION,
    // class diagram relatedComponent types
    ASSOCIATION,
    COMPOSITION,
    INHERITANCE,
    REALIZATION,
    // for package and its members
    PACKAGE,
    PACKAGE_MEMBER,
    // class members
    MEMBER,
    MEMBER_FUNCTION,
    // When type is function, belows are for return types and function parameters
    FUNCTION_RETURN_TYPE,
    FUNCTION_PARAMETER,
    // When type is a collection, these are for the types used in the collection
    COLLECTION_TYPE

}
