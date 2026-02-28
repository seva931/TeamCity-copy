package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgentsPageTexts {
    UNAUTHORIZE_BUTTON("Unauthorize..."),
    AUTHORIZE_BUTTON("Authorize..."),
    AUTHORIZED_STATUS("Authorized"),
    UNAUTHORIZED_STATUS("Unauthorized");

    private final String text;
}
