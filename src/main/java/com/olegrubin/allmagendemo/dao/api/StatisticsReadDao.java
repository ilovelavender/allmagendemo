package com.olegrubin.allmagendemo.dao.api;

import com.olegrubin.allmagendemo.model.dto.AggregatedTableRecord;
import com.olegrubin.allmagendemo.model.dto.TimeDataRecord;
import com.olegrubin.allmagendemo.model.enums.AggregationField;
import com.olegrubin.allmagendemo.model.enums.StatsPeriod;

import java.util.List;

public interface StatisticsReadDao {

    List<TimeDataRecord> getStatisticsOverTime(StatsPeriod statsPeriod);

    List<TimeDataRecord> getStatisticsOverTime(StatsPeriod statsPeriod, String eventKey);

    List<AggregatedTableRecord> getAggregatedStatistics(AggregationField field);

    List<AggregatedTableRecord> getAggregatedStatistics(AggregationField field, String eventKey);

    List<String> selectEventsTags();
}
