package tech.hiddenproject.aide.optional;

import static org.mockito.ArgumentMatchers.any;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Danila Rassokhin
 */
public class BooleanOptionalTest {

  @Test
  public void createOptionalInvalidValueTest() {
    Assertions.assertThrows(NullPointerException.class, () -> BooleanOptional.of(null));
  }

  @Test
  public void ifValueThenTest() {
    BooleanConsumer booleanConsumer = Mockito.mock(BooleanConsumer.class);
    BooleanAction booleanAction = Mockito.mock(BooleanAction.class);
    ThrowableSupplier throwableSupplier = Mockito.mock(ThrowableSupplier.class);

    Mockito.doNothing().when(booleanConsumer).accept(any());
    Mockito.doNothing().when(booleanAction).make();
    Mockito.when(throwableSupplier.get()).thenThrow(new RuntimeException());

    BooleanOptional.of(true)
        .ifTrueThen(booleanConsumer);
    BooleanOptional.of(false)
        .ifFalseThen(booleanAction);

    Assertions.assertThrows(RuntimeException.class, () -> BooleanOptional.of(true)
        .ifTrueThrow(throwableSupplier));
    Assertions.assertThrows(RuntimeException.class, () -> BooleanOptional.of(false)
        .ifFalseThrow(throwableSupplier));

    Mockito.verify(booleanConsumer).accept(any());
    Mockito.verify(booleanAction).make();
    Mockito.verify(throwableSupplier, Mockito.times(2));
  }

  public class BooleanConsumer implements Consumer<Boolean> {

    @Override
    public void accept(Boolean aBoolean) {
    }
  }

  public class BooleanAction implements Action {

    @Override
    public void make() {
    }
  }

  public class ThrowableSupplier implements Supplier<RuntimeException> {

    @Override
    public RuntimeException get() {
      return new RuntimeException();
    }
  }

}
