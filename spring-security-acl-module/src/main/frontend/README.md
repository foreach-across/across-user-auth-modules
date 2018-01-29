# Foreach Boilerplate
The Foreach Boilerplate helps you build fast, robust, and adaptable web apps or sites. Kick-start your project, save time!

## Getting started
Before you start coding away, you're gonna need to do a few things first.

##### Prerequisites
* Install [Node](https://nodejs.org/en/download/ )'s recommended version: [7.10.0](https://nodejs.org/dist/v7.10.0/node-v7.10.0-x64.msi)
* Install the latest version of [yarn](https://yarnpkg.com/lang/en/)
* run `npm install gulp-cli@1.2.2 -g` to install gulp on your machine globally (that's what the -g flag is for)
* run `npm install eslint -g` to intall [eslint](http://eslint.org/)

#### Installing dependencies
* run `yarn` in the commandline from the project's root; this will install all the dependencies

#### Running a quickstart local development server
* run `gulp` in the commandline from the project's root; this will spin up a browsersync server that watches changes in templates/scss/javascript. By default, this will run at http://localhost:3000/ (should start automatically in chrome)

#### Optional "cleanup"
* can be safely deleted in non-java projects: build.gradle
* If you do not wish to ever use these tasks, you can safely delete /gulp/tasks/images.js and /gulp/tasks/svg.js. The svg-src folder is also an optional example
* The scss/ folder and all files there are compiled to css/ and are just a guideline. Feel free to delete or modify any of them.
* Same goes for the javascript setup in /js-src. This will get compiled to /js
It is advisable to keep at least the main.js and app/init.js along with the folder structure/naming.

***

## General code conventions

These are configured in the .editorconfig file and should be self explanatory.

Tl;dr: 

Java/PHP/...: 4 spaces, no tabs.

Javascript, CSS/SCSS: 2 spaces, no tabs.

***

## (S)CSS
Our scss structure uses the [BEM](https://css-tricks.com/bem-101/) syntax with a form of [SMACSS](https://smacss.com/book/categorizing) folder organisation - we strongly advise to stick to at least these ideas to keep at least some form of consistency across all our projects. 

#### (S)CSS code conventions

Please refer to the [sass guidelines](https://sass-guidelin.es/)

***

## Javascript
We use [webpack2](https://webpack.js.org/) to bundle our javascript.
The config file is **"webpack.config.js"**.

Examples for loading modules and codesplitting can be found in **js-src/app/init.js**
#### Javascript code conventions
Please refer to the [Airbnb Javascript Style Guide](https://github.com/airbnb/javascript/blob/master/README.md)
Only notable exception: we use double quotes, not single quotes.
These conventions are enforced by the use of eslint. Configuration (exceptions) can be found in the .eslintrc file in the root folder. Try not to change these unless absolutely necessary (changes to this file should probably happen in the foreach-boilerplate repository first/as well)

#### Babel
We use [babel](http://babeljs.io/).

Babel is a transpiler for JavaScript best known for its ability to turn ES6 (the next version of JavaScript) into code that runs in your browser (or on your server) today. For example the following ES6 code:
```
const input = [1, 2, 3];
console.log(input.map(item => item + 1)); // [2, 3, 4]
```
is compiled by Babel to:
```
var input = [1, 2, 3];
console.log(input.map(function (item) {
  return item + 1;
})); // [2, 3, 4]
```
Which runs in just about any JavaScript environment.

Configuration of what subset(s) of ECMAScript we use can be found in the .babelrc file

#### Unit tests
We run unit tests with the [karma test runner](http://karma-runner.github.io)
We use [jasmine](https://jasmine.github.io/) syntax for unit tests.
Examples can be found under **js-src/main.test.js**

***

## Gulp 
[Gulp](https://gulpjs.com/) is used as the taskrunner for our buildprocess.

All project specific configuration can be found under gulp/config.js
This *should* be the only file that you need to touch. 

DO NOT MODIFY any gulp/tasks/ files unless absolutely necessary. If you're missing features, it's probably a better idea to add them in the foreach-boilerplate repository first, so later projects can benefit from them as well.

These are the tasks:

#### gulp (default task) -> same as gulp serve
This will spin up a [browsersync](https://browsersync.io/)  server.
any changes in templates or css will automatically reload the browser.
a standard gulp serve will proxy the devURL as configured in gulp/config.js
`gulp serve --slicing`
will default to serving the /slicing subfolder, and compiling css.js to there.
`gulp serve --hot`
enables [hot module replacement](https://webpack.js.org/concepts/hot-module-replacement/)

#### gulp js
Compiles your javascript using webpack (config can be found in webpack.config.js)
This task will watch changes by default.

When the --production flag is added, JS will be minified and sourcemaps will be omitted

When the --watch flag is added, the task will run continuously

#### gulp scss
Compiles your scss setup using SASS (more specifically, gulp-sass)
This task will watch changes by default.

When the --production flag is added, CSS will be minified

When the --watch flag is added, the task will run continuously

*optional subtask:
```
gulp scss:lint
``` 
Linting requires [Ruby](https://www.ruby-lang.org/en/documentation/installation/) and scss-lint (`gem install scss_lint`)

#### gulp images
Minify all images in a provided folder. JPG, PNG formats are supported.

*this task is disabled by default. to enable it, install these extra dependencies:*

```
yarn add gulp-imagemin@2.4.0 imagemin-pngquant@4.2.2 gulp-size@2.1.0 --save
```

#### gulp svg
Compile many smaller svg icons into one big "sprite" svg

*this task is disabled by default. to enable it, install these extra dependencies:*
```
yarn add gulp-svg-sprite@1.2.19 path@0.12.7 glob@7.0.3 gulp-size@2.1.0 --save
```

## Slicing

Please create html pages that are only for slicing under the subfolder /slicing.
