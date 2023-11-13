package com.olegrubin.allmagendemo.model.dto;

import java.time.LocalDateTime;

public class TimeDataRecord extends StatisticsRecord {

    private LocalDateTime period;

    public LocalDateTime getPeriod() {
        return period;
    }

    public TimeDataRecord setPeriod(LocalDateTime period) {
        this.period = period;
        return this;
    }
}
