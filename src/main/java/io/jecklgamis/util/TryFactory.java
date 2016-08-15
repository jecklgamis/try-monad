package io.jecklgamis.util;

/**
 * A factory class for creating Try instance.
 */
public final class TryFactory {

    public static <T> Try<T> Try(TrySupplier<T> fn) {
        try {
            return new Success(fn.get());
        } catch (Throwable e) {
            return new Failure(e);
        }
    }
}
