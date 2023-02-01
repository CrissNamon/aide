package tech.hiddenproject.aide.reflection.signature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents exact method signature. {@link ExactMethodSignature#equals(Object)} will check if
 * method signatures equals exactly, i.e. return type, parameters count and parameters type.
 *
 * @author Danila Rassokhin
 */
public class ExactMethodSignature implements AbstractSignature {

  private final Class<?> rType;

  private final Class<?>[] pType;

  public ExactMethodSignature(Class<?> rType, Class<?>[] pType) {
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
    return new ExactMethodSignature(method.getReturnType(), removeCaller(method));
  }

  /**
   * Creates signature from any method.
   *
   * @param method Method to create signature from
   * @return Signature of method
   */
  public static ExactMethodSignature from(Method method) {
    return new ExactMethodSignature(method.getReturnType(), method.getParameterTypes());
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
}
