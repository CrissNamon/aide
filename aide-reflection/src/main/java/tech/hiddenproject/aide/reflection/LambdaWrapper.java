package tech.hiddenproject.aide.reflection;

import java.lang.reflect.Method;

/**
 * @author Danila Rassokhin
 */
public interface AnyFunction {

  class AnyFunctionStatic {

    public static final Method GETTER = ReflectionUtil.getMethod(AnyFunction.class, "get",
                                                                        Object.class);
    public static final Method SETTER =
        ReflectionUtil.getMethod(AnyFunction.class, "set", Object.class, 2);
    public static final Method CONSUMER_ARGS_2 =
        ReflectionUtil.getMethod(AnyFunction.class, "accept", Object.class, 3);
    public static final Method FUNCTION_ARGS_1 = ReflectionUtil.getMethod(AnyFunction.class, "apply",
                                                                          Object.class, 2);

    public static Method[] get() {
      return new Method[]{
          AnyFunctionStatic.GETTER, AnyFunctionStatic.SETTER,
          AnyFunctionStatic.CONSUMER_ARGS_2, AnyFunctionStatic.FUNCTION_ARGS_1
      };
    }
  }

  @Invoker
  <T> T get(Object caller);
  
  @Invoker
  void set(Object caller, Object arg0);

  @Invoker
  void accept(Object caller, Object arg0, Object arg1);

  @Invoker
  void accept(Object caller, Object arg0, Object arg1, Object arg2);

  @Invoker
  void accept(Object caller, Object arg0, Object arg1, Object arg2, Object arg3);

  @Invoker
  void accept(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4);

  @Invoker
  void accept(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
              Object arg5);

  @Invoker
  void accept(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
              Object arg5, Object arg6);

  @Invoker
  <T> T apply(Object caller, Object arg0);

  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1);

  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1, Object arg2);

  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1, Object arg2, Object arg3);

  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4);

  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
              Object arg5);

  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
              Object arg5, Object arg6);

}
