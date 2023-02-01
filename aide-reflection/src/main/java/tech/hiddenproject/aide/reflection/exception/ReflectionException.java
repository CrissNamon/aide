package tech.hiddenproject.aide.reflection.exception;

/**
 * @author Danila Rassokhin
 */
public class ReflectionException extends RuntimeException {

  public ReflectionException(String message) {
    super(message);
  }

  public ReflectionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReflectionException(Throwable cause) {
    super(cause);
  }

  public static ReflectionException format(String msg, Object... args) {
    return new ReflectionException(String.format(msg, args));
  }
}
