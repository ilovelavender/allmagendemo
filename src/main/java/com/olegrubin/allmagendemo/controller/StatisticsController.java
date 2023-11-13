package com.olegrubin.allmagendemo.controller;

import com.olegrubin.allmagendemo.model.dto.AggregatedTableRecord;
import com.olegrubin.allmagendemo.model.dto.TimeDataRecord;
import com.olegrubin.allmagendemo.model.dto.common.SuccessResponse;
import com.olegrubin.allmagendemo.model.enums.AggregationField;
import com.olegrubin.allmagendemo.model.enums.StatsPeriod;
import com.olegrubin.allmagendemo.service.api.StatisticsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/time-data")
    public SuccessResponse<List<TimeDataRecord>> getTimeData(
        @RequestParam StatsPeriod period,
        @RequestParam(required = false) String eventKey
    ) {
        List<TimeDataRecord> records =
            statisticsService.getDataOverTime(period, eventKey);
        return new SuccessResponse<>(records);
    }

    @GetMapping("/aggregated-data")
    public SuccessResponse<List<AggregatedTableRecord>> getAggregatedData(
        @RequestParam AggregationField field,
        @RequestParam(required = false) String eventKey
    ) {
        List<AggregatedTableRecord> records =
                statisticsService.getAggregatedData(field, eventKey);
        return new SuccessResponse<>(records);
    }

    @GetMapping("/select-options")
    public SuccessResponse<Map<String, List<String>>> getSelectOptions() {
        return new SuccessResponse<>(statisticsService.getSelectOptions());
    }
}
