package tech.hiddenproject.aide;

import java.util.Objects;
import tech.hiddenproject.aide.optional.IfTrueConditional;
import tech.hiddenproject.aide.optional.WhenConditional;

/**
 * @author Danila Rassokhin
 */
public class ConditionalExample {

  public ConditionalExample() {

    Object result = IfTrueConditional.create()
        .ifTrue(false).then(this::getObject)
        .ifTrue(null, Objects::nonNull).then(() -> new Object())
        .ifTrue(true).then(() -> "Hi")
        .orElseThrows(() -> new RuntimeException());

    WhenConditional.create()
        .when(true).then(() -> System.out.println("1 when is true"))
        .when(true).then(() -> System.out.println("2 when is true"))
        .when(new Object(), Objects::nonNull).then(() -> System.out.println("Object is not null"))
        .orFinally(() -> System.out.println("Will be executed anyway"));

  }

  private Object getObject() {
    return new Object();
  }
}
