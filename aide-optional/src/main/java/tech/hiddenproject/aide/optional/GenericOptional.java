package tech.hiddenproject.aide.optional;

import java.util.Optional;

/**
 * Represents generic optional.
 *
 * @author Danila Rassokhin
 */
public interface GenericOptional<V> {

  /**
   * @return Value of this optional
   */
  V get();

  /**
   * Converts this optional to {@link Optional}
   *
   * @return {@link Optional}
   */
  default Optional<V> generic() {
    return Optional.ofNullable(get());
  }

  /**
   * Checks if value is present.
   *
   * @return true - if value is present
   */
  default boolean isPresent() {
    return get() != null;
  }

}
