package org.acme.common.utility;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Utility class providing common time and date manipulation functions for the application.
 * <p>
 * Ensures all time operations are consistently handled in the Coordinated Universal Time (UTC)
 * zone to maintain consistency across distributed systems and logging.
 */
public final class TimestampUtility {

    /**
     * Gets the current date and time in the Coordinated Universal Time (UTC) zone.
     *
     * @return The current {@link OffsetDateTime} anchored to {@link ZoneOffset#UTC}.
     */
    public static OffsetDateTime getOffsetDateTimeNow() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
