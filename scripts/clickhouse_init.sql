create table if not exists views (
    reg_time DateTime,
    uid String,
    fc_imp_chk Int8,
    fc_time_chk Int8,
    utmtr Int8,
    mm_dma String,
    osName String,
    model String,
    hardware String,
    site_id String
) engine = ReplacingMergeTree() partition by toStartOfWeek(reg_time) order by (reg_time, uid);

create table if not exists events (
    uid String,
    tag String,
    is_click Boolean default if(left(tag, 1) = 'v', false, true),
    processed_tag String default if(left(tag, 1) = 'v', substr(tag, 2), tag)
) engine = ReplacingMergeTree() partition by (processed_tag) order by (uid, processed_tag, is_click);

-- for hourly chart
create materialized view if not exists hourly_stats
engine = MergeTree()
order by (period, eventTag)
as
select
    toStartOfHour(reg_time) as period,
    e.processed_tag as eventTag,
    count(*) as impressions,
    count(distinct case when e.is_click then e.uid end) as clicks,
    count(distinct e.uid) as events
from views v
left join events e on v.uid = e.uid
group by (period, eventTag);

-- for daily chart
create materialized view if not exists daily_stats
engine = AggregatingMergeTree()
order by (period, eventTag)
as
select
    toStartOfDay(reg_time) as period,
    e.processed_tag as eventTag,
    count(*) as impressions,
    count(distinct case when e.is_click then e.uid end) as clicks,
    count(distinct e.uid) as events
from views v
left join events e on v.uid = e.uid
group by (period, eventTag);

-- for monthly chart
create materialized view if not exists monthly_stats
engine = AggregatingMergeTree()
order by (period, eventTag)
as
select
    toDateTime(toStartOfMonth(reg_time)) as period,
    e.processed_tag as eventTag,
    count(*) as impressions,
    count(distinct case when e.is_click then e.uid end) as clicks,
    count(distinct e.uid) as events
from views v
left join events e on v.uid = e.uid
group by (period, eventTag);

-- for mmDma
create materialized view if not exists mm_dma_stats
engine = AggregatingMergeTree()
order by (groupingKey, eventTag)
as
select
    mm_dma as groupingKey,
    e.processed_tag as eventTag,
    count(*) as impressions,
    count(distinct case when e.is_click then e.uid end) as clicks,
    count(distinct e.uid) as events
from views v
left join events e on v.uid = e.uid
group by (groupingKey, eventTag);

-- for site_id
create materialized view if not exists site_id_stats
engine = AggregatingMergeTree()
order by (groupingKey, eventTag)
as
select
    site_id as groupingKey,
    e.processed_tag as eventTag,
    count(*) as impressions,
    count(distinct case when e.is_click then e.uid end) as clicks,
    count(distinct e.uid) as events
from views v
left join events e on v.uid = e.uid
group by (groupingKey, eventTag);
