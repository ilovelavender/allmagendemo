package com.olegrubin.allmagendemo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public enum StatsPeriod {
    HOURLY("Hours", ChronoUnit.HOURS),
    DAILY("Days", ChronoUnit.DAYS),
    MONTHLY("Months", ChronoUnit.MONTHS);

    @JsonCreator
    public static StatsPeriod forValue(String value) {
        for (StatsPeriod field : values()) {
            if (field.getDisplayName().equals(value)) {
                return field;
            }
        }
        throw new RuntimeException("Cannot find aggregation field " + value);
    }

    private final String displayName;

    private final TemporalUnit interval;

    StatsPeriod(String displayName, TemporalUnit interval) {
        this.displayName = displayName;
        this.interval = interval;
    }

    public TemporalUnit getInterval() {
        return interval;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDbName() {
        return this.toString().toLowerCase();
    }
}
