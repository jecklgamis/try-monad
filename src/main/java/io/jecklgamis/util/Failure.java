package io.jecklgamis.util;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A Try instance that indicates a failed execution.
 *
 * @param <T> the return type of the expression result.
 */
class Failure<T> extends Try<T> {
    private Throwable t;


    public Failure(Throwable t) {
        this.t = t;
    }

    @Override
    public T get() {
        throw new RuntimeException(t);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public <U> Try<U> map(TryFunction<T, U> fn) {
        return (Try<U>) this;
    }

    @Override
    public <U> Try<U> flatMap(TryFunction<? super T, Try<U>> fn) {
        return (Try<U>) this;
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.empty();
    }

    @Override
    public Try<T> filter(Predicate<? super T> p) {
        return this;
    }

    @Override
    public Try<T> orElse(Supplier<Try<T>> fn) {
        return fn.get();
    }

    @Override
    public Try<T> recover(TryFunction<? super Throwable, T> fn) {
        try {
            return new Success(fn.apply(t));
        } catch (Throwable e) {
            return new Failure(e);
        }
    }

    @Override
    public Try<T> recoverWith(TryFunction<? super Throwable, Try<T>> fn) {
        try {
            return fn.apply(t);
        } catch (Throwable t) {
            return new Failure(t);
        }
    }
}
