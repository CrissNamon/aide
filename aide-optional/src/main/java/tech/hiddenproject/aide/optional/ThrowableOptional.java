package tech.hiddenproject.aide.optional;

import java.util.function.Function;

/**
 * Contains methods to catch exceptions.
 *
 * @author Danila Rassokhin
 */
public class ThrowableOptional {

  /**
   * Executes {@link SneakyAction} and catches any Throwable, then converts Throwable to RuntimeException
   * and throws it. Can be used to work with checked exceptions in lambdas.
   *
   * @param action {@link SneakyAction}
   */
  public static void sneaky(SneakyAction action) {
    try {
      action.make();
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  /**
   * Executes {@link SneakyAction}. Catches any Throwable if it will be thrown, then converts Throwable to
   * RuntimeException with given mapper function and throws it. Can be used to work with checked
   * exceptions in lambdas.
   *
   * @param action {@link SneakyAction}
   * @param mapper Mapper function to convert {@link Throwable} to {@link RuntimeException}
   */
  public static void sneaky(SneakyAction action, Function<Throwable, ? extends RuntimeException> mapper) {
    try {
      action.make();
    } catch (Throwable throwable) {
      throw mapper.apply(throwable);
    }
  }

  /**
   * Executes {@link java.util.function.Supplier} and returns result. Catches any Throwable if it
   * will be thrown, then converts Throwable to RuntimeException and throws it. Can be used to work
   * with checked exceptions in lambdas.
   *
   * @param supplier {@link java.util.function.Supplier}
   */
  public static <T> T sneaky(SneakySupplier<T> supplier) {
    try {
      return supplier.get();
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  /**
   * Executes {@link java.util.function.Supplier} and returns result. Catches any Throwable if it
   * will be thrown, then converts Throwable to RuntimeException with given mapper function and
   * throws it. Can be used to work with checked exceptions in lambdas.
   *
   * @param supplier {@link java.util.function.Supplier}
   * @param mapper   Mapper function to convert {@link Throwable} to {@link RuntimeException}
   */
  public static <T> T sneaky(SneakySupplier<T> supplier,
                             Function<Throwable, ? extends RuntimeException> mapper) {
    try {
      return supplier.get();
    } catch (Throwable throwable) {
      throw mapper.apply(throwable);
    }
  }
}
