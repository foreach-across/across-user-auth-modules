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

package com.foreach.across.modules.user.ui;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.entity.controllers.EntityControllerAttributes;
import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class GroupSelectionProcessorAdapter extends WebViewProcessorAdapter<EntityListView>
{
	static class SelectedGroups
	{
		private List<Group> groups = new ArrayList<>();
		private boolean addToParent;

		public List<Group> getGroups() {
			return groups;
		}

		public void setGroups( List<Group> groups ) {
			this.groups = groups;
		}

		public boolean isAddToParent() {
			return addToParent;
		}

		public void setAddToParent( boolean addToParent ) {
			this.addToParent = addToParent;
		}
	}
	@Override
	protected void registerCommandExtensions( EntityViewCommand command ) {
		command.addExtensions( "groupSelector", new SelectedGroups());
	}

	@Override
	protected void modifyViewElements( ContainerViewElement elements ) {
		ButtonViewElement btn = new ButtonViewElement();
		btn.setType( ButtonViewElement.Type.BUTTON_SUBMIT );
		btn.setText( "Update groups" );
		btn.setName( "entity.extensions[groupSelector].addToParent" );
		btn.setAttribute( "value", "true" );

		elements.addChild( btn );

	}

	@Override
	protected void applyCustomPostProcessing( WebViewCreationContext creationContext, EntityListView view ) {
		EntityViewCommand command = ((EntityViewCommand) view.getAttribute( EntityControllerAttributes.VIEW_COMMAND ));

		System.out.println( "hallo" );
	}
}
