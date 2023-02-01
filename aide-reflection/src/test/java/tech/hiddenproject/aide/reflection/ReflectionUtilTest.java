package tech.hiddenproject.aide;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.hiddenproject.aide.reflection.ReflectionUtil;

/**
 * @author Danila Rassokhin
 */
public class ReflectionUtilTest {

  @Test
  public void getMethodTest() throws NoSuchMethodException {
    Method expected = TestClass.class.getDeclaredMethod("get");

    Method actual = ReflectionUtil.getMethod(TestClass.class, "get");

    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void getVarArgsMethodTest() throws NoSuchMethodException {
    Method expected = TestClass.class.getDeclaredMethod("get", Object.class, Object.class);

    Method actual = ReflectionUtil.getMethod(TestClass.class, "get", Object.class, Object.class);

    Assertions.assertEquals(expected, actual);
  }

  public static class TestClass {

    public Object get() {
      return new Object();
    }

    public void get(Object arg0, Object arg1) {

    }

  }

}
