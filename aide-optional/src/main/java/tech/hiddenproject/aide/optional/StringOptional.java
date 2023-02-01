package tech.hiddenproject.aide.optional;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Implementation of {@link GenericOptional} for {@link String} type.
 *
 * @author Danila Rassokhin
 */
public class StringOptional implements GenericOptional<String> {

  private final String value;

  private StringOptional(String value) {
    this.value = value;
  }

  public static StringOptional of(String value) {
    Objects.requireNonNull(value);
    return new StringOptional(value);
  }

  @Override
  public String get() {
    return value;
  }

  /**
   * @param defaultValue Default value
   * @return Returns value if value is not {@link String#isEmpty()} or default value otherwise.
   */
  public String ifPresentOrElse(String defaultValue) {
    if (value.isEmpty()) {
      return defaultValue;
    }
    return value;
  }

  /**
   * @param defaultValue Default value
   * @return Returns value if value is {@link String#startsWith(String)} or default value otherwise.
   */
  public String ifStartWithOrElse(String prefix, String defaultValue) {
    if (value.startsWith(prefix)) {
      return value;
    }
    return defaultValue;
  }

  /**
   * Changes value if condition is success.
   *
   * @param condition Condition to check
   * @param mapper Mapper {@link Function}
   * @return New {@link StringOptional}
   */
  public StringOptional mapOnCondition(Predicate<String> condition, Function<String, String> mapper) {
    if (condition.test(value)) {
      return of(mapper.apply(value));
    }
    return this;
  }
}
