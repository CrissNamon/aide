package tech.hiddenproject.aide.reflection;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.aide.reflection.annotation.ExactInvoker;
import tech.hiddenproject.aide.reflection.annotation.Invoker;
import tech.hiddenproject.aide.reflection.exception.ReflectionException;
import tech.hiddenproject.aide.reflection.signature.AbstractSignature;
import tech.hiddenproject.aide.reflection.signature.ExactMethodSignature;
import tech.hiddenproject.aide.reflection.signature.MethodSignature;

/**
 * Stores all wrapper signatures and wraps method into them. Uses {@link LambdaMetafactory} to wrap
 * methods into lambda functions dynamically, so reflective method call will be as fast as direct
 * calls.
 *
 * @author Danila Rassokhin
 */
public enum LambdaWrapperHolder {

  INSTANCE(LambdaWrapper.Factory.get());

  private final MethodHandles.Lookup lookup = MethodHandles.lookup();
  private final Map<AbstractSignature, LambdaMetadata> invokers = new HashMap<>();
  private final Map<AbstractSignature, LambdaMetadata> exactInvokers = new HashMap<>();

  LambdaWrapperHolder(Method... methods) {
    add(methods);
  }

  /**
   * Adds new interface to create wrappers from.
   *
   * @param declaringInterface Must be an interface
   */
  public void add(Class<?> declaringInterface) {
    BooleanOptional.of(declaringInterface.isInterface())
        .ifFalseThrow(
            () -> ReflectionException.format("Class %s must be an interface", declaringInterface));

    Arrays.stream(declaringInterface.getDeclaredMethods())
        .forEach(this::add);
  }

  /**
   * Adds new method as wrapper. See {@link LambdaWrapperHolder#addMethod(Method)}.
   *
   * @param methods {@link Method}s to add
   */
  public void add(Method... methods) {
    Arrays.stream(methods).forEach(this::addMethod);
  }

  /**
   * Adds new method as wrapper function. Method must be annotated as {@link Invoker} or
   * {@link ExactInvoker}. First parameter of given method must be {@link Object} to pass method
   * caller.
   *
   * @param m {@link Method} to create wrapper from
   */
  public void addMethod(Method m) {
    BooleanOptional.of(checkAnnotations(m))
        .ifFalseThrow(() -> ReflectionException.format("Method %s must be annotated as @Invoker "
                                                           + "or @ExactInvoker", m));
    Invoker invoker = m.getAnnotation(Invoker.class);
    ExactInvoker exactInvoker = m.getAnnotation(ExactInvoker.class);
    BooleanOptional.of(Objects.nonNull(invoker)).ifTrueThen(v -> addInvoker(m));
    BooleanOptional.of(Objects.nonNull(exactInvoker)).ifTrueThen(v -> addExactInvoker(m));
  }

  /**
   * Wraps method into wrapper function to invoke it fast. Method must be {@link Modifier#PUBLIC}.
   *
   * @param m   {@link Method}
   * @param <F> Interface declaring wrapper function
   * @return Interface wrapper
   */
  public <F> F wrap(Method m) {
    return wrap(m.getDeclaringClass(), m.getName(), m.getParameterTypes());
  }

  /**
   * Searches for function with given name and parameter types in {@link Object#getClass()} and
   * wraps method into wrapper function to invoke it fast. Method must be {@link Modifier#PUBLIC}.
   *
   * @param obj            Object to search method in
   * @param name           {@link Method} name
   * @param parameterTypes {@link Method} parameter types
   * @param <F>            Interface declaring wrapper function
   * @return Interface wrapper
   */
  public <F> F wrap(Object obj, String name, Class<?>... parameterTypes) {
    return wrap(obj.getClass(), name, parameterTypes);
  }

  /**
   * Searches for function with given name and parameter types in given class and wraps method into
   * wrapper function to invoke it fast. Method must be {@link Modifier#PUBLIC}.
   *
   * @param c              Class to search method in
   * @param name           {@link Method} name
   * @param parameterTypes {@link Method} parameter types
   * @param <F>            Interface declaring wrapper function
   * @return Interface wrapper
   */
  public <F> F wrap(Class<?> c, String name, Class<?>... parameterTypes) {
    return createWrapper(false, c, name, parameterTypes);
  }

  /**
   * Searches for function with given name and parameter types in given class and wraps method into
   * wrapper function to invoke it fast. Method must be {@link Modifier#PUBLIC}. Uses
   * {@link MethodHolder} to provide type safety.
   *
   * @param c              Class to search method in
   * @param name           {@link Method} name
   * @param parameterTypes {@link Method} parameter types
   * @param <F>            Interface declaring wrapper function
   * @param <C>            Caller type
   * @param <R>            Return type
   * @return Interface wrapper
   */
  public <F, C, R> MethodHolder<F, C, R> wrapSafe(Class<C> c, String name,
                                                  Class<?>... parameterTypes) {
    Method realMethod = ReflectionUtil.getMethod(c, name, parameterTypes);
    return wrapSafe(realMethod);
  }

