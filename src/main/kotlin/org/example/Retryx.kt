package org.example

import kotlin.math.pow

data class RetryConfig(
    val times: Int = 3,
    val delay: Long = 500L,
    val exponentialBackoff: Boolean = false,
    val retryOn: List<Class<out Throwable>> = emptyList(),
    val onFailure: (attempt: Int, exception: Throwable) -> Unit = { _, _ -> }
)

class RetryException(val attempts: Int, cause: Throwable) : Exception("Failed after $attempts attempts", cause)

inline fun <T> retry(config: RetryConfig = RetryConfig(), block: () -> T): T {
    var lastException: Throwable? = null
    repeat(config.times) { attempt ->
        try {
            return block()
        } catch (e: Throwable) {
            if (config.retryOn.isNotEmpty() && config.retryOn.none { it.isInstance(e) }) throw e
            lastException = e
            config.onFailure(attempt + 1, e)
            if (attempt < config.times - 1) {
                val wait = if (config.exponentialBackoff) (config.delay * 2.0.pow(attempt)).toLong() else config.delay
                Thread.sleep(wait)
            }
        }
    }
    throw RetryException(config.times, lastException!!)
}

inline fun <T> retry(times: Int = 3, delay: Long = 500L, block: () -> T): T =
    retry(RetryConfig(times, delay), block)
