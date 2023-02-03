package tech.hiddenproject.aide.reflection;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.aide.optional.IfTrueConditional;
import tech.hiddenproject.aide.optional.ObjectUtils;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.aide.reflection.annotation.ExactInvoker;
import tech.hiddenproject.aide.reflection.annotation.Invoker;
import tech.hiddenproject.aide.reflection.exception.ReflectionException;
import tech.hiddenproject.aide.reflection.signature.AbstractSignature;
import tech.hiddenproject.aide.reflection.signature.ExactMethodSignature;
import tech.hiddenproject.aide.reflection.signature.LambdaMetadata;
import tech.hiddenproject.aide.reflection.signature.MethodSignature;

/**
 * Stores all wrapper signatures and wraps method into them. Uses {@link LambdaMetafactory} to wrap
 * methods into lambda functions dynamically, so reflective method call will be as fast as direct
 * calls.
 *
 * @author Danila Rassokhin
 */
public enum LambdaWrapperHolder {

  DEFAULT(LambdaWrapper.class),
  EMPTY;

  private final MethodHandles.Lookup lookup = MethodHandles.lookup();
  private final Map<Class<?>, Map<AbstractSignature, LambdaMetadata>> invokers = new HashMap<>();
  private final Map<Class<?>, Map<AbstractSignature, LambdaMetadata>> exactInvokers = new HashMap<>();

  LambdaWrapperHolder() {
  }

