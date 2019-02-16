package org.robinbird.main.newmodel;

public enum AccessModifier {
    PUBLIC("public"), PRIVATE("private"), PROTECTED("protected");

    private String name;

    AccessModifier(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public static AccessModifier fromName(String name) throws IllegalArgumentException {
        return AccessModifier.valueOf(name.toUpperCase());
    }
}