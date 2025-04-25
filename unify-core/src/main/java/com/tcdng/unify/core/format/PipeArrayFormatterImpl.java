/*
 * Copyright 2018-2025 The Code Department.
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

package com.tcdng.unify.core.format;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Pipe array formatter.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(name = "pipearrayformat", description = "Pipe Array Format")
public class PipeArrayFormatterImpl extends AbstractFormatter<String[]> {

    public PipeArrayFormatterImpl() {
        super(String[].class);
    }

    @Override
    protected String doFormat(String[] value) throws UnifyException {
        return value != null ? StringUtils.concatenateUsingSeparator('|', (Object[]) value) : null;
    }

    @Override
    protected String[] doParse(String string) throws UnifyException {
        return StringUtils.trimAll(string != null? StringUtils.split(string, "\\|") : null);
    }

    @Override
    public String getPattern() throws UnifyException {
        return null;
    }

}
