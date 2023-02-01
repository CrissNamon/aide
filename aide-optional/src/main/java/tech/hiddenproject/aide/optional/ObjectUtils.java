package tech.hiddenproject.aide.optional;

import java.util.Arrays;
import java.util.Objects;

/**
 * Helper to work with objects.
 *
 * @author Danila Rassokhin
 */
public class ObjectUtils {

  /**
   * Checks if null objects count is greater than maxCount.
   *
   * @param maxCount Max count of null objects
   * @param objects Objects to check
   * @return true if null objects count is greater than maxCount
   */
  public static boolean isMoreThanNull(int maxCount, Object... objects) {
    return Arrays.stream(objects)
        .filter(Objects::nonNull)
        .count() > maxCount;
  }

  /**
   * Checks if null objects count is greater than 1.
   *
   * @param objects Objects to check
   * @return true if null objects count is greater than 1
   */
  public static boolean isMoreThanNull(Object... objects) {
    return isMoreThanNull(1, objects);
  }

}
