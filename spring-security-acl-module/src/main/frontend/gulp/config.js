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

const root = "";
let dest;
import gutil from "gulp-util";

const slicingConfig = {
  "dest": "./slicing/",
  "URL": "./slicing/",
  "templates": ["./slicing/**/*.html", "./slicing/css/main.css"]
};

const devConfig = {
  "dest": "../resources/views/static/SpringSecurityAclModule/",
  "URL": "../resources/views/static/SpringSecurityAclModule/",
  "templates": []
};

if ( gutil.env.slicing ) {
  dest = slicingConfig.dest;
}
else {
  dest = devConfig.dest
}

export default {
  "root": root,
  "dest": dest,
  "devURL":
    (gutil.env.slicing
      ? slicingConfig.URL
      : devConfig.URL),
  "templates":
    (gutil.env.slicing
      ? slicingConfig.templates
      : devConfig.templates),
  "scss": {
    "src": root + (gutil.env.path ? gutil.env.path : "") + "scss/**/*.scss",
    "lint": root + (gutil.env.path ? gutil.env.path : "") + "scss/**/*.scss",
    "dest": dest + (gutil.env.path ? gutil.env.path : "") + "css/"
  },
  "css": {
    "src": root + (gutil.env.path ? gutil.env.path : "") + "css/**/*.css"
  },
  "js": {
    "src": root + "js-src/",
    "lint": [root + "js-src/app/**/*.js"],
    "test": root + "js-src/",
    "dest": dest + "js/"
  },
  "svg": {
    "src": root + "svg-src/**",
    "dest": dest + "svg/"
  },
  "images_src": "static/**/*",
  "images_dest": "static/"
};
