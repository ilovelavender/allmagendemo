package com.olegrubin.allmagendemo.dao.impl;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseException;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.client.config.ClickHouseClientOption;
import com.clickhouse.client.http.config.ClickHouseHttpOption;
import com.clickhouse.client.http.config.HttpConnectionProvider;
import com.clickhouse.data.ClickHouseCompression;
import com.clickhouse.data.ClickHouseFile;
import com.clickhouse.data.ClickHouseFormat;
import com.clickhouse.data.ClickHouseRecord;
import com.olegrubin.allmagendemo.dao.api.StatisticsReadDao;
import com.olegrubin.allmagendemo.dao.api.StatisticsWriteDao;
import com.olegrubin.allmagendemo.model.dto.AggregatedTableRecord;
import com.olegrubin.allmagendemo.model.dto.TimeDataRecord;
import com.olegrubin.allmagendemo.model.enums.AggregationField;
import com.olegrubin.allmagendemo.model.enums.StatsPeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.olegrubin.allmagendemo.model.consts.ClickHouseNames.viewName;
import static com.olegrubin.allmagendemo.model.consts.ClickHouseQueries.AGGREGATED_DATA;
import static com.olegrubin.allmagendemo.model.consts.ClickHouseQueries.AGGREGATED_DATA_BY_EVENT;
import static com.olegrubin.allmagendemo.model.consts.ClickHouseQueries.TIME_DATA;
import static com.olegrubin.allmagendemo.model.consts.ClickHouseQueries.TIME_DATA_BY_EVENT;

@Repository
public class ClickHouseStatisticsDaoImpl implements StatisticsReadDao, StatisticsWriteDao {
    private final Logger LOG = LoggerFactory.getLogger(ClickHouseStatisticsDaoImpl.class);

    private static final String DB_NULL = " ";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static boolean isNotNull(String str) {
        return str != null && !DB_NULL.equals(str);
    }

