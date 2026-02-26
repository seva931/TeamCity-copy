package jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithAgent {
    String[] configKeys() default {
            "teamcity.agent.1.name",
            "teamcity.agent.2.name"
    };
}
