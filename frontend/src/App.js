import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Line } from 'react-chartjs-2';
import './App.css';
import { useTable, usePagination, useSortBy, useFilters } from "react-table";

const BASE_URL = '';

const Chart = ({ data, label, yAxisLabel }) => {
  const chartData = {
    labels: data.map((entry) => entry.period),
    datasets: [
      {
        label: yAxisLabel,
        data: data.map((entry) => entry[label]),
        borderColor: 'rgba(75,192,192,1)',
        borderWidth: 2,
        fill: false,
      },
    ],
  };

  const options = {
    scales: {
      x: {
        type: 'time',
        time: {
          unit: 'hour',
          tooltipFormat: 'YYYY-MM-DDTHH:mm:ss',
        },
        title: {
          display: true,
          text: 'Datetime',
        },
      },
      yAxes: [{
        display: true,
        ticks: {
          beginAtZero: true,
        },
      }],
      xAxes: [{
        display: true,
        ticks: {
          maxTicksLimit: 11,
          autoSkip: true,
        }
      }],
      y: {
        display: true,
        ticks: {
          beginAtZero: true   // minimum value will be 0.
        },
        title: {
          display: true,
          text: yAxisLabel,
        },
      },
    },
  };

  return <Line data={chartData} options={options} />;
};

const Table = ({
   getTableProps,
   getTableBodyProps,
   headerGroups,
   page,
   prepareRow,
   canPreviousPage,
   canNextPage,
   pageCount,
   pageOptions,
   gotoPage,
   nextPage,
   previousPage,
   pageIndex,
   setFilter,
 }) => {
  return (
      <div>
          <div>
              <label>Filter by Site: </label>
              <input
                  onChange={(e) => setFilter('groupingKey', e.target.value)}
                  placeholder="Filter..."
              />
          </div>
          <table {...getTableProps()} style={{ width: '100%' }}>
              <thead>
              {headerGroups.map((headerGroup) => (
                  <tr {...headerGroup.getHeaderGroupProps()}>
                      {headerGroup.headers.map((column) => (
                          <th {...column.getHeaderProps(column.getSortByToggleProps())}>
                              {column.render('Header')}
                              <span>
                    {column.isSorted ? (column.isSortedDesc ? ' 🔽' : ' 🔼') : ''}
                  </span>
                          </th>
                      ))}
                  </tr>
              ))}
              </thead>
              <tbody {...getTableBodyProps()}>
              {page.map((row) => {
                  prepareRow(row);
                  return (
                      <tr {...row.getRowProps()}>
                          {row.cells.map((cell) => (
                              <td style={{ textAlign: 'center' }} {...cell.getCellProps()}>{cell.render('Cell')}</td>
                          ))}
                      </tr>
                  );
              })}
              </tbody>
          </table>
          <div>
        <span>
          Page{' '}
            <strong>
            {pageIndex + 1} of {pageOptions.length}
          </strong>{' '}
        </span>
              <button onClick={() => gotoPage(0)} disabled={!canPreviousPage}>
                  {'<<'}
              </button>
              <button onClick={() => previousPage()} disabled={!canPreviousPage}>
                  {'<'}
              </button>
              <button onClick={() => nextPage()} disabled={!canNextPage}>
                  {'>'}
              </button>
              <button onClick={() => gotoPage(pageCount - 1)} disabled={!canNextPage}>
                  {'>>'}
              </button>
          </div>
      </div>
  );
};

