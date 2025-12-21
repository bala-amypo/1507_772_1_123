package com.example.demo.util;

import java.util.Set;

public class ProficiencyValidator {

    private static final Set<String> VALID_LEVELS =
            Set.of("Beginner", "Intermediate", "Advanced", "Expert");

    public static boolean isValid(String level) {
        return VALID_LEVELS.contains(level);
    }
}
