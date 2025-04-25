/*
 * Copyright (c) 2018-2025 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.unify.web.ui;

import com.tcdng.unify.core.constant.MimeType;

/**
 * AggregateItem mapping data object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Result {

	private final MimeType mimeType;

	private final PageControllerResponse[] pageControllerResponses;

	private final boolean reload;

	public Result(PageControllerResponse[] responses) {
		this(MimeType.APPLICATION_JSON, responses);
	}

	public Result(PageControllerResponse[] responses, boolean reload) {
		this(MimeType.APPLICATION_JSON, responses, reload);
	}

	public Result(MimeType mimeType, PageControllerResponse[] responses) {
		this(mimeType, responses, false);
	}

	public Result(MimeType mimeType, PageControllerResponse[] responses, boolean reload) {
		this.mimeType = mimeType;
		this.pageControllerResponses = responses;
		this.reload = reload;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public PageControllerResponse[] getResponses() {
		return pageControllerResponses;
	}

	public boolean isReload() {
		return reload;
	}
}
