# try-monad

[![Build](https://github.com/jecklgamis/try-monad/actions/workflows/build.yml/badge.svg)](https://github.com/jecklgamis/try-monad/actions/workflows/build.yml)

Inspired by `scala.util.Try`, this is a small library that provides an abstraction of a result of a function application.

## Getting Started

### Maven
```xml
<dependency>
    <groupId>com.jecklgamis</groupId>
    <artifactId>try-monad</artifactId>
    <version>1.0</version>
</dependency>
```

### Gradle
```groovy
compile 'com.jecklgamis:try-monad:1.0'
```

## Example Usage

```java
import java.net.Socket;
import static com.jecklgamis.util.TryFactory.attempt;

public class ExampleUsage {
    public static boolean canConnect(String host, int port) {
        return attempt(() -> new Socket(host, port))
                .map(Socket::isConnected)
                .getOrElse(false);
    }
}
```

`TryFactory.attempt` wraps a computation that may throw, returning a `Success` or `Failure`. Subsequent calls like `map` and `getOrElse` chain naturally — `map` is skipped on `Failure`, and `getOrElse` provides a fallback value.

## Building

```bash
mvn package
```
