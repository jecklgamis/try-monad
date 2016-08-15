package io.jecklgamis.util;

/**
 * A Function that accepts T and returns R and could throw a Throwable.
 *
 * @param <T> the return type
 */
@FunctionalInterface
public interface TryFunction<T, R> {
    R apply(T t) throws Throwable;
}
