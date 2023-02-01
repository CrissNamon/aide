package tech.hiddenproject.jut;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import tech.hiddenproject.jut.IfTrueConditional.UnaryPredicate;

/**
 * @author Danila Rassokhin
 */
public class WhenConditional<T> {

  private final Map<UnaryPredicate, Action> conditions = new LinkedHashMap<>();

  public <B> ConditionalThen<T> when(B on, Predicate<B> condition) {
    UnaryPredicate unaryPredicate = () -> condition.test(on);
    return new ConditionalThen<>(this, unaryPredicate);
  }

  public ConditionalThen<T> when(UnaryPredicate condition) {
    return new ConditionalThen<>(this, condition);
  }

  public ConditionalThen<T> when(boolean condition) {
    return new ConditionalThen<>(this, () -> condition);
  }

  public void orElseDo(Action action) {
    Action value = get().orElseGet(() -> action);
    value.make();
  }

  public void orFinally(Action action) {
    Action value = get().orElse(() -> {});
    value.make();
    action.make();
  }

  public <X extends Throwable, D> D orElseThrows(Supplier<? extends X> exceptionSupplier) throws X {
    return get().map(value -> (D) value).orElseThrow(exceptionSupplier);
  }

  private Optional<Action> get() {
    return conditions.entrySet().stream().filter(entry -> entry.getKey().test())
        .map(Entry::getValue)
        .findFirst();
  }

  private WhenConditional<T> add(Action action, UnaryPredicate unaryPredicate) {
    conditions.put(unaryPredicate, action);
    return this;
  }

  public static class ConditionalThen<T> {

    private final WhenConditional<T> root;

    private final UnaryPredicate predicate;

    public ConditionalThen(WhenConditional<T> root, UnaryPredicate predicate) {
      this.root = root;
      this.predicate = predicate;
    }

    public WhenConditional<T> then(Action action) {
      return root.add(action, predicate);
    }
  }

  public interface Action {
    void make();
  }

}

