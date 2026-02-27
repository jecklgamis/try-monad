package com.jecklgamis.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static java.lang.Integer.toBinaryString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TryUnitTest {

    @Test
    public void isSuccessOnSuccessShouldBeTrue() {
        assertTrue(TryFactory.attempt(() -> 1).isSuccess());
        assertFalse(TryFactory.attempt(() -> 1).isFailure());
    }

    @Test
    public void isFailureOnFailureShouldBeTrue() {
        assertTrue(TryFactory.attempt(() -> {
            throw new Exception();
        }).isFailure());
        assertFalse(TryFactory.attempt(() -> {
            throw new Exception();
        }).isSuccess());
    }

    @Test
    public void getOnSuccessShouldReturnTheExpressionResult() {
        assertEquals(2, TryFactory.attempt(() -> 2).get().intValue());
    }

    @Test
    public void getOnFailureShouldThrowException() {
        assertThrows(RuntimeException.class, () -> TryFactory.attempt(() -> {
            throw new Exception();
        }).get());
    }

    @Test
    public void getOrElseOnFailureShouldReturnGivenValue() {
        assertEquals(Integer.valueOf(3), TryFactory.attempt(() -> {
            throw new Exception();
        }).getOrElse(3));
    }

    @Test
    public void getOrElseThisOnSuccessShouldReturnTheExpressionResult() {
        assertEquals(1, TryFactory.attempt(() -> 1).getOrElse(0).intValue());
    }

    @Test
    public void forEachOnSuccessShouldApplyTheGivenFunction() {
        Try<Integer> result = TryFactory.attempt(() -> 1);
        final int[] sideEffect = new int[1];
        sideEffect[0] = 0;
        result.forEach((r) -> sideEffect[0] = r);
        assertTrue(sideEffect[0] == 1);
    }

    @Test
    public void forEachOnSuccessWithFailingSideEffect() {
        assertThrows(RuntimeException.class, () -> TryFactory.attempt(() -> 1).forEach(((v) -> {
            throw new IOException();
        })));
    }

    @Test
    public void forEachOnFailureShouldNotApplyTheGivenFunction() {
        Try<Integer> result = TryFactory.attempt(() -> {
            throw new Exception();
        });
        final int[] sideEffect = new int[1];
        sideEffect[0] = 0;
        result.forEach((r) -> sideEffect[0] = r);
        assertTrue(sideEffect[0] == 0);
    }

    @Test
    public void getOrElseOnOnSuccessShouldReturnExpressionResult() {
        assertEquals(1, TryFactory.attempt(() -> 1).getOrElse(() -> 0).intValue());
    }

    @Test
    public void getOrElseOnFailureShouldReturnExpressionResult() {
        assertEquals(Integer.valueOf(3), TryFactory.attempt(() -> {
            throw new Exception();
        }).getOrElse(() -> 3));
    }

    @Test
    public void OrElseOnOnSuccessShouldReturnExpressionResult() {
        assertEquals(2, TryFactory.attempt(() -> 2).orElse(() -> TryFactory.attempt(() -> 3)).get().intValue());
    }

    @Test
    public void OrElseOnOnFailureShouldReturnExpressionResultOfTheGivenTry() {
        assertEquals(Integer.valueOf(3), TryFactory.attempt(() -> {
            throw new Exception();
        }).orElse(() -> TryFactory.attempt(() -> 3)).get());
    }

    @Test
    public void OrElseOnOnFailureShouldReturnNewFailureIfTheGivenTryFails() {
        Try result = TryFactory.attempt(() -> {
            throw new Exception();
        }).orElse(() -> TryFactory.attempt(() -> {
            throw new NullPointerException();
        }));
        assertTrue(result.isFailure());
    }

    @Test
    public void mapOnSuccessShouldReturnSuccess() {
        Try<Integer> result = TryFactory.attempt(() -> 2).map((r) -> 2 * r.intValue());
        assertTrue(result.isSuccess());
        assertEquals(4, result.get().intValue());
    }

    @Test
    public void mapOnSuccessShouldReturnSuccessForSubTypes() {
        assertEquals("10", TryFactory.attempt(() -> 2).map((r) -> toBinaryString(r)).get());
        assertEquals(true, TryFactory.attempt(() -> 2).map((r) -> r % 2 == 0).get().booleanValue());
    }

    @Test
    public void mapOnSuccessShouldReturnAFailureIfTheMappingFunctionFails() {
        Try<Integer> result = TryFactory.attempt(() -> 2).map((v) -> v / 0);
        assertTrue(result.isFailure());
    }

    @Test
    public void mapOnFailureShouldJustReturnTheFailure() {
        Try result = TryFactory.attempt(() -> {
            throw new Exception();
        }).map((v) -> "ignored");
        assertTrue(result.isFailure());
    }

    @Test
    public void flatMapOnSuccessShouldApplyTheGivenFunctionThatSucceeds() {
        Try result = TryFactory.attempt(() -> 2).flatMap((v) -> TryFactory.attempt(() -> toBinaryString(v)));
        assertTrue(result.isSuccess());
        assertEquals("10", result.get());
    }

    @Test
    public void flatMapOnSuccessShouldReturnFailureIfTheMappingFunctionFails() {
        Try result = TryFactory.attempt(() -> 1).flatMap((v) -> {
            throw new Exception();
        });
        assertTrue(result.isFailure());
    }

    @Test
    public void flatMapOnSuccessShouldShouldReturnFailureIfApplyingTheMappingFunctionFails() {
        Try result = TryFactory.attempt(() -> 1).flatMap((v) -> TryFactory.attempt(() -> {
            throw new Throwable();
        }));
        assertTrue(result.isFailure());
    }

    @Test
    public void flatMapOnFailureShouldJustReturnTheFailure() {
        Try result = TryFactory.attempt(() -> {
            throw new IOException();
        }).flatMap((v -> TryFactory.attempt(() -> toBinaryString((Integer) v))));
        try {
            result.get();
            fail("should throw exception");
        } catch (Throwable t) {
            assertTrue(t.getCause().getClass() == IOException.class);
        }
    }

    @Test
    public void toOptionalOnSuccessShouldHaveNonEmptyValue() {
        assertEquals(2, TryFactory.attempt(() -> 2).toOptional().get().intValue());
    }

    @Test
    public void toOptionalOnFailureShouldReturnEmpty() {
        assertFalse(TryFactory.attempt(() -> {
            throw new Exception();
        }).toOptional().isPresent());
    }

    @Test
    public void filterOnSuccessShouldReturnSuccessIfPredicateIsSatisfied() {
        assertTrue(TryFactory.attempt(() -> 2).filter((v) -> v == 2).isSuccess());
    }

    @Test
    public void filterOnSuccessShouldReturnFailureIfPredicateIsNotSatisfied() {
        assertTrue(TryFactory.attempt(() -> 2).filter((v) -> v == 0).isFailure());
    }

    @Test
    public void filterOnFailureShouldJustReturnTheFailure() {
        assertTrue(TryFactory.attempt(() -> {
            throw new RuntimeException();
        }).filter((v) -> false).isFailure());
    }

    @Test
    public void recoverOnSuccessShouldShouldJustReturnSuccess() {
        Try<Integer> result = TryFactory.attempt(() -> 2).recover((t) -> 0);
        assertTrue(result.isSuccess());
        assertEquals(2, result.get().intValue());
    }

    @Test
    public void recoverOnFailureShouldReturnSuccessIfRecoverySucceeds() {
        Try result = TryFactory.attempt(() -> {
            throw new Exception();
        }).recover((t) -> 0);
        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(0), result.get());
    }

    @Test
    public void recoverOnFailureShouldReturnFailureIfRecoveryFails() {
        Try result = TryFactory.attempt(() -> {
            throw new Exception();
        }).recover((t) -> {
            throw new Exception();
        });
        assertTrue(result.isFailure());
    }

    @Test
    public void recoverWithOnSuccessShouldJustReturnSuccess() {
        Try<Integer> result = TryFactory.attempt(() -> 2).recoverWith((t) -> TryFactory.attempt(() -> 0));
        assertTrue(result.isSuccess());
        assertEquals(2, result.get().intValue());
    }

    @Test
    public void recoverWithOnFailureShouldReturnSuccessIfRecoverySucceeds() {
        Try result = TryFactory.attempt(() -> {
            throw new Exception();
        }).recoverWith((t) -> TryFactory.attempt(() -> 0));
        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(0), result.get());
    }

    @Test
    public void recoverWithOnFailureShouldReturnFailureIfRecoveryFails() {
        Try result = TryFactory.attempt(() -> {
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
        assertThrows(RuntimeException.class, () -> result.get());
    }

    static class SomeException extends Exception {
    }

    @Test
    public void OrElseOnOnFailureShouldThrowException() {
        assertThrows(SomeException.class, () -> TryFactory.attempt(() -> {
            throw new RuntimeException();
        }).orElseThrow(new SomeException()));
    }
}
