package tech.hiddenproject.aide.reflection.filter;

import java.lang.reflect.Executable;

/**
 * Filters {@link Executable} before
 * {@link tech.hiddenproject.aide.reflection.LambdaWrapperHolder#wrap(Executable)}.
 *
 * @author Danila Rassokhin
 */
public interface ExecutableFilter {

  /**
   * Allows only {@link java.lang.reflect.Modifier#PUBLIC} {@link Executable} to be wrapped.
   */
  ExecutableFilter PUBLIC_ONLY = new PublicOnlyFilter();

  /**
   * Allows only any {@link Executable} to be wrapped.
   */
  ExecutableFilter ANY = new AnyFilter();

  /**
   * @param executable {@link Executable}
   * @return true if executable can be wrapped
   */
  boolean filter(Executable executable);

  /**
   * Will be thrown if {@link #filter(Executable)} returns false.
   *
   * @return {@link RuntimeException}
   */
  RuntimeException getException();

}
