package api.sample;

import api.BaseTest;
import api.models.CreateBuildTypeResponse;
import api.models.CreateUserResponse;
import api.models.ProjectResponse;
import jupiter.annotation.Build;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.ApiTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
@ApiTest
@WithBuild
public class SampleTest extends BaseTest {

    @Test
    public void buildSampleTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build
    ){

        System.out.println("Project Sample Test");
        System.out.println(project);

        System.out.println("User Sample Test");
        System.out.println(user);

        System.out.println("Build Sample Test");
        System.out.println(build);
    }
}
