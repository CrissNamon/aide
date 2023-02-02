package tech.hiddenproject.aide.optional;

import static org.mockito.ArgumentMatchers.any;

import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Danila Rassokhin
 */
public class IfTrueConditionalTest {

  @Test
  public void branchTest() {
    Predicate booleanPredicate = Mockito.mock(Predicate.class);
    Supplier stringSupplier = Mockito.mock(Supplier.class);
    Mockito.when(booleanPredicate.test(any()))
        .thenReturn(false);
    Mockito.when(stringSupplier.get())
        .thenReturn("result");

    String actual = (String) IfTrueConditional.create()
        .ifTrue(false).then("false")
        .ifTrue(null, booleanPredicate).then(stringSupplier)
        .ifTrue(true).then(stringSupplier)
        .orElse("WRONG");

    Assertions.assertEquals("result", actual);
    Mockito.verify(booleanPredicate).test(any());
    Mockito.verify(stringSupplier).get();
    Mockito.verifyNoMoreInteractions(booleanPredicate, stringSupplier);
  }

  @Test
  public void orElseTest() {
    Predicate booleanPredicate = Mockito.mock(Predicate.class);
    Supplier stringSupplier = Mockito.mock(Supplier.class);
    Mockito.when(booleanPredicate.test(any()))
        .thenReturn(false);
    Mockito.when(stringSupplier.get())
        .thenReturn("result");

    String actual = (String) IfTrueConditional.create()
        .ifTrue(false).then("false")
        .ifTrue(null, booleanPredicate).then(stringSupplier)
        .ifTrue(false).then(stringSupplier)
        .orElse("WRONG");

    Assertions.assertEquals("WRONG", actual);
    Mockito.verify(booleanPredicate).test(any());
    Mockito.verifyNoInteractions(stringSupplier);
    Mockito.verifyNoMoreInteractions(booleanPredicate);
  }

  @Test
  public void orElseThrowTest() {

    IfTrueConditional ifTrueConditional = IfTrueConditional.create();

    Assertions.assertThrows(
        RuntimeException.class,
        () -> ifTrueConditional.orElseThrows(() -> new RuntimeException())
    );
  }

}
