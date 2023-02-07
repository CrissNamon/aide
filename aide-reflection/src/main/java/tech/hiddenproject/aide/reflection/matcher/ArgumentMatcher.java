package tech.hiddenproject.aide.reflection.matcher;

import tech.hiddenproject.aide.optional.Action;
import tech.hiddenproject.aide.reflection.WrapperHolder;

import java.lang.reflect.Executable;

/**
 * Matches arguments from wrapper to original.
 *
 * @param <W> Wrapper interface type
 * @param <T> Arguments type
 * @param <R> Return type
 * @author Danila Rassokhin
 */
public interface ArgumentMatcher<W, T, R> {

  static Object voidable(Action action) {
    action.make();
    return null;
  }

  R apply(WrapperHolder<W> holder, Executable original, T args);

}
