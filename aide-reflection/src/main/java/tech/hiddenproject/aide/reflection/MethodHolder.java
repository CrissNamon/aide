package tech.hiddenproject.aide.reflection;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import tech.hiddenproject.aide.reflection.matcher.ArgumentMatcher;
import tech.hiddenproject.aide.reflection.matcher.ArgumentMatcherHolder;

/**
 * Holder for wrapper function and wrapped {@link Executable}.
 *
 * @param <W> Wrapper interface type
 * @param <C> Caller object type (Void for static methods)
 * @param <R> Wrapped {@link Executable} return type
 * @author Danila Rassokhin
 */
public class MethodHolder<W, C, R> {

  private final WrapperHolder<W> wrapper;

  private final Executable method;

  public MethodHolder(WrapperHolder<W> wrapper, Executable method) {
    this.wrapper = wrapper;
    this.method = method;
  }

  /**
   * Invokes wrapped method.
   *
   * @param caller          Object to call method from
   * @param argumentMatcher {@link ArgumentMatcher} to match provided args with wrapper args.
   * @param args            Arguments to pass in wrapper
   * @return Invocation result
   */
  public R invoke(C caller, ArgumentMatcher<W, Object[], R> argumentMatcher, Object... args) {
    List<Object> listArgs = Arrays.stream(args)
        .collect(Collectors.toList());
    listArgs.add(0, caller);
    return argumentMatcher.apply(wrapper, method, listArgs.toArray(new Object[]{}));
  }

  /**
   * Invokes wrapped method. Uses default {@link ArgumentMatcher} to match provided args with See
   * {@link ArgumentMatcherHolder#apply(WrapperHolder, Executable, Object[])}
   *
   * @param caller Object to call method from
   * @param args   Arguments to pass in wrapper
   * @return Invocation result
   */
  public R invoke(C caller, Object... args) {
    return invoke(caller, ArgumentMatcherHolder.INSTANCE::apply, args);
  }

  /**
   * Invokes wrapped method without caller object. May be used to invoke static methods and
   * constructors. Uses default {@link ArgumentMatcher} to match provided args with wrapper args.
   * See {@link ArgumentMatcherHolder#apply(WrapperHolder, Executable, Object[])}
   *
   * @param args Arguments to pass in wrapper
   * @return Invocation result
   */
  public R invokeStatic(Object... args) {
    return ArgumentMatcherHolder.INSTANCE.apply(wrapper, method, args);
  }

  /**
   * Invokes wrapped method without caller object. May be used to invoke static methods and
   * constructors. Uses default {@link ArgumentMatcher} to match provided args with wrapper args.
   * See {@link ArgumentMatcherHolder#apply(WrapperHolder, Executable, Object[])}
   *
   * @param argumentMatcher argumentMatcher {@link ArgumentMatcher} to match provided args with
   *                        wrapper args.
   * @param args            Arguments to pass in wrapper
   * @return Invocation result
   */
  public R invokeStatic(ArgumentMatcher<W, Object[], R> argumentMatcher, Object... args) {
    return argumentMatcher.apply(wrapper, method, args);
  }
}
