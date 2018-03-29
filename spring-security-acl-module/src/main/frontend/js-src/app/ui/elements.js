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

//
//  general UI elements behavior get attached here
//

// necessary so fancybox can attach itself to this version!
import $ from "jquery";
import "jquery.fancybox";
import logger from "logger";

// $.fancybox available here
$( () => {
  logger.log( "fancybox got loaded" );
} );

export default "elements loaded";