    private static final Function<ClickHouseRecord, TimeDataRecord> TIME_DATA_MAPPER =
            c -> {
                String[] chRecord = c.getValue(0).asString().split("\t");
                return (TimeDataRecord) new TimeDataRecord()
                    .setPeriod(LocalDateTime.parse(chRecord[0], DATE_TIME_FORMATTER))
                    .setImpressions(isNotNull(chRecord[1]) ? Long.parseLong(chRecord[1]) : 0)
                    .setClicks(isNotNull(chRecord[2]) ? Long.parseLong(chRecord[2]) : 0)
                    .setEvents(isNotNull(chRecord[3]) ? Long.parseLong(chRecord[3]) : 0)
                    .setCtr(isNotNull(chRecord[4]) ?
                        new BigDecimal(chRecord[4]).setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .setEvpm(isNotNull(chRecord[4]) ?
                        new BigDecimal(chRecord[5]).setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            };

    private static final Function<ClickHouseRecord, AggregatedTableRecord> AGGREGATED_DATA_MAPPER =
            c -> {
                String[] chRecord = c.getValue(0).asString().split("\t");
                return (AggregatedTableRecord) new AggregatedTableRecord()
                    .setGroupingKey(chRecord[0])
                    .setImpressions(isNotNull(chRecord[1]) ? Long.parseLong(chRecord[1]) : 0)
                    .setClicks(isNotNull(chRecord[2]) ? Long.parseLong(chRecord[2]) : 0)
                    .setEvents(isNotNull(chRecord[3]) ? Long.parseLong(chRecord[3]) : 0)
                    .setCtr(isNotNull(chRecord[4]) ?
                            new BigDecimal(chRecord[4]).setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .setEvpm(isNotNull(chRecord[4]) ?
                            new BigDecimal(chRecord[5]).setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            };

    private final ClickHouseNode clickHouseNode;

    public ClickHouseStatisticsDaoImpl(ClickHouseNode clickHouseNode) {
        this.clickHouseNode = clickHouseNode;
    }

    @Override
    public long insertFromCsvFile(String filePath, String tableName) {
        ClickHouseFile clickHouseFile = ClickHouseFile.of(
                filePath, ClickHouseCompression.NONE, ClickHouseFormat.CSV);
        try (ClickHouseClient client = ClickHouseClient.newInstance(clickHouseNode.getProtocol())) {
            try (ClickHouseResponse response = client.write(clickHouseNode)
                    .option(ClickHouseClientOption.COMPRESS, ClickHouseCompression.NONE)
                    .option(ClickHouseHttpOption.CONNECTION_PROVIDER, HttpConnectionProvider.HTTP_URL_CONNECTION)
                    .table(tableName)
                    .data(clickHouseFile)
                    .executeAndWait()) {
                return response.getSummary().getProgress().getWrittenRows();
            }
        } catch (ClickHouseException exe) {
            throw new RuntimeException(
                "Error importing " + filePath
                    + " csv file to table " + tableName, exe);
        }
    }

    @Override
    public void truncateTable(String tableName) {
        query("truncate table :tableName", null, tableName);
    }

    @Override
    public List<TimeDataRecord> getStatisticsOverTime(StatsPeriod statsPeriod) {
        return query(TIME_DATA, TIME_DATA_MAPPER, viewName(statsPeriod.getDbName()));
    }

    @Override
    public List<TimeDataRecord> getStatisticsOverTime(StatsPeriod statsPeriod, String eventKey) {
        return query(TIME_DATA_BY_EVENT, TIME_DATA_MAPPER, new HashMap<>() {{
            put("eventTag", "'" + eventKey + "'");
            put("periodName", viewName(statsPeriod.getDbName()));
        }});
    }

    @Override
    public List<AggregatedTableRecord> getAggregatedStatistics(AggregationField field) {
        return query(AGGREGATED_DATA, AGGREGATED_DATA_MAPPER, viewName(field.toString().toLowerCase()));
    }

    @Override
    public List<AggregatedTableRecord> getAggregatedStatistics(AggregationField field, String eventKey) {
        return query(AGGREGATED_DATA_BY_EVENT, AGGREGATED_DATA_MAPPER, new HashMap<>() {{
            put("eventTag", "'" + eventKey + "'");
            put("fieldName", viewName(field.toString().toLowerCase()));
        }});
    }

    @Override
    public List<String> selectEventsTags() {
        return query("select distinct processed_tag from events",
            c -> c.getValue(0).asString());
    }

    private <T> List<T> query(String sql, Function<ClickHouseRecord, T> mapper, String... params) {
        try (ClickHouseClient client = ClickHouseClient.newInstance(clickHouseNode.getProtocol())) {
            List<T> result = new LinkedList<>();
            try (ClickHouseResponse response = client.write(clickHouseNode)
                .option(ClickHouseClientOption.COMPRESS, ClickHouseCompression.NONE)
                .option(ClickHouseHttpOption.CONNECTION_PROVIDER, HttpConnectionProvider.HTTP_URL_CONNECTION)
                .query(sql)
                .params(params)
                .executeAndWait()
            ) {
                if (mapper != null) {
                    response.records().forEach(r -> {
                        result.add(mapper.apply(r));
                    });
                } else {
                    LOG.info("Successfully executed " + sql);
                }
            }
            return result;
        } catch (ClickHouseException exe) {
            throw new RuntimeException("Error on query " + sql
                + " with params " + Arrays.toString(params), exe);
        }
    }

    private <T> List<T> query(String sql, Function<ClickHouseRecord, T> mapper, Map<String, Object> params) {
        try (ClickHouseClient client = ClickHouseClient.newInstance(clickHouseNode.getProtocol())) {
            List<T> result = new LinkedList<>();
            //noinspection unchecked,rawtypes
            try (ClickHouseResponse response = client.write(clickHouseNode)
                .option(ClickHouseClientOption.COMPRESS, ClickHouseCompression.NONE)
                .option(ClickHouseHttpOption.CONNECTION_PROVIDER, HttpConnectionProvider.HTTP_URL_CONNECTION)
                .query(sql)
                .params((Map) params)
                .executeAndWait()
            ) {
                if (mapper != null) {
                    response.records().forEach(r -> {
                        result.add(mapper.apply(r));
                    });
                } else {
                    LOG.info("Successfully executed " + sql);
                }
            }
            return result;
        } catch (ClickHouseException exe) {
            throw new RuntimeException("Error on query " + sql
                    + " with params " + params, exe);
        }
    }
}
