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
package com.tcdng.unify.web.ui.writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.AbstractBehaviorWriter;
import com.tcdng.unify.web.ui.Behavior;
import com.tcdng.unify.web.ui.PageAction;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Abstract base class for page action writers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractPageActionWriter extends AbstractBehaviorWriter implements PageActionWriter {

    @Override
    public void writeBehavior(ResponseWriter writer, Behavior behavior, String id) throws UnifyException {
        PageAction pageAction = (PageAction) behavior;
        if (StringUtils.isNotBlank(behavior.getUplAttribute(String.class, "shortcut"))) {
            writeShortcutHandlerJs(writer, null, id, pageAction);
        }
    }

}
