package tech.hiddenproject.aide.reflection.signature;

import tech.hiddenproject.aide.reflection.LambdaWrapperHolder;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Stores metadata of wrapper function for {@link LambdaWrapperHolder}.
 *
 * @author Danila Rassokhin
 */
public class LambdaMetadata {

  private final String methodName;
  private final MethodType declaringInterfaceType;

  private final Class<?> declaringInterface;
  private final MethodType methodType;

  public LambdaMetadata(Class<?> declaringInterfaceType, Method method) {
    this.methodName = method.getName();
    this.declaringInterface = declaringInterfaceType;
    this.declaringInterfaceType = MethodType.methodType(declaringInterfaceType);
    this.methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
  }

  public static LambdaMetadata from(Method method) {
    return new LambdaMetadata(method.getDeclaringClass(), method);
  }

  public String getMethodName() {
    return methodName;
  }

  public MethodType getDeclaringInterfaceType() {
    return declaringInterfaceType;
  }

  public Class<?> getDeclaringInterface() {
    return declaringInterface;
  }

  public MethodType getMethodType() {
    return methodType;
  }

  @Override
  public String toString() {
    return "LambdaMetadata{" + "methodName='" + methodName + '\'' + ", factory="
        + declaringInterfaceType +
        ", methodType=" + methodType + '}';
  }
}
