package tech.hiddenproject.aide.reflection;

/**
 * @author Danila Rassokhin
 */
public class MethodHolder<W, C, R> {

  private final W wrapper;


  public MethodHolder(W wrapper) {
    this.wrapper = wrapper;
  }

  public R invoke(C caller, ArgumentMatcher<W, C, Object[], R> argumentMatcher, Object... args) {
    return argumentMatcher.apply(wrapper, caller, args);
  }

  public R invoke(C caller, Object... args) {
    return invoke(caller, LambdaWrapper.SafeInvoker::apply, args);
  }

}
