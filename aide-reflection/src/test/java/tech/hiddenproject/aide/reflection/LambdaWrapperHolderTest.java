package tech.hiddenproject.aide.reflection;

import static org.mockito.ArgumentMatchers.anyString;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.hiddenproject.aide.reflection.annotation.ExactInvoker;
import tech.hiddenproject.aide.reflection.annotation.Invoker;
import tech.hiddenproject.aide.reflection.exception.ReflectionException;

/**
 * @author Danila Rassokhin
 */
public class LambdaWrapperHolderTest {

  private static final LambdaWrapperHolder holder = LambdaWrapperHolder.INSTANCE;

  @Test
  public void addInterfaceTest() {
    Assertions.assertDoesNotThrow(() -> holder.add(TestWrapper.class));
  }

  @Test
  public void addInvalidMethodTest() {
    Method method = ReflectionUtil.getMethod(InvalidTestWrapper.class, "invalid");

    Assertions.assertThrows(ReflectionException.class, () -> holder.add(method));
  }

  @Test
  public void wrapMethodInvokerTest() {
    Method realMethod = ReflectionUtil.getMethod(TestClass.class, "callAction");
    Method wrapperMethod = ReflectionUtil.getMethod(TestWrapper.class, "action", Object.class);
    TestClass caller = Mockito.mock(TestClass.class);

    Assertions.assertDoesNotThrow(() -> holder.add(wrapperMethod));

    TestWrapper wrapper = holder.wrap(realMethod);
    wrapper.action(caller);

    Mockito.verify(caller).callAction();
    Mockito.verifyNoMoreInteractions(caller);
  }

  @Test
  public void wrapMethodExactInvokerTest() {
    Method realMethod = ReflectionUtil.getMethod(TestClass.class, "callConvert", String.class);
    Method wrapperMethod = ReflectionUtil.getMethod(TestWrapper.class, "convert", Object.class,
                                                    String.class);
    TestClass caller = Mockito.mock(TestClass.class);
    Mockito.when(caller.callConvert(anyString()))
            .thenReturn(2);

    Assertions.assertDoesNotThrow(() -> holder.add(wrapperMethod));

    TestWrapper wrapper = holder.wrapExact(realMethod);
    int expected = wrapper.convert(caller, "Hi");

    Mockito.verify(caller).callConvert("Hi");
    Mockito.verifyNoMoreInteractions(caller);

    Assertions.assertEquals(2, expected);
  }

  @Test
  public void wrapMethodNoWrapperTest() {
    Method realMethod = ReflectionUtil.getMethod(TestClass.class, "callNoWrapper");

    Assertions.assertThrows(ReflectionException.class, () -> holder.wrapExact(realMethod));
  }

  public static class TestClass {

    public void callAction() {

    }

    public int callConvert(String text) {
      return text.length();
    }

    public int callNoWrapper() {
     return 0;
    }

  }

  public interface TestWrapper {
    @Invoker
    void action(Object caller);

    @ExactInvoker
    int convert(Object caller, String text);
  }

  public interface InvalidTestWrapper {
    void invalid();
  }
}
