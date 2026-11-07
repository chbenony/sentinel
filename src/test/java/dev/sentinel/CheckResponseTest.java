package dev.sentinel;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CheckResponseTest {
    @Test
    void roundsSubMillisecondRetryUp() {
        Decision decision = new Decision(false, 0, Duration.ofNanos(1));

        CheckResponse expected = new CheckResponse(false, 0, 1L);
        CheckResponse actual = CheckResponse.from(decision);
        assertEquals(expected, actual);
    }

    @Test
    void convertsMillisecondsExactly() {
        Decision decision = new Decision(false, 0, Duration.ofMillis(500));

        CheckResponse expected = new CheckResponse(false, 0, 500L);
        CheckResponse actual = CheckResponse.from(decision);
        assertEquals(expected, actual);
    }
}
