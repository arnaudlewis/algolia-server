import h from 'inferno-hyperscript';
import Inferno from 'inferno';

import Chart from './components/Chart';

const $dashboardChart = document.querySelector('#dashboard-chart');
if($dashboardChart) Inferno.render(h(Chart, {}), $dashboardChart);
