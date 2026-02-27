# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Java library implementing the Try monad (inspired by `scala.util.Try`), providing an abstraction for computations that may fail with exceptions. Published to Maven Central as `com.jecklgamis:try-monad`.

## Build Commands

```bash
mvn compile          # Compile
mvn test             # Run all tests
mvn package          # Build JAR
mvn test -Dtest=TryUnitTest#testMethodName  # Run a single test method
```

Requires Java 8+. Uses Maven with `maven-compiler-plugin` targeting Java 1.8.

## Architecture

All source is in `com.jecklgamis.util` package:

- **`Try<T>`** — Core interface defining the monad operations: `get`, `map`, `flatMap`, `filter`, `forEach`, `getOrElse`, `orElse`, `recover`, `recoverWith`, `toOptional`
- **`Success<T>`** / **`Failure<T>`** — Package-private implementations of `Try<T>`. Not directly instantiated by users.
- **`TryFactory`** — Entry point via `TryFactory.attempt(TrySupplier<T>)`, which wraps a computation in a Try
- **`TryFunction<T, R>`** — Functional interface like `Function<T, R>` but allows throwing `Throwable`
- **`TrySupplier<T>`** — Functional interface like `Supplier<T>` but allows throwing `Throwable`

Usage pattern: `TryFactory.attempt(() -> riskyOperation()).map(...).getOrElse(fallback)`
