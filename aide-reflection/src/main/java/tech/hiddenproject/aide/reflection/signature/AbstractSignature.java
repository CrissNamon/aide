package tech.hiddenproject.aide.reflection;

/**
 * @author Danila Rassokhin
 */
public interface AbstractSignature {

  Class<?> getReturnType();

  int getParameterCount();

  Class<?>[] getParameterTypes();

}
