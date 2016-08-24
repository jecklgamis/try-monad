package io.jecklgamis.util;

import org.junit.Test;

import java.io.IOException;

import static io.jecklgamis.util.TryFactory.attempt;
import static java.lang.Integer.toBinaryString;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class TryUnitTest {

    @Test
    public void isSuccessOnSuccessShouldBeTrue() {
        assertTrue(attempt(() -> 1).isSuccess());
        assertFalse(attempt(() -> 1).isFailure());
    }

    @Test
    public void isFailureOnFailureShouldBeTrue() {
        assertTrue(attempt(() -> {
            throw new Exception();
        }).isFailure());
        assertFalse(attempt(() -> {
            throw new Exception();
        }).isSuccess());
    }

    @Test
    public void getOnSuccessShouldReturnTheExpressionResult() {
        assertEquals(2, attempt(() -> 2).get(), 0);
    }

    @Test(expected = RuntimeException.class)
    public void getOnFailureShouldThrowException() {
        attempt(() -> {
            throw new Exception();
        }).get();
    }

    @Test
    public void getOrElseOnFailureShouldReturnGivenValue() {
        assertEquals(new Integer(3), attempt(() -> {
            throw new Exception();
        }).getOrElse(3));
    }

    @Test
    public void getOrElseThisOnSuccessShouldReturnTheExpressionResult() {
        assertEquals(1, attempt(() -> 1).getOrElse(0).intValue());
    }

    @Test
    public void forEachOnSuccessShouldApplyTheGivenFunction() {
        Try<Integer> result = attempt(() -> 1);
        final int[] sideEffect = new int[1];
        sideEffect[0] = 0;
        result.forEach((r) -> sideEffect[0] = r);
        assertTrue(sideEffect[0] == 1);
    }

    @Test(expected = RuntimeException.class)
    public void forEachOnSuccessWithFailingSideEffect() {
        attempt(() -> 1).forEach(((v) -> {
            throw new IOException();
        }));
    }

    @Test
    public void forEachOnFailureShouldNotApplyTheGivenFunction() {
        Try<Integer> result = attempt(() -> {
            throw new Exception();
        });
        final int[] sideEffect = new int[1];
        sideEffect[0] = 0;
        result.forEach((r) -> sideEffect[0] = r);
        assertTrue(sideEffect[0] == 0);
    }

    @Test
    public void getOrElseOnOnSuccessShouldReturnExpressionResult() {
        assertEquals(1, attempt(() -> 1).getOrElse(() -> 0).intValue());
    }

    @Test
    public void getOrElseOnFailureShouldReturnExpressionResult() {
        assertEquals(new Integer(3), attempt(() -> {
            throw new Exception();
        }).getOrElse(() -> 3));
    }

    @Test
    public void OrElseOnOnSuccessShouldReturnExpressionResult() {
        assertEquals(2, attempt(() -> 2).orElse(() -> attempt(() -> 3)).get().intValue());
    }

    @Test
    public void OrElseOnOnFailureShouldReturnExpressionResultOfTheGivenTry() {
        assertEquals(new Integer(3), attempt(() -> {
            throw new Exception();
        }).orElse(() -> attempt(() -> 3)).get());
    }

    @Test
    public void OrElseOnOnFailureShouldReturnNewFailureIfTheGivenTryFails() {
        Try result = attempt(() -> {
            throw new Exception();
        }).orElse(() -> attempt(() -> {
            throw new NullPointerException();
        }));
        assertTrue(result.isFailure());
    }

    @Test
    public void mapOnSuccessShouldReturnSuccess() {
        Try<Integer> result = attempt(() -> 2).map((r) -> 2 * r.intValue());
        assertTrue(result.isSuccess());
        assertEquals(4, result.get().intValue());
    }

    @Test
    public void mapOnSuccessShouldReturnSuccessForSubTypes() {
        assertEquals("10", attempt(() -> 2).map((r) -> toBinaryString(r)).get());
        assertEquals(true, attempt(() -> 2).map((r) -> r % 2 == 0).get().booleanValue());
    }

    @Test
    public void mapOnSuccessShouldReturnAFailureIfTheMappingFunctionFails() {
        Try<Integer> result = attempt(() -> 2).map((v) -> v / 0);
        assertTrue(result.isFailure());
    }

    @Test
    public void mapOnFailureShouldJustReturnTheFailure() {
        Try result = attempt(() -> {
            throw new Exception();
        }).map((v) -> "ignored");
        assertTrue(result.isFailure());
    }

    @Test
    public void flatMapOnSuccessShouldApplyTheGivenFunctionThatSucceeds() {
        Try result = attempt(() -> 2).flatMap((v) -> attempt(() -> toBinaryString(v)));
        assertTrue(result.isSuccess());
        assertEquals("10", result.get());
    }

    @Test
    public void flatMapOnSuccessShouldReturnFailureIfTheMappingFunctionFails() {
        Try result = attempt(() -> 1).flatMap((v) -> {
            throw new Exception();
        });
        assertTrue(result.isFailure());
    }

    @Test
    public void flatMapOnSuccessShouldShouldReturnFailureIfApplyingTheMappingFunctionFails() {
        Try result = attempt(() -> 1).flatMap((v) -> attempt(() -> {
            throw new Throwable();
        }));
        assertTrue(result.isFailure());
    }

    @Test
    public void flatMapOnFailureShouldJustReturnTheFailure() {
        Try result = attempt(() -> {
            throw new IOException();
        }).flatMap((v -> attempt(() -> toBinaryString((Integer) v))));
        try {
            result.get();
            fail("should throw exception");
        } catch (Throwable t) {
            assertTrue(t.getCause().getClass() == IOException.class);
        }
    }

    @Test
    public void toOptionalOnSuccessShouldHaveNonEmptyValue() {
        assertEquals(2, attempt(() -> 2).toOptional().get().intValue());
    }

    @Test
    public void toOptionalOnFailureShouldReturnEmpty() {
        assertFalse(attempt(() -> {
            throw new Exception();
        }).toOptional().isPresent());
    }

    @Test
    public void filterOnSuccessShouldReturnSuccessIfPredicateIsSatisfied() {
        assertTrue(attempt(() -> 2).filter((v) -> v == 2).isSuccess());
    }

    @Test
    public void filterOnSuccessShouldReturnFailureIfPredicateIsNotSatisfied() {
        assertTrue(attempt(() -> 2).filter((v) -> v == 0).isFailure());
    }

    @Test
    public void filterOnFailureShouldJustReturnTheFailure() {
        assertTrue(attempt(() -> {
            throw new RuntimeException();
        }).filter((v) -> false).isFailure());
    }

    @Test
    public void recoverOnSuccessShouldShouldJustReturnSuccess() {
        Try<Integer> result = attempt(() -> 2).recover((t) -> 0);
        assertTrue(result.isSuccess());
        assertEquals(2, result.get().intValue());
    }

    @Test
    public void recoverOnFailureShouldReturnSuccessIfRecoverySucceeds() {
        Try result = attempt(() -> {
            throw new Exception();
        }).recover((t) -> 0);
        assertTrue(result.isSuccess());
        assertEquals(new Integer(0), result.get());
    }

    @Test
    public void recoverOnFailureShouldReturnFailureIfRecoveryFails() {
        Try result = attempt(() -> {
            throw new Exception();
        }).recover((t) -> {
            throw new Exception();
        });
        assertTrue(result.isFailure());
    }

    @Test
    public void recoverWithOnSuccessShouldJustReturnSuccess() {
        Try<Integer> result = attempt(() -> 2).recoverWith((t) -> attempt(() -> 0));
        assertTrue(result.isSuccess());
        assertEquals(2, result.get().intValue());
    }

    @Test
    public void recoverWithOnFailureShouldReturnSuccessIfRecoverySucceeds() {
        Try result = attempt(() -> {
            throw new Exception();
        }).recoverWith((t) -> attempt(() -> 0));
        assertTrue(result.isSuccess());
        assertEquals(new Integer(0), result.get());
    }

    @Test(expected = RuntimeException.class)
    public void recoverWithOnFailureShouldReturnFailureIfRecoveryFails() {
        Try result = attempt(() -> {
            throw new IOException();
        }).recover((t) -> {
            if (t instanceof ClassCastException) {
                return 1;
            }
            throw new NullPointerException();
        }).recover(t -> {
            throw new ArrayIndexOutOfBoundsException();
        });
        assertTrue(result.isFailure());
        result.get();
    }

    class SomeException extends Exception {
    }

    @Test(expected = SomeException.class)
    public void OrElseOnOnFailureShouldThrowException() throws Throwable {
        attempt(() -> {
            throw new RuntimeException();
        }).orElseThrow(new SomeException());
    }

}


