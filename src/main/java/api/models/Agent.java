package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Agent {
    private long id;
    private String name;
    private long typeId;
    private String href;
    private String webUrl;
    private boolean connected;
    private boolean enabled;
    private boolean authorized;
    private String ip;
    private AgentEnvironment environment;
    private AgentPool pool;
}
