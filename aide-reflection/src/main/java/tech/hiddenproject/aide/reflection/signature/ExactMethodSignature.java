package tech.hiddenproject.aide.reflection.signature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import tech.hiddenproject.aide.optional.IfTrueConditional;
import tech.hiddenproject.aide.reflection.exception.ReflectionException;

/**
 * Represents exact method signature. {@link ExactMethodSignature#equals(Object)} will check if
 * method signatures equals exactly, i.e. return type, parameters count and parameters type.
 *
 * @author Danila Rassokhin
 */
public class ExactMethodSignature implements AbstractSignature {

  private final Class<?> rType;

  private final Class<?>[] pType;

  private final Class<?> declaringClass;

  public ExactMethodSignature(Class<?> declaringClass, Class<?> rType, Class<?>[] pType) {
    this.declaringClass = declaringClass;
    this.rType = rType;
    this.pType = pType;
  }

  /**
   * Creates signature from wrapper method, i.e. removes first parameter type which should be a
   * caller object.
   *
   * @param method Method to create signature from
   * @return Signature of method
   */
  public static ExactMethodSignature fromWrapper(Method method) {
    return new ExactMethodSignature(
        method.getDeclaringClass(), method.getReturnType(), removeCaller(method));
  }

  /**
   * Creates signature from any method.
   *
   * @param executable {@link Executable} to create signature from (Method or constructor)
   * @return Signature of method
   */
  public static ExactMethodSignature from(Executable executable) {
    Class<?> rType = IfTrueConditional.create()
        .ifTrue(executable.getClass().equals(Method.class))
        .then(() -> ((Method) executable).getReturnType())
        .ifTrue(executable.getClass().equals(Constructor.class))
        .then(Object.class)
        .orElseThrows(() -> ReflectionException.format("Wrapping is supported for "
                                                           + "constructors and methods only!"));
    return new ExactMethodSignature(
        executable.getDeclaringClass(), rType, executable.getParameterTypes());
  }

  private static Class<?>[] removeCaller(Method method) {
    if (method.getParameterCount() < 2) {
      return new Class[]{};
    }
    return Arrays.copyOfRange(method.getParameterTypes(), 1, method.getParameterCount());
  }

  @Override
  public int hashCode() {
    return Objects.hash(rType, Arrays.hashCode(pType));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExactMethodSignature that = (ExactMethodSignature) o;
    return rType.equals(that.getReturnType()) && Arrays.equals(pType, that.getParameterTypes());
  }

  @Override
  public String toString() {
    return "ExactMethodSignature{" +
        "rType=" + rType +
        ", pType=" + Arrays.toString(pType) +
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
    return pType.length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?>[] getParameterTypes() {
    return pType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getDeclaringClass() {
    return declaringClass;
  }
}
