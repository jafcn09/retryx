# Retryx

Lightweight Kotlin retry library. | Librería ligera de reintentos para Kotlin.

## Installation | Instalación

```kotlin
dependencies {
    implementation("org.example:retryx:1.0.0")
}
```

## Usage | Uso

### Simple

```kotlin
val result = retry(times = 3, delay = 500) {
    httpClient.get(url)
}
```

### Advanced | Avanzado

```kotlin
val result = retry(RetryConfig(
    times = 5,
    delay = 1000,
    exponentialBackoff = true,
    retryOn = listOf(IOException::class.java),
    onFailure = { attempt, ex -> logger.warn("Attempt $attempt: ${ex.message}") }
)) {
    httpClient.get(url)
}
```

## Configuration | Configuración

| Parameter | Type | Default | Description / Descripción |
|-----------|------|---------|---------------------------|
| `times` | Int | 3 | Max attempts / Intentos máximos |
| `delay` | Long | 500 | Delay in ms / Retraso en ms |
| `exponentialBackoff` | Boolean | false | Double delay each failure / Duplicar retraso cada fallo |
| `retryOn` | List | empty | Retry only these exceptions / Reintentar solo estas excepciones |
| `onFailure` | Lambda | no-op | Callback on failure / Callback en cada fallo |

## Backoff Example | Ejemplo de Backoff

`delay = 1000`, `exponentialBackoff = true`:
```
Attempt 1 fails → 1000ms
Attempt 2 fails → 2000ms
Attempt 3 fails → 4000ms
```

## Requirements | Requisitos

- Java 8+
- Kotlin 1.8+

## License | Licencia

MIT License

Copyright (c) 2025 Jhafet Cánepa