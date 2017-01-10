import Inferno from 'inferno';
import Component from 'inferno-component';
import h from 'inferno-hyperscript';

import ProbeEventsGraph from './ProbeEventsGraph';
import OriginSelector from './OriginSelector';

class StatsLayout extends Component {

  constructor(props) {
    super(props);
    this.handleOriginChange = ::this.handleOriginChange;

    this.state = {
      currentOrigin: props.origins[0],
    };
  }

  handleOriginChange(origin) {
    this.setState({currentOrigin: origin});
  }

  render() {
    return h('div.stats-layout', [
      h(OriginSelector, {
        origins: this.props.origins,
        onChange: this.handleOriginChange,
        value: this.state.currentOrigin,
      }),
      h(ProbeEventsGraph, {currentOrigin: this.state.currentOrigin}),
    ]);
  }
}

export default StatsLayout;
