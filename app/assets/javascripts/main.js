import h from 'inferno-hyperscript';
import Inferno from 'inferno';

import StatsLayout from './components/StatsLayout';

const $dashboardChart = document.querySelector('#dashboard-chart');
if($dashboardChart) Inferno.render(h(StatsLayout, {origins: Window.origins}), $dashboardChart);
