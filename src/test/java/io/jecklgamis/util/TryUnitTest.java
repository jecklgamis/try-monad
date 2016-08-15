package io.jecklgamis.util;

import org.junit.Test;

import java.io.IOException;

import static io.jecklgamis.util.TryFactory.Try;
import static java.lang.Integer.toBinaryString;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class TryUnitTest {

    @Test
    public void isSuccessOnSuccessShouldBeTrue() {
        assertTrue(Try(() -> 1).isSuccess());
        assertFalse(Try(() -> 1).isFailure());
    }

    @Test
    public void isFailureOnFailureShouldBeTrue() {
        assertTrue(Try(() -> {
            throw new Exception();
        }).isFailure());
        assertFalse(Try(() -> {
            throw new Exception();
        }).isSuccess());
    }

    @Test
    public void getOnSuccessShouldReturnTheExpressionResult() {
        assertEquals(2, Try(() -> 2).get(), 0);
    }

    @Test(expected = RuntimeException.class)
    public void getOnFailureShouldThrowException() {
        Try(() -> {
            throw new Exception();
        }).get();
    }

    @Test
    public void getOrElseThisOnFailureShouldReturnGivenValue() {
        assertEquals(new Integer(3), Try(() -> {
            throw new Exception();
        }).getOrElseThis(3));
    }

    @Test
    public void getOrElseThisOnSuccessShouldReturnTheExpressionResult() {
        assertEquals(1, Try(() -> 1).getOrElseThis(0).intValue());
    }

    @Test
    public void forEachOnSuccessShouldApplyTheGivenFunction() {
        Try<Integer> result = Try(() -> 1);
        final int[] sideEffect = new int[1];
        sideEffect[0] = 0;
        result.forEach((r) -> sideEffect[0] = r);
        assertTrue(sideEffect[0] == 1);
    }

    @Test(expected = RuntimeException.class)
    public void forEachOnSuccessWithFailingSideEffect() {
        Try(() -> 1).forEach(((v) -> {
            throw new IOException();
        }));
    }

    @Test
    public void forEachOnFailureShouldNotApplyTheGivenFunction() {
        Try<Integer> result = Try(() -> {
            throw new Exception();
        });
        final int[] sideEffect = new int[1];
        sideEffect[0] = 0;
        result.forEach((r) -> sideEffect[0] = r);
        assertTrue(sideEffect[0] == 0);
    }

    @Test
    public void getOrElseOnOnSuccessShouldReturnExpressionResult() {
        assertEquals(1, Try(() -> 1).getOrElse(() -> 0).intValue());
    }

    @Test
    public void getOrElseOnFailureShouldReturnExpressionResult() {
        assertEquals(new Integer(3), Try(() -> {
            throw new Exception();
        }).getOrElse(() -> 3));
    }

    @Test
    public void OrElseOnOnSuccessShouldReturnExpressionResult() {
        assertEquals(2, Try(() -> 2).orElse(() -> Try(() -> 3)).get().intValue());
    }

    @Test
    public void OrElseOnOnFailureShouldReturnExpressionResultOfTheGivenTry() {
        assertEquals(new Integer(3), Try(() -> {
            throw new Exception();
        }).orElse(() -> Try(() -> 3)).get());
    }

    @Test
    public void OrElseOnOnFailureShouldReturnNewFailureIfTheGivenTryFails() {
        Try result = Try(() -> {
            throw new Exception();
        }).orElse(() -> Try(() -> {
            throw new NullPointerException();
        }));
        assertTrue(result.isFailure());
    }

    @Test
    public void mapOnSuccessShouldReturnSuccess() {
        Try<Integer> result = Try(() -> 2).map((r) -> 2 * r.intValue());
        assertTrue(result.isSuccess());
        assertEquals(4, result.get().intValue());
    }

    @Test
    public void mapOnSuccessShouldReturnSuccessForSubTypes() {
        assertEquals("10", Try(() -> 2).map((r) -> toBinaryString(r)).get());
        assertEquals(true, Try(() -> 2).map((r) -> r % 2 == 0).get().booleanValue());
    }

    @Test
    public void mapOnSuccessShouldReturnAFailureIfTheMappingFunctionFails() {
        Try<Integer> result = Try(() -> 2).map((v) -> v / 0);
        assertTrue(result.isFailure());
    }

    @Test
    public void mapOnFailureShouldJustReturnTheFailure() {
        Try result = Try(() -> {
            throw new Exception();
        }).map((v) -> "ignored");
        assertTrue(result.isFailure());
    }

    @Test
    public void flatMapOnSuccessShouldApplyTheGivenFunctionThatSucceeds() {
        Try result = Try(() -> 2).flatMap((v) -> Try(() -> toBinaryString(v)));
        assertTrue(result.isSuccess());
        assertEquals("10", result.get());
    }

    @Test
    public void flatMapOnSuccessShouldReturnFailureIfTheMappingFunctionFails() {
        Try result = Try(() -> 1).flatMap((v) -> {
            throw new Exception();
        });
        assertTrue(result.isFailure());
    }

    @Test
    public void flatMapOnSuccessShouldShouldReturnFailureIfApplyingTheMappingFunctionFails() {
        Try result = Try(() -> 1).flatMap((v) -> Try(() -> {
            throw new Throwable();
        }));
        assertTrue(result.isFailure());
    }

    @Test
    public void flatMapOnFailureShouldJustReturnTheFailure() {
        Try result = Try(() -> {
            throw new IOException();
        }).flatMap((v -> Try(() -> toBinaryString((Integer) v))));
        try {
            result.get();
            fail("should throw exception");
        } catch (Throwable t) {
            assertTrue(t.getCause().getClass() == IOException.class);
        }
    }

    @Test
    public void toOptionalOnSuccessShouldHaveNonEmptyValue() {
        assertEquals(2, Try(() -> 2).toOptional().get().intValue());
    }

    @Test
    public void toOptionalOnFailureShouldReturnEmpty() {
        assertFalse(Try(() -> {
            throw new Exception();
        }).toOptional().isPresent());
    }

    @Test
    public void filterOnSuccessShouldReturnSuccessIfPredicateIsSatisfied() {
        assertTrue(Try(() -> 2).filter((v) -> v == 2).isSuccess());
    }

    @Test
    public void filterOnSuccessShouldReturnFailureIfPredicateIsNotSatisfied() {
        assertTrue(Try(() -> 2).filter((v) -> v == 0).isFailure());
    }

    @Test
    public void filterOnFailureShouldJustReturnTheFailure() {
        assertTrue(Try(() -> {
            throw new RuntimeException();
        }).filter((v) -> false).isFailure());
    }

    @Test
    public void recoverOnSuccessShouldShouldJustReturnSuccess() {
        Try<Integer> result = Try(() -> 2).recover((t) -> 0);
        assertTrue(result.isSuccess());
        assertEquals(2, result.get().intValue());
    }

    @Test
    public void recoverOnFailureShouldReturnSuccessIfRecoverySucceeds() {
        Try result = Try(() -> {
            throw new Exception();
        }).recover((t) -> 0);
        assertTrue(result.isSuccess());
        assertEquals(new Integer(0), result.get());
    }

    @Test
    public void recoverOnFailureShouldReturnFailureIfRecoveryFails() {
        Try result = Try(() -> {
            throw new Exception();
        }).recover((t) -> {
            throw new Exception();
        });
        assertTrue(result.isFailure());
    }

    @Test
    public void recoverWithOnSuccessShouldJustReturnSuccess() {
        Try<Integer> result = Try(() -> 2).recoverWith((t) -> Try(() -> 0));
        assertTrue(result.isSuccess());
        assertEquals(2, result.get().intValue());
    }

    @Test
    public void recoverWithOnFailureShouldReturnSuccessIfRecoverySucceeds() {
        Try result = Try(() -> {
            throw new Exception();
        }).recoverWith((t) -> Try(() -> 0));
        assertTrue(result.isSuccess());
        assertEquals(new Integer(0), result.get());
    }

    @Test(expected = RuntimeException.class)
    public void recoverWithOnFailureShouldReturnFailureIfRecoveryFails() {
        Try result = Try(() -> {
            throw new IOException();
        }).recoverWith((t) -> {
            throw new Exception();
        });
        assertTrue(result.isFailure());
        result.get();
    }

}