  /**
   * Searches for function with given name and parameter types in given class and wraps method into
   * wrapper function to invoke it fast. Method must be {@link Modifier#PUBLIC}. Uses
   * {@link MethodHolder} to provide type safety.
   *
   * @param method Method to wrap
   * @param <F>    Interface declaring wrapper function
   * @param <C>    Caller type
   * @param <R>    Return type
   * @return Interface wrapper
   */
  public <F, C, R> MethodHolder<F, C, R> wrapSafe(Method method) {
    F wrapper = wrap(method);
    return new MethodHolder<>(wrapper);
  }

  /**
   * Searches for function with given name and parameter types in given class and wraps method into
   * wrapper function to invoke it fast. Method must be {@link Modifier#PUBLIC}. Uses
   * {@link MethodHolder} to provide type safety.
   *
   * @param caller         Object to search method in class of
   * @param name           {@link Method} name
   * @param parameterTypes {@link Method} parameter types
   * @param <F>            Interface declaring wrapper function
   * @param <C>            Caller type
   * @param <R>            Return type
   * @return Interface wrapper
   */
  public <F, C, R> MethodHolder<F, C, R> wrapSafe(C caller, String name,
                                                  Class<?>... parameterTypes) {
    Method realMethod = ReflectionUtil.getMethod(caller.getClass(), name, parameterTypes);
    return wrapSafe(realMethod);
  }

  /**
   * Searches for function with given name and parameter types in given class and wraps method into
   * wrapper function with exactly same signature to invoke it fast. Method must be
   * {@link Modifier#PUBLIC}.
   *
   * @param c              Class to search method in
   * @param name           {@link Method} name
   * @param parameterTypes {@link Method} parameter types
   * @param <F>            Interface declaring wrapper function
   * @return Interface wrapper
   */
  public <F> F wrapExact(Class<?> c, String name, Class<?>... parameterTypes) {
    return createWrapper(true, c, name, parameterTypes);
  }

  /**
   * Wraps method into wrapper function with exactly same signature to invoke it fast. Method must
   * be {@link Modifier#PUBLIC}.
   *
   * @param m   {@link Method}
   * @param <F> Interface declaring wrapper function
   * @return Interface wrapper
   */
  public <F> F wrapExact(Method m) {
    return wrapExact(m.getDeclaringClass(), m.getName(), m.getParameterTypes());
  }

  /**
   * Searches for function with given name and parameter types in {@link Object#getClass()} and
   * wraps method into wrapper function with exactly same signature to invoke it fast. Method must
   * be {@link Modifier#PUBLIC}.
   *
   * @param obj            Object to search method in
   * @param name           {@link Method} name
   * @param parameterTypes {@link Method} parameter types
   * @param <F>            Interface declaring wrapper function
   * @return Interface wrapper
   */
  public <F> F wrapExact(Object obj, String name, Class<?>... parameterTypes) {
    return wrapExact(obj.getClass(), name, parameterTypes);
  }

  private <F> F createWrapper(boolean exact, Class<?> c, String name, Class<?>... parameterTypes) {
    try {
      Method method = ReflectionUtil.getMethod(c, name, parameterTypes);
      BooleanOptional.of(Modifier.isPublic(method.getModifiers()))
          .ifFalseThrow(
              () -> new IllegalAccessException("Wrapping is supported for PUBLIC methods only!"));
      return (F) createCallSite(method, exact).getTarget().invoke();
    } catch (Throwable e) {
      throw new ReflectionException(e);
    }
  }

  private void addExactInvoker(Method method) {
    ExactMethodSignature methodSignature = ExactMethodSignature.fromWrapper(method);
    LambdaMetadata metadata = new LambdaMetadata(method.getDeclaringClass(), method);
    exactInvokers.putIfAbsent(methodSignature, metadata);
  }

  private void addInvoker(Method method) {
    MethodSignature methodSignature = MethodSignature.fromWrapper(method);
    LambdaMetadata metadata = new LambdaMetadata(method.getDeclaringClass(), method);
    invokers.putIfAbsent(methodSignature, metadata);
  }

  private boolean checkAnnotations(Method method) {
    return method.isAnnotationPresent(Invoker.class)
        || method.isAnnotationPresent(ExactInvoker.class);
  }

  private CallSite createCallSite(Method m, boolean exact) throws Exception {
    LambdaMetadata metadata = getMetadata(m, exact);
    MethodHandle methodHandle = lookup.unreflect(m);
    return LambdaMetafactory.metafactory(lookup, metadata.getMethodName(),
                                         metadata.getDeclaringInterface(),
                                         metadata.getMethodType(),
                                         methodHandle, methodHandle.type()
    );
  }

  private LambdaMetadata getMetadata(Method method, boolean exact) {
    AbstractSignature signature;
    Map<AbstractSignature, LambdaMetadata> container;
    if (exact) {
      signature = ExactMethodSignature.from(method);
      container = exactInvokers;
    } else {
      signature = MethodSignature.from(method);
      container = invokers;
    }
    if (!container.containsKey(signature)) {
      throw ReflectionException.format("No wrappers found for method %s", method);
    }
    return container.get(signature);
  }
}
