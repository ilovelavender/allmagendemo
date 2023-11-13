package com.olegrubin.allmagendemo.model.dto;

import java.math.BigDecimal;

public abstract class StatisticsRecord {

    private long impressions;

    private long clicks;

    private long events;

    private BigDecimal ctr;

    private BigDecimal evpm;

    public long getImpressions() {
        return impressions;
    }

    public StatisticsRecord setImpressions(long impressions) {
        this.impressions = impressions;
        return this;
    }

    public long getClicks() {
        return clicks;
    }

    public StatisticsRecord setClicks(long clicks) {
        this.clicks = clicks;
        return this;
    }

    public long getEvents() {
        return events;
    }

    public StatisticsRecord setEvents(long events) {
        this.events = events;
        return this;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public StatisticsRecord setCtr(BigDecimal ctr) {
        this.ctr = ctr;
        return this;
    }

    public BigDecimal getEvpm() {
        return evpm;
    }

    public StatisticsRecord setEvpm(BigDecimal evpm) {
        this.evpm = evpm;
        return this;
    }
}
