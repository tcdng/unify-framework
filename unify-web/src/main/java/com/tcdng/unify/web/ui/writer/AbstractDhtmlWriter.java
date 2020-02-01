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

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.upl.AbstractUplComponentWriter;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.TokenUtils;
import com.tcdng.unify.web.RequestContextUtil;
import com.tcdng.unify.web.ThemeManager;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.PageAction;
import com.tcdng.unify.web.ui.PageManager;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.util.WebUtils;
import com.tcdng.unify.web.util.WriterUtils;

/**
 * Abstract base class for DHTML writers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractDhtmlWriter extends AbstractUplComponentWriter {

    @Configurable
    private ThemeManager themeManager;

    @Configurable
    private PageManager pageManager;

    /**
     * Writes tag attributes id, name, class, style and title.
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose attributes to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagAttributes(ResponseWriter writer, Widget widget) throws UnifyException {
        writer.write(" id=\"").write(widget.getId()).write("\"");

        String groupId = widget.getGroupId();
        if (groupId != null) {
            writer.write(" name=\"").write(groupId).write("\"");
        }

        writer.write(" class=\"").write(widget.getStyleClass());
        String valStyleClass = widget.getStyleClassValue();
        if (valStyleClass != null) {
            writer.write(" ").write(valStyleClass);
        }
        writer.write("\"");

        String style = widget.getStyle();
        if (style != null) {
            writer.write(" style=\"").write(style).write("\"");
        }

        String title = widget.getHint();
        if (title != null) {
            writer.write(" title=\"").write(title).write("\"");
        }

        if (widget.isSupportDisabled()) {
            if (widget.isContainerDisabled() || (!widget.isSupportReadOnly() && !widget.isContainerEditable())) {
                writer.write(" disabled");
            }
        }

        if (widget.isSupportReadOnly() && !widget.isContainerEditable()) {
            writer.write(" readonly");
        }
    }

    /**
     * Writes tag edit attributes
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose attributes to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagEditAttributes(ResponseWriter writer, Widget widget) throws UnifyException {
        if (widget.isContainerDisabled() || (!widget.isSupportReadOnly() && !widget.isContainerEditable())) {
            writer.write(" disabled=\"true\"");
        }

        if (widget.isSupportReadOnly() && !widget.isContainerEditable()) {
            writer.write(" readonly");
        }
    }

    /**
     * Writes tag id attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose id to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagId(ResponseWriter writer, Widget widget) throws UnifyException {
        writer.write(" id=\"").write(widget.getId()).write("\"");
    }

    /**
     * Writes tag id attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param id
     *            the id to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagId(ResponseWriter writer, String id) throws UnifyException {
        writer.write(" id=\"").write(id).write("\"");
    }

    /**
     * Writes tag name attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose name to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagName(ResponseWriter writer, Widget widget) throws UnifyException {
        String name = widget.getGroupId();
        if (StringUtils.isNotBlank(name)) {
            writer.write(" name=\"").write(name).write("\"");
        }
    }

    /**
     * Writes tag name attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param name
     *            the name to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagName(ResponseWriter writer, String name) throws UnifyException {
        writer.write(" name=\"").write(name).write("\"");
    }

    /**
     * Writes tag class attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose style class to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagStyleClass(ResponseWriter writer, Widget widget) throws UnifyException {
        writer.write(" class=\"").write(widget.getStyleClass());
        String valStyleClass = widget.getStyleClassValue();
        if (valStyleClass != null) {
            writer.write(" ").write(valStyleClass);
        }
        writer.write("\"");
    }

    /**
     * Writes tag class attribute with extra classes.
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose style class to write
     * @param extraLeading
     *            indicates extra classes should be leading
     * @param extraClasses
     *            the extra classes to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagStyleClass(ResponseWriter writer, Widget widget, boolean extraLeading,
            String... extraClasses) throws UnifyException {
        writer.write(" class=\"");
        if (extraLeading) {
            for (String extraClass : extraClasses) {
                if (extraClass != null) {
                    writer.write(extraClass).write(" ");
                }
            }
            writer.write(widget.getStyleClass());
        } else {
            writer.write(widget.getStyleClass());
            for (String extraClass : extraClasses) {
                if (extraClass != null) {
                    writer.write(" ").write(extraClass);
                }
            }
        }

        String valStyleClass = widget.getStyleClassValue();
        if (valStyleClass != null) {
            writer.write(" ").write(valStyleClass);
        }
        writer.write("\"");
    }

    /**
     * Writes tag class attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param styleClass
     *            the styleClass to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagStyleClass(ResponseWriter writer, String styleClass) throws UnifyException {
        writer.write(" class=\"").write(styleClass).write("\"");
    }

    /**
     * Writes tag style attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose style to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagStyle(ResponseWriter writer, Widget widget) throws UnifyException {
        if (StringUtils.isNotBlank(widget.getStyle())) {
            writer.write(" style=\"").write(widget.getStyle()).write("\"");
        }
    }

    /**
     * Writes tag style attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param style
     *            the style to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagStyle(ResponseWriter writer, String style) throws UnifyException {
        if (StringUtils.isNotBlank(style)) {
            writer.write(" style=\"").write(style).write("\"");
        }
    }

    /**
     * Writes combined tag style attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose style to write
     * @param style
     *            the style to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagStyle(ResponseWriter writer, Widget widget, String style) throws UnifyException {
        if (StringUtils.isNotBlank(style) || StringUtils.isNotBlank(widget.getStyle())) {
            writer.write(" style=\"");
            if (StringUtils.isNotBlank(widget.getStyle())) {
                writer.write(widget.getStyle());
            }

            if (StringUtils.isNotBlank(style)) {
                writer.write(style);
            }
            writer.write("\"");
        }
    }

    /**
     * Writes tag title attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose title to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagTitle(ResponseWriter writer, Widget widget) throws UnifyException {
        if (StringUtils.isNotBlank(widget.getHint())) {
            writer.write(" title=\"").write(widget.getHint()).write("\"");
        }
    }

    /**
     * Writes tag title attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param title
     *            the title to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagTitle(ResponseWriter writer, String title) throws UnifyException {
        if (StringUtils.isNotBlank(title)) {
            writer.write(" title=\"").write(title).write("\"");
        }
    }

    /**
     * Writes tag value attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param widget
     *            the widget whose value to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagValue(ResponseWriter writer, Widget widget) throws UnifyException {
        String value = widget.getStringValue();
        if (value != null) {
            writer.write(" value=\"").writeWithHtmlEscape(value).write("\"");
        }
    }

    /**
     * Writes tag value attribute.
     * 
     * @param writer
     *            the writer to use to write
     * @param value
     *            the value to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagValue(ResponseWriter writer, Object value) throws UnifyException {
        if (value != null) {
            writer.write(" value=\"").writeWithHtmlEscape(String.valueOf(value)).write("\"");
        }
    }

    /**
     * Writes tag readonly attribute
     * 
     * @param writer
     *            the writer to use
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagReadOnly(ResponseWriter writer) throws UnifyException {
        writer.write(" readonly");
    }

    /**
     * Writes tag id attribute.
     * 
     * @param sb
     *            the sting builder to use to write
     * @param id
     *            the id to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagId(StringBuilder sb, String id) throws UnifyException {
        sb.append(" id=\"").append(id).append("\"");
    }

    /**
     * Writes tag name attribute.
     * 
     * @param sb
     *            the sting builder to use to write
     * @param name
     *            the name to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagName(StringBuilder sb, String name) throws UnifyException {
        sb.append(" name=\"").append(name).append("\"");
    }

    /**
     * Writes tag class attribute.
     * 
     * @param sb
     *            the sting builder to use to write
     * @param styleClass
     *            the styleClass to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagStyleClass(StringBuilder sb, String styleClass) throws UnifyException {
        sb.append(" class=\"").append(styleClass).append("\"");
    }

    /**
     * Writes tag style attribute.
     * 
     * @param sb
     *            the sting builder to use to write
     * @param style
     *            the style to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagStyle(StringBuilder sb, String style) throws UnifyException {
        sb.append(" style=\"").append(style).append("\"");
    }

    /**
     * Writes tag title attribute.
     * 
     * @param sb
     *            the sting builder to use to write
     * @param title
     *            the title to write
     * @throws UnifyException
     *             if an error occurs
     */
    protected final void writeTagTitle(StringBuilder sb, String title) throws UnifyException {
        sb.append(" title=\"").append(title).append("\"");
    }

    /**
     * Writes file image HTML element.
     * 
     * @param writer
     *            the writer to use
     * @param src
     *            the image source file
     * @param id
     *            the image id
     * @param styleClass
     *            the style class
     * @param style
     *            the style
     * @throws UnifyException
     *             if an error occurs
     */
    protected void writeFileImageHtmlElement(ResponseWriter writer, String src, String id, String styleClass,
            String style) throws UnifyException {
        writer.write("<img");
        if (StringUtils.isNotBlank(id)) {
            writer.write(" id=\"").write(id).write("\"");
        }

        if (StringUtils.isNotBlank(styleClass)) {
            writer.write(" class=\"").write(styleClass).write("\"");
        }

        if (StringUtils.isNotBlank(style)) {
            writer.write(" style=\"").write(style).write("\"");
        }

        writer.write(" src=\"");
        writer.writeContextResourceURL("/resource/file", MimeType.IMAGE.template(), themeManager.expandThemeTag(src));
        writer.write("\">");
    }

    protected boolean writeCaption(ResponseWriter writer, Widget widget) throws UnifyException {
        String caption = widget.getCaption();
        if (caption != null) {
            writer.writeWithHtmlEscape(caption);
            return true;
        }

        return false;
    }

    protected boolean writeAttributeWithEscape(ResponseWriter writer, Widget widget, String attribute)
            throws UnifyException {
        String cattributeValue = widget.getUplAttribute(String.class, attribute);
        if (cattributeValue != null) {
            writer.writeWithHtmlEscape(cattributeValue);
            return true;
        }

        return false;
    }

    protected void writePostToPathJs(ResponseWriter writer, String path) throws UnifyException {
        writer.write(WriterUtils.getActionJSFunction("posttopath")).write("(\"");
        writer.writeContextURL(path);
        writer.write("\");");
    }

    protected void writeShortcutHandlerJs(ResponseWriter writer, String pageControllerName, String id,
            PageAction pageAction) throws UnifyException {
        String function = WriterUtils.getActionJSFunction(pageAction.getAction().toLowerCase());
        String eventParams = writeActionParamsJS(writer, null, function, id, pageAction, null, null, null);
        String shortcut = pageAction.getUplAttribute(String.class, "shortcut");
        writer.write("ux.setShortcut(\"").write(WebUtils.encodeShortcut(shortcut)).write("\",");
        writer.write(eventParams).write(");");
    }

    protected void writeEventJs(ResponseWriter writer, String event, String action, String pageName,
            String... targetPageNames) throws UnifyException {
        event = WriterUtils.getEventJS(event.toLowerCase());
        String function = WriterUtils.getActionJSFunction(action);
        String eventParams = writeActionParamsJS(writer, event, function, pageName, null, targetPageNames, null, null);
        writer.write("ux.setOnEvent(").write(eventParams).write(");");
    }

    protected void writePathEventHandlerJS(ResponseWriter writer, String id, String eventType, String action,
            String path) throws UnifyException {
        String event = WriterUtils.getEventJS(eventType.toLowerCase());
        String function = WriterUtils.getActionJSFunction(action.toLowerCase());
        String eventParams = writeActionParamsJS(writer, event, function, id, null, null, null, path);
        writer.write("ux.setOnEvent(").write(eventParams).write(");");
    }

    protected void writeOpenPopupJS(ResponseWriter writer, String event, String pageName, String frameId,
            String popupId, long stayOpenForMillSec, String onShowAction, String onShowParamObject, String onHideAction,
            String onHideParamObject) throws UnifyException {
        StringBuilder psb = new StringBuilder();
        psb.append("{\"popupId\":\"").append(popupId).append("\"");
        if (frameId != null) {
            psb.append(",\"frameId\":\"").append(frameId).append("\"");
        }

        psb.append(",\"stayOpenForMillSec\":").append(stayOpenForMillSec);
        if (onShowAction != null) {
            psb.append(",\"showHandler\":").append(WriterUtils.getActionJSFunctionOptional(onShowAction));
            if (onShowParamObject != null) {
                psb.append(",\"showParam\":").append(onShowParamObject);
            }
        }
        if (onHideAction != null) {
            psb.append(",\"hideHandler\":").append(WriterUtils.getActionJSFunctionOptional(onHideAction));
            if (onHideParamObject != null) {
                psb.append(",\"hideParam\":").append(onHideParamObject);
            }
        }
        psb.append("}");
        writeRefObjectEventHandlerJS(writer, pageName, event, "openpopup", psb.toString());
    }

    protected String writeActionParamsJS(ResponseWriter writer, String event, String function, String id,
            PageAction pageAction, String[] refPageNames, String refObject, String path) throws UnifyException {
        String getPathId = getRequestContextUtil().getResponsePathParts().getPathId();
        PageManager pageManager = getPageManager();
        String eventParams = "_act" + (WriterUtils.getNextRefId()) + "Prm";
        writer.write("var ").write(eventParams).write("={");
        if (StringUtils.isNotBlank(event)) {
            writer.write("\"uEvnt\":\"").write(event).write("\",");
        }
        if (StringUtils.isNotBlank(function)) {
            writer.write("\"uFunc\":").write(function).write(",");
        }
        writer.write("\"uId\":\"").write(id).write("\"");

        if (getRequestContextUtil().isRemoteViewer()) {
            writer.write(",\"uViewer\":\"").write(getRequestContextUtil().getRemoteViewer()).write("\"");
        }

        if (pageAction != null) {
            if ("ux.disable".equals(function)) {
                writer.write(",\"uFire\":true");
            }

            if (pageAction.isUplAttribute("validations")) {
                if (pageAction.getUplAttribute(Object.class, "validations") != null) {
                    writer.write(",\"uValidateAct\":\"").write(pageAction.getId()).write("\"");
                }
            }

            if (pageAction.isUplAttribute("command")) {
                boolean isPage = Page.class.isAssignableFrom(
                        getComponentType(getRequestContextUtil().getResponsePathParts().getControllerName()));
                String cmd = pageAction.getUplAttribute(String.class, "command");
                if (cmd != null) {
                    writer.write(",\"uCmdURL\":\"");
                    writer.writeCommandURL(getPathId);
                    writer.write('"');
                    writer.write(",\"uTrgCmd\":\"").write(cmd).write("\"");
                }

                String dynamicPanelPgNm = getRequestContextUtil().getDynamicPanelPageName();
                String targetPgNm = dynamicPanelPgNm;
                if (isPage) {
                    targetPgNm = pageManager.getPageName(pageAction.getParentLongName());
                }

                String commandTarget = pageAction.getUplAttribute(String.class, "target");
                if (commandTarget != null) {
                    targetPgNm = pageManager.getPageName(commandTarget);
                }

                if (targetPgNm == null) {
                    targetPgNm = pageManager.getPageName(pageAction.getParentLongName());
                }

                writer.write(",\"uTrgPnl\":\"").write(targetPgNm).write("\"");

                UplElementReferences uer = pageAction.getUplAttribute(UplElementReferences.class, "refresh");
                if (uer != null) {
                    writer.write(",\"uRefreshPnls\":").writeJsonArray(pageManager.getPageNames(uer.getLongNames()));
                } else {
                    writer.write(",\"uRefreshPnls\":[\"");
                    if (targetPgNm == dynamicPanelPgNm) {
                        writer.write(getRequestContextUtil().getDynamicPanelParentPageName());
                    } else {
                        writer.write(targetPgNm);
                    }
                    writer.write("\"]");
                }
            }

            List<String> componentList = pageManager.getExpandedReferences(pageAction.getId());
            writer.write(",\"uRef\":").writeJsonArray(componentList);

            List<String> valueComponentList = pageManager.getValueReferences(pageAction.getId());
            writer.write(",\"uVRef\":").writeJsonArray(valueComponentList);

            if (pageAction.isUplAttribute("valueList")) {
                String[] valueList = pageAction.getUplAttribute(String[].class, "valueList");
                if (valueList != null) {
                    writer.write(",\"uVal\":").writeJsonArray(valueList);
                }
            }

            if (pageAction.isUplAttribute("path")) {
                String actionPath = pageAction.getUplAttribute(String.class, "path");
                if (actionPath != null) {
                    if (TokenUtils.isNameTag(actionPath)) {
                        actionPath = getPathId + TokenUtils.extractTokenValue(actionPath);
                    } else if (TokenUtils.isQuickReferenceTag(actionPath)) {
                        actionPath = (String) ((ValueStore) getRequestContext().getQuickReference())
                                .retrieve(TokenUtils.extractTokenValue(actionPath));
                    }

                    writer.write(",\"uURL\":\"");
                    writer.writeContextURL(actionPath);
                    writer.write('"');
                }
            }

            if ("ux.download".equals(function)) {
                writer.write(",\"uURL\":\"");
                writer.writeContextResourceURL("/resource/downloadpath", MimeType.APPLICATION_OCTETSTREAM.template(),
                        pageAction.getUplAttribute(String.class, "resource"), null, true, false);
                writer.write('"');
            }

            if (pageAction.isUplAttribute("debounce")) {
                writer.write(",\"uIsDebounce\":");
                writer.write(pageAction.getUplAttribute(boolean.class, "debounce"));
            }
            
            if (pageAction.isUplAttribute("confirm")) {
                String confirm = pageAction.getUplAttribute(String.class, "confirm");
                if (StringUtils.isNotBlank(confirm)) {
                    writer.write(",\"uConf\":");
                    writeStringParameter(writer, confirm);
                    writer.write(",\"uIconIndex\":");
                    writer.write(pageAction.getUplAttribute(int.class, "iconIndex"));
                    writer.write(",\"uConfURL\":\"");
                    writer.writeContextURL(getPathId, "/confirm");
                    writer.write('"');
                }
            }
        } else if (refPageNames != null) {
            writer.write(",\"uRef\":").writeJsonArray(refPageNames);
        } else if (StringUtils.isNotBlank(refObject)) {
            writer.write(",\"uRef\":").write(refObject);
        } else if (path != null) {
            if (TokenUtils.isNameTag(path)) {
                path = getPathId + TokenUtils.extractTokenValue(path);
            }

            writer.write(",\"uURL\":\"");
            writer.writeContextURL(path);
            writer.write('"');
        }

        writer.write("};");
        return eventParams;
    }

    protected RequestContextUtil getRequestContextUtil() throws UnifyException {
        return (RequestContextUtil) getComponent(WebApplicationComponents.APPLICATION_REQUESTCONTEXTUTIL);
    }

    protected PageManager getPageManager() throws UnifyException {
        return pageManager;
    }

    protected void writeStringParameter(ResponseWriter writer, String string) {
        if (string != null && !string.trim().isEmpty()) {
            writer.write("\"").write(string).write("\"");
        } else {
            writer.write("null");
        }
    }

    private void writeRefObjectEventHandlerJS(ResponseWriter writer, String pageName, String eventType, String action,
            String refObject) throws UnifyException {
        String event = WriterUtils.getEventJS(eventType.toLowerCase());
        String function = WriterUtils.getActionJSFunction(action.toLowerCase());
        String eventParams = writeActionParamsJS(writer, event, function, pageName, null, null, refObject, null);
        writer.write("ux.setOnEvent(").write(eventParams).write(");");
    }

}
