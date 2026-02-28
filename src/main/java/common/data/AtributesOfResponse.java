package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AtributesOfResponse {
    NO_BUILD_TYPE_ERROR("No build type nor template is found by id '%s'."),
    YOU_DONT_HAVE_ENOUGH_PERMISSIONS_ERROR("You do not have enough permissions to edit project with id: %s."),
    BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR("Build configuration with name \"%s\" already exists in project: \"%s\"."),
    NO_PROJECT_FOUND_BY_ID_ERROR("No project found by locator 'count:1,id:%s'. Project cannot be found by external id '%s'."),
    NO_AGENT_CAN_BE_FOUND_BY_ID("No agent can be found by id '%s'."),
    YOU_DO_NOT_HAVE_ENABLE_DISABLE_AGENTS_ASSOCIATED_WITH_PROJECT_PERMISSION_FOR_POOL_DEFAULT("You do not have \"Enable / disable agents associated with project\" permission for pool 'Default'"),
    BUILD_STEP_WITH_THE_SPECIFIED_ID_ALREADY_EXIST("Build step with the specified id already exists: %s");

    private final String message;

    public String getFormatedText(Object... args) {
        return String.format(message, args);
    }
}
