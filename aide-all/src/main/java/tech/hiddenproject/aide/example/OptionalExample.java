package tech.hiddenproject.aide.example;

import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.aide.optional.StringOptional;

/**
 * @author Danila Rassokhin
 */
public class OptionalExample {

  public OptionalExample() {

    BooleanOptional.of(true).ifTrueThen(value -> System.out.println("Value is true!"));

    BooleanOptional.of(false).ifTrueThen(value -> System.out.println("Value is false!"));

    String value = StringOptional.of("").ifPresentOrElse("value");
    assert value.equals("value");

    value = StringOptional.of("value").ifStartWithOrElse("va", "newValue");
    assert value.equals("value");

    value = StringOptional.of("value").mapOnCondition(v -> v.equals("value"), v -> "my_" + v).get();
    assert value.equals("my_value");

  }
}
