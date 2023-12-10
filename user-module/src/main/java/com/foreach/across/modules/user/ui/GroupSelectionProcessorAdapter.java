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
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.springframework.web.bind.WebDataBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is currently not used.
 *
 * @author Arne Vandamme
 */
@Deprecated
public class GroupSelectionProcessorAdapter extends EntityViewProcessorAdapter
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
	public void initializeCommandObject( EntityViewRequest entityViewRequest,
	                                     EntityViewCommand command,
	                                     WebDataBinder dataBinder ) {
		command.addExtension( "groupSelector", new SelectedGroups() );
	}

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		ButtonViewElement btn = new ButtonViewElement();
		btn.setType( ButtonViewElement.Type.BUTTON_SUBMIT );
		btn.setText( "Update groups" );
		btn.setName( "entity.extensions[groupSelector].addToParent" );
		btn.setAttribute( "value", "true" );

		container.addChild( btn );

	}
}
