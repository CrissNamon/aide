package tech.hiddenproject.aide.optional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Makes actions based on given conditions. Will execute action on first success branch.
 *
 * @author Danila Rassokhin
 */
public class WhenConditional {

  private final Map<BooleanAction, Action> conditions = new LinkedHashMap<>();

  /**
   * @return {@link WhenConditional}
   */
  public static WhenConditional create() {
    return new WhenConditional();
  }

  /**
   * Adds new conditional branch.
   *
   * @param on        Object to check predicate on
   * @param condition Predicate to check
   * @param <B>       Object type
   * @return {@link ConditionalThen}
   */
  public <B> ConditionalThen when(B on, Predicate<B> condition) {
    BooleanAction booleanAction = () -> condition.test(on);
    return new ConditionalThen(this, booleanAction);
  }

  /**
   * Adds new conditional branch.
   *
   * @param condition Predicate to check
   * @return {@link ConditionalThen}
   */
  public ConditionalThen when(BooleanAction condition) {
    return new ConditionalThen(this, condition);
  }

  /**
   * Adds new conditional branch.
   *
   * @param condition Predicate to check
   * @return {@link ConditionalThen}
   */
  public ConditionalThen when(boolean condition) {
    return new ConditionalThen(this, () -> condition);
  }

  /**
   * @param action {@link Action} to execute if all other branches will fail.
   */
  public void orElseDo(Action action) {
    Action value = get().orElse(action);
    value.make();
  }

  /**
   * @param action {@link Action} to execute anyway.
   */
  public void orFinally(Action action) {
    Action value = get().orElse(() -> {});
    value.make();
    action.make();
  }

  /**
   * Does nothing if no branches' success.
   */
  public void orDoNothing() {
    Action value = get().orElse(() -> {});
    value.make();
  }

  /**
   * Adds new exception supplier to throw if all branches will fail.
   *
   * @param exceptionSupplier {@link Supplier} for {@link Throwable}
   * @param <X>               {@link Throwable} type
   * @throws X if all branches will fail
   */
  public <X extends Throwable> void orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    get().orElseThrow(exceptionSupplier);
  }

  private Optional<Action> get() {
    return conditions.entrySet().stream().filter(entry -> entry.getKey().test())
        .map(Entry::getValue)
        .findFirst();
  }

  private WhenConditional add(Action action, BooleanAction booleanAction) {
    conditions.put(booleanAction, action);
    return this;
  }

  /**
   * Represents `then` part of branch.
   */
  public static class ConditionalThen {

    private final WhenConditional root;

    private final BooleanAction predicate;

    private ConditionalThen(WhenConditional root, BooleanAction predicate) {
      this.root = root;
      this.predicate = predicate;
    }

    /**
     * Adds {@link Action} to execute if this branch will success.
     *
     * @param action {@link Action} to execute
     * @return {@link WhenConditional}
     */
    public WhenConditional then(Action action) {
      return root.add(action, predicate);
    }
  }
}

