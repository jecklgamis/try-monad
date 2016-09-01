package com.jecklgamis.util;

/**
 * A supplier of T that and could throw a Throwable.
 *
 * @param <T> the return type
 */
@FunctionalInterface
public interface TrySupplier<T> {
    T get() throws Throwable;
}
