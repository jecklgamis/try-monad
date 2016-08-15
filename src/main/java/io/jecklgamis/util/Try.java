package io.jecklgamis.util;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An abstraction of a result that could possibly fail because of an exception.
 *
 * @param <T> the type of the result
 */
public abstract class Try<T> {

    /**
     * Returns the resulting value of the given expression if this is a Success, otherwise throws a RuntimeException wrapping
     * the original exception if this is a Failure.
     *
     * @return the value or exception being thrown
     */
    public abstract T get();

    /**
     * Returns true if this is Success, false if this is a Failure.
     *
     * @return true or false
     */
    public abstract boolean isSuccess();

    /**
     * Returns true if this is Failure, false if this is a Success.
     *
     * @return true or otherwise
     */
    public abstract boolean isFailure();

    /**
     * Applies the given function to the result wrapped in a Try if this is a Success, otherwise return this Failure.
     *
     * @param fn  mapping function
     * @param <U> mapped return value type
     * @return mapped value
     */
    public abstract <U> Try<U> map(TryFunction<T, U> fn);

    /**
     * Applies the given function to the result if this is a Success. If that function fails a Failure is returned.
     * If this is a Failure, the Failure is simply returned.
     *
     * @param fn  the function to apply
     * @param <U> the return type of the resulting Try
     * @return Try
     */
    public abstract <U> Try<U> flatMap(TryFunction<? super T, Try<U>> fn);

    /**
     * Returns this Success if the predicate is satisfied. Else return this Failure.
     *
     * @param p predicate
     * @return a Try
     */
    public abstract Try<T> filter(Predicate<? super T> p);

    /**
     * Applies the the given method (side-effect) on the result if this is a Success. Else do nothing.
     *
     * @param fn  the function to apply
     * @param <U> the return type of the applied function
     */
    public <U> void forEach(TryFunction<T, U> fn) {
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
    public T getOrElse(Supplier<T> fn) {
        return isSuccess() ? get() : fn.get();
    }

    /**
     * Returns the given value if this is a Failure otherwise return this Success.
     *
     * @param value the value
     * @return value  or the result of this Success
     */
    public T getOrElseThis(T value) {
        return getOrElse(() -> value);
    }

    /**
     * Returns this if it is a Success, otherwise apply the given function. If an exception occurs when applying the function, a Failure
     * is returned with the thrown exception
     *
     * @param fn the function to apply
     * @return Try
     */
    public abstract Try<T> orElse(Supplier<Try<T>> fn);

    /**
     * Converts this to a non-empty Optional if this is Success, otherwise return Optional.empty
     *
     * @return the Optional
     */
    public abstract Optional<T> toOptional();

    /**
     * Applies this function if this is a Failure, otherwise return this Failure
     *
     * @param fn the function that recovers from the throwable
     * @return the Try
     */
    public abstract Try<T> recover(TryFunction<? super Throwable, T> fn);

    /**
     * Applies this function if this is a Failure, otherwise return this Failure
     *
     * @param fn the function that recovers from the throwable
     * @return the Try
     */
    public abstract Try<T> recoverWith(TryFunction<? super Throwable, Try<T>> fn);

}


