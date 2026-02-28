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

    public static String generateUsername(String prefix) {
        return prefix + "_" + UUID.randomUUID();
    }

    public static String generateProjectID() {
        return "project_id" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    public static String generateProjectName() {
        return "project_name" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    public static String generateBuildName() {
        return "buildName" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    public static String generateBuildStepName() {
        return "buildStepName" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    public static String generateBuildId(String projectId, String buildName) {
        return projectId + "_" + buildName;
    }
    public static String generateBuildId() {
        return "project_id" + RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }

    public static String generateVCSName(){
        return RandomStringUtils.randomAlphabetic(5);
    }


}
