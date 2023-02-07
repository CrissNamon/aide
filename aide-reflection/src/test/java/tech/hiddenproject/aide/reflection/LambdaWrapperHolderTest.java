package tech.hiddenproject.aide.reflection;

import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.hiddenproject.aide.reflection.annotation.ExactInvoker;
import tech.hiddenproject.aide.reflection.annotation.Invoker;
import tech.hiddenproject.aide.reflection.exception.ReflectionException;
import tech.hiddenproject.aide.reflection.filter.ExecutableFilter;
import tech.hiddenproject.aide.reflection.matcher.ArgumentMatcher;
import tech.hiddenproject.aide.reflection.matcher.ArgumentMatcherHolder;
import tech.hiddenproject.aide.reflection.signature.MatcherSignature;
import tech.hiddenproject.aide.reflection.signature.MethodSignature;
import tech.hiddenproject.aide.reflection.util.ReflectionUtil;

import java.lang.reflect.Method;

/**
 * @author Danila Rassokhin
 */
public class LambdaWrapperHolderTest {

  private static final LambdaWrapperHolder holder = LambdaWrapperHolder.EMPTY;

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
    WrapperHolder<TestWrapper> wrapper = holder.wrap(realMethod, TestWrapper.class);
    wrapper.getWrapper().action(caller);

    Mockito.verify(caller).callAction();
    Mockito.verifyNoMoreInteractions(caller);
  }

  @Test
  public void wrapMethodExactInvokerTest() {
    Method realMethod = ReflectionUtil.getMethod(TestClass.class, "callConvert", String.class);
    Method wrapperMethod = ReflectionUtil.getMethod(
        TestWrapper.class, "convert", Object.class, String.class);
    TestClass caller = Mockito.mock(TestClass.class);
    Mockito.when(caller.callConvert(anyString())).thenReturn(2);

    Assertions.assertDoesNotThrow(() -> holder.add(wrapperMethod));

    WrapperHolder<TestWrapper> wrapper = holder.wrapExact(realMethod, TestWrapper.class);
    int expected = wrapper.getWrapper().convert(caller, "Hi");

    Mockito.verify(caller).callConvert("Hi");
    Mockito.verifyNoMoreInteractions(caller);

    Assertions.assertEquals(2, expected);
  }

  @Test
  public void wrapMethodNoWrapperTest() {
    Method realMethod = ReflectionUtil.getMethod(TestClass.class, "callNoWrapper");

    Assertions.assertThrows(
        RuntimeException.class, () -> holder.wrapExact(realMethod, TestWrapper.class));
  }

  @Test
  public void wrapSafeTest() {
    Method realMethod = ReflectionUtil.getMethod(TestClass.class, "callAction");
    Method wrapperMethod = ReflectionUtil.getMethod(TestWrapper.class, "action", Object.class);
    TestClass caller = Mockito.mock(TestClass.class);
    Mockito.doNothing().when(caller).callAction();

    Assertions.assertDoesNotThrow(() -> holder.add(wrapperMethod));
    MethodSignature methodSignature = MethodSignature.fromWrapper(wrapperMethod);
    MatcherSignature<TestWrapper> matcherSignature = new MatcherSignature<>(
        TestWrapper.class, methodSignature);
    Assertions.assertDoesNotThrow(() -> ArgumentMatcherHolder.INSTANCE.addMatcher(
        matcherSignature,
        (holder1, original, args) -> ArgumentMatcher.voidable(
            () -> holder1.getWrapper()
                         .action(args[0]))
    ));

    MethodHolder<TestWrapper, TestClass, Integer> wrapper = holder.wrapSafe(
        realMethod, TestWrapper.class);
    wrapper.invoke(caller);

    Mockito.verify(caller).callAction();
    Mockito.verifyNoMoreInteractions(caller);
  }

  @Test
  public void publicFilterTest() {
    holder.setFilter(ExecutableFilter.PUBLIC_ONLY);
    Method privateMethod = ReflectionUtil.getMethod(TestClass.class, "privateMethod");
    Assertions.assertThrows(ReflectionException.class, () -> holder.wrapSafe(privateMethod));
  }

  @Test
  public void anyFilterTest() {
    LambdaWrapperHolder lambdaWrapperHolder = LambdaWrapperHolder.DEFAULT;
    lambdaWrapperHolder.setFilter(ExecutableFilter.ANY);
    Method privateMethod = ReflectionUtil.getMethod(TestClass.class, "privateMethod");
    Assertions.assertThrows(RuntimeException.class, () -> lambdaWrapperHolder.wrapSafe(privateMethod));
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

  public static class TestClass {

    public void callAction() {

    }

    public int callConvert(String text) {
      return text.length();
    }

    public int callNoWrapper() {
      return 0;
    }

    private void privateMethod() {}

  }
}
