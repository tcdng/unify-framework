/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.upl.artifacts;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.core.upl.UplElementReferences;

/**
 * Test class for deeper document test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("test-upldocumente")
@UplBinding("com/tcdng/unify/core/resources/test-upldocumente.upl")
@UplAttributes({ @UplAttribute(name = "rootList", type = UplElementReferences.class) })
public class TestDocumentE extends AbstractTestDocument {

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
