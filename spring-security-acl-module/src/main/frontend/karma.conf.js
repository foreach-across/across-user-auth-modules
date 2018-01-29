/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const webpack = require( "karma-webpack" );
const config = require( "./gulp/config" ).default.js;
const webpackConfig = require( "./webpack.config" ).default;

module.exports = function( karmaConfig ) {
  const configObject = {
    "frameworks": ["jasmine"],
    "files": [
      "./node_modules/babel-polyfill/dist/polyfill.js",
      "./node_modules/phantomjs-polyfill/bind-polyfill.js",
      //config.dest + "common.bundle.js"
    ],
    "plugins": [
      "karma-babel-preprocessor",
      "webpack",
      "karma-jasmine",
      'karma-chrome-launcher',
      "karma-phantomjs-launcher",
      "karma-webpack",
    ],
    "browsers": ["PhantomJS"], /* production browsers: ["PhantomJS", "Chrome", "Firefox", "IE", "IE9", "Safari"], */
    preprocessors: {
      'src/**/*.js': ['babel'],
      'test/**/*.js': ['babel']
    },
    babelPreprocessor: {
      options: {
        presets: ['es2015'],
        sourceMap: 'inline'
      },
      filename: function( file ) {
        return file.originalPath.replace( /\.js$/, '.es5.js' );
      },
      sourceFileName: function( file ) {
        return file.originalPath;
      }
    },
    "webpack": webpackConfig,
    "webpackMiddleware": {
      "noInfo": true /* we already log in js task */
    }
  };

  configObject.files.push( "https://cdnjs.cloudflare.com/ajax/libs/lodash.js/4.17.4/lodash.min.js" );
  configObject.files.push( config.test + "**/*.test.js" );

  configObject.preprocessors[config.src + "**/*.js"] = ["webpack"];
  configObject.preprocessors[config.test + "**/*.test.js"] = ["webpack"];

  karmaConfig.set( configObject );
};
