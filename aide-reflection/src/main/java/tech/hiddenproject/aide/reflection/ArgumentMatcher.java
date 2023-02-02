package tech.hiddenproject.aide.reflection;

/**
 * @author Danila Rassokhin
 */
public interface ArgumentMatcher<F, S, T, R> {

  R apply(F holder, S caller, T args);

}
