package tech.hiddenproject.aide.reflection;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Stores metadata of wrapper function for {@link LambdaWrapperHolder}.
 *
 * @author Danila Rassokhin
 */
public class LambdaMetadata {

  private final String methodName;
  private final MethodType declaringInterface;
  private final MethodType methodType;

  public LambdaMetadata(Class<?> declaringInterface, Method method) {
    this.methodName = method.getName();
    this.declaringInterface = MethodType.methodType(declaringInterface);
    this.methodType = MethodType.methodType(
        method.getReturnType(), Object.class, getParameterTypes(method));
  }

  public String getMethodName() {
    return methodName;
  }

  public MethodType getDeclaringInterface() {
    return declaringInterface;
  }

  public MethodType getMethodType() {
    return methodType;
  }

  @Override
  public String toString() {
    return "LambdaMetadata{" +
        "methodName='" + methodName + '\'' +
        ", factory=" + declaringInterface +
        ", methodType=" + methodType +
        '}';
  }

  private Class<?>[] getParameterTypes(Method m) {
    return Arrays.stream(m.getParameterTypes())
        .skip(1)
        .collect(Collectors.toList())
        .toArray(new Class[]{});
  }
}
