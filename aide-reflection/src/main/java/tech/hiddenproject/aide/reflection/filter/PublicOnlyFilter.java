package tech.hiddenproject.aide.reflection.filter;

import tech.hiddenproject.aide.reflection.exception.ReflectionException;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;

/**
 * Allows only {@link java.lang.reflect.Modifier#PUBLIC} {@link Executable} to be wrapped.
 *
 * @author Danila Rassokhin
 */
public class PublicOnlyFilter implements ExecutableFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean filter(Executable executable) {
    return Modifier.isPublic(executable.getModifiers());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReflectionException getException() {
    return ReflectionException.format("Wrapping is supported for PUBLIC methods only!");
  }
}
