package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QueryParamData {
    LOCATOR("locator"),
    FIELDS("fields");
    private final String name;
}
