package tech.hiddenproject.aide.example;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import tech.hiddenproject.aide.example.ReflectionExample.TestClass;
import tech.hiddenproject.aide.reflection.util.ReflectionUtil;

/**
 * @author Danila Rassokhin
 */
public class Main {

  public static void main(String... args) {

    System.out.println("REFLECTION EXAMPLE");
    ReflectionExample reflectionExample = new ReflectionExample();

    System.out.println("OPTIONAL EXAMPLE");
    OptionalExample optionalExample = new OptionalExample();

    System.out.println("CONDITIONALS EXAMPLE");
    ConditionalExample conditionalExample = new ConditionalExample();
  }

}
