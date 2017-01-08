'use strict'

module.exports = {
  entry: ['./app/assets/javascripts/main.js'],
  devtool: 'source-map',
  output: {
    path: './public/javascripts/compiled/',
    filename: 'client.js',
  },
  module: {
    loaders: [{
      test: /\.js?$/,
      exclude: /node_modules/,
      loader: 'babel-loader',
    },
    {
      test: /\.jsx?$/,
      exclude: /node_modules/,
      loader: 'babel-loader',
    },
    {
      test: /\.json$/,
      loader: 'json'
    }]
  },
}
