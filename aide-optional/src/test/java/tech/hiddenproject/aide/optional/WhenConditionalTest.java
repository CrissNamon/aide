package tech.hiddenproject.aide.optional;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Danila Rassokhin
 */
public class WhenConditionalTest {

  public static int id = 1;

  public static void action() {
    System.out.println("ACTION: " + id);
    id++;
  }

  @Test
  public void branchTest() {
    Predicate predicate1 = Mockito.mock(Predicate.class);
    Predicate predicate2 = Mockito.mock(Predicate.class);

    Action action1 = Mockito.mock(Action.class);
    Action action2 = Mockito.mock(Action.class);

    Mockito.when(predicate1.test(any())).thenReturn(true);
    Mockito.when(predicate2.test(any())).thenReturn(false);
    Mockito.doNothing().when(action1).make();
    Mockito.doNothing().when(action2).make();

    WhenConditional.create().when(new Object(), predicate1).then(action1)
                            .when(new Object(), predicate2)
                            .then(action2).orDoNothing();

    Mockito.verify(predicate1, Mockito.times(1)).test(any());
    Mockito.verifyNoInteractions(predicate2);
    Mockito.verify(action1, Mockito.times(1)).make();
    Mockito.verifyNoInteractions(action2);

    Mockito.verifyNoMoreInteractions(predicate1, predicate2, action1, action2);
  }

  @Test
  public void orFinallyTest() {
    Predicate predicate1 = Mockito.mock(Predicate.class);
    Predicate predicate2 = Mockito.mock(Predicate.class);

    Action action1 = Mockito.mock(Action.class);
    Action action2 = Mockito.mock(Action.class);
    Action action3 = Mockito.mock(Action.class);

    Mockito.when(predicate1.test(any())).thenReturn(false);
    Mockito.when(predicate2.test(any())).thenReturn(true);
    Mockito.doNothing().when(action1).make();
    Mockito.doNothing().when(action2).make();
    Mockito.doNothing().when(action3).make();

    WhenConditional.create().when(new Object(), predicate1).then(action1)
                            .when(new Object(), predicate2)
                            .then(action2).orFinally(action3);

    Mockito.verify(predicate1, Mockito.times(1)).test(any());
    Mockito.verify(predicate2, Mockito.times(1)).test(any());
    Mockito.verifyNoInteractions(action1);
    Mockito.verify(action2, Mockito.times(1)).make();
    Mockito.verify(action3, Mockito.times(1)).make();

    Mockito.verifyNoMoreInteractions(predicate1, predicate2, action1, action2, action3);
  }

  @Test
  public void orElseDoTest() {
    Predicate predicate1 = Mockito.mock(Predicate.class);
    Predicate predicate2 = Mockito.mock(Predicate.class);

    Action action1 = Mockito.mock(Action.class);
    Action action2 = Mockito.mock(Action.class);
    Action action3 = Mockito.mock(Action.class);

    Mockito.when(predicate1.test(any())).thenReturn(false);
    Mockito.when(predicate2.test(any())).thenReturn(false);
    Mockito.doNothing().when(action1).make();
    Mockito.doNothing().when(action2).make();
    Mockito.doNothing().when(action3).make();

    WhenConditional.create().when(new Object(), predicate1).then(action1)
                            .when(new Object(), predicate2)
                            .then(action2).orElseDo(action3);

    Mockito.verify(predicate1, Mockito.times(1)).test(any());
    Mockito.verify(predicate2, Mockito.times(1)).test(any());
    Mockito.verifyNoInteractions(action1);
    Mockito.verifyNoInteractions(action2);
    Mockito.verify(action3, Mockito.times(1)).make();

    Mockito.verifyNoMoreInteractions(predicate1, predicate2, action1, action2, action3);
  }

  @Test
  public void orElseThrowTest() {
    Predicate predicate1 = Mockito.mock(Predicate.class);
    Predicate predicate2 = Mockito.mock(Predicate.class);

    Action action1 = Mockito.mock(Action.class);
    Action action2 = Mockito.mock(Action.class);

    Supplier supplier = Mockito.mock(Supplier.class);

    Mockito.when(predicate1.test(any())).thenReturn(false);
    Mockito.when(predicate2.test(any())).thenReturn(false);
    Mockito.doNothing().when(action1).make();
    Mockito.doNothing().when(action2).make();
    Mockito.when(supplier.get()).thenThrow(new RuntimeException());

    Assertions.assertThrows(
        RuntimeException.class,
        () -> WhenConditional.create().when(new Object(), predicate1).then(action1)
                                      .when(new Object(), predicate2).then(action2)
                                      .orElseThrow(supplier)
    );

    Mockito.verify(predicate1, Mockito.times(1)).test(any());
    Mockito.verify(predicate2, Mockito.times(1)).test(any());
    Mockito.verifyNoInteractions(action1);
    Mockito.verifyNoInteractions(action2);
    Mockito.verify(supplier, Mockito.times(1)).get();

    Mockito.verifyNoMoreInteractions(predicate1, predicate2, action1, action2, supplier);
  }

  @Test
  public void whenTest() {

    WhenConditional.create().when(false).then(WhenConditionalTest::action)
                            .when(new Object(), Objects::nonNull)
                            .then(() -> WhenConditionalTest.action()).orDoNothing();

  }
}
