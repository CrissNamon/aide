package tech.hiddenproject.aide.reflection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method will be used as wrapper function for reflective method calls with exactly same
 * signature. {@link tech.hiddenproject.aide.reflection.signature.ExactMethodSignature} will be
 * created for such invoker.
 *
 * @author Danila Rassokhin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExactInvoker {

}
