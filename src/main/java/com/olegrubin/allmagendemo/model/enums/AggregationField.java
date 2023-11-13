package com.olegrubin.allmagendemo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.converter.HttpMessageNotReadableException;

public enum AggregationField {
    MM_DMA("DMA", false),
    SITE_ID("Site", true);

    @JsonCreator
    public static AggregationField forValue(String value) {
        for (AggregationField field : values()) {
            if (field.getDisplayName().equals(value)) {
                return field;
            }
        }
        // noinspection deprecation
        throw new HttpMessageNotReadableException(
                "Cannot find aggregation field " + value);
    }

    private final String displayName;

    private final boolean usingPrefix;

    AggregationField(String displayName, boolean isUsingPrefix) {
        this.displayName = displayName;
        this.usingPrefix = isUsingPrefix;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    public boolean isUsingPrefix() {
        return usingPrefix;
    }
}
