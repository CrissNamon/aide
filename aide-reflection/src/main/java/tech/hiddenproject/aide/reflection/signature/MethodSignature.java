package tech.hiddenproject.aide.reflection;

import java.lang.reflect.Method;
import java.util.Objects;

/**
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractSignature that = (AbstractSignature) o;
    return getReturnType().equals(that.getReturnType())
        && getParameterCount() == that.getParameterCount();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getReturnType(), getParameterCount());
  }

  @Override
  public String toString() {
    return "MethodSignature{" +
        "rType=" + rType +
        ", pCount=" + pCount +
        '}';
  }

  @Override
  public Class<?> getReturnType() {
    return rType;
  }

  @Override
  public int getParameterCount() {
    return pCount;
  }

  @Override
  public Class<?>[] getParameterTypes() {
    return new Class[0];
  }

  public static MethodSignature fromWrapper(Method method) {
    return new MethodSignature(method.getReturnType(), method.getParameterCount() - 1);
  }
}
