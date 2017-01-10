import Inferno from 'inferno';
import Component from 'inferno-component';
import h from 'inferno-hyperscript';

class OriginSelector extends Component {

  constructor(props) {
    super(props);

    this.handleChange = ::this.handleChange;
  }

  renderOptions() {
    return this.props.origins.map(o => {
      return h('option', {value: o}, o);
    });
  }

  handleChange(event) {
    const value = event.target.value;
    this.props.onChange(value);
  }

  render() {
    return h('select', {value: this.props.value, onChange: this.handleChange},this.renderOptions());
  }
}

export default OriginSelector;
