package com.jecklgamis.util;

/**
 * A factory class for creating Try instance.
 */
public final class TryFactory {

    /**
     * Apply the given function and return the result abstracted in a Try.
     *
     * @param fn  the function to apply
     * @param <T> the return type of the function
     * @return `Success` or `Failure`
     */
    public static <T> Try<T> attempt(TrySupplier<T> fn) {
        try {
            return new Success(fn.get());
        } catch (Throwable e) {
            return new Failure(e);
        }
    }
}