package tech.hiddenproject.aide.optional;

/**
 * Version of {@link java.util.function.Supplier} with {@link Throwable}.
 *
 * @author Danila Rassokhin
 */
public interface SneakySupplier<T> {

  T get() throws Throwable;

}
