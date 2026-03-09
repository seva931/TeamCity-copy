package common.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class TestDataGenerator {
    public static String generateUsername() {
        return "user_" + UUID.randomUUID();
    }

    public static String generatePassword() {
        return "pass_" + UUID.randomUUID();
    }

    public static String generateBuildName() {
        return "buildName" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    public static String generateBuildStepName() {
        return "buildStepName" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    public static String generateBuildId() {
        return "project_id" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    public static String generateVCSName() {
        return RandomStringUtils.randomAlphabetic(5);
    }


}
