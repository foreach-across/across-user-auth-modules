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

import gulp from "gulp";
import gutil from "gulp-util";
import config from "../config";
import webpack from "webpack";
import webpackConfig from "../../webpack.config.js";
import del from "del";
import karma from "karma";
import path from "path";

const eslint = require( "gulp-eslint" );

// base config, to be extended
let myConfig = Object.create( webpackConfig );

function clearBuildFolder( callback ) {
  callback();

  return del( [
                `${config.js.dest}*.js`
              ], {"force": true} );
}

function jsTest( cb ) {
  if ( !gutil.env.skipTests && !gutil.env.skipTest && config.js.test ) {
    console.log( "[UNIT TESTS]: running (use --skipTests to skip)" );
    /* Unit tests: karma */
    return new karma.Server( {
                               configFile: __dirname + "/../../karma.conf.js",
                               singleRun: true,
                               autoWatch: false
                             }, function karmaCompleted() {
      console.log( "[UNIT TESTS]: completed" );
      cb();
    } ).start();
  }
  else {
    console.log( "[UNIT TESTS]: skipped" );
  }
}

gulp.task( "js:test", jsTest );

gulp.task( "js:lint", function() {
  console.log( "linting " + config.js.lint );

  return gulp.src( config.js.lint )
    .pipe( eslint() )
    // eslint.format() outputs the lint results to the console.
    // Alternatively use eslint.formatEach() (see Docs).
    .pipe( eslint.format() )
    // To have the process exit with an error code (1) on
    // lint error, return the stream and pipe to failAfterError last.
    .pipe( eslint.failAfterError() );
} );

gulp.task( "js", gulp.series( "js:lint", clearBuildFolder, function compile( callback ) {
  // if we are production, single compile
  // if we are dev, watch with sourcemaps

  if ( gutil.env.hot ) {
    // "injecting" hot module reloading in config
    myConfig.entry.main.unshift( "webpack/hot/only-dev-server" ); // "only" prevents reload on syntax errors
    myConfig.entry.main.unshift( "webpack-hot-middleware/client?path=http://localhost:3000/__webpack_hmr" );

    myConfig.output.hotUpdateChunkFilename = "hot/[id].[hash].hot-update.js";
    myConfig.output.hotUpdateMainFilename = "hot/[hash].hot-update.json";

    myConfig.module.rules.push( {
                                  "test": /\.js$/,
                                  "include": path.join( __dirname, config.js.src ),
                                  "loader": "webpack-module-hot-accept"
                                } );

    myConfig.devServer = {
      "hot": true, // Tell the dev-server we're using HMR
      "inline": true,
      "port": 3000,
      "contentBase": "/",
      "publicPath": "/"
    };
  }

  if ( gutil.env.watch ) {
    console.log( "WATCHING" );
    myConfig.watch = true;
  }
  else {
    callback();
  }

  if ( gutil.env.production ) {
    myConfig.devtool = "";
    myConfig.plugins = myConfig.plugins.concat(
      new webpack.DefinePlugin( {
                                  "process.env": {
                                    // This has effect on the react lib size
                                    "NODE_ENV": JSON.stringify( "production" )
                                  }
                                } ),
      new webpack.optimize.UglifyJsPlugin()
    );

  }
  else {
    myConfig.plugins = myConfig.plugins.concat(
      new webpack.LoaderOptionsPlugin( {
                                         debug: true
                                       } ) );
    myConfig.cache = false;
  }

  // run webpack
  return webpack( myConfig, function( err, stats ) {

    jsTest( function() {
      /* callback; unit tests done here */
    } );

    if ( err ) {
      throw new gutil.PluginError( "webpack:build", err );
    }
    gutil.log( "[webpack:build]", stats.toString( {
                                                    colors: true
                                                  } ) );
  } );

} ) );

