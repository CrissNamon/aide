package tech.hiddenproject.aide;

import tech.hiddenproject.aide.reflection.LambdaWrapper;
import tech.hiddenproject.aide.reflection.LambdaWrapperHolder;
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
  }
}
