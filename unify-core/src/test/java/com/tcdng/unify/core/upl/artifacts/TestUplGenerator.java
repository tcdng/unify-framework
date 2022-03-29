/*
 * Copyright 2018-2022 The Code Department.
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
import com.tcdng.unify.core.upl.AbstractUplGenerator;

/**
 * Test UPL generator.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("test-uplgenerator")
public class TestUplGenerator extends AbstractUplGenerator {

    public TestUplGenerator() {
        super("test-generateddoc");
    }

    @Override
    public boolean isNewerVersion(String target) throws UnifyException {
        if ("type1".equals(target)) {
            // type 1 version is always stale
            return true;
        }

        return false;
    }

    @Override
    protected void generateBody(StringBuilder sb, String target) throws UnifyException {
        if ("type1".equals(target)) {
            sb.append("names:$s{Tweak}").append(" names:$s{Peak}").append(" names:$s{Leak}");
        } else {
            sb.append("names:$s{Gain}").append(" names:$s{Train}");
        }

        appendNewline(sb);
    }
}
