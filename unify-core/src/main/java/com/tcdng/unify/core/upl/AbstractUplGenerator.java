/*
 * Copyright 2018-2024 The Code Department.
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

package com.tcdng.unify.core.upl;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Convenient base class for UPL generators.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractUplGenerator extends AbstractUnifyComponent implements UplGenerator {

    private String uplComponentName;

    public AbstractUplGenerator(String uplComponentName) {
        this.uplComponentName = uplComponentName;
    }

    @Override
    public String getUplComponentName() throws UnifyException {
        return uplComponentName;
    }

    @Override
    public String generateUplSource(String target) throws UnifyException {
        StringBuilder sb = new StringBuilder();
        // Write header
        sb.append('!').append(uplComponentName);
        appendNewline(sb);

        // Write body
        generateBody(sb, target);

        return sb.toString();
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected void appendNewline(StringBuilder sb) throws UnifyException {
        sb.append(getLineSeparator());
    }

    protected abstract void generateBody(StringBuilder sb, String target) throws UnifyException;
}
