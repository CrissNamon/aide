package tech.hiddenproject.aide.optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Danila Rassokhin
 */
public class StringOptionalTest {

  @Test
  public void createOptionalInvalidValueTest() {
    Assertions.assertThrows(NullPointerException.class, () -> StringOptional.of(null));
  }

  @Test
  public void ifMethodsTest() {
    StringOptional stringOptional = StringOptional.of("String");

    Assertions.assertEquals("String", stringOptional.ifPresentOrElse("Hello"));
    Assertions.assertEquals("NewString", stringOptional.ifStartWithOrElse("NO_PREFIX", "NewString"));
  }

  @Test
  public void mapTest() {
    StringOptional stringOptional = StringOptional.of("String");

    String expected = "MyString";
    String actual = stringOptional.mapOnCondition(s -> true, v -> "My" + v).get();

    Assertions.assertEquals(expected, actual);
  }
}
