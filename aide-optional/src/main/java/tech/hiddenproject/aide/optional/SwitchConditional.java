package tech.hiddenproject.aide.optional;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Functional form of standard switch-case. Will return result or execute action of first
 * success branch.
 *
 * @param <I> Object type
 * @param <R> Return type
 * @author Danila Rassokhin
 */
public class SwitchConditional<I, R> {

  private final Map<Predicate<I>, SwitchBranch<R>> conditions = new LinkedHashMap<>();

  private final I obj;

  private SwitchConditional(I obj) {
    this.obj = obj;
  }


  /**
   * Creates new switch conditional for given object.
   *
   * @param obj Object
   * @param <I> Object type
   * @param <R> Return type
   * @return {@link SwitchConditional}
   */
  public static <I, R> SwitchConditional<I, R> on(I obj) {
    return new SwitchConditional<>(obj);
  }

  /**
   * Creates new {@link ConditionalThen}.
   *
   * @param condition Predicate to check
   * @return {@link ConditionalThen}
   */
  public ConditionalThen<I, R> caseOn(Predicate<I> condition, boolean isBreak) {
    return new ConditionalThen<>(this, condition, isBreak);
  }

  /**
   * Creates new {@link IfTrueConditional.ConditionalThen}.
   *
   * @param condition Predicate to check
   * @return {@link IfTrueConditional.ConditionalThen}
   */
  public ConditionalThen<I, R> caseOn(Predicate<I> condition) {
    return caseOn(condition, true);
  }

  /**
   * Sets default value for conditional.
   *
   * @param defaultValue Value to use if all branches fail.
   * @return Result of conditional
   */
  public R orElse(R defaultValue) {
    return orElseGet(() -> defaultValue);
  }

  /**
   * Sets default action for conditional.
   *
   * @param action {@link Action}
   */
  public void orElseDo(Action action) {
    orElseGet(() -> {
      action.make();
      return null;
    });
  }

  /**
   * Sets default value for conditional as {@link Supplier}
   *
   * @param defaultValue Value to use if all branches failed.
   * @return Result of conditional
   */
  public R orElseGet(Supplier<R> defaultValue) {
    Optional<Entry<Predicate<I>, SwitchBranch<R>>> successfulBranch = get();
    if (!successfulBranch.isPresent()) {
      return defaultValue.get();
    }
    R value = successfulBranch.get().getValue().getSupplier().get();
    if (!successfulBranch.get().getValue().isBreak()) {
      Iterator<Entry<Predicate<I>, SwitchBranch<R>>> iterator = conditions.entrySet().iterator();
      while (iterator.hasNext() && !iterator.next().equals(successfulBranch.get()));
      iterator.forEachRemaining(
          predicate -> predicate.getValue().getSupplier().get());
    }
    if (!successfulBranch.get().getValue().isBreak()) {
      defaultValue.get();
    }
    return value;
  }

  /**
   * Sets {@link Throwable} {@link Supplier} to throw exception if all branches failed.
   *
   * @param exceptionSupplier {@link Supplier} for {@link Throwable}
   * @param <X>               {@link Throwable} type
   * @return Result of conditional
   * @throws X if all branches failed
   */
  public <X extends Throwable> R orElseThrows(Supplier<? extends X> exceptionSupplier) throws X {
    Optional<Entry<Predicate<I>, SwitchBranch<R>>> successfulBranch = get();
    if (!successfulBranch.isPresent()) {
      throw exceptionSupplier.get();
    }
    return orElseGet(() -> null);
  }

  private Optional<Entry<Predicate<I>, SwitchBranch<R>>> get() {
    return conditions.entrySet()
        .stream().filter(entry -> entry.getKey().test(obj)).findFirst();
  }

  private SwitchConditional<I, R> add(Predicate<I> predicate, SwitchBranch<R> supplier) {
    conditions.put(predicate, supplier);
    return this;
  }

  public static class ConditionalThen<I, R> {

    private final SwitchConditional<I, R> root;

    private final Predicate<I> predicate;

    private final boolean isBreak;

    private ConditionalThen(SwitchConditional<I, R> root, Predicate<I> predicate, boolean isBreak) {
      this.root = root;
      this.predicate = predicate;
      this.isBreak = isBreak;
    }

    /**
     * Adds value to return if this branch will success.
     *
     * @param supplier Value to return
     * @return {@link SwitchConditional}
     */
    public SwitchConditional<I, R> thenGet(R supplier) {
      return root.add(predicate, new SwitchBranch<>(() -> supplier, isBreak));
    }

    /**
     * Adds value to return in {@link Supplier} form if this branch will success.
     *
     * @param supplier Value to return
     * @return {@link SwitchConditional}
     */
    public SwitchConditional<I, R> thenGet(Supplier<R> supplier) {
      return root.add(predicate, new SwitchBranch<>(supplier, isBreak));
    }

    /**
     * Adds {@link Action} to execute if this branch will success.
     *
     * @param action {@link Action}
     * @return {@link SwitchConditional}
     */
    public SwitchConditional<I, R> thenDo(Action action) {
      Supplier<R> supplier = () -> {
        action.make();
        return null;
      };
      return root.add(predicate, new SwitchBranch<>(supplier, isBreak));
    }
  }

  private static class SwitchBranch<R> {

    private final Supplier<R> supplier;
    private final boolean isBreak;

    public SwitchBranch(Supplier<R> supplier, boolean isBreak) {
      this.supplier = supplier;
      this.isBreak = isBreak;
    }

    public Supplier<R> getSupplier() {
      return supplier;
    }

    public boolean isBreak() {
      return isBreak;
    }
  }
}
