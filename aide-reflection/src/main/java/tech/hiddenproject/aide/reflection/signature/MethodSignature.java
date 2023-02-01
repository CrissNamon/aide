package tech.hiddenproject.aide.reflection.signature;

import java.lang.reflect.Method;
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

  public MethodSignature(Class<?> rType, Integer pCount) {
    this.rType = rType;
    this.pCount = pCount;
  }

  public MethodSignature(Method method) {
    this(method.getReturnType(), method.getParameterCount());
  }

  /**
   * Creates signature from wrapper method, i.e. removes first parameter which should be a caller
   * object.
   *
   * @param method Method to create signature from
   * @return Signature of method
   */
  public static MethodSignature fromWrapper(Method method) {
    return new MethodSignature(method.getReturnType(), method.getParameterCount() - 1);
  }

  /**
   * Creates signature from any method. If method return type is void, then return type of signature
   * will be void and {@link Object} otherwise.
   *
   * @param method Method to create signature from
   * @return Signature of method
   */
  public static MethodSignature from(Method method) {
    Class<?> rType = method.getReturnType() == void.class ? void.class : Object.class;
    return new MethodSignature(rType, method.getParameterCount());
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
    return "MethodSignature{" +
        "rType=" + rType +
        ", pCount=" + pCount +
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
}
