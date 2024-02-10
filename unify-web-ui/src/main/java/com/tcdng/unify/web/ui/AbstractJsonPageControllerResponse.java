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
package com.tcdng.unify.web.ui;

import java.util.Collection;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.FormatHelper;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Convenient JSON object page controller response.
 * 
 * @author The Code Department
 * @version 1.0
 */
public abstract class AbstractJsonPageControllerResponse extends AbstractPageControllerResponse {

    private String handlerName;

    private boolean processSaveList;

    public AbstractJsonPageControllerResponse(String handlerName, boolean processSaveList) {
        this.handlerName = handlerName;
        this.processSaveList = processSaveList;
    }

	@Override
    public void generate(ResponseWriter writer, Page page) throws UnifyException {
        writer.write("{\"handler\":\"").write(handlerName).write("\"");
        PageRequestContextUtil reqUtils = getRequestContextUtil();
        doGenerate(writer, page);

        if (reqUtils.isFocusOnWidgetOrDefault()) {
            writer.write(",\"focusOnWidget\":\"").write(reqUtils.getFocusOnWidgetIdOrDefault()).write("\"");
            reqUtils.clearFocusOnWidget();
        }

        if (processSaveList) {
            List<String> saveList = reqUtils.getOnSaveContentWidgets();
            if (DataUtils.isNotBlank(saveList)) {
                writer.write(",\"pSaveList\":").writeJsonArray(saveList);
                reqUtils.clearOnSaveContentWidgets();
            }
        }

        writer.write("}");
    }
    
    protected void appendRegisteredDebounceWidgets(ResponseWriter writer, boolean clear) throws UnifyException {
        Collection<String> widgetIds = getRequestContextUtil().getAndClearRegisteredDebounceWidgetIds();
        if (!DataUtils.isBlank(widgetIds)) {
            writer.write(",\"debounceClear\":").write(clear);
            writer.write(",\"debounceList\":").writeJsonArray(widgetIds);
        }
    }

    protected String getTimestampedResourceName(String resourceName) throws UnifyException {
        int index = resourceName.indexOf('.');
        if (index > 0) {
            return StringUtils.underscore(resourceName.substring(0, index)) + "_"
                    + getFormatHelper().formatNow(FormatHelper.yyyyMMdd_HHmmss) + resourceName.substring(index);

        }

        return StringUtils.underscore(resourceName) + "_" + getFormatHelper().formatNow(FormatHelper.yyyyMMdd_HHmmss);
    }

    protected abstract void doGenerate(ResponseWriter writer, Page page) throws UnifyException;
}
