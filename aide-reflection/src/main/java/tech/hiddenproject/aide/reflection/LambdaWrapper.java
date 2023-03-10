package tech.hiddenproject.aide.reflection;

import tech.hiddenproject.aide.reflection.annotation.Invoker;
import tech.hiddenproject.aide.reflection.util.ReflectionUtil;

import java.lang.reflect.Method;

/**
 * Basic wrapper functions for {@link LambdaWrapperHolder}. Will be loaded on
 * {@link LambdaWrapperHolder} initialization.
 *
 * @author Danila Rassokhin
 */
public interface LambdaWrapper {

  @Invoker
  <T> T construct();

  @Invoker
  void invoke();

  @Invoker
  void action(Object caller);

  /**
   * Wraps getter.
   *
   * @param caller Object to call method from
   * @param <T>    Getter return type
   * @return Invocation result
   */
  @Invoker
  <T> T get(Object caller);

  /**
   * Wraps setter.
   *
   * @param caller Object to call method from
   * @param arg0   Argument to pass in setter
   */
  @Invoker
  void set(Object caller, Object arg0);

  /**
   * Wraps consumer with 2 arguments.
   *
   * @param caller Object to call method from
   * @param arg0   Argument to pass in consumer
   * @param arg1   Argument to pass in consumer
   */
  @Invoker
  void accept(Object caller, Object arg0, Object arg1);

  /**
   * Wraps consumer with 3 arguments. See {@link #accept(Object, Object, Object)}
   */
  @Invoker
  void accept(Object caller, Object arg0, Object arg1, Object arg2);

  /**
   * Wraps consumer with 4 arguments. See {@link #accept(Object, Object, Object)}
   */
  @Invoker
  void accept(Object caller, Object arg0, Object arg1, Object arg2, Object arg3);

  /**
   * Wraps consumer with 5 arguments. See {@link #accept(Object, Object, Object)}
   */
  @Invoker
  void accept(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4);

  /**
   * Wraps consumer with 6 arguments. See {@link #accept(Object, Object, Object)}
   */
  @Invoker
  void accept(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
              Object arg5);

  /**
   * Wraps function with 1 argument.
   *
   * @param caller Object to invoke method from
   * @param arg0   Argument to mass in method
   * @return Invocation result
   */
  @Invoker
  <T> T apply(Object caller, Object arg0);

  /**
   * Wraps function with 2 arguments. See {@link #apply(Object, Object)}.
   *
   * @return Invocation result
   */
  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1);

  /**
   * Wraps function with 3 arguments. See {@link #apply(Object, Object)}.
   *
   * @return Invocation result
   */
  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1, Object arg2);

  /**
   * Wraps function with 4 arguments. See {@link #apply(Object, Object)}.
   *
   * @return Invocation result
   */
  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1, Object arg2, Object arg3);

  /**
   * Wraps function with 5 arguments. See {@link #apply(Object, Object)}.
   *
   * @return Invocation result
   */
  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4);

  /**
   * Wraps function with 6 arguments. See {@link #apply(Object, Object)}.
   *
   * @return Invocation result
   */
  @Invoker
  <T> T apply(Object caller, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4,
              Object arg5);

  /**
   * Factory for {@link LambdaWrapper} to prepare all methods. Created for optimization.
   */
  class Factory {

    private static final String GETTER_NAME = "get";
    public static final Method GETTER = ReflectionUtil.getMethod(LambdaWrapper.class, GETTER_NAME,
                                                                 Object.class
    );
    private static final String SETTER_NAME = "set";
    public static final Method SETTER = ReflectionUtil.getMethod(LambdaWrapper.class, SETTER_NAME,
                                                                 Object.class, 2
    );
    public static final Method CONSUMER_ARGS_1 = SETTER;
    private static final String CONSUMER_NAME = "accept";
    public static final Method CONSUMER_ARGS_2 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          CONSUMER_NAME,
                                                                          Object.class, 3
    );
    public static final Method CONSUMER_ARGS_3 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          CONSUMER_NAME,
                                                                          Object.class, 4
    );
    public static final Method CONSUMER_ARGS_4 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          CONSUMER_NAME,
                                                                          Object.class, 5
    );
    public static final Method CONSUMER_ARGS_5 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          CONSUMER_NAME,
                                                                          Object.class, 6
    );
    public static final Method CONSUMER_ARGS_6 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          CONSUMER_NAME,
                                                                          Object.class, 7
    );
    private static final String FUNCTION_NAME = "apply";
    public static final Method FUNCTION_ARGS_1 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          FUNCTION_NAME,
                                                                          Object.class, 2
    );
    public static final Method FUNCTION_ARGS_2 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          FUNCTION_NAME,
                                                                          Object.class, 3
    );
    public static final Method FUNCTION_ARGS_3 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          FUNCTION_NAME,
                                                                          Object.class, 4
    );
    public static final Method FUNCTION_ARGS_4 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          FUNCTION_NAME,
                                                                          Object.class, 5
    );
    public static final Method FUNCTION_ARGS_5 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          FUNCTION_NAME,
                                                                          Object.class, 6
    );
    public static final Method FUNCTION_ARGS_6 = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                          FUNCTION_NAME,
                                                                          Object.class, 7
    );
    private static final String ACTION_NAME = "action";
    public static final Method ACTION = ReflectionUtil.getMethod(LambdaWrapper.class, ACTION_NAME,
                                                                 Object.class
    );
    private static final String CONSTRUCT_NAME = "construct";
    public static final Method CONSTRUCT = ReflectionUtil.getMethod(LambdaWrapper.class,
                                                                    CONSTRUCT_NAME
    );
    private static final String INVOKE_NAME = "invoke";
    public static final Method INVOKE = ReflectionUtil.getMethod(LambdaWrapper.class, INVOKE_NAME);

    /**
     * @return All wrappers from {@link LambdaWrapper}
     */
    public static Method[] get() {
      return new Method[]{Factory.ACTION, Factory.GETTER, Factory.SETTER, Factory.CONSUMER_ARGS_2,
          Factory.CONSUMER_ARGS_3, Factory.CONSUMER_ARGS_4, Factory.CONSUMER_ARGS_5,
          Factory.CONSUMER_ARGS_6, Factory.FUNCTION_ARGS_1, Factory.FUNCTION_ARGS_2,
          Factory.FUNCTION_ARGS_3, Factory.FUNCTION_ARGS_4, Factory.FUNCTION_ARGS_5,
          Factory.FUNCTION_ARGS_6, CONSTRUCT, INVOKE};
    }
  }
}
