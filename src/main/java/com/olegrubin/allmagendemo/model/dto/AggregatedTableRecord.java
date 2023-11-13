package com.olegrubin.allmagendemo.model.dto;

public class AggregatedTableRecord extends StatisticsRecord {

    private String groupingKey;

    public String getGroupingKey() {
        return groupingKey;
    }

    public AggregatedTableRecord setGroupingKey(String groupingKey) {
        this.groupingKey = groupingKey;
        return this;
    }
}
