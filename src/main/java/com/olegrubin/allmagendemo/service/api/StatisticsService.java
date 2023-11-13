package com.olegrubin.allmagendemo.service.api;

import com.olegrubin.allmagendemo.model.dto.AggregatedTableRecord;
import com.olegrubin.allmagendemo.model.dto.TimeDataRecord;
import com.olegrubin.allmagendemo.model.enums.AggregationField;
import com.olegrubin.allmagendemo.model.enums.StatsPeriod;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    Map<String, List<String>> getSelectOptions();

    List<TimeDataRecord> getDataOverTime(
        StatsPeriod statsPeriod, String eventKey);

    List<AggregatedTableRecord> getAggregatedData(
        AggregationField aggregationField, String eventKey);
}
