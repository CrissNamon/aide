package tech.hiddenproject.aide.reflection.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.aide.reflection.exception.ReflectionException;

/**
 * Useful methods to work with reflection.
 *
 * @author Danila Rassokhin
 */
public class ReflectionUtil {

  /**
   * Searches for {@link Method}.
   *
   * @param c        Class to search in
   * @param name     {@link Method} name
   * @param argTypes {@link Method} parameter types
   * @return {@link Method}
   * @throws ReflectionException if no method found
   */
  public static Method getMethod(Class<?> c, String name, Class<?>... argTypes) {
    return ThrowableOptional.sneaky(() -> c.getDeclaredMethod(name, argTypes));
  }

  /**
   * Searches for {@link Method}.
   *
   * @param c          Class to search in
   * @param name       {@link Method} name
   * @param varAgsType Type of arguments
   * @param argCount   Count of same arguments in signature
   * @return {@link Method}
   * @throws ReflectionException if no method found or argCount < 0
   */
  public static Method getMethod(Class<?> c, String name, Class<?> varAgsType, int argCount)
      throws ReflectionException {
    if (argCount < 0) {
      throw ReflectionException.format("Arguments count must be greater than zero!");
    }
    Class<?>[] argTypes = new Class[argCount];
    Arrays.fill(argTypes, varAgsType);
    return getMethod(c, name, argTypes);
  }

  /**
   * Searches for {@link Constructor}.
   *
   * @param c        Class to search in
   * @param argTypes Types of arguments
   * @param <T>      Class type
   * @return {@link Constructor}
   */
  public static <T> Constructor<T> getConstructor(Class<T> c, Class<?>... argTypes) {
    return ThrowableOptional.sneaky(() -> c.getDeclaredConstructor(argTypes));
  }

  /**
   * Gets types of given arguments.
   *
   * @param args Arguments
   * @return Array of {@link Class}
   */
  public static Class<?>[] getVarArgTypes(Object... args) {
    return Arrays.stream(args)
        .map(Object::getClass)
        .collect(Collectors.toList())
        .toArray(new Class[]{});
  }
}
