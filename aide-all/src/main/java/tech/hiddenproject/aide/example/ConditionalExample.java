package tech.hiddenproject.aide.example;

import tech.hiddenproject.aide.optional.IfTrueConditional;
import tech.hiddenproject.aide.optional.SwitchConditional;
import tech.hiddenproject.aide.optional.WhenConditional;
import tech.hiddenproject.aide.reflection.matcher.ArgumentMatcher;

import java.util.Objects;

/**
 * @author Danila Rassokhin
 */
public class ConditionalExample {

  public ConditionalExample() {

    Object result = IfTrueConditional.create().ifTrue(false).then(this::getObject)
                                              .ifTrue(null, Objects::nonNull)
                                              .then(() -> new Object()).ifTrue(true)
                                              .then(() -> "Hi")
                                              .orElseThrows(() -> new RuntimeException());

    WhenConditional.create().when(true).then(() -> System.out.println("1 when is true")).when(true)
                            .then(() -> System.out.println("2 when is true"))
                            .when(new Object(), Objects::nonNull)
                            .then(() -> System.out.println("Object is not null"))
                            .orFinally(() -> System.out.println("Will be executed anyway"));

    Status status = Status.BAD_REQUEST;

    String message = SwitchConditional.<Status, String>on(status)
        .caseOn(Status.BAD_REQUEST::equals).thenGet("Error: Bad request")
        .caseOn(Status.INTERNAL_ERROR::equals).thenGet("Error: Internal error")
        .orElse("");

    assert message.equals("Error: Bad request");

    SwitchConditional.on(status)
        .caseOn(Status.BAD_REQUEST::equals, false).thenDo(this::action)
        .caseOn(Status.INTERNAL_ERROR::equals, false).thenDo(this::action)
        .orElseDo(() -> System.out.println("No action"));

  }

  enum Status {
    OK, BAD_REQUEST, INTERNAL_ERROR
  }

  private Object getObject() {
    return new Object();
  }

  private void action() {}
}
