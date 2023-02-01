package tech.hiddenproject.progressive.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Processes annotations.
 */
public final class ComponentAnnotationProcessor {

  /**
   * Checks if class annotated with annotation.
   *
   * @param an Annotation to check
   * @param c  Class to check
   * @return true if class is annotated with given annotation
   */
  public static boolean isAnnotationPresent(Class<? extends Annotation> an, Class<?> c) {
    return findAnnotation(c, an) != null;
  }

  /**
   * Searches for annotation on class.
   *
   * @param clazz          Class to search in
   * @param annotationType Annotation class to search
   * @param <A>            Annotation to search
   * @return Annotation or null
   */
  @SuppressWarnings("unchecked")
  public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
    if (clazz.equals(Target.class)
        || clazz.equals(Documented.class)
        || clazz.equals(Retention.class)
        || clazz.equals(Inherited.class)
        || clazz.equals(Deprecated.class)) {
      return null;
    }
    Annotation[] anns = clazz.getDeclaredAnnotations();
    for (Annotation ann : anns) {
      if (ann.annotationType() == annotationType) {
        return (A) ann;
      }
    }
    for (Annotation ann : anns) {
      A annotation = findAnnotation(ann.annotationType(), annotationType);
      if (annotation != null) {
        return annotation;
      }
    }
    for (Class<?> ifc : clazz.getInterfaces()) {
      A annotation = findAnnotation(ifc, annotationType);
      if (annotation != null) {
        return annotation;
      }
    }
    Class<?> superclass = clazz.getSuperclass();
    if (superclass == null || Object.class == superclass) {
      return null;
    }
    return findAnnotation(superclass, annotationType);
  }
}
