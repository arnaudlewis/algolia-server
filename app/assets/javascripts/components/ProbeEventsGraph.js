import Inferno from 'inferno';
import Chart from 'chart.js';
import Component from 'inferno-component';
import h from 'inferno-hyperscript';
import R from 'ramda';
import moment from 'moment';

import communication from '../communication';

const DateFormat = 'YYYY-MM-DDTHH:mm:ss.SSSS';
const DisplayDateFormat = 'MM-DD Ha';
const GraphPoints = 12;

Chart.defaults.global.animation.duration = 0;

function sortByDate(data) {
  return data.sort(function (left, right) {
    return left.date.valueOf() - right.date.valueOf();
  });
}

function convertDates(data) {
  return data.map(tuple => {
    return R.merge(tuple, {date: moment(tuple.date, DateFormat)});
  });
}

function buildGraphData(data) {

  function buildResponse(label, value) {
    return {label, value};
  }

  function formatDateAsLabel(date) {
    return date.format(DisplayDateFormat);
  }

  const withDates = convertDates(data);
  const sorted = sortByDate(withDates);
  const length = sorted.length;
  if(length > GraphPoints) {
    const sliced = R.drop(length % GraphPoints, sorted);
    const slicedLength = sliced.length;
    const clusterCount = slicedLength / GraphPoints;

    const tupled = R.splitEvery(clusterCount, sliced);
    return tupled.reduce((acc, current) => {
      const clusterDate = R.nth(Math.floor(clusterCount/2 - 1), current).date;
      const clusterAvgTime = current.reduce((acc, tuple, index) => acc + tuple.avg_transfer_time, 0) / current.length;
      return acc.concat(buildResponse(formatDateAsLabel(clusterDate), clusterAvgTime));
    }, []);
  } else {
    const defaultData = Array.apply(null, {length: GraphPoints - length}).map(empty => buildResponse('', null));
    const toGraphData = sorted.map(tuple => buildResponse(formatDateAsLabel(tuple.date), tuple.avg_transfer_time));
    return toGraphData.concat(defaultData);
  }


  return sorted;
}

class ProbeEventsGraph extends Component {

  constructor(props) {
    super(props);
    this.renderChart = ::this.renderChart;
    this.state = {data: [], chart: null};
  }

  componentDidMount() {
    setInterval(() => {
      this.refreshData(this.props.currentOrigin);
    }, 3000);
    this.refreshData(this.props.currentOrigin);
  }

  componentWillReceiveProps(props) {
    this.refreshData(props.currentOrigin);
  }

  refreshData(origin) {
    communication.getDataByOrigin(origin)
    .then((data) => {
      const computedData = buildGraphData(data);
      this.setState({data: computedData}, () => this.renderChart());
    });
  }

  renderChart() {
    const c = this.state.chart;
    if(c) {
      c.destroy();
      this.state.chart = null;
    }

    this.state.chart = new Chart(document.getElementById('canvas'), {
        type: 'line',
        animation: {
          duration: 1,
        },
        data: {
            labels: this.state.data.map(d => d.label),
            datasets: [{
                label: this.props.currentOrigin,
                data: this.state.data.map(d => d.value),
                borderWidth: 3,
                borderColor: '#0AD6BE',
                pointBorderColor: '#76E2AE',
                lineTension: 0,
                backgroundColor: 'transparent',
            }]
        },
        options: {
          responsive: true,
          legend: {
            display: false,
          },
          scales: {
            yAxes: [{
              ticks: {
                beginAtZero:true
              }
            }]
          }
        }
    });
  }

  render() {
    return h('div.probe-events-graph', [
      h('h2', this.props.currentOrigin),
      h('canvas#canvas')
    ]);
  }
}

export default ProbeEventsGraph;
