package org.robinbird.model;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import lombok.NonNull;
import org.robinbird.exception.RobinbirdException;

public enum AccessLevel {
    PUBLIC, PRIVATE, PROTECTED;

    private static Set<String> accessLevelStrings = ImmutableSet.of(PUBLIC.name(), PRIVATE.name(), PROTECTED.name(),
                                                                    "public", "private", "protected");

    public static AccessLevel fromString(@NonNull final String accessLevelStr) {
        try {
            return AccessLevel.valueOf(accessLevelStr);
        } catch (final IllegalArgumentException e) {
            if ("public".equals(accessLevelStr)) {
                return PUBLIC;
            } else if ("private".equals(accessLevelStr)) {
                return PRIVATE;
            } else if ("protected".equals(accessLevelStr)) {
                return PROTECTED;
            }
            throw new RobinbirdException("Failed to parse access modifier string, " + accessLevelStr);
        }
    }

    public static boolean isAccessLevelString(@NonNull final String accessLevelStr) {
        return accessLevelStrings.contains(accessLevelStr);
    }
}
