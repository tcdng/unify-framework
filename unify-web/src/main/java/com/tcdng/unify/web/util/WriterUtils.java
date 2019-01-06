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
package com.tcdng.unify.web.util;

import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.UnifyWebErrorConstants;

/**
 * Writer utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class WriterUtils {

    private static Map<String, String> actionToJSMap;

    private static Map<String, String> eventToJSMap;

    private static long refCounter;

    static {
        actionToJSMap = new HashMap<String, String>();
        actionToJSMap.put("forward", "ux.forward");
        actionToJSMap.put("submit", "ux.submit");
        actionToJSMap.put("post", "ux.post");
        actionToJSMap.put("posttopath", "ux.postToPath");
        actionToJSMap.put("postcommand", "ux.postCommand");
        actionToJSMap.put("openwindow", "ux.openWindow");
        actionToJSMap.put("download", "ux.download");
        actionToJSMap.put("clear", "ux.clear");
        actionToJSMap.put("disable", "ux.disable");
        actionToJSMap.put("show", "ux.show");
        actionToJSMap.put("hide", "ux.hide");
        actionToJSMap.put("delegate", "ux.delegate");
        actionToJSMap.put("setallchecked", "ux.setAllChecked");
        actionToJSMap.put("setcheckedpatternvalue", "ux.setCheckedPatternValue");
        actionToJSMap.put("populateselect", "ux.populateSelectOptions");
        actionToJSMap.put("openpopup", "ux.openPopup");
        actionToJSMap.put("hidepopup", "ux.hidePopup");
        actionToJSMap.put("repositionmenupopup", "ux.repositionMenuPopup");

        eventToJSMap = new HashMap<String, String>();
        eventToJSMap.put("onblur", "blur");
        eventToJSMap.put("onchange", "change");
        eventToJSMap.put("onclick", "click");
        eventToJSMap.put("ondblclick", "dblclick");
        eventToJSMap.put("onmouseover", "mouseover");
        eventToJSMap.put("onmouseout", "mouseout");
        eventToJSMap.put("onenter", "enter");
    }

    private WriterUtils() {

    }

    public static String getActionJSFunction(String actionType) throws UnifyException {
        String functionName = actionToJSMap.get(actionType);
        if (functionName == null) {
            throw new UnifyException(UnifyWebErrorConstants.MISSING_ACTION_HANDLER, actionType);
        }
        return functionName;
    }

    public static String getActionJSFunctionOptional(String actionType) throws UnifyException {
        String functionName = actionToJSMap.get(actionType);
        if (functionName == null) {
            return actionType;
        }
        return functionName;
    }

    public static String getEventJS(String eventType) throws UnifyException {
        String eventJS = eventToJSMap.get(eventType);
        if (eventJS == null) {
            throw new UnifyException(UnifyWebErrorConstants.MISSING_ACTION_HANDLER, eventType);
        }
        return eventJS;
    }

    public static long getNextRefId() {
        return ++refCounter;
    }
}
