/*
 * Copyright 2018-2023 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tcdng.unify.web.ui.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.list.AbstractZeroParamsListCommand;
import com.tcdng.unify.core.list.ZeroParams;
import com.tcdng.unify.web.annotation.WebApplication;
import com.tcdng.unify.web.ui.PageController;

/**
 * Web application list command.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("webapplicationlist")
public class WebApplicationListCommand extends AbstractZeroParamsListCommand {

    private static List<ListData> list;

    @Override
    public List<? extends Listable> execute(Locale locale, ZeroParams params) throws UnifyException {
        if (list == null) {
            synchronized (WebApplicationListCommand.class) {
                if (list == null) {
                    list = new ArrayList<ListData>();
                    for (UnifyComponentConfig unifyComponentConfig : getComponentConfigs(PageController.class)) {
                        WebApplication aa = unifyComponentConfig.getType().getAnnotation(WebApplication.class);
                        if (aa != null) {
                            list.add(new ListData(unifyComponentConfig.getName(), resolveMessage(locale, aa.value())));
                        }
                    }
                }
            }
        }
        return list;
    }
}
