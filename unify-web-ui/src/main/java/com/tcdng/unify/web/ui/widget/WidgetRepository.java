/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.web.ui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.FactoryMaps;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Repository for widget instances.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class WidgetRepository {

    private static FactoryMaps<String, String, String> longNamesByShortName;

    static {
        longNamesByShortName = new FactoryMaps<String, String, String>()
            {

                @Override
                protected String createObject(String parentName, String shortName, Object... params) throws Exception {
                    return StringUtils.dotify(parentName, shortName);
                }

            };
    }

    private Map<String, Widget> widgets;

    private Map<String, Widget> widgetsByBase;

    private Map<String, WidgetNameInfo> widgetNameInfos;

    public WidgetRepository(Map<String, WidgetNameInfo> widgetNameInfos) {
        widgets = new HashMap<String, Widget>();
        this.widgetNameInfos = widgetNameInfos;
    }

    public Widget getWidget(String longName) throws UnifyException {
        return widgets.get(longName);
    }

    public Widget getWidgetByBaseId(String baseId) throws UnifyException {
        if (widgetsByBase == null) {
            synchronized (this) {
                if (widgetsByBase == null) {
                    widgetsByBase = new HashMap<String, Widget>();
                    for (Widget widget : widgets.values()) {
                        widgetsByBase.put(widget.getBaseId(), widget);
                    }
                }
            }
        }

        return widgetsByBase.get(baseId);
    }

    public Widget getWidget(String parentName, String shortName) throws UnifyException {
        return widgets.get(longNamesByShortName.get(parentName, shortName));
    }

    public boolean isWidget(String longName) throws UnifyException {
        return widgets.containsKey(longName);
    }

    public void putWidget(Widget widget) throws UnifyException {
        widgets.put(widget.getLongName(), widget);
    }

    public WidgetNameInfo getWidgetInfo(String ownerLongName) {
        return widgetNameInfos.get(ownerLongName);
    }

    public Set<String> getWidgetLongNames() {
        return widgets.keySet();
    }

    public List<String> getWidgetIds() throws UnifyException {
        List<String> list = new ArrayList<String>();
        for (Widget widget : widgets.values()) {
            list.add(widget.getId());
        }

        return list;
    }
}
