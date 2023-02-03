package tech.hiddenproject.aide.reflection.signature;

/**
 * Represents method signature.
 *
 * @author Danila Rassokhin
 */
public interface AbstractSignature {

  /**
   * @return Return type of method
   */
  Class<?> getReturnType();

  /**
   * @return Parameters count of method
   */
  int getParameterCount();

  /**
   * @return Parameter types of method
   */
  Class<?>[] getParameterTypes();

  /**
   * @return Method's declaring class
   */
  Class<?> getDeclaringClass();

}
