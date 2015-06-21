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
package com.foreach.across.modules.entity.views.support;

import com.foreach.across.modules.entity.newviews.bootstrapui.util.PagingMessages;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.springframework.data.domain.Page;

/**
 * @author Arne Vandamme
 */
public class ListViewEntityMessages extends EntityMessages implements PagingMessages
{
	public static final String RESULTS_FOUND = "views.listView.resultsFound";
	public static final String PAGER = "views.listView.pager";
	public static final String PAGE = "views.listView.pager.page";
	public static final String OF_PAGES = "views.listView.pager.ofPages";
	public static final String NEXT_PAGE = "views.listView.pager.nextPage";
	public static final String PREVIOUS_PAGE = "views.listView.pager.previousPage";

	public ListViewEntityMessages( EntityMessageCodeResolver messageCodeResolver ) {
		super( messageCodeResolver );
	}

	@Override
	public String pagerText( Page currentPage, Object... args ) {
		return messageWithFallback( PAGER, currentPage.getNumber() + 1, currentPage.getTotalPages(), args );
	}

	@Override
	public String page( Page currentPage, Object... args ) {
		return messageWithFallback( PAGE, currentPage.getNumber() + 1, currentPage.getTotalPages(), args );
	}

	@Override
	public String ofPages( Page currentPage, Object... args ) {
		return messageWithFallback( OF_PAGES, currentPage.getNumber() + 1, currentPage.getTotalPages(), args );
	}

	@Override
	public String nextPage( Page currentPage, Object... args ) {
		return messageWithFallback( NEXT_PAGE, currentPage.getNumber() + 2, args );
	}

	@Override
	public String previousPage( Page currentPage, Object... args ) {
		return messageWithFallback( PREVIOUS_PAGE, currentPage.getNumber(), args );
	}

	@Override
	public String resultsFound( Page currentPage, Object... args ) {
		return messageWithFallback( RESULTS_FOUND,
		                            currentPage.getTotalElements(),
		                            messageCodeResolver.getNameSingularInline(),
		                            messageCodeResolver.getNamePluralInline(),
		                            args );
	}
}
