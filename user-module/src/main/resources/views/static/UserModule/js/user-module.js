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

$( document ).ready( function () {

    $.each( $( '.js-typeahead' ), function ( i, $typeahead ) {
        var map = {};

        var container = $( this );
        var $typeaheadInstance = $( this ).find( ".js-typeahead-input" );
        var url = $typeaheadInstance.data( "typeaheadUrl" ) + '?query=%QUERY';
        var engine = new Bloodhound( {
            datumTokenizer: Bloodhound.tokenizers.whitespace,
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            remote: {
                wildcard: '%QUERY',
                url: url,
                prepare: function ( query, settings ) {
                    settings.url = settings.url.replace( '%QUERY', query );
                    if ( $( ".js-customer-id" ).val() ) {
                        settings.url += '&customer=' + $( ".js-customer-id" ).val();
                    }
                    if ( $( ".js-project-id" ).val() ) {
                        settings.url += '&project=' + $( ".js-project-id" ).val();
                    }
                    return settings;
                },
                filter: function ( data ) {
                    var suggestions = [];
                    map = {};
                    $.each( data, function ( i, suggestion ) {
                        map[suggestion.description] = suggestion;
                        suggestions.push( suggestion );
                    } );
                    return suggestions;
                }
            }
        } );
        engine.initialize();
        $typeaheadInstance.typeahead( {
                                          highlight: true,
                                          minLength: 1
                                      }, {
                                          source: engine.ttAdapter(),
                                          display: 'description',
                                          templates: {
                                              notFound: ['<div class="empty-message"><b>Not Found</b></div>'],
                                              suggestion: function ( item ) {
                                                  return '<div>' + item.description + '</div>';
                                              }
                                          }
                                      } );
        $typeaheadInstance.on( 'typeahead:select', function ( evt, item ) {
            if ( container.find( '.js-typeahead-item input[value=' + item.id + ']' ).length == 0 ) {
                var template = container.find( '.js-typeahead-template' ).clone( false );
                template.removeClass( 'hidden js-typeahead-template' );
                template.addClass( 'js-typeahead-item' );

                template.find( '[data-as-property]' ).each( function ( i, node ) {
                    node.innerText = item[$( node ).attr( 'data-as-property' )];
                } );
                template.find( '[type=hidden]' ).val( item.id ).removeAttr( 'disabled' );
                container.find( 'table' ).append( template );

                template.find( 'a' ).on( 'click', function () {
                    $( this ).closest( 'tr' ).remove();
                } );
            }

            $typeaheadInstance.typeahead( 'val', '' );
            /*
             $typeaheadInstance.parent().parent().find( ".js-typeahead-suggestion-id" ).val( map[item].id );
             if ( $( ".js-project-item-id" ).val() !== undefined && $( ".js-project-item-id" ).val() !== 0 && $(
             ".js-project-item-id" ).val() !== "" ) {
             $( ".js-new-log-wrapper" ).removeClass( 'hidden' );
             }
             else {
             $( ".js-new-log-wrapper" ).addClass( 'hidden' );
             }
             */
        } );

        container.find( '.js-typeahead-item a' ).on( 'click', function () {
            $( this ).closest( 'tr' ).remove();
        } )
    } );

} );