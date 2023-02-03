package tech.hiddenproject.aide.reflection.signature;

import java.util.Objects;

/**
 * Represents signature for {@link tech.hiddenproject.aide.reflection.matcher.ArgumentMatcher}.
 *
 * @param <W> Wrapper interface type
 * @author Danila Rassokhin
 */
public class MatcherSignature<W> {

  /**
   * Interface type of wrapper function.
   */
  private final Class<?> declaringClass;

  /**
   * {@link MethodSignature} of wrapper function.
   */
  private final MethodSignature methodSignature;

  public MatcherSignature(Class<W> declaringClass, MethodSignature methodSignature) {
    this.declaringClass = declaringClass;
    this.methodSignature = methodSignature;
  }

  @Override
  public int hashCode() {
    return Objects.hash(declaringClass, methodSignature);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MatcherSignature that = (MatcherSignature) o;
    return declaringClass.equals(that.declaringClass) && methodSignature.equals(
        that.methodSignature);
  }

  @Override
  public String toString() {
    return "MatcherSignature{" +
        "declaringClass=" + declaringClass +
        ", methodSignature=" + methodSignature +
        '}';
  }
}
