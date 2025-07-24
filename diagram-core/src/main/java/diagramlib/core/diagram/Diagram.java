package diagramlib.core.diagram;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Diagram {
    String value() default "";
}