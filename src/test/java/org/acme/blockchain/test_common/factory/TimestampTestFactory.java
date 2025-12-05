package org.acme.blockchain.test_common.factory;

import org.acme.blockchain.common.utility.TimestampUtility;
import org.instancio.Instancio;

import java.time.OffsetDateTime;

import static org.instancio.Select.all;

public final class TimestampTestFactory {

    public static OffsetDateTime generateTimestamp() {
        OffsetDateTime now = TimestampUtility.getOffsetDateTimeNow();

        return Instancio.of(OffsetDateTime.class)
                .generate(all(OffsetDateTime.class),
                        gen -> gen.temporal().offsetDateTime()
                                .min(now.minusHours(1))
                                .max(now.minusSeconds(1)))
                .create();
    }

    public static OffsetDateTime generateTimestampBefore(OffsetDateTime beforeTime) {
        return Instancio.of(OffsetDateTime.class)
                .generate(all(OffsetDateTime.class),
                        gen -> gen.temporal().offsetDateTime()
                                .min(beforeTime.minusYears(1))
                                .max(beforeTime.minusSeconds(1)))
                .create();
    }
}
