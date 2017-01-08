import Inferno from 'inferno';
import Chart from 'chart.js';
import Component from 'inferno-component';
import h from 'inferno-hyperscript';

class ProbeEventsGraph extends Component {

  constructor(props) {
    super(props);
    this.renderChart = ::this.renderChart;
  }

  componentDidMount() {
    this.renderChart();
  }

  componentShouldUpdate() {

  }

  renderChart(canvas) {
    if(!canvas) return;
    new Chart(canvas, {
        type: 'bar',
        data: {
            labels: ["Red", "Blue", "Yellow", "Green", "Purple", "Orange"],
            datasets: [{
                label: '# of Votes',
                data: [12, 19, 3, 5, 2, 3],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 159, 64, 0.2)'
                ],
                borderColor: [
                    'rgba(255,99,132,1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
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
