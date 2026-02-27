# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Java library implementing the Try monad (inspired by `scala.util.Try`), providing an abstraction for computations that may fail with exceptions. Published to Maven Central as `com.jecklgamis:try-monad`.

## Build Commands

```bash
mvn compile          # Compile
mvn test             # Run all tests
mvn package          # Build JAR
mvn clean verify     # Clean build with all checks
mvn test -Dtest=TryUnitTest#testMethodName  # Run a single test method
```

Requires Java 8+. Uses Maven with `maven-compiler-plugin` targeting Java 1.8. Tests use JUnit 5 (Jupiter).

## CI

GitHub Actions workflow (`.github/workflows/build.yml`) runs on pushes to `main` and pull requests. Matrix build across Java LTS versions: 8, 11, 17, 21, 25.

## Architecture

All source is in `com.jecklgamis.util` package:

- **`Try<T>`** — Core interface defining the monad operations: `get`, `map`, `flatMap`, `filter`, `forEach`, `getOrElse`, `orElse`, `recover`, `recoverWith`, `toOptional`
- **`Success<T>`** / **`Failure<T>`** — Package-private implementations of `Try<T>`. Not directly instantiated by users.
- **`TryFactory`** — Entry point via `TryFactory.attempt(TrySupplier<T>)`, which wraps a computation in a Try
- **`TryFunction<T, R>`** — Functional interface like `Function<T, R>` but allows throwing `Throwable`
- **`TrySupplier<T>`** — Functional interface like `Supplier<T>` but allows throwing `Throwable`

Usage pattern: `TryFactory.attempt(() -> riskyOperation()).map(...).getOrElse(fallback)`

## Versioning

Uses semantic versioning (MAJOR.MINOR.PATCH). Current version: `1.1.0-SNAPSHOT`.
