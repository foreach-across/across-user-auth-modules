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
  const size = require( "gulp-size" );
  const imagemin = require( "gulp-imagemin" );
  const config = require( "../config" );
  const pngquant = require( "imagemin-pngquant" );

  // Optimize images
  gulp.task( "images", function() {
               console.log( "processing folder " + config.images_src );

               return gulp.src( config.images_src )
                 .pipe( imagemin( {
                                    progressive: true,
                                    interlaced: true,
                                    use: [pngquant( {quality: "65-80", speed: 4} )]
                                  } ) )
                 .pipe( gulp.dest( config.images_dest ) )
                 .pipe( size( {title: "images"} ) );
             }
  );
}
catch ( e ) {
  console.log( "gulp images task DISABLED" );

  // to enable again:
  // npm install gulp-imagemin@2.4.0 --save
  // && npm install imagemin-pngquant@4.2.2 --save
  // && npm install gulp-size@2.1.0 --save
}

