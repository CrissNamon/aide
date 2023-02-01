package tech.hiddenproject.hic.util;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Danila Rassokhin
 */
public class ObjectUtils {

  public static boolean isMoreThanNull(int maxCount, Object... objects) {
    return Arrays.stream(objects)
        .filter(Objects::nonNull)
        .count() > maxCount;
  }

  public static boolean isMoreThanNull(Object... objects) {
    return isMoreThanNull(1, objects);
  }

}
