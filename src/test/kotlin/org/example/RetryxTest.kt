package org.example

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RetryxTest {

    @Test fun `retry succeeds on first attempt`() {
        val result = retry { "success" }
        assertEquals("success", result)
    }

    @Test fun `retry succeeds after failures`() {
        var attempts = 0
        val result = retry(times = 3, delay = 10) {
            attempts++
            if (attempts < 3) throw RuntimeException("fail")
            "success"
        }
        assertEquals("success", result)
        assertEquals(3, attempts)
    }

    @Test fun `retry throws after max attempts`() {
        val exception = assertFailsWith<RetryException> {
            retry(times = 2, delay = 10) { throw RuntimeException("always fails") }
        }
        assertEquals(2, exception.attempts)
    }

    @Test fun `exponential backoff increases delay`() {
        val delays = mutableListOf<Long>()
        var lastTime = System.currentTimeMillis()
        assertFailsWith<RetryException> {
            retry(RetryConfig(times = 3, delay = 50, exponentialBackoff = true)) {
                val now = System.currentTimeMillis()
                if (delays.isNotEmpty() || lastTime != now) delays.add(now - lastTime)
                lastTime = now
                throw RuntimeException("fail")
            }
        }
        assertTrue(delays[1] > delays[0], "Second delay should be longer")
    }

    @Test fun `onFailure callback is invoked`() {
        val failures = mutableListOf<Int>()
        assertFailsWith<RetryException> {
            retry(RetryConfig(times = 3, delay = 10, onFailure = { attempt, _ -> failures.add(attempt) })) {
                throw RuntimeException("fail")
            }
        }
        assertEquals(listOf(1, 2, 3), failures)
    }

    @Test fun `retryOn filters exceptions`() {
        assertFailsWith<IllegalStateException> {
            retry(RetryConfig(times = 3, delay = 10, retryOn = listOf(IllegalArgumentException::class.java))) {
                throw IllegalStateException("not retryable")
            }
        }
    }

    @Test fun `retryOn allows specified exceptions`() {
        var attempts = 0
        assertFailsWith<RetryException> {
            retry(RetryConfig(times = 2, delay = 10, retryOn = listOf(IllegalArgumentException::class.java))) {
                attempts++
                throw IllegalArgumentException("retryable")
            }
        }
        assertEquals(2, attempts)
    }
}
