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
package com.foreach.across.modules.entity.newviews.bootstrapui.processors.builder;

import com.foreach.across.modules.bootstrapui.elements.builder.FormGroupElementBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Attempts to set FormGroup as required based on the validation annotations specified.
 * By default the presence of {@link NotNull}, {@link NotBlank} or {@link NotEmpty} annotations will result
 * in the control being required.
 *
 * @author Arne Vandamme
 */
@Deprecated
public class FormGroupRequiredBuilderProcessor extends ValidationConstraintsBuilderProcessor<FormGroupElementBuilder>
{
	@Override
	protected void handleConstraint( FormGroupElementBuilder builder,
	                                 Annotation annotation,
	                                 Map<String, Object> annotationAttributes,
	                                 ConstraintDescriptor constraint ) {
		if ( isOfType( annotation, NotNull.class, NotEmpty.class, NotBlank.class ) ) {
			builder.required();

			/*FormControlElementBuilderSupport controlBuilder
					= builder.getControl( FormControlElementBuilderSupport.class );

			if ( controlBuilder != null ) {
				controlBuilder.required();
			}*/
		}
	}
}
