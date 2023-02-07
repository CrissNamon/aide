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
   * @param objects  Objects to check
   * @return true if null objects count is greater than maxCount
   */
  public static boolean isMoreThanNull(int maxCount, Object... objects) {
    return Arrays.stream(objects).filter(Objects::nonNull).count() > maxCount;
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

  /**
   * Checks if object classes equals.
   *
   * @param o1 {@link Object}
   * @param o2 {@link Object}
   * @return true if o1.getClass.equals(o2.getClass())
   */
  public static boolean equalsClass(Object o1, Object o2) {
    return o1.getClass().equals(o2.getClass());
  }

  /**
   * Checks if object is instance of class.
   *
   * @param o {@link Object}
   * @param c {@link Class}
   * @return true if o.getClass().equals(c)
   */
  public static boolean isInstanceOf(Object o, Class<?> c) {
    return o.getClass().equals(c);
  }
}
