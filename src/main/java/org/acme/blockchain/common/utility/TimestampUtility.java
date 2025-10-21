package org.acme.blockchain.common.utility;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Utility class providing common time and date manipulation functions for the application,
 * essential for creating reliable, timestamped records in a distributed blockchain.
 * <p>
 * **Crucially, this utility ensures all time operations are consistently handled in the**
 * **Coordinated Universal Time (UTC) zone** to maintain consistency across distributed
 * nodes, prevent time drift issues, and simplify chronological verification.
 */
public final class TimestampUtility {

    /**
     * Gets the current date and time anchored to the Coordinated Universal Time (UTC) zone.
     * <p>
     * This method should be used for all blockchain-related timestamps.
     *
     * @return The current {@link OffsetDateTime} anchored to {@link ZoneOffset#UTC}.
     */
    public static OffsetDateTime getOffsetDateTimeNow() {
        return Instant.now().atOffset(ZoneOffset.UTC);
    }

    /**
     * Checks if a given {@link OffsetDateTime} is within a 60-second window of the current UTC time.
     * <p>
     * This is useful for validating timestamps on received data to ensure they are reasonably current
     * and prevent replay attacks or extreme time discrepancies.
     *
     * @param other The {@link OffsetDateTime} to compare against the current time.
     * @return {@code true} if the absolute difference between the two times is less than or equal to 60 seconds;
     * {@code false} if the difference is greater than 60 seconds or if {@code other} is null.
     */
    public static boolean isWithinMinute(OffsetDateTime other) {
        if (other == null) {
            return false;
        }

        OffsetDateTime now = getOffsetDateTimeNow();

        Duration difference = Duration.between(now.toInstant(), other.toInstant());

        return difference.abs().getSeconds() <= 60;
    }
}
