package org.robinbird.main.oldmodel2;

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