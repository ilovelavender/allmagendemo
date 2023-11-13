package com.olegrubin.allmagendemo.model.consts;

public interface ClickHouseQueries {

    String TIME_DATA = """
        select
          period,
          sum(impressions) as impressions,
          sum(clicks) as clicks,
          sum(events) as events,
          ((clicks / impressions) * 100) as ctr,
          ((events / impressions) * 1000) as evpm
        from :periodName
        group by period
        order by period;
    """;

    String TIME_DATA_BY_EVENT = """
        select
          period,
          sum(impressions) as impressions,
          sum(case when eventTag = :eventTag then clicks end) as clicks,
          sum(case when eventTag = :eventTag then events end) as events,
          ((clicks / impressions) * 100) as ctr,
          ((events / impressions) * 1000) as evpm
        from :periodName
        group by period
        order by period;
    """;

    String AGGREGATED_DATA = """
        select
          groupingKey,
          sum(impressions) as impressions,
          sum(clicks) as clicks,
          sum(events) as events,
          ((clicks / impressions) * 100) as ctr,
          ((events / impressions) * 1000) as evpm
        from :fieldName
        group by groupingKey;
    """;

    String AGGREGATED_DATA_BY_EVENT = """
        select
          groupingKey,
          sum(impressions) as impressions,
          sum(case when eventTag = :eventTag then clicks end) as clicks,
          sum(case when eventTag = :eventTag then events end) as events,
          ((clicks / impressions) * 100) as ctr,
          ((events / impressions) * 1000) as evpm
        from :fieldName
        group by groupingKey;
    """;
}
