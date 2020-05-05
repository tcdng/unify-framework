/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.web;

import com.tcdng.unify.core.constant.MimeType;

/**
 * AggregateItem mapping data object.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Result {

    private MimeType mimeType;

    private PageControllerResponse[] pageControllerResponses;

    public Result(PageControllerResponse[] responses) {
        this(MimeType.APPLICATION_JSON, responses);
    }

    public Result(MimeType mimeType, PageControllerResponse[] responses) {
        this.mimeType = mimeType;
        this.pageControllerResponses = responses;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public PageControllerResponse[] getResponses() {
        return pageControllerResponses;
    }
}
