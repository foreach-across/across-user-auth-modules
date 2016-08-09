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

import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityFormViewFactory;
import com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;

import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.*;

/**
 * Alters the default form by moving the permissions controls to the right form.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class RoleFormProcessorAdapter extends WebViewProcessorAdapter<EntityFormView>
{
	@Override
	protected void extendViewModel( EntityFormView view ) {
		move( view.getViewElements(), "formGroup-permissions", EntityFormViewFactory.FORM_RIGHT );

		// move auditable properties in left form
		move( view.getViewElements(), "formGroup-created", EntityFormViewFactory.FORM_LEFT );
		move( view.getViewElements(), "formGroup-lastModified", EntityFormViewFactory.FORM_LEFT );

		find( view.getViewElements(), EntityFormViewFactory.FORM_LEFT, ContainerViewElement.class )
				.ifPresent(
						c -> sortRecursively( c,
						                      "formGroup-name", "formGroup-authority", "formGroup-description"
						)
				);

	}
}