  LambdaWrapperHolder(Class<?> interfaceClass) {
    add(interfaceClass);
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
   * Checks if {@link LambdaWrapperHolder} has a wrapper for given method
   *
   * @param m {@link Executable} (Must be {@link Method} or {@link Constructor}
   * @return true if there is {@link Invoker} or {@link ExactInvoker} exists for this method inside
   * {@link LambdaWrapperHolder}
   */
  public boolean canBeWrapped(Executable m) {
    return getWrappers(m).size() > 0;
  }

  /**
   * Collects all available wrappers for given {@link Executable}.
   *
   * @param executable {@link Executable} must be {@link Method} or {@link Constructor}
   * @return List of {@link LambdaMetadata}
   */
  public List<LambdaMetadata> getWrappers(Executable executable) {
    List<LambdaMetadata> metadata = new ArrayList<>();
    MethodSignature methodSignature = MethodSignature.from(executable);
    ExactMethodSignature exactMethodSignature = ExactMethodSignature.from(executable);
    metadata.addAll(invokers.values().stream()
                        .flatMap(map -> map.entrySet().stream())
                        .filter(entry -> entry.getKey().equals(methodSignature))
                        .map(Entry::getValue)
                        .collect(Collectors.toList()));
    metadata.addAll(exactInvokers.values().stream()
                        .flatMap(map -> map.entrySet().stream())
                        .filter(entry -> entry.getKey().equals(exactMethodSignature))
                        .map(Entry::getValue)
                        .collect(Collectors.toList()));
    return metadata;
  }

  /**
   * Wraps {@link Executable} into wrapper function to invoke it fast. {@link Executable} must be
   * {@link Modifier#PUBLIC}. Wrapping is supported only for {@link Constructor} and
   * {@link Method}.
   *
   * @param executable {@link Executable}
   * @return {@link  WrapperHolder} for {@link LambdaWrapper}
   */
  public WrapperHolder<LambdaWrapper> wrap(Executable executable) {
    return createWrapper(false, executable);
  }

  /**
   * Wraps {@link Executable} into wrapper function from given interface to invoke it fast.
   * {@link Executable} must be {@link Modifier#PUBLIC}. Wrapping is supported only for
   * {@link Constructor} and {@link Method}.
   *
   * @param interfaceClass Interface to use wrapper from
   * @param executable     {@link Executable}
   * @return {@link WrapperHolder} for given interface
   */
  public <F> WrapperHolder<F> wrap(Executable executable, Class<F> interfaceClass) {
    return createWrapper(false, executable, interfaceClass);
  }

  /**
   * Wraps {@link Executable} with given {@link LambdaMetadata}. See {@link #wrap(Executable)}.
   *
   * @param executable     {@link Executable}
   * @param lambdaMetadata {@link LambdaMetadata} of wrapper function
   * @param <F>            {@link LambdaMetadata#getDeclaringInterfaceType()}
   * @return Interface wrapper {@link LambdaMetadata#getDeclaringInterfaceType()}
   */
  public <F> WrapperHolder<F> wrap(Executable executable, LambdaMetadata lambdaMetadata) {
    return createWrapper(executable, lambdaMetadata);
  }

  /**
   * Wraps {@link Executable} with given {@link LambdaMetadata}. See {@link #wrapSafe(Executable)}.
   *
   * @param executable     {@link Executable}
   * @param lambdaMetadata {@link LambdaMetadata} of wrapper function
   * @param <F>            Interface declaring wrapper function
   * @param <C>            Caller type
   * @param <R>            Return type
   * @return Interface wrapper {@link LambdaMetadata#getDeclaringInterfaceType()}
   */
  public <F, C, R> MethodHolder<F, C, R> wrapSafe(Executable executable,
                                                  LambdaMetadata lambdaMetadata) {
    WrapperHolder<F> wrapper = createWrapper(executable, lambdaMetadata);
    return new MethodHolder<>(wrapper, executable);
  }

  /**
   * Wraps {@link Executable} into wrapper function with exactly same signature to invoke it fast.
   * {@link Executable} must be {@link Modifier#PUBLIC}. Wrapping is supported only for
   * {@link Constructor} and {@link Method}.
   *
   * @param executable {@link Executable}
   * @param <F>        Interface declaring wrapper function
   * @return Interface wrapper
   */
  public <F> WrapperHolder<F> wrapExact(Executable executable, LambdaMetadata lambdaMetadata) {
    return createWrapper(executable, lambdaMetadata);
  }

  /**
   * Wraps {@link Executable} into wrapper function from given interface with exactly same signature
   * to invoke it fast. {@link Executable} must be {@link Modifier#PUBLIC}. Wrapping is supported
   * only for {@link Constructor} and {@link Method}.
   *
   * @param executable     {@link Executable}
   * @param <F>            Interface declaring wrapper function
   * @param interfaceClass Interface to use wrapper from
   * @return Interface wrapper
   */
  public <F> WrapperHolder<F> wrapExact(Executable executable, Class<F> interfaceClass) {
    return createWrapper(true, executable, interfaceClass);
  }

  /**
   * Wraps {@link Executable} into wrapper function to invoke it fast. Executable must be
   * {@link Modifier#PUBLIC}. Uses {@link MethodHolder} to provide type safety. Wrapping is
   * supported for {@link Constructor} and {@link Method}!
   *
   * @param executable {@link Executable} to wrap
   * @param <C>        Caller type
   * @param <R>        Return type
   * @return Interface wrapper
   */
  public <C, R> MethodHolder<LambdaWrapper, C, R> wrapSafe(Executable executable) {
    WrapperHolder<LambdaWrapper> wrapper = createWrapper(false, executable);
    return new MethodHolder<>(wrapper, executable);
  }

  /**
   * Wraps {@link Executable} into wrapper function from given interface to invoke it fast.
   * Executable must be {@link Modifier#PUBLIC}. Uses {@link MethodHolder} to provide type safety.
   * Wrapping is supported for {@link Constructor} and {@link Method}!
   *
   * @param executable     {@link Executable} to wrap
   * @param <C>            Caller type
   * @param <R>            Return type
   * @param interfaceClass Interface to use wrapper from
   * @return Interface wrapper
   */
  public <F, C, R> MethodHolder<F, C, R> wrapSafe(Executable executable, Class<F> interfaceClass) {
    WrapperHolder<F> wrapper = createWrapper(false, executable, interfaceClass);
    return new MethodHolder<>(wrapper, executable);
  }

  private <F> WrapperHolder<F> createWrapper(boolean exact, Executable executable,
                                             Class<?> interfaceClass) {
    BooleanOptional.of(Modifier.isPublic(executable.getModifiers()))
        .ifFalseThrow(() -> ReflectionException.format("Wrapping is supported for "
                                                           + "PUBLIC methods only!"));
    return new WrapperHolder<>(
        ThrowableOptional.sneaky(
            () -> (F) createCallSite(executable, exact, interfaceClass).getTarget().invoke()),
        interfaceClass
    );
  }

  private <F> WrapperHolder<F> createWrapper(boolean exact, Executable executable) {
    return createWrapper(exact, executable, LambdaWrapper.class);
  }

  private <F> WrapperHolder<F> createWrapper(Executable executable, LambdaMetadata lambdaMetadata) {
    BooleanOptional.of(Modifier.isPublic(executable.getModifiers()))
        .ifFalseThrow(() -> ReflectionException.format("Wrapping is supported for "
                                                           + "PUBLIC methods only!"));
    return new WrapperHolder<>(
        ThrowableOptional.sneaky(
            () -> (F) createCallSite(executable, lambdaMetadata).getTarget().invoke()),
        lambdaMetadata.getDeclaringInterface()
    );
  }

  private void addExactInvoker(Method method) {
    ExactMethodSignature methodSignature = ExactMethodSignature.fromWrapper(method);
    LambdaMetadata metadata = new LambdaMetadata(method.getDeclaringClass(), method);
    exactInvokers.putIfAbsent(method.getDeclaringClass(), new HashMap<>());
    exactInvokers.get(method.getDeclaringClass()).put(methodSignature, metadata);
  }

  private void addInvoker(Method method) {
    MethodSignature methodSignature = MethodSignature.fromWrapper(method);
    LambdaMetadata metadata = new LambdaMetadata(method.getDeclaringClass(), method);
    invokers.putIfAbsent(method.getDeclaringClass(), new HashMap<>());
    invokers.get(method.getDeclaringClass()).put(methodSignature, metadata);
  }

  private boolean checkAnnotations(Method method) {
    return method.isAnnotationPresent(Invoker.class)
        || method.isAnnotationPresent(ExactInvoker.class);
  }

  private CallSite createCallSite(Executable executable, boolean exact, Class<?> interfaceClass)
      throws Exception {
    LambdaMetadata metadata = getMetadata(executable, exact, interfaceClass);
    return createCallSite(executable, metadata);
  }

  private CallSite createCallSite(Executable executable, LambdaMetadata lambdaMetadata)
      throws Exception {
    MethodHandle methodHandle = IfTrueConditional.create()
        .ifTrue(ObjectUtils.isInstanceOf(executable, Constructor.class))
        .then(() -> unreflect((Constructor<?>) executable))
        .ifTrue(ObjectUtils.isInstanceOf(executable, Method.class))
        .then(() -> unreflect((Method) executable))
        .orElseThrows(() -> ReflectionException.format("Wrapping is supported for constructors "
                                                           + "and methods only!"));
    return LambdaMetafactory.metafactory(lookup, lambdaMetadata.getMethodName(),
                                         lambdaMetadata.getDeclaringInterfaceType(),
                                         lambdaMetadata.getMethodType(),
                                         methodHandle, methodHandle.type()
    );
  }

  private MethodHandle unreflect(Method method) {
    return ThrowableOptional.sneaky(() -> lookup.unreflect(method));
  }

  private MethodHandle unreflect(Constructor<?> constructor) {
    return ThrowableOptional.sneaky(() -> lookup.unreflectConstructor(constructor));
  }

  private LambdaMetadata getMetadata(Executable method, boolean exact, Class<?> interfaceClass) {
    AbstractSignature signature = IfTrueConditional.create()
        .ifTrue(exact).then(() -> ExactMethodSignature.from(method))
        .orElseGet(() -> MethodSignature.from(method));
    Map<Class<?>, Map<AbstractSignature, LambdaMetadata>> container = IfTrueConditional.create()
        .ifTrue(exact).then(exactInvokers)
        .orElse(invokers);
    if (!container.containsKey(interfaceClass)) {
      throw ReflectionException.format("No wrappers with type %s", interfaceClass);
    }
    if (!container.get(interfaceClass).containsKey(signature)) {
      throw ReflectionException.format("No wrappers found for method %s", method);
    }
    return container.get(interfaceClass).get(signature);
  }
}
