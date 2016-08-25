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

$( document ).ready( function ()
                     {
                         var requiredElements = $( "#entity\\.port, #entity\\.hostName, #ldapConnectorType" );
                         var testLdapConnectorOutput = $( ".js-form-test-ldapconnector-output" );
                         var testLdapConnectorButton = $( ".js-form-test-ldapconnector" );
                         testLdapConnectorButton.on( 'click', function ()
                         {
                             testLdapConnectorButton.addClass( "disabled" );
                             testLdapConnectorOutput.parent().removeClass( "hidden" );
                             testLdapConnectorOutput.html( 'Connecting...' );
                             // Test connector
                             $.post( 'test', $( "form[name='entityForm']" ).serialize(), function ( data )
                             {
                                 testLdapConnectorOutput.html( data.response );
                                 testLdapConnectorButton.removeClass( "disabled" );
                             } );
                             return false;
                         } );

                         requiredElements.change( function ()
                                                  {
                                                      enableOrDisableTestButton();
                                                  } );
                         enableOrDisableTestButton();

                         function enableOrDisableTestButton()
                         {
                             var testButtonAvailable = true;
                             requiredElements.each( function ( idx, el )
                                                    {
                                                        if ( $( el ).val().length == 0 ) {
                                                            testButtonAvailable = false;
                                                            return false;
                                                        }
                                                    } );
                             if ( testButtonAvailable ) {
                                 testLdapConnectorButton.removeClass( "disabled" );
                             }
                             else {
                                 testLdapConnectorButton.addClass( "disabled" );
                             }

                         }
                     } );