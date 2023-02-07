package tech.hiddenproject.aide.reflection.matcher;

import tech.hiddenproject.aide.optional.Action;
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.aide.reflection.LambdaWrapper;
import tech.hiddenproject.aide.reflection.LambdaWrapper.Factory;
import tech.hiddenproject.aide.reflection.WrapperHolder;
import tech.hiddenproject.aide.reflection.exception.ReflectionException;
import tech.hiddenproject.aide.reflection.signature.MatcherSignature;
import tech.hiddenproject.aide.reflection.signature.MethodSignature;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores all {@link ArgumentMatcher}s for wrappers.
 *
 * @author Danila Rassokhin
 */
public enum ArgumentMatcherHolder {

  INSTANCE;

  private final Map<MatcherSignature, ArgumentMatcher<Object, Object[], ?>> argumentMatchers
      = new HashMap<>();

  {
    /*
     * Void wrappers
     */
    addMatcher(Factory.INVOKE,
               (holder, original, args) -> fromVoid(() -> holder.getWrapper().invoke())
    );
    addMatcher(Factory.ACTION,
               (holder, original, args) -> fromVoid(() -> holder.getWrapper().action(args[0]))
    );
    addMatcher(Factory.SETTER,
               (holder, original, args) -> fromVoid(() -> holder.getWrapper().set(args[0], args[1]))
    );
    addMatcher(Factory.CONSUMER_ARGS_2, (holder, original, args) -> fromVoid(
        () -> holder.getWrapper().accept(args[0], args[1], args[2])));
    addMatcher(Factory.CONSUMER_ARGS_3, (holder, original, args) -> fromVoid(
        () -> holder.getWrapper().accept(args[0], args[1], args[2], args[3])));
    addMatcher(Factory.CONSUMER_ARGS_4, (holder, original, args) -> fromVoid(
        () -> holder.getWrapper().accept(args[0], args[1], args[2], args[3], args[4])));
    addMatcher(Factory.CONSUMER_ARGS_5, (holder, original, args) -> fromVoid(
        () -> holder.getWrapper().accept(args[0], args[1], args[2], args[3], args[4], args[5])));
    addMatcher(Factory.CONSUMER_ARGS_6, (holder, original, args) -> fromVoid(
        () -> holder.getWrapper()
                    .accept(args[0], args[1], args[2], args[3], args[4], args[5], args[6])));

    /*
     * Object wrappers
     */
    addMatcher(Factory.CONSTRUCT, (holder, original, args) -> holder.getWrapper().construct());
    addMatcher(Factory.GETTER, (holder, original, args) -> holder.getWrapper().get(args[0]));
    addMatcher(Factory.FUNCTION_ARGS_1,
               (holder, original, args) -> holder.getWrapper().apply(args[0], args[1])
    );
    addMatcher(Factory.FUNCTION_ARGS_2,
               (holder, original, args) -> holder.getWrapper().apply(args[0], args[1], args[2])
    );
    addMatcher(Factory.FUNCTION_ARGS_3, (holder, original, args) -> holder.getWrapper()
                                                                          .apply(args[0], args[1],
                                                                                 args[2], args[3]
                                                                          ));
    addMatcher(Factory.FUNCTION_ARGS_4, (holder, original, args) -> holder.getWrapper()
                                                                          .apply(args[0], args[1],
                                                                                 args[2], args[3],
                                                                                 args[4]
                                                                          ));
    addMatcher(Factory.FUNCTION_ARGS_5, (holder, original, args) -> holder.getWrapper()
                                                                          .apply(args[0], args[1],
                                                                                 args[2], args[3],
                                                                                 args[4], args[5]
                                                                          ));
    addMatcher(Factory.FUNCTION_ARGS_6, (holder, original, args) -> holder.getWrapper()
                                                                          .apply(args[0], args[1],
                                                                                 args[2], args[3],
                                                                                 args[4], args[5],
                                                                                 args[6]
                                                                          ));
  }

  /**
   * Adds new matcher for given {@link MethodSignature}.
   *
   * @param matcherSignature {@link MatcherSignature}
   * @param argumentMatcher  {@link ArgumentMatcher}
   */
  public <W> void addMatcher(MatcherSignature<W> matcherSignature,
                             ArgumentMatcher<W, Object[], ?> argumentMatcher) {
    argumentMatchers.put(matcherSignature, (ArgumentMatcher<Object, Object[], ?>) argumentMatcher);
  }

  /**
   * Checks if there is {@link ArgumentMatcher} exists for given signature.
   *
   * @param target {@link MatcherSignature}
   * @return true if {@link ArgumentMatcher} exists
   */
  public boolean hasMatcher(MatcherSignature target) {
    return argumentMatchers.containsKey(target);
  }

  /**
   * Calls {@link ArgumentMatcher} for {@link MethodSignature} of given {@link Executable}. If
   * method requires caller object (non-static functions e.g.) it must be passed as first argument.
   *
   * @param holder   Wrapper interface
   * @param original Original {@link Executable}
   * @param args     Arguments to pass in wrapper
   * @param <T>      Return type
   * @return Wrapped function result or null if void
   */
  public <T> T apply(WrapperHolder holder, Executable original, Object[] args) {
    MethodSignature signature = MethodSignature.from(original);
    MatcherSignature matcherSignature = new MatcherSignature(holder.getDeclaringInterface(),
                                                             signature
    );
    BooleanOptional.of(argumentMatchers.containsKey(matcherSignature)).ifFalseThrow(
        () -> ReflectionException.format(
            "No matchers found for %s!" + "See ArgumentMatcherHolder#addMatcher", original));
    return (T) argumentMatchers.get(matcherSignature).apply(holder, original, args);
  }

  private void addMatcher(Method method, ArgumentMatcher<LambdaWrapper, Object[], ?> matcher) {
    MethodSignature methodSignature = MethodSignature.fromWrapper(method);
    MatcherSignature matcherSignature = new MatcherSignature(LambdaWrapper.class, methodSignature);
    argumentMatchers.put(matcherSignature, (ArgumentMatcher) matcher);
  }

  private Object fromVoid(Action action) {
    action.make();
    return null;
  }
}
