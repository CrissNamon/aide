package tech.hiddenproject.aide.reflection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.hiddenproject.aide.reflection.util.ReflectionUtil;

import java.lang.reflect.Method;

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

  @Test
  public void getArgTypesTest() {
    Object[] args = new Object[]{"Hi", 1};
    Class<?>[] expected = new Class[]{String.class, Integer.class};

    Class<?>[] actual = ReflectionUtil.getVarArgTypes(args);

    Assertions.assertArrayEquals(expected, actual);
  }

  public static class TestClass {

    public Object get() {
      return new Object();
    }

    public void get(Object arg0, Object arg1) {
    }
  }
}
