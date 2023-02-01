package tech.hiddenproject.aide.reflection;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import tech.hiddenproject.aide.optional.BooleanOptional;

/**
 * @author Danila Rassokhin
 */
public class LambdaFactoryHolder {

  private final MethodHandles.Lookup lookup = MethodHandles.lookup();
  private final Map<ExactMethodSignature, FactoryMetadata> factoryMetadata = new HashMap<>();
  private final Map<MethodSignature, FactoryMetadata> rawFactoryMetadata = new HashMap<>();

  public void add(Class<?> factory) {
    BooleanOptional.of(factory.isInterface())
        .ifFalseThrow(() -> new RuntimeException());

    Arrays.stream(factory.getDeclaredMethods())
        .forEach(this::add);
  }

  public void add(Method... methods) {
    Arrays.stream(methods).forEach(this::addMethod);
  }

  public void addMethod(Method m) {
    BooleanOptional.of(checkAnnotations(m))
        .ifFalseThrow(() -> new RuntimeException());
    Invoker invoker = m.getAnnotation(Invoker.class);
    ExactInvoker exactInvoker = m.getAnnotation(ExactInvoker.class);

    BooleanOptional.of(Objects.nonNull(invoker)).ifTrueThen(v -> addInvoker(m));
    BooleanOptional.of(Objects.nonNull(exactInvoker)).ifTrueThen(v -> addExactInvoker(m));
  }

  private void addExactInvoker(Method method) {
    ExactMethodSignature methodSignature = factorySignature(method);
    FactoryMetadata metadata = createMetadata(method.getDeclaringClass(), method);
    factoryMetadata.put(methodSignature, metadata);
  }

  private void addInvoker(Method method) {
    MethodSignature methodSignature = new MethodSignature(method.getReturnType(),
                                                          method.getParameterCount() - 1);
    FactoryMetadata metadata = createMetadata(method.getDeclaringClass(), method);
    rawFactoryMetadata.put(methodSignature, metadata);
  }

  private boolean checkAnnotations(Method method) {
    return method.isAnnotationPresent(Invoker.class)
        || method.isAnnotationPresent(ExactInvoker.class);
  }

  public <F> F wrap(Method m) throws Throwable {
    return (F) createCallSite(m).getTarget().invoke();
  }

  public <F> F wrap(Object obj, String name, Class<?>... parameterTypes) {
    return wrap(obj.getClass(), name, parameterTypes);
  }

  public <F> F wrap(Class<?> c, String name, Class<?>... parameterTypes) {
    try {
      Method method = ReflectionUtil.getMethod(c, name, parameterTypes);
      return (F) createCallSite(method).getTarget().invoke();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private CallSite createCallSite(Method m) throws Exception {
    Class<?> rType = m.getReturnType() == void.class ? void.class : Object.class;
    ExactMethodSignature exactMethodSignature = rawSignature(m);
    MethodSignature methodSignature = new MethodSignature(rType, m.getParameterCount());
    if (!rawFactoryMetadata.containsKey(methodSignature) && !factoryMetadata.containsKey(
        exactMethodSignature)) {
      throw new RuntimeException("No factories found!");
    }
    FactoryMetadata metadata = factoryMetadata.getOrDefault(
        exactMethodSignature,
        rawFactoryMetadata.get(methodSignature));
    MethodHandle methodHandle = lookup.unreflect(m);
    return LambdaMetafactory.metafactory(lookup, metadata.getMethodName(), metadata.getFactory(),
                                         metadata.getMethodType(),
                                         methodHandle, methodHandle.type());
  }

  private FactoryMetadata createMetadata(Class<?> factory, Method m) {
    return new FactoryMetadata(m.getName(), factory, m);
  }

  private static Class<?>[] getParameterTypes(Method m) {
    return Arrays.stream(m.getParameterTypes())
        .skip(1)
        .collect(Collectors.toList())
        .toArray(new Class[]{});
  }

  private static ExactMethodSignature rawSignature(Method method) {
    return new ExactMethodSignature(method.getReturnType(), method.getParameterTypes());
  }

  private static ExactMethodSignature factorySignature(Method method) {
    return new ExactMethodSignature(method.getReturnType(), getParameterTypes(method));
  }

  private class FactoryMetadata {

    private final String methodName;
    private final MethodType factory;
    private final MethodType methodType;

    public FactoryMetadata(String methodName, Class<?> factory, Method method) {
      this.methodName = methodName;
      this.factory = MethodType.methodType(factory);
      this.methodType = MethodType.methodType(method.getReturnType(), Object.class, getParameterTypes(method));
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
  }
}
