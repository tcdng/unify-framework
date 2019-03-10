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
package com.tcdng.unify.core.transform;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Tooling;

/**
 * Forward transforms a string to lower case letters. Performs no transformation
 * on reverse.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Tooling(name = "lowerCaseTrans", description = "Lower Case Transformer")
@Component("lowercase-transformer")
public class LowerCaseTransformer extends AbstractStringTransformer {

    @Override
    public String forwardTransform(String value) throws UnifyException {
        if (value != null) {
            return value.toLowerCase();
        }
        return null;
    }
}
