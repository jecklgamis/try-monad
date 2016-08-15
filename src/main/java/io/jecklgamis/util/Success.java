package io.jecklgamis.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.jecklgamis.util.TryFactory.Try;

/**
 * A Try instance that indicates a successful execution.
 *
 * @param <T> the return type of the expression result.
 */
class Success<T> extends Try<T> {
    private T v;

    public Success(T v) {
        this.v = v;
    }

    @Override
    public T get() {
        return v;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public <U> Try<U> map(TryFunction<T, U> fn) {
        return Try(() -> fn.apply(get()));
    }

    @Override
    public <U> Try<U> flatMap(TryFunction<? super T, Try<U>> fn) {
        try {
            return fn.apply(get());
        } catch (Throwable t) {
            return new Failure(t);
        }
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.of(get());
    }

    @Override
    public Try<T> filter(Predicate<? super T> p) {
        return (p.test(get())) ? this : new Failure(new NoSuchElementException());
    }

    @Override
    public Try<T> orElse(Supplier<Try<T>> fn) {
        return this;
    }

    @Override
    public Try<T> recover(TryFunction<? super Throwable, T> fn) {
        return this;
    }

    @Override
    public Try<T> recoverWith(TryFunction<? super Throwable, Try<T>> fn) {
        return this;
    }

}
