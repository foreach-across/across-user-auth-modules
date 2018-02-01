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
import $ from "jquery";

/**
 * Selector for a SELECT element.
 */
class SelectSelectorControl {
  constructor( node, controller ) {
    this.control = node;
    this.control.on( 'change', () => controller.setButtonState( this.value.id !== '' ) );
  }

  get value() {
    return {id: this.control.val(), label: this.control.find( 'option:selected' ).text()};
  }

  reset() {
    if ( this.control.selectpicker ) {
      this.control.selectpicker( 'val', '' );
    }
    else {
      this.control.val( '' ).change();
    }
  }
}

/**
 * Selector for text input element.
 */
class TextboxSelectorControl {
  constructor( node, controller ) {
    this.control = node;
    this.control
      .on( 'input', () => controller.setButtonState( this.value.id !== '' ) )
      .on( 'keyup keypress', e => {
        if ( e.which === 13 || e.which === 10 ) {
          e.preventDefault();
          controller.addItem( this );
          return false;
        }
      } );
  }

  get value() {
    return {id: this.control.val(), label: this.control.val()};
  }

  reset() {
    this.control.val( '' );
  }
}

/**
 * Controller for managing an ACL permissions form section.
 */
class AclPermissionsFormController {

  constructor( section ) {
    this.section = $( section );
    this.selectorRow = this.section.find( '.acl-permissions-form-selector' );
    this.selectorButton = this.selectorRow.find( '.acl-permissions-form-selector-button' );

    this.section.find( '.acl-permissions-form-remove-item a' ).on( 'click', e => this.removeItem( e ) );

    if ( !this.selectorButton ) {
      console.warn( "Expected button with class acl-permissions-form-selector-button was not found." );
    }

    let selector = this.createSelectorControl( this.selectorRow );
    this.selectorButton.on( 'click', () => this.addItem( selector ) );

    this.setButtonState( false );
  }

  /**
   * Create selector control depending on the DOM element present.
   *
   * @param selectorRow that contains the DOM element
   * @returns xSelectorControl
   */
  createSelectorControl( selectorRow ) {
    let control = selectorRow.find( 'select' );

    if ( control.length ) {
      return new SelectSelectorControl( control, this );
    }
    else {
      control = selectorRow.find( 'input[type=text]' );

      if ( control.length ) {
        return new TextboxSelectorControl( control, this );
      }
    }

    console.warn( "Could not create a valid selector control" );
    return null;
  }

  /**
   * Set the button state of the section.
   *
   * @param enabled if button should be clickable
   */
  setButtonState( enabled ) {
    if ( enabled ) {
      this.selectorButton.removeAttr( 'disabled' );
    }
    else {
      this.selectorButton.attr( 'disabled', 'disabled' );
    }
  }

  /**
   * Add an item to the list.
   */
  addItem( selector ) {
    if ( selector ) {
      let value = selector.value;
      if ( value && value.id && value.id !== '' ) {
        let table = this.section.find( 'table' );
        let existing = table.find( 'input[name$=".id"][value="' + value.id + '"]' );
        if ( existing.length === 0 ) {
          let templateRow = $( 'tr.acl-permissions-form-template-row', table ).clone();
          templateRow.removeClass( 'hidden' );

          let index = Date.now();
          $( 'input', templateRow ).each( function () {
            let control = $( this );
            control
              .attr( 'name', control.attr( 'name' ).replace( '{{itemIndex}}', index ) )
              .attr( 'id', control.attr( 'name' ) )
              .removeAttr( 'disabled' );
          } );
          templateRow.find( 'input[name$=".id"]' ).val( value.id );
          $( 'td:first', templateRow ).text( value.label );

          templateRow.insertBefore( $( 'tr.acl-permissions-form-no-entries-row', table ) );
          templateRow.find( '.acl-permissions-form-remove-item' ).on( 'click', e => this.removeItem( e ) );
        }
        else {
          let row = existing.closest( 'tr' );
          if ( row.hasClass( 'hidden' ) ) {
            row.insertBefore( $( 'tr.acl-permissions-form-no-entries-row', table ) );
            row.removeClass( 'hidden' );
          }
        }

        selector.reset();
        this.setButtonState( false );

        this.toggleNoEntriesRow();
      }
    }
  }

  removeItem( e ) {
    e.preventDefault();

    let row = $( e.currentTarget ).closest( 'tr' );

    row.addClass( 'hidden' );
    row.find( 'input[type=checkbox]' ).prop( 'checked', false );
    row.insertAfter( row.closest( 'table' ).find( 'tr.hidden:last' ) );

    this.toggleNoEntriesRow();
  }

  toggleNoEntriesRow() {
    this.section.find()
    this.section
      .find( 'tr.acl-permissions-form-no-entries-row' )
      .toggleClass( 'hidden', this.section.find( 'tr.acl-permissions-form-item-row:not(.hidden)' ).length > 0 );
  }
}

/**
 * Define JQuery extension.
 */
$.fn.aclPermissionsForm = function () {
  return this.each( function () {
    if ( !this._aclPermissionsForm ) {
      this._aclPermissionsForm = new AclPermissionsFormController( this );
    }
  } );
};

EntityModule.registerInitializer( container => $( '.acl-permissions-form-section', container ).aclPermissionsForm() );

export {AclPermissionsFormController};
