/*
 * Copyright (c) 2018-2025 The Code Department.
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
package com.tcdng.unify.web.ui.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.UnifyWebUIErrorConstants;

/**
 * Writer utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class WriterUtils {

    private static Map<String, String> actionToJSMap;

    private static Map<String, String> eventToJSMap;

    private static Map<String, String> jsAliasMap;

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
        actionToJSMap.put("populateselect", "ux.populateSelectOptions");
        actionToJSMap.put("openpopup", "ux.openPopup");
        actionToJSMap.put("hidepopup", "ux.hidePopup");
        actionToJSMap.put("repositionmenupopup", "ux.repositionMenuPopup");
        actionToJSMap = Collections.unmodifiableMap(actionToJSMap);

        jsAliasMap = new HashMap<String, String>();
        jsAliasMap.put("ux.forward", "ux01");
        jsAliasMap.put("ux.submit", "ux02"); 
        jsAliasMap.put("ux.post", "ux03");   
        jsAliasMap.put("ux.postToPath", "ux04");   
        jsAliasMap.put("ux.postCommand", "ux05");   
        jsAliasMap.put("ux.openWindow", "ux06");
        jsAliasMap.put("ux.download", "ux07");
        jsAliasMap.put("ux.clear", "ux08");
        jsAliasMap.put("ux.disable", "ux09");
        jsAliasMap.put("ux.show", "ux0a");
        jsAliasMap.put("ux.hide", "ux0b");
        jsAliasMap.put("ux.delegate", "ux0c");
        jsAliasMap.put("ux.setAllChecked", "ux0d");
        jsAliasMap.put("ux.populateSelectOptions", "ux0e");
        jsAliasMap.put("ux.openPopup", "ux0f");  
        jsAliasMap.put("ux.hidePopup", "ux10");
        jsAliasMap.put("ux.repositionMenuPopup", "ux11");
        jsAliasMap.put("ux.setFocus", "ux12");
        jsAliasMap.put("ux.rigAssignmentBox", "ux13");  
        jsAliasMap.put("ux.rigCheckbox", "ux14");  
        jsAliasMap.put("ux.rigChecklist", "ux15");  
        jsAliasMap.put("ux.rigDateField", "ux16");  
        jsAliasMap.put("ux.rigDebitCreditField", "ux17");  
        jsAliasMap.put("ux.rigDropdownChecklist", "ux18");  
        jsAliasMap.put("ux.dcHidePopup", "ux19"); 
        jsAliasMap.put("ux.rigDurationSelect", "ux1a");  
        jsAliasMap.put("ux.rigFileAttachment", "ux1b");  
        jsAliasMap.put("ux.rigFileDownload", "ux1c");  
        jsAliasMap.put("ux.rigFileUploadView", "ux1d");  
        jsAliasMap.put("ux.rigFileUpload", "ux1e");  
        jsAliasMap.put("ux.rigDragAndDropPopup", "ux1f");  
        jsAliasMap.put("ux.rigLinkGrid", "ux20");  
        jsAliasMap.put("ux.rigMoneyField", "ux21");  
        jsAliasMap.put("ux.mfOnShow", "ux22"); 
        jsAliasMap.put("ux.rigMultiSelect", "ux23");  
        jsAliasMap.put("ux.rigOptionsTextArea", "ux24");  
        jsAliasMap.put("ux.rigPeriodField", "ux25");  
        jsAliasMap.put("ux.pfOnShow", "ux26");
        jsAliasMap.put("ux.rigPhotoUpload", "ux27");  
        jsAliasMap.put("ux.rigRadioButtons", "ux28");  
        jsAliasMap.put("ux.rigSearchField", "ux29");  
        jsAliasMap.put("ux.sfWireResult", "ux2a");  
        jsAliasMap.put("ux.sfOnShow", "ux2b"); 
        jsAliasMap.put("ux.rigSingleSelect", "ux2c");  
        jsAliasMap.put("ux.ssOnShow", "ux2d");
        jsAliasMap.put("ux.rigTable", "ux2e");  
        jsAliasMap.put("ux.rigTextArea", "ux2f");  
        jsAliasMap.put("ux.rigTextClock", "ux30");  
        jsAliasMap.put("ux.setTextRegexFormatting", "ux31");  
        jsAliasMap.put("ux.rigTimeField", "ux32");  
        jsAliasMap.put("ux.rigDragAndDropPopup", "ux33");  
        jsAliasMap.put("ux.rigTreeExplorer", "ux34");  
        jsAliasMap.put("ux.rigDesktopType2", "ux35");  
        jsAliasMap.put("ux.rigAccordion", "ux36");  
        jsAliasMap.put("ux.rigContentPanel", "ux37");  
        jsAliasMap.put("ux.rigDetachedPanel", "ux38");  
        jsAliasMap.put("ux.rigFixedContentPanel", "ux39");  
        jsAliasMap.put("ux.loadRemoteDocViewPanel", "ux3a");  
        jsAliasMap.put("ux.rigSplitPanel", "ux3b");  
        jsAliasMap.put("ux.rigStretchPanel", "ux3c");  
        jsAliasMap.put("ux.rigTabbedPanel", "ux3d");  
        jsAliasMap.put("ux.rigValueAccessor", "ux3e");
        jsAliasMap.put("ux.setShortcut", "ux3f");
        jsAliasMap.put("ux.setOnEvent", "ux40"); 
        jsAliasMap.put("ux.setDelayedPanelPost", "ux41");
        jsAliasMap.put("ux.optionsTextAreaOnShow", "ux42");
        jsAliasMap.put("ux.rigFileUploadButton", "ux43");  
        jsAliasMap.put("ux.rigRichTextEditor", "ux44");
        jsAliasMap.put("ux.rigPalette", "ux45");  
        jsAliasMap.put("ux.rigTarget", "ux46");  
        
        eventToJSMap = new HashMap<String, String>();
        eventToJSMap.put("onblur", "blur");
        eventToJSMap.put("onchange", "change");
        eventToJSMap.put("oninput", "input");
        eventToJSMap.put("onclick", "click");
        eventToJSMap.put("ondblclick", "dblclick");
        eventToJSMap.put("onmouseover", "mouseover");
        eventToJSMap.put("onmouseout", "mouseout");
        eventToJSMap.put("onenter", "enter");
        eventToJSMap.put("onfocus", "focus");
        eventToJSMap = Collections.unmodifiableMap(eventToJSMap);
    }

    private WriterUtils() {

    }

    public static void registerJSAlias(String function, String alias) {
        jsAliasMap.put(function, alias);
    }
    
    public static String getActionJSFunction(String actionType) throws UnifyException {
        String functionName = actionToJSMap.get(actionType);
        if (functionName == null) {
            throw new UnifyException(UnifyWebUIErrorConstants.MISSING_ACTION_HANDLER, actionType);
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
            throw new UnifyException(UnifyWebUIErrorConstants.MISSING_ACTION_HANDLER, eventType);
        }
        return eventJS;
    }

    public static String getActionJSAlias(String function) throws UnifyException {
        String alias = jsAliasMap.get(function);
        if (alias == null) {
            throw new UnifyException(UnifyWebUIErrorConstants.MISSING_ACTION_HANDLER, alias);
        }
        return alias;
    }

    public static long getNextRefId() {
        return ++refCounter;
    }
}
