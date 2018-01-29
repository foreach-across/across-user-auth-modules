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

import "./scss";
import "./js";
import gulp from "gulp";
import browserSync from "browser-sync";
import webpackDevMiddleware from "webpack-dev-middleware";
import webpackHotMiddleware from "webpack-hot-middleware";
import config from "../config";
import webpack from "webpack";
import webpackConfig from "../../webpack.config.js";
import proxy from "http-proxy-middleware";
import gutil from "gulp-util";

const bundler = webpack( webpackConfig );

// Static server
gulp.task( "serve", gulp.series( "scss", "js", function() {

  const browserSyncConfig = {};
  const filter = function( pathname, req ) {
    // true passes
    return (!pathname.match( /__webpack_hmr/g ) && !pathname.match( /hot/g ));
  };

  let jsonPlaceholderProxy = proxy( filter, {
    target: config.devURL,
    logLevel: "debug"
  } );

  browserSyncConfig.server = {
    "notify": true,
    // Customize the Browsersync console logging prefix
    "logPrefix": "FE",
    "baseDir": gutil.env.slicing ? config.dest : "./",
    "path": config.dest + "",
    "middleware": [
      webpackDevMiddleware( bundler, {
        "publicPath": webpackConfig.output.publicPath,
        "watchOptions": {
          "ignored": /node_modules/,
          "poll": true
        },
        "stats": {
          "colors": true,
          "assets": true,
          "version": true,
          "hash": true,
          "timings": true,
          "chunks": true,
          "chunkModules": true
        }
      } ),
      webpackHotMiddleware( bundler )
    ],
    "port": 3000
  };

  if ( gutil.env.slicing ) {
    browserSyncConfig.startPath = "/";
  }
  else {
    browserSyncConfig.server.middleware.push( jsonPlaceholderProxy );
    browserSyncConfig.startPath = "/";
    /* the homepage */
  }

  // watch for changes in markup/templates, css
  browserSyncConfig.files = config.templates;
  browserSync.create().init( browserSyncConfig );

  gulp.watch( config.scss.src, gulp.series( ["scss"] ) );

} ) );
