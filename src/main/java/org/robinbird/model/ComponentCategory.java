package org.robinbird.model;

import com.google.common.collect.Sets;
import java.util.Set;

public enum ComponentCategory {
    // source code related categories
    PRIMITIVE_TYPE,
    PACKAGE,
    CLASS,
    INTERFACE,
    TEMPLATE_CLASS,
    CONTAINER,
    ARRAY,
    VARARGS,
    FUNCTION,
    // for clustering
    CLUSTERING_NODE;

    private static Set<ComponentCategory> classCategories = Sets.newHashSet(CLASS, INTERFACE, TEMPLATE_CLASS);
    public static Set<ComponentCategory> classCategories() {
        return classCategories;
    }

    boolean isMemberVariableCategory() {
        return (this != PACKAGE) && (this != VARARGS) && (this != FUNCTION);
    }

    boolean isFunctionParameterCategory() {
        return (this != PACKAGE);
    }

    public boolean isClassCategory() {
        return (this == CLASS) || (this == TEMPLATE_CLASS) || (this == INTERFACE);
    }

    public boolean isCompositeComponentCategory() {
        return (this == CONTAINER) || (this == ARRAY) || (this == VARARGS);
    }


}
