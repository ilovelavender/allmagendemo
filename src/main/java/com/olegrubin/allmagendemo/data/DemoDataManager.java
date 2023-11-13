package com.olegrubin.allmagendemo.data;

import com.olegrubin.allmagendemo.dao.api.StatisticsWriteDao;
import com.olegrubin.allmagendemo.model.enums.AggregationField;
import com.olegrubin.allmagendemo.model.enums.StatsPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.olegrubin.allmagendemo.model.consts.ClickHouseNames.EVENTS;
import static com.olegrubin.allmagendemo.model.consts.ClickHouseNames.MTV_POSTFIX;
import static com.olegrubin.allmagendemo.model.consts.ClickHouseNames.VIEWS;

/**
 * This class is used only for demo to insert/clear demo data
 */
@Component
public class DemoDataManager {
    private static final Logger LOG = LoggerFactory.getLogger(DemoDataManager.class);

    private final StatisticsWriteDao statisticsDao;

    private final String eventsFileLocation;
    private final String viewsFileLocation;

    public DemoDataManager(
            StatisticsWriteDao statisticsDao,
            @Value("${events.file.location}") String eventsFileLocation,
            @Value("${views.file.location}") String viewsFileLocation) {
        this.statisticsDao = statisticsDao;
        this.eventsFileLocation = eventsFileLocation;
        this.viewsFileLocation = viewsFileLocation;
    }

    public void loadData() {
        LOG.info("Populating events table from file " + eventsFileLocation);
        statisticsDao.insertFromCsvFile(eventsFileLocation, EVENTS);
        LOG.info("Populating views table from file " + viewsFileLocation);
        statisticsDao.insertFromCsvFile(viewsFileLocation, VIEWS);
        LOG.info("Finished inserting the data");
    }

    public void clearData() {
        LOG.info("Truncating tables");
        statisticsDao.truncateTable(EVENTS);
        statisticsDao.truncateTable(VIEWS);
        LOG.info("Truncating MVs");
        Arrays.stream(StatsPeriod.values()).forEach(sp ->
            statisticsDao.truncateTable(
                sp.toString().toLowerCase() + MTV_POSTFIX));
        Arrays.stream(AggregationField.values()).forEach(af ->
            statisticsDao.truncateTable(
                af.toString().toLowerCase() + MTV_POSTFIX));
    }
}
