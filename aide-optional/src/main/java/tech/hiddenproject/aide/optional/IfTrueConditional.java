package tech.hiddenproject.hic.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Danila Rassokhin
 */
public class IfTrueConditional<T> {

  private final Map<UnaryPredicate, Supplier<T>> conditions = new LinkedHashMap<>();

  public static <D> IfTrueConditional<D> create() {
    return new IfTrueConditional<>();
  }

  public <B> ConditionalThen<T> ifTrue(B on, Predicate<B> condition) {
    UnaryPredicate unaryPredicate = () -> condition.test(on);
    return new ConditionalThen<>(this, unaryPredicate);
  }

  public ConditionalThen<T> ifTrue(UnaryPredicate condition) {
    return new ConditionalThen<>(this, condition);
  }

  public ConditionalThen<T> ifTrue(boolean condition) {
    return new ConditionalThen<>(this, () -> condition);
  }

  public <D> D orElse(D defaultValue) {
    return get().map(value -> (D) value).orElse(defaultValue);
  }

  public <D> D orElseGet(Supplier<D> defaultValue) {
    return get().map(value -> (D) value).orElseGet(defaultValue);
  }

  public <X extends Throwable, D> D orElseThrows(Supplier<? extends X> exceptionSupplier) throws X {
    return get().map(value -> (D) value).orElseThrow(exceptionSupplier);
  }

  private Optional<T> get() {
    return conditions.entrySet().stream().filter(entry -> entry.getKey().test())
        .map(entry -> entry.getValue().get())
        .findFirst();
  }

  private IfTrueConditional<T> add(Supplier<T> supplier, UnaryPredicate unaryPredicate) {
    conditions.put(unaryPredicate, supplier);
    return this;
  }

  public interface UnaryPredicate {

    boolean test();
  }

  public static class ConditionalThen<T> {

    private final IfTrueConditional<T> root;

    private final UnaryPredicate predicate;

    public ConditionalThen(IfTrueConditional<T> root, UnaryPredicate predicate) {
      this.root = root;
      this.predicate = predicate;
    }

    public IfTrueConditional<T> then(T supplier) {
      return root.add(() -> supplier, predicate);
    }

    public IfTrueConditional<T> then(Supplier<T> supplier) {
      return root.add(supplier, predicate);
    }
  }

}

