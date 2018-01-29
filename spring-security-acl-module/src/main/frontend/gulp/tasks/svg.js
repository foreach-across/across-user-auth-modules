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

try {
  const gulp = require( "gulp" );
  const svgSprite = require( "gulp-svg-sprite" );
  const config = require( "../config" );
  const path = require( "path" );
  const glob = require( "glob" );
  const size = require( "gulp-size" );

  gulp.task( "svg", function() {
    const svgDest = config.svg.dest;

    function makeSvgSpriteOptions( dirPath ) {
      return {
        mode: {
          symbol: {
            dest: ".",
            example: true,
            sprite: "main.svg"
          },
        }
      };
    }

    return glob( config.svg.src, function( err, dirs ) {
      dirs.forEach( function( dir ) {
        gulp.src( path.join( dir, "*.svg" ) )
          .pipe( svgSprite( makeSvgSpriteOptions( dir ) ) )
          .pipe( size( {showFiles: true, title: svgDest} ) )
          .pipe( gulp.dest( svgDest ) )
      } )
    } );

  } );
}
catch ( e ) {
  console.log( "gulp svg task DISABLED" );

  // to enable again:
  // npm install gulp-svg-sprite@1.2.19 --save
  // && npm install path@0.12.7 --save
  // && npm install glob@7.0.3 --save
  // && npm install gulp-size@2.1.0 --save
}

