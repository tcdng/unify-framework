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

package com.tcdng.unify.core.data;

/**
 * Tagged message.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TaggedMessage {

    private String tag;

    private byte[] message;

    public TaggedMessage(String tag, byte[] message) {
        this.tag = tag;
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public byte[] getMessage() {
        return message;
    }
}
