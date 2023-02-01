package tech.hiddenproject.aide.reflection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Danila Rassokhin
 */
public class ExactMethodSignature implements AbstractSignature {

  private final Class<?> rType;

  private final Class<?>[] pType;

  public ExactMethodSignature(Class<?> rType, Class<?>[] pType) {
    this.rType = rType;
    this.pType = pType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractSignature that = (AbstractSignature) o;
    return rType.equals(that.getReturnType()) && Arrays.equals(pType, that.getParameterTypes());
  }

  @Override
  public int hashCode() {
    return Objects.hash(rType, Arrays.hashCode(pType));
  }

  @Override
  public String toString() {
    return "ExactMethodSignature{" +
        "rType=" + rType +
        ", pType=" + Arrays.toString(pType) +
        '}';
  }

  public static ExactMethodSignature fromWrapper(Method method) {
    return new ExactMethodSignature(method.getReturnType(), removeCaller(method));
  }

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
  public Class<?> getReturnType() {
    return rType;
  }

  @Override
  public int getParameterCount() {
    return pType.length;
  }

  @Override
  public Class<?>[] getParameterTypes() {
    return pType;
  }
}
