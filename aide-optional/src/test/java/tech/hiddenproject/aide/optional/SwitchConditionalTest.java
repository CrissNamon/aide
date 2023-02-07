package tech.hiddenproject.aide.optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Supplier;

/**
 * @author Danila Rassokhin
 */
public class SwitchConditionalTest {

  @Test
  public void returnValueTest() {
    Status status = Status.BAD_REQUEST;

    String message = SwitchConditional.<Status, String>on(status)
                                      .caseOn(Status.BAD_REQUEST::equals).thenGet("Error: Bad request")
                                      .caseOn(Status.INTERNAL_ERROR::equals).thenGet("Error: Internal error")
                                      .orElse("");
    Assertions.assertEquals("Error: Bad request", message);
  }

  @Test
  public void returnValueSupplierTest() {
    Status status = Status.BAD_REQUEST;

    Supplier supplier1 = Mockito.mock(Supplier.class);
    Supplier supplier2 = Mockito.mock(Supplier.class);
    Mockito.when(supplier1.get()).thenReturn("Error: Bad request");
    Mockito.when(supplier2.get()).thenReturn("Error: Internal error");

    String message = SwitchConditional.<Status, String>on(status)
                                      .caseOn(Status.BAD_REQUEST::equals).thenGet((Supplier<String>) supplier1)
                                      .caseOn(Status.INTERNAL_ERROR::equals).thenGet((Supplier<String>) supplier2)
                                      .orElse("");

    Assertions.assertEquals("Error: Bad request", message);
    Mockito.verify(supplier1, Mockito.times(1)).get();
    Mockito.verifyNoMoreInteractions(supplier1);
    Mockito.verifyNoInteractions(supplier2);
  }

  @Test
  public void actionTest() {
    Status status = Status.OK;

    Action action1 = Mockito.mock(Action.class);
    Action action2 = Mockito.mock(Action.class);
    Action action3 = Mockito.mock(Action.class);
    Mockito.doNothing().when(action1).make();
    Mockito.doNothing().when(action2).make();
    Mockito.doNothing().when(action3).make();

    SwitchConditional.on(status)
        .caseOn(Status.BAD_REQUEST::equals).thenDo(action1)
        .caseOn(Status.INTERNAL_ERROR::equals).thenDo(action2)
        .orElseDo(action3);

    Mockito.verifyNoInteractions(action1, action2);
    Mockito.verify(action3, Mockito.times(1)).make();
    Mockito.verifyNoMoreInteractions(action3);
  }

  @Test
  public void actionNoBreakTest() {
    Status status = Status.BAD_REQUEST;

    Action action1 = Mockito.mock(Action.class);
    Action action2 = Mockito.mock(Action.class);
    Action action3 = Mockito.mock(Action.class);
    Mockito.doNothing().when(action1).make();
    Mockito.doNothing().when(action2).make();
    Mockito.doNothing().when(action3).make();

    SwitchConditional.on(status)
                     .caseOn(Status.BAD_REQUEST::equals, false).thenDo(action1)
                     .caseOn(Status.INTERNAL_ERROR::equals).thenDo(action2)
                     .orElseDo(action3);

    Mockito.verify(action1, Mockito.times(1)).make();
    Mockito.verify(action2, Mockito.times(1)).make();
    Mockito.verify(action3, Mockito.times(1)).make();
    Mockito.verifyNoMoreInteractions(action1, action2, action3);
  }

  enum Status {
    OK, BAD_REQUEST, INTERNAL_ERROR
  }

}