const App = () => {
  const [period, setPeriod] = useState('Hours');
  const [eventKey, setEventKey] = useState('*');
  const [statisticsData, setStatisticsData] = useState([]);
  const [selectOptions, setSelectOptions] = useState({});
  const [eventKeySiteTable, setEventKeySiteTable] = useState('*');
  const [eventKeyDmaTable, setEventKeyDmaTable] = useState('*');
  const [siteData, setSiteData] = useState([]);
  const [dmaData, setDmaData] = useState([]);

  const handleEventKeyChange = (newEventKey) => {
      setEventKeySiteTable(newEventKey);
  };

    const handleEventKeyDmaChange = (newEventKey) => {
        setEventKeyDmaTable(newEventKey);
    };

    useEffect(() => {
        const fetchSiteData = async () => {
            try {
                const response = await axios.get(
                    `${BASE_URL}/api/statistics/aggregated-data?field=Site`
                    + (eventKeySiteTable !== '*' ? `&eventKey=${eventKeySiteTable}` : '')
                );
                setSiteData(response.data.data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        fetchSiteData();
    }, [eventKeySiteTable]);

    useEffect(() => {
        const fetchDmaData = async () => {
            try {
                const response = await axios.get(
                    `${BASE_URL}/api/statistics/aggregated-data?field=DMA`
                    + (eventKeyDmaTable !== '*' ? `&eventKey=${eventKeyDmaTable}` : '')
                );
                setDmaData(response.data.data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        fetchDmaData();
    }, [eventKeyDmaTable]);

  const columns = React.useMemo(
      () => [
        {
          Header: 'Site',
          accessor: 'groupingKey',
          Filter: DefaultColumnFilter,
        },
        {
          Header: 'Impressions',
          accessor: 'impressions',
          sortType: 'alphanumeric',
        },
        {
          Header: 'CTR',
          accessor: 'ctr',
          sortType: 'alphanumeric',
        },
        {
          Header: 'EvPM',
          accessor: 'evpm',
          sortType: 'alphanumeric',
        },
      ],
      []
  );

    const columns2 = React.useMemo(
        () => [
            {
                Header: 'DMA',
                accessor: 'groupingKey',
                Filter: DefaultColumnFilter,
            },
            {
                Header: 'Impressions',
                accessor: 'impressions',
                sortType: 'alphanumeric',
            },
            {
                Header: 'CTR',
                accessor: 'ctr',
                sortType: 'alphanumeric',
            },
            {
                Header: 'EvPM',
                accessor: 'evpm',
                sortType: 'alphanumeric',
            },
        ],
        []
    );

  const {
    getTableProps,
    getTableBodyProps,
    headerGroups,
    prepareRow,
    page,
    canPreviousPage,
    canNextPage,
    pageOptions,
    pageCount,
    gotoPage,
    nextPage,
    previousPage,
    state: { pageIndex },
    setFilter,
  } = useTable({ columns, data: siteData }, useFilters, useSortBy, usePagination);

  const {
    getTableProps: getTableProps2,
    getTableBodyProps: getTableBodyProps2,
    headerGroups: headerGroups2,
    prepareRow: prepareRow2,
    page: page2,
    canPreviousPage: canPreviousPage2,
    canNextPage: canNextPage2,
    pageOptions: pageOptions2,
    pageCount: pageCount2,
    gotoPage: gotoPage2,
    nextPage: nextPage2,
    previousPage: previousPage2,
    state: { pageIndex: pageIndex2 },
    setFilter: setFilter2,
  } = useTable({ columns: columns2, data: dmaData }, useFilters, useSortBy, usePagination);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(
            `${BASE_URL}/api/statistics/time-data?period=${period}`
            + (eventKey !== '*' ? `&eventKey=${eventKey}` : '')
        );
        const selectOptionsResponse = await axios.get(
            `${BASE_URL}/api/statistics/select-options`
        )
        setStatisticsData(response.data.data);
        setSelectOptions(selectOptionsResponse.data.data);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [period, eventKey]);

  return (
      <div className="app-container">
        <div className="controls-container">
          <label>Period:</label>
          <select onChange={(e) => setPeriod(e.target.value)} value={period}>
            <option value="Hours">Hours</option>
            <option value="Days">Days</option>
            <option value="Months">Months</option>
          </select>

          <label>Event Key:</label>
          <select onChange={(e) => setEventKey(e.target.value)} value={eventKey}>
            <option value="*">*</option>
            <option value="content">Content</option>
            <option value="signup">Signup</option>
            <option value="misc">Misc</option>
            <option value="lead">Lead</option>
            <option value="registration">Registration</option>
            <option value="fclick">Fclick</option>
          </select>
        </div>

        <div className="chart-container">
          <Chart data={statisticsData} label="ctr" yAxisLabel="CTR" />
        </div>

        <div className="chart-container">
          <Chart data={statisticsData} label="evpm" yAxisLabel="EVPM" />
        </div>
          <br></br>
          <br></br>
        <div className="chart-container">
            <label htmlFor="eventKeySelect">Select Event Key: </label>
            <select
                id="eventKeySelect"
                value={eventKeySiteTable}
                onChange={(e) => handleEventKeyChange(e.target.value)}
            >
                <option value="*">*</option>
                  <option value="content">Content</option>
                  <option value="signup">Signup</option>
                  <option value="misc">Misc</option>
                  <option value="lead">Lead</option>
                  <option value="registration">Registration</option>
                  <option value="fclick">FClick</option>
            </select>
            <Table
                getTableProps={getTableProps}
                getTableBodyProps={getTableBodyProps}
                headerGroups={headerGroups}
                page={page}
                prepareRow={prepareRow}
                canPreviousPage={canPreviousPage}
                canNextPage={canNextPage}
                pageCount={pageCount}
                pageOptions={pageOptions}
                gotoPage={gotoPage}
                nextPage={nextPage}
                previousPage={previousPage}
                pageIndex={pageIndex}
                setFilter={setFilter}
            />
        </div>
          <br></br>
          <br></br>
          <div className="chart-container">
              <label htmlFor="eventKeyDMASelect">Select Event Key: </label>
              <select
                  id="eventKeyDMASelect"
                  value={eventKeyDmaTable}
                  onChange={(e) => handleEventKeyDmaChange(e.target.value)}
              >
                  <option value="*">*</option>
                  <option value="content">Content</option>
                  <option value="signup">Signup</option>
                  <option value="misc">Misc</option>
                  <option value="lead">Lead</option>
                  <option value="registration">Registration</option>
                  <option value="fclick">FClick</option>
              </select>
              <Table
                  getTableProps={getTableProps2}
                  getTableBodyProps={getTableBodyProps2}
                  headerGroups={headerGroups2}
                  page={page2}
                  prepareRow={prepareRow2}
                  canPreviousPage={canPreviousPage2}
                  canNextPage={canNextPage2}
                  pageCount={pageCount2}
                  pageOptions={pageOptions2}
                  gotoPage={gotoPage2}
                  nextPage={nextPage2}
                  previousPage={previousPage2}
                  pageIndex={pageIndex2}
                  setFilter={setFilter2}
              />
          </div>
      </div>
  );
};

const DefaultColumnFilter = ({
    column: { filterValue, setFilter },
}) => {
    return (
        <input
            value={filterValue || ''}
            onChange={(e) => setFilter(e.target.value || undefined)}
            placeholder="Start entering..."
        />
    );
};

export default App;
