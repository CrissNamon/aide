package tech.hiddenproject.progressive;

import java.util.function.Supplier;

/**
 * @author Danila Rassokhin
 */
public class BooleanOptional {

  private final boolean value;

  private BooleanOptional(boolean value) {
    this.value = value;
  }

  public static BooleanOptional of(boolean value) {
    return new BooleanOptional(value);
  }

  public <X extends Throwable> void ifTrueThrow(Supplier<X> throwableSupplier) throws X {
    if (value) {
      throw throwableSupplier.get();
    }
  }

  public <X extends Throwable> void ifFalseThrow(Supplier<X> throwableSupplier) throws X {
    if (!value) {
      throw throwableSupplier.get();
    }
  }
}
