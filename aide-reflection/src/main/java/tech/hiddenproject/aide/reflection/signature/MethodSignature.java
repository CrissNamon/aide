package tech.hiddenproject.aide.reflection.signature;

import tech.hiddenproject.aide.optional.IfTrueConditional;
import tech.hiddenproject.aide.optional.ObjectUtils;
import tech.hiddenproject.aide.reflection.exception.ReflectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Represents generic method signature. {@link MethodSignature#equals(Object)} will check if method
 * signatures equals in simple way, i.e. if method return type is void or not and parameters count.
 *
 * @author Danila Rassokhin
 */
public class MethodSignature implements AbstractSignature {

  private final Class<?> rType;

  private final Integer pCount;

  private final Class<?> declaringClass;

  public MethodSignature(Class<?> declaringClass, Class<?> rType, Integer pCount) {
    this.declaringClass = declaringClass;
    this.rType = rType;
    this.pCount = pCount;
  }

  public MethodSignature(Method method) {
    this(method.getDeclaringClass(), method.getReturnType(), method.getParameterCount());
  }

  /**
   * Creates signature from wrapper method, i.e. removes first parameter which should be a caller
   * object.
   *
   * @param method Method to create signature from
   * @return Signature of method
   */
  public static MethodSignature fromWrapper(Method method) {
    return new MethodSignature(
        method.getDeclaringClass(), getReturnType(method), method.getParameterCount());
  }

  /**
   * Creates signature from any executable. If method return type is void, then return type of
   * signature will be void and {@link Object} otherwise.
   *
   * @param executable {@link Executable} to create signature from (Method or constructor)
   * @return Signature of method
   */
  public static MethodSignature from(Executable executable) {
    Class<?> rType = IfTrueConditional.create().ifTrue(executable.getClass().equals(Method.class))
                                               .then(() -> getReturnType((Method) executable))
                                               .ifTrue(
                                                   executable.getClass().equals(Constructor.class))
                                               .then(Object.class).orElseThrows(
            () -> ReflectionException.format(
                "Wrapping is supported for " + "constructors and methods only!"));
    int pCount = IfTrueConditional.create()
        .ifTrue(ObjectUtils.isInstanceOf(executable, Method.class) && Modifier.isStatic(
            executable.getModifiers()))
        .then(executable.getParameterCount())
        .ifTrue(ObjectUtils.isInstanceOf(executable, Method.class))
        .then(executable.getParameterCount() + 1).orElseGet(executable::getParameterCount);
    return new MethodSignature(executable.getDeclaringClass(), rType, pCount);
  }

  public static Class<?> getReturnType(Method method) {
    return method.getReturnType() == void.class ? void.class : Object.class;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getReturnType(), getParameterCount());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MethodSignature that = (MethodSignature) o;
    return getReturnType().equals(that.getReturnType())
        && getParameterCount() == that.getParameterCount();
  }

  @Override
  public String toString() {
    return "MethodSignature{" + "rType=" + rType + ", pCount=" + pCount + ", declaringClass="
        + declaringClass +
        '}';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getReturnType() {
    return rType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getParameterCount() {
    return pCount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?>[] getParameterTypes() {
    return new Class[0];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getDeclaringClass() {
    return declaringClass;
  }
}
