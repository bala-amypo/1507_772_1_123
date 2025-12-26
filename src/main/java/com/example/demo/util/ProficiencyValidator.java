package com.example.demo.util;

import java.util.Set;

public final class ProficiencyValidator {

    private static final Set<String> ALLOWED_LEVELS = Set.of(
            "Beginner",
            "Intermediate",
            "Advanced",
            "Expert"
    );

    private ProficiencyValidator() {
        // utility class – no instances
    }

    public static boolean isValid(String proficiencyLevel) {
        return proficiencyLevel != null && ALLOWED_LEVELS.contains(proficiencyLevel);
    }
}
