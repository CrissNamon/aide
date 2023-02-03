package tech.hiddenproject.aide.reflection;

/**
 * Holds wrapper interface.
 *
 * @param <W> Interface type
 * @author Danila Rassokhin
 */
public class WrapperHolder<W> {

  private final W wrapper;

  private final Class<?> declaringInterface;

  public WrapperHolder(W wrapper, Class<?> declaringInterface) {
    this.wrapper = wrapper;
    this.declaringInterface = declaringInterface;
  }

  public W getWrapper() {
    return wrapper;
  }

  public Class<?> getDeclaringInterface() {
    return declaringInterface;
  }

  @Override
  public String toString() {
    return "WrapperHolder{" +
        "wrapper=" + wrapper +
        ", declaringInterface=" + declaringInterface +
        '}';
  }
}
