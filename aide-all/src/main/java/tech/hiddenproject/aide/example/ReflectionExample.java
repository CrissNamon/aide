package tech.hiddenproject.aide.example;

import tech.hiddenproject.aide.reflection.LambdaWrapper;
import tech.hiddenproject.aide.reflection.LambdaWrapperHolder;
import tech.hiddenproject.aide.reflection.MethodHolder;
import tech.hiddenproject.aide.reflection.WrapperHolder;
import tech.hiddenproject.aide.reflection.annotation.ExactInvoker;
import tech.hiddenproject.aide.reflection.annotation.Invoker;
import tech.hiddenproject.aide.reflection.matcher.ArgumentMatcherHolder;
import tech.hiddenproject.aide.reflection.signature.LambdaMetadata;
import tech.hiddenproject.aide.reflection.signature.MatcherSignature;
import tech.hiddenproject.aide.reflection.signature.MethodSignature;
import tech.hiddenproject.aide.reflection.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Danila Rassokhin
 */
public class ReflectionExample {

  public ReflectionExample() {
    // Create holder with default LambdaWrapper initialization
    LambdaWrapperHolder lambdaWrapperHolder = LambdaWrapperHolder.DEFAULT;
    // Load all annotated methods from TestInterface
    lambdaWrapperHolder.add(TestInterface.class);
    // Create caller
    TestClass caller = new TestClass();

    // Find getter
    Method getMethod = ReflectionUtil.getMethod(TestClass.class, "get");
    // Wrap getter
    WrapperHolder<LambdaWrapper> getterHolder = lambdaWrapperHolder.wrap(getMethod);
    LambdaWrapper getter = getterHolder.getWrapper();
    // Invoke getter
    Long id = getter.get(caller);
    System.out.println("Getter result: " + id);

    // Get setter
    Method setMethod = ReflectionUtil.getMethod(TestClass.class, "set", String.class);
    // Wrap setter
    WrapperHolder<LambdaWrapper> setterHolder = lambdaWrapperHolder.wrap(setMethod);
    LambdaWrapper setter = setterHolder.getWrapper();
    // Invoke setter
    setter.set(caller, "Hello");

    // Find apply method
    Method functionMethod = ReflectionUtil.getMethod(TestClass.class, "apply", String.class);
    // Wrap apply method
    WrapperHolder<LambdaWrapper> functionHolder = lambdaWrapperHolder.wrap(functionMethod);
    LambdaWrapper function = functionHolder.getWrapper();
    // Invoke apply method and get result
    int res = function.apply(caller, "Hi");
    System.out.println("Function result: " + res);

    // Find special method
    Method specialMethod = ReflectionUtil.getMethod(TestClass.class, "exact", int.class);
    // Wrap special method with exact same wrapper from TestInterface
    WrapperHolder<TestInterface> specialHolder = lambdaWrapperHolder.wrapExact(
        specialMethod, TestInterface.class);
    TestInterface testInterface = specialHolder.getWrapper();
    // Invoke special method and get result
    String special = testInterface.exact(caller, 1);
    System.out.println("Special result: " + special);

    // Get method with var args
    Method varArgsMethod = ReflectionUtil.getMethod(TestClass.class, "varargs", Object[].class);
    // Wrap method with var args
    WrapperHolder<LambdaWrapper> varArgsHolder = lambdaWrapperHolder.wrap(varArgsMethod);
    LambdaWrapper varargs = varArgsHolder.getWrapper();
    // Invoke method and get result
    Class<?>[] varArgsTypes = varargs.apply(caller, new Object[]{"Hello", 1});
    System.out.println("Var args result: " + Arrays.toString(varArgsTypes));

    // Find constructor of TestClass
    Constructor<TestClass> constructorMethod = ReflectionUtil.getConstructor(TestClass.class);
    // Wrap constructor
    MethodHolder<LambdaWrapper, Void, TestClass> constructor = lambdaWrapperHolder.wrapSafe(
        constructorMethod);
    // Call constructor
    caller = constructor.invokeStatic();

    // Wrap special method
    MethodHolder<LambdaWrapper, TestClass, String> methodHolder = lambdaWrapperHolder.wrapSafe(
        specialMethod);
    // Invoke special method
    String r = methodHolder.invoke(caller, 1);

    // Wrap setter
    MethodHolder<LambdaWrapper, TestClass, Void> setHolder = lambdaWrapperHolder.wrapSafe(
        setMethod);
    // Invoke setter
    setHolder.invoke(caller, "hello");

    // Find static method
    Method staticMethod = ReflectionUtil.getMethod(TestClass.class, "staticMethod", String.class);
    // Wrap static method
    MethodHolder<LambdaWrapper, Void, Integer> staticHolder = lambdaWrapperHolder.wrapSafe(
        staticMethod);
    // Invoke static method without caller
    int staticResult = staticHolder.invokeStatic("Hello");
    System.out.println("Static result: " + staticResult);

    // Create metadata for wrapper lambda
    Method testMethod = ReflectionUtil.getMethod(
        TestInterface.class, "exact", Object.class, int.class);
    LambdaMetadata testWrapper = LambdaMetadata.from(testMethod);
    // Wrap special method with your own lambda metadata
    MethodHolder<TestInterface, TestClass, String> testHolder =
        lambdaWrapperHolder.wrapSafe(specialMethod, testWrapper);
    // Create new ArgumentMatcher for wrapper
    MethodSignature methodSignature = MethodSignature.fromWrapper(testMethod);
    MatcherSignature<TestInterface> matcherSignature = new MatcherSignature<>(
        TestInterface.class, methodSignature);
    ArgumentMatcherHolder.INSTANCE.addMatcher(
        matcherSignature, (holder, original, args) -> holder.getWrapper()
                                                            .exact(args[0], (int) args[1]));
    // Invoke special method with custom argument matcher
    testHolder.invoke(caller, 1);
  }

  public interface TestInterface {

    @ExactInvoker
    String exact(Object caller, int id);

    @Invoker
    Object get(Object caller);
  }

  public static class TestClass {

    public TestClass() {
      System.out.println("Test class constructor invoked!");
    }

    public static int staticMethod(String s) {
      System.out.println("Static invoked");
      return s.length();
    }

    public Long get() {
      System.out.println("Getter invoked");
      return 1L;
    }

    public void set(String arg) {
      System.out.println("Setter invoked: " + arg);
    }

    public int apply(String arg) {
      System.out.println("Function invokes: " + arg);
      return arg.length();
    }

    public String exact(int id) {
      System.out.println("Special invoked: " + id);
      return String.valueOf(id);
    }

    public Class<?>[] varargs(Object... args) {
      return Arrays.stream(args).map(Object::getClass).collect(Collectors.toList())
                                .toArray(new Class[]{});
    }
  }
}
