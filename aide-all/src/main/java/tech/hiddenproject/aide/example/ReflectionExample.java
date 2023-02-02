package tech.hiddenproject.aide.example;

import java.util.Arrays;
import java.util.stream.Collectors;
import tech.hiddenproject.aide.reflection.LambdaWrapper;
import tech.hiddenproject.aide.reflection.LambdaWrapperHolder;
import tech.hiddenproject.aide.reflection.MethodHolder;
import tech.hiddenproject.aide.reflection.annotation.ExactInvoker;

/**
 * @author Danila Rassokhin
 */
public class ReflectionExample {


  public ReflectionExample() {
    LambdaWrapperHolder lambdaWrapperHolder = LambdaWrapperHolder.INSTANCE;
    lambdaWrapperHolder.add(TestInterface.class);

    TestClass caller = new TestClass();

    LambdaWrapper getter = lambdaWrapperHolder.wrap(caller, "get");

    Long id = getter.get(caller);
    System.out.println("Getter result: " + id);

    LambdaWrapper setter = lambdaWrapperHolder.wrap(caller, "set", String.class);

    setter.set(caller, "Hello");

    LambdaWrapper function = lambdaWrapperHolder.wrap(caller, "apply", String.class);

    int res = function.apply(caller, "Hi");
    System.out.println("Function result: " + res);

    TestInterface testInterface = lambdaWrapperHolder.wrapExact(caller, "special", int.class);
    String special = testInterface.special(caller, 1);
    System.out.println("Special result: " + special);

    LambdaWrapper varargs = lambdaWrapperHolder.wrap(caller, "varargs", Object[].class);
    Class<?>[] varArgsTypes = varargs.apply(caller, new Object[]{"Hello", 1});
    System.out.println(Arrays.toString(varArgsTypes));

    MethodHolder<LambdaWrapper, TestClass, String> methodHolder = lambdaWrapperHolder.wrapSafe(
        caller, "special", int.class);
    String r = methodHolder.invoke(caller, 1);
  }

  public interface TestInterface {

    @ExactInvoker
    String special(Object caller, int id);
  }

  public static class TestClass {

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

    public String special(int id) {
      System.out.println("Special invoked: " + id);
      return String.valueOf(id);
    }

    public Class<?>[] varargs(Object... args) {
      return Arrays.stream(args).map(Object::getClass).collect(Collectors.toList())
          .toArray(new Class[]{});
    }

  }
}
