package tech.hiddenproject.aide.reflection.filter;

import java.lang.reflect.Executable;

/**
 * Allows only any {@link Executable} to be wrapped.
 *
 * @author Danila Rassokhin
 */
public class AnyFilter implements ExecutableFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean filter(Executable executable) {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RuntimeException getException() {
    return null;
  }
}
