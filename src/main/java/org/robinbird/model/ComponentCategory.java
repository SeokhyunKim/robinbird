package org.robinbird.model;

public enum ComponentCategory {
    // source code related categories
    PRIMITIVE_TYPE,
    PACKAGE,
    CLASS,
    INTERFACE,
    TEMPLATE_CLASS,
    COLLECTION,
    ARRAY,
    VARARGS,
    FUNCTION,
    // for clustering
    CLUSTERING_NODE;

    boolean isMemberVariableCategory() {
        return (this != PACKAGE) && (this != VARARGS) && (this != FUNCTION);
    }

    boolean isFunctionParameterCategory() {
        return (this != PACKAGE);
    }

}
