/*
 * Copyright 2014 The Code Department
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

import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.UserPlatform;
import com.tcdng.unify.core.upl.AbstractUplComponentWriter;

/**
 * Test document writer 1.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(target = UserPlatform.MOBILE, value = TestDocument.class)
@Component("testdocument-writer2")
public class TestDocumentWriter2 extends AbstractUplComponentWriter {

}
