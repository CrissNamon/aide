package tech.hiddenproject.aide.optional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Functional form of standard if-else. Will return result of first success branch.
 *
 * @author Danila Rassokhin
 */
public class IfTrueConditional<T> {

  private final Map<BooleanAction, Supplier<T>> conditions = new LinkedHashMap<>();

  /**
   * Creates new conditional.
   *
   * @param <D> Return type
   * @return {@link IfTrueConditional}
   */
  public static <D> IfTrueConditional<D> create() {
    return new IfTrueConditional<>();
  }

  /**
   * Creates new {@link ConditionalThen}.
   *
   * @param on        Object to check predicate on
   * @param condition Predicate to check
   * @param <B>       Object type
   * @return {@link ConditionalThen}
   */
  public <B> ConditionalThen<T> ifTrue(B on, Predicate<B> condition) {
    BooleanAction booleanAction = () -> condition.test(on);
    return new ConditionalThen<>(this, booleanAction);
  }

  /**
   * Creates new {@link ConditionalThen}.
   *
   * @param condition Predicate to check. {@link BooleanAction}
   * @return {@link ConditionalThen}
   */
  public ConditionalThen<T> ifTrue(BooleanAction condition) {
    return new ConditionalThen<>(this, condition);
  }

  /**
   * Creates new {@link ConditionalThen}.
   *
   * @param condition Predicate to check
   * @return {@link ConditionalThen}
   */
  public ConditionalThen<T> ifTrue(boolean condition) {
    return new ConditionalThen<>(this, () -> condition);
  }

  /**
   * Sets default value for conditional.
   *
   * @param defaultValue Value to use if all branches fail.
   * @param <D>          Return type
   * @return Result of conditional
   */
  public <D> D orElse(D defaultValue) {
    return get().map(value -> (D) value.getValue().get()).orElse(defaultValue);
  }

  /**
   * Sets default value for conditional as {@link Supplier}
   *
   * @param defaultValue Value to use if all branches failed.
   * @param <D>          Return type
   * @return Result of conditional
   */
  public <D> D orElseGet(Supplier<D> defaultValue) {
    return get().map(value -> (D) value.getValue().get()).orElseGet(defaultValue);
  }

  /**
   * Sets {@link Throwable} {@link Supplier} to throw exception if all branches failed.
   *
   * @param exceptionSupplier {@link Supplier} for {@link Throwable}
   * @param <X>               {@link Throwable} type
   * @param <D>               Return type
   * @return Result of conditional
   * @throws X if all branches failed
   */
  public <X extends Throwable, D> D orElseThrows(Supplier<? extends X> exceptionSupplier) throws X {
    return get().map(value -> (D) value.getValue().get()).orElseThrow(exceptionSupplier);
  }

  /**
   * @return Result of conditional as {@link Optional}
   */
  private Optional<Entry<BooleanAction, Supplier<T>>> get() {
    return conditions.entrySet().stream()
        .filter(entry -> entry.getKey().test())
        .findFirst();
  }

  private IfTrueConditional<T> add(Supplier<T> supplier, BooleanAction booleanAction) {
    conditions.put(booleanAction, supplier);
    return this;
  }

  /**
   * Represents `then` part of branch.
   */
  public static class ConditionalThen<T> {

    private final IfTrueConditional<T> root;

    private final BooleanAction predicate;

    private ConditionalThen(IfTrueConditional<T> root, BooleanAction predicate) {
      this.root = root;
      this.predicate = predicate;
    }

    /**
     * Adds value to return if this branch will success.
     *
     * @param supplier Value to return
     * @return {@link IfTrueConditional}
     */
    public IfTrueConditional<T> then(T supplier) {
      return root.add(() -> supplier, predicate);
    }

    /**
     * Adds value to return in {@link Supplier} form if this branch will success.
     *
     * @param supplier Value to return
     * @return {@link IfTrueConditional}
     */
    public IfTrueConditional<T> then(Supplier<T> supplier) {
      return root.add(supplier, predicate);
    }
  }
}

