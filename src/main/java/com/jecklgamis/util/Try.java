package com.jecklgamis.util;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An abstraction of a result that could possibly fail because of an exception. Use @see TryFactory.attempt() to create a Try.
 *
 * @param <T> the type of the result
 */
public interface Try<T> {

    /**
     * Returns the resulting value of the given expression if this is a Success, otherwise throws a RuntimeException wrapping
     * the original exception if this is a Failure.
     *
     * @return the value or exception being thrown
     */
    T get();

    /**
     * Returns true if this is Success, false if this is a Failure.
     *
     * @return true or false
     */
    boolean isSuccess();

    /**
     * Returns true if this is Failure, false if this is a Success.
     *
     * @return true or otherwise
     */
    boolean isFailure();

    /**
     * Applies the given function to the result wrapped in a Try if this is a Success, otherwise return this Failure.
     *
     * @param fn  mapping function
     * @param <U> mapped return value type
     * @return mapped value
     */
    <U> Try<U> map(TryFunction<? super T, U> fn);

    /**
     * Applies the given function to the result if this is a Success. If that function fails a Failure is returned.
     * If this is a Failure, the Failure is simply returned.
     *
     * @param fn  the function to apply
     * @param <U> the return type of the resulting Try
     * @return Try
     */
    <U> Try<U> flatMap(TryFunction<? super T, Try<U>> fn);

    /**
     * Returns this Success if the predicate is satisfied. Else return this Failure.
     *
     * @param p predicate
     * @return a Try
     */
    Try<T> filter(Predicate<? super T> p);

    /**
     * Applies the the given method (side-effect) on the result if this is a Success. Otherwise, do nothing.
     *
     * @param fn  the function to apply
     * @param <U> the return type of the applied function
     */
    default <U> void forEach(TryFunction<T, U> fn) {
        if (isSuccess()) {
            try {
                fn.apply(get());
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    /**
     * Applies the given function if this is a Failure otherwise return this Success.
     *
     * @param fn the function to apply
     * @return function application result or result of Success
     */
    T getOrElse(Supplier<T> fn);

    /**
     * Returns the given value if this is a Failure otherwise return this Success.
     *
     * @param value the value
     * @return value  or the result of this Success
     */
    T getOrElse(T value);

    /**
     * Returns this if it is a Success, otherwise apply the given function. If an exception occurs when applying the function, a Failure
     * is returned with the thrown exception
     *
     * @param fn the function to apply
     * @return Try
     */
    Try<T> orElse(Supplier<Try<T>> fn);

    /**
     * Throws the given Throwable if this is a Failure.
     *
     * @param t the Throwable
     * @throws Throwable the Throwable
     */
    default void orElseThrow(Throwable t) throws Throwable {
        if (isFailure()) throw t;
    }

    /**
     * Converts this to a non-empty Optional if this is Success, otherwise return Optional.empty
     *
     * @return the Optional
     */
    Optional<T> toOptional();

    /**
     * Applies this function if this is a Failure, otherwise return this Failure
     *
     * @param fn the function that recovers from the throwable
     * @return the Try
     */
    Try<T> recover(TryFunction<? super Throwable, T> fn);

    /**
     * Applies this function if this is a Failure, otherwise return this Failure
     *
     * @param fn the function that recovers from the throwable
     * @return the Try
     */
    Try<T> recoverWith(TryFunction<? super Throwable, Try<T>> fn);

}


