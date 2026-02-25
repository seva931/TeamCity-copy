package api.requests.steps;

import configs.Config;
import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class VCSSteps {
    public static AllVcsRootsResponse getAllRoots() {
        return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.VCS_ROOTS, ResponseSpecs.ok()).get().extract().as(AllVcsRootsResponse.class);
    }
    public static AllVcsRootsResponse getAllRoots(CreateUserResponse user) {
        return new CrudRequester(RequestSpecs.authAsUser(user), Endpoint.VCS_ROOTS, ResponseSpecs.ok()).get().extract().as(AllVcsRootsResponse.class);
    }

    public static AddNewRootResponse createNewRoot() {
        AddNewRootRequest request = AddNewRootRequest.createRoot();
        return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.VCS_ROOTS, ResponseSpecs.ok())
                .post(request).extract().as(AddNewRootResponse.class);
    }

    public static AddNewRootResponse createNewRoot(CreateUserResponse user) {
        AddNewRootRequest request = AddNewRootRequest.createRoot();
        return new CrudRequester(RequestSpecs.authAsUser(user), Endpoint.VCS_ROOTS, ResponseSpecs.ok())
                .post(request).extract().as(AddNewRootResponse.class);
    }

    public static AddNewRootResponse createNewRoot(String name) {
        AddNewRootRequest request = AddNewRootRequest.createRoot();
        request.setName(name);
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

        return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.VCS_ROOTS, ResponseSpecs.ok())
                .post(request).extract().as(AddNewRootResponse.class);
    }

    public static ErrorResponse createNewRootWithError(String name) {
        AddNewRootRequest request = new AddNewRootRequest();
        request.setName(name);
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

        return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.VCS_ROOTS, ResponseSpecs.InternalServerError())
                .post(request).extract().as(ErrorResponse.class);
    }

    public static VcsRoot getRootByName(String name) {
        AllVcsRootsResponse allRoots = getAllRoots();
        return allRoots.getVcsRoot().stream()
                .filter(root -> root.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("VCS root not found: " + name));
    }

}