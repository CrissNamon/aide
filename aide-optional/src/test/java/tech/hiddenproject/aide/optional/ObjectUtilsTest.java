package tech.hiddenproject.aide.optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Danila Rassokhin
 */
public class ObjectUtilsTest {

  @Test
  public void isMoreThanNullTest() {
    Object[] args = new Object[]{new Object(), null, new Object(), null};

    Assertions.assertTrue(ObjectUtils.isMoreThanNull(args));
    Assertions.assertFalse(ObjectUtils.isMoreThanNull(2, args));
  }

}
