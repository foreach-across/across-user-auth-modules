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

import {createCookie, eraseCookie, readCookie} from "./cookie";

if ( window.location.search.length > 0 ) {
  if ( window.location.search.match( /[?&]debugMode=true/ ) ) {
    /* eslint-disable */
    console.debug( "Turning debug mode on and creating cookie" );
    /* eslint-enable */
    createCookie( "debugMode", "true", 31 );
  }
  else if ( window.location.search.match( /[?&]debugMode=false/ ) ) {
    /* eslint-disable */
    console.debug( "Deleting debug cookie" );
    /* eslint-enable */
    eraseCookie( "debugMode" );
  }
}

const cookieValue = readCookie( "debugMode" );
const loggerEnabled = (cookieValue !== null);

function _log( debugText ) {
  if ( loggerEnabled ) {
    /* eslint-disable */
    console.log( `Logger ${new Date().toLocaleString()} : ${debugText}` );
    /* eslint-enable */
  }
}

export default ({

  "debug": function( debugText ) {
    if ( loggerEnabled ) {
      /* eslint-disable */
      console.debug( "Logger " + new Date().toLocaleString() + " : " + debugText );
      /* eslint-enable */
    }
  },

  "log": _log,

  "warn": function( debugText ) {
    if ( loggerEnabled ) {
      /* eslint-disable */
      console.warn( "Logger " + new Date().toLocaleString() + " : " + debugText );
      /* eslint-enable */
    }
  },

  "error": function( debugText ) {
    if ( loggerEnabled ) {
      /* eslint-disable */
      console.error( "Logger " + new Date().toLocaleString() + " : " + debugText );
      /* eslint-enable */
    }
  }
});
