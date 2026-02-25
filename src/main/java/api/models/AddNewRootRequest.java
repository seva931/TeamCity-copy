package api.models;

import configs.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.generators.TestDataGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddNewRootRequest extends BaseModel{
    private String name;
    private String vcsName;
    private VcsProject project;
    private VcsProperties properties;

    public static AddNewRootRequest createRoot () {
        AddNewRootRequest request = new AddNewRootRequest();
        request.setName(TestDataGenerator.generateVCSName());
        request.setVcsName(Config.getProperty("vcsRootName"));
        request.setProject(new VcsProject() {{
            setId(Config.getProperty("vcsId"));
        }});
        request.setProperties(new VcsProperties() {{
            setProperty(List.of(
                    new VcsProperty() {{
                        setName(Config.getProperty("vcsPropertyName"));
                        setValue(Config.getProperty("vcsPropertyValue"));
                    }}

            ));
        }});
        return request;
    }
}

