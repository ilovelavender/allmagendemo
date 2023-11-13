package com.olegrubin.allmagendemo.service.impl;

import com.olegrubin.allmagendemo.dao.api.StatisticsReadDao;
import com.olegrubin.allmagendemo.model.dto.AggregatedTableRecord;
import com.olegrubin.allmagendemo.model.dto.TimeDataRecord;
import com.olegrubin.allmagendemo.model.enums.AggregationField;
import com.olegrubin.allmagendemo.model.enums.StatsPeriod;
import com.olegrubin.allmagendemo.service.api.StatisticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final int IMPRESSIONS_THRESHOLD = 50;
    private static final int TYPEAHEAD_LIMIT = 5;

    private static final Map<String, List<String>> SELECT_OPTIONS = Map.of(
        AggregationField.class.getSimpleName(),
            Arrays.stream(AggregationField.values())
                .map(AggregationField::getDisplayName)
                .collect(Collectors.toList()),
        StatsPeriod.class.getSimpleName(),
            Arrays.stream(StatsPeriod.values())
                .map(StatsPeriod::getDisplayName)
                .collect(Collectors.toList())
    );

    private static final Function<LocalDateTime, TimeDataRecord> ZERO_TDR = (period) ->
            (TimeDataRecord) new TimeDataRecord()
                .setPeriod(period)
                .setImpressions(0)
                .setClicks(0)
                .setEvents(0)
                .setCtr(BigDecimal.ZERO)
                .setEvpm(BigDecimal.ZERO);

    private static final Consumer<AggregatedTableRecord> MAKE_ADR_ZERO = (record) ->
            record
                .setClicks(0)
                .setEvents(0)
                .setCtr(BigDecimal.ZERO)
                .setEvpm(BigDecimal.ZERO);

    private final StatisticsReadDao statisticsReadDao;

    public StatisticsServiceImpl(StatisticsReadDao statisticsReadDao) {
        this.statisticsReadDao = statisticsReadDao;
    }

    @Override
    public Map<String, List<String>> getSelectOptions() {
        Map<String, List<String>> result = new HashMap<>(SELECT_OPTIONS);
        result.put("EventsTags", statisticsReadDao.selectEventsTags());
        return result;
    }

    @Override
    public List<TimeDataRecord> getDataOverTime(StatsPeriod statsPeriod, String eventKey) {
        List<TimeDataRecord> dbResult =
            eventKey != null
                ? statisticsReadDao.getStatisticsOverTime(statsPeriod, eventKey)
                : statisticsReadDao.getStatisticsOverTime(statsPeriod);

        return fixMissingIntervalsAndBrokenRecords(dbResult, statsPeriod);
    }

    @Override
    public List<AggregatedTableRecord> getAggregatedData(AggregationField aggregationField, String eventKey) {
        List<AggregatedTableRecord> dbResult = eventKey != null
            ? statisticsReadDao.getAggregatedStatistics(aggregationField, eventKey)
            : statisticsReadDao.getAggregatedStatistics(aggregationField);

        return dbResult.stream()
            .peek(d -> {
                if (d.getImpressions() < IMPRESSIONS_THRESHOLD) {
                    MAKE_ADR_ZERO.accept(d);
                }
            }).collect(Collectors.toList());
    }

    @Override
    public List<String> getTypeaheadHint(AggregationField aggregationField, String prefix) {
        return statisticsReadDao.selectValuesForTypeahead(
            aggregationField, prefix, TYPEAHEAD_LIMIT);
    }

    private List<TimeDataRecord> fixMissingIntervalsAndBrokenRecords(
            List<TimeDataRecord> dbResult, StatsPeriod statsPeriod) {
        Map<LocalDateTime, TimeDataRecord> dateTimeToTdr = dbResult.stream()
                .collect(Collectors.toMap(TimeDataRecord::getPeriod, tdr -> tdr));

        LocalDateTime currentPoint = dbResult.get(0).getPeriod();
        LocalDateTime endOfRange = dbResult.get(dbResult.size() - 1).getPeriod();

        List<TimeDataRecord> result = new LinkedList<>();
        while (currentPoint.isBefore(endOfRange) || currentPoint.equals(endOfRange)) {
            TimeDataRecord record = dateTimeToTdr.get(currentPoint);
            if (record == null) {
                // interval is missing
                record = ZERO_TDR.apply(currentPoint);
            } else if (record.getImpressions() < IMPRESSIONS_THRESHOLD) {
                // not enough data to consider result good
                record = (TimeDataRecord) ZERO_TDR.apply(currentPoint)
                        .setImpressions(record.getImpressions());
            }
            result.add(record);
            currentPoint = currentPoint.plus(1, statsPeriod.getInterval());
        }

        return result;
    }
}
