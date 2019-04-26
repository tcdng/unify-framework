/*
 * Copyright 2018-2019 The Code Department.
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

package com.tcdng.unify.web.data;

import com.tcdng.unify.core.data.TaggedBinaryMessage;
import com.tcdng.unify.web.RemoteCallParams;

/**
 * Tagged binary message remote call parameters.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TaggedBinaryMessageParams extends RemoteCallParams {

    private TaggedBinaryMessage taggedMessage;

    public TaggedBinaryMessageParams(String methodCode, String clientAppCode, TaggedBinaryMessage taggedMessage) {
        super(methodCode, clientAppCode);
        this.taggedMessage = taggedMessage;
    }

    public TaggedBinaryMessageParams(String methodCode, TaggedBinaryMessage taggedMessage) {
        super(methodCode);
        this.taggedMessage = taggedMessage;
    }

    public TaggedBinaryMessageParams() {

    }

    public TaggedBinaryMessage getTaggedMessage() {
        return taggedMessage;
    }
}
