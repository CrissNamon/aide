package tech.hiddenproject.aide.reflection;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Danila Rassokhin
 */
public class FactoryMetadata {

  private final String methodName;
  private final MethodType factory;
  private final MethodType methodType;

  public FactoryMetadata(Class<?> factory, Method method) {
    this.methodName = method.getName();
    this.factory = MethodType.methodType(factory);
    this.methodType = MethodType.methodType(
        method.getReturnType(), Object.class, getParameterTypes(method));
  }

  public String getMethodName() {
    return methodName;
  }

  public MethodType getFactory() {
    return factory;
  }

  public MethodType getMethodType() {
    return methodType;
  }

  @Override
  public String toString() {
    return "FactoryMetadata{" +
        "methodName='" + methodName + '\'' +
        ", factory=" + factory +
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
