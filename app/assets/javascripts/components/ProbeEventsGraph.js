import Inferno from 'inferno';
import Chart from 'chart.js';
import Component from 'inferno-component';
import h from 'inferno-hyperscript';
import R from 'ramda';
import moment from 'moment';

import communication from '../communication';

const DateFormat = '"YYYY-MM-DDTHH:mm:ss.SSSS"';
const GraphPoints = 12;

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
    return date.toString();
  }

  const withDates = convertDates(data);
  const sorted = sortByDate(withDates);
  const length = sorted.length;
  if(length > GraphPoints) {
    const sliced = R.slice(0, length % GraphPoints, sorted);
    const slicedLength = sliced.length;
    const clusterCount = slicedLength / GraphPoints;

    const tupled = R.splitEvery(clusterCount, sliced);
    return tupled.reduce((acc, current) => {
      const clusterDate = R.nth(Math.floor(clusterCount/2), current).date;
      const clusterAvgTime = current.reduce((acc, tuple, index) => acc + tuple.avg_transfer_time, 0) / current.length;
      return buildResponse(formatDateAsLabel(clusterDate), clusterAvgTime);
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
    this.state = {data: []};
  }

  componentDidMount() {
    this.renderChart();
    this.refreshData();
  }

  refreshData() {
    // setInterval(() => {
      communication.getDataByOrigin('sdn-probe-tokyo')
      .then((data) => {
        const computedData = buildGraphData(data);
        this.setState({data: computedData});
      });
    // }, 3000);
  }

  renderChart(canvas) {
    if(!canvas) return;
    new Chart(canvas, {
        type: 'line',
        data: {
            labels: this.state.data.map(d => d.label),
            datasets: [{
                label: this.props.currentOrigin,
                data: this.state.data.map(d => d.value),
                borderWidth: 3
            }]
        },
        options: {
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
      h('canvas', {ref: (canvas) => this.renderChart(canvas)})
    ]);
  }
}

export default ProbeEventsGraph;
