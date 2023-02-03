package tech.hiddenproject.aide.optional;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implementation of {@link GenericOptional} for {@link Boolean} type.
 *
 * @author Danila Rassokhin
 */
public class BooleanOptional implements GenericOptional<Boolean> {

  private final Boolean value;

  private BooleanOptional(Boolean value) {
    this.value = value;
  }

  public static BooleanOptional of(Boolean value) {
    Objects.requireNonNull(value);
    return new BooleanOptional(value);
  }

  /**
   * If value is true then executes consumer.
   *
   * @param consumer {@link Consumer}
   */
  public void ifTrueThen(Consumer<Boolean> consumer) {
    if (value) {
      consumer.accept(value);
    }
  }

  /**
   * If value is true then executes action.
   *
   * @param action {@link Action}
   */
  public void ifTrueThen(Action action) {
    if (value) {
      action.make();
    }
  }

  /**
   * If value is false then executes consumer.
   *
   * @param consumer {@link Consumer}
   */
  public void ifFalseThen(Consumer<Boolean> consumer) {
    if (!value) {
      consumer.accept(value);
    }
  }

  /**
   * If value is false then executes action.
   *
   * @param action {@link Action}
   */
  public void ifFalseThen(Action action) {
    if (!value) {
      action.make();
    }
  }

  /**
   * If value is true then throws exception.
   *
   * @param throwableSupplier {@link Supplier} for {@link Throwable}
   * @param <X>               {@link Throwable} type
   * @throws X if value is true
   */
  public <X extends Throwable> void ifTrueThrow(Supplier<X> throwableSupplier) throws X {
    if (value) {
      throw throwableSupplier.get();
    }
  }

  /**
   * If value is false then throws exception.
   *
   * @param throwableSupplier {@link Supplier} for {@link Throwable}
   * @param <X>               {@link Throwable} type
   * @throws X if false is true
   */
  public <X extends Throwable> void ifFalseThrow(Supplier<X> throwableSupplier) throws X {
    if (!value) {
      throw throwableSupplier.get();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean get() {
    return value;
  }
}
