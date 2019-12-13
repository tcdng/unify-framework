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
package com.tcdng.unify.web.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.RequestContext;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.format.DateTimeFormat;
import com.tcdng.unify.core.format.NumberSymbols;
import com.tcdng.unify.core.format.NumberType;
import com.tcdng.unify.core.format.Pattern;
import com.tcdng.unify.core.upl.UplComponent;
import com.tcdng.unify.core.upl.UplComponentWriter;
import com.tcdng.unify.core.util.QueryUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.RequestContextUtil;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.data.WebStringWriter;
import com.tcdng.unify.web.ui.writer.BehaviorWriter;
import com.tcdng.unify.web.ui.writer.DocumentLayoutWriter;
import com.tcdng.unify.web.ui.writer.LayoutWriter;
import com.tcdng.unify.web.ui.writer.PanelWriter;
import com.tcdng.unify.web.ui.writer.WidgetWriter;
import com.tcdng.unify.web.util.WebUtils;

/**
 * Default implementation of a response writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Singleton(false)
@Component(WebApplicationComponents.APPLICATION_RESPONSEWRITER)
public class ResponseWriterImpl extends AbstractUnifyComponent implements ResponseWriter {

    @Configurable
    private RequestContextUtil requestContextUtil;

    @Configurable("8192")
    private int initialBufferCapacity;

    private WebStringWriter buf;

    private List<WebStringWriter> secordaryList;

    private Map<Class<? extends UplComponent>, UplComponentWriter> writers;

    private boolean tableMode;

    @Override
    public ResponseWriter writeStructureAndContent(Widget component) throws UnifyException {
        ((WidgetWriter) getWriter(component)).writeStructureAndContent(this, component);
        return this;
    }

    @Override
    public ResponseWriter writeStructureAndContent(Widget component, String id) throws UnifyException {
        ((WidgetWriter) getWriter(component)).writeStructureAndContent(this, component, id);
        return this;
    }

    @Override
    public ResponseWriter writeStructureAndContent(DocumentLayout documentLayout, Document document)
            throws UnifyException {
        ((DocumentLayoutWriter) getWriter(documentLayout)).writeStructureAndContent(this, documentLayout, document);
        return this;
    }

    @Override
    public ResponseWriter writeStructureAndContent(Layout layout, Container container) throws UnifyException {
        ((LayoutWriter) getWriter(layout)).writeStructureAndContent(this, layout, container);
        return this;
    }

    @Override
    public ResponseWriter writeInnerStructureAndContent(Panel panel) throws UnifyException {
        ((PanelWriter) getWriter(panel)).writeInnerStructureAndContent(this, panel);
        return this;
    }

    @Override
    public ResponseWriter writeBehaviour(DocumentLayout documentLayout, Document document) throws UnifyException {
        ((DocumentLayoutWriter) getWriter(documentLayout)).writeBehaviour(this, documentLayout, document);
        return this;
    }

    @Override
    public ResponseWriter writeBehaviour(Widget component) throws UnifyException {
        ((WidgetWriter) getWriter(component)).writeBehavior(this, component);
        return this;
    }

    @Override
    public ResponseWriter writeBehavior(Behavior behavior, String id) throws UnifyException {
        ((BehaviorWriter) getWriter(behavior)).writeBehavior(this, behavior, id);
        return this;
    }

    @Override
    public ResponseWriter write(Object object) {
        buf.append(object);
        return this;
    }

    @Override
    public ResponseWriter writeNotNull(Object object) {
        if (object != null) {
            buf.append(object);
        }
        return this;
    }

    @Override
    public ResponseWriter writeHtmlFixedSpace() {
        buf.append("&nbsp;");
        return this;
    }

    @Override
    public ResponseWriter writeWithHtmlEscape(String string) {
        buf.appendHtmlEscaped(string);
        return this;
    }

    @Override
    public ResponseWriter writeJsonArray(String... stringArr) throws UnifyException {
        return writeJsonArray((Object[]) stringArr, true);
    }

    @Override
    public ResponseWriter writeJsonArray(Integer... intArr) throws UnifyException {
        return writeJsonArray((Object[]) intArr, false);
    }

    @Override
    public ResponseWriter writeJsonArray(Long... longArr) throws UnifyException {
        return writeJsonArray((Object[]) longArr, false);
    }

    @Override
    public ResponseWriter writeJsonArray(BigDecimal... bigArr) throws UnifyException {
        return writeJsonArray((Object[]) bigArr, false);
    }

    @Override
    public ResponseWriter writeJsonArray(Double... doubleArr) throws UnifyException {
        return writeJsonArray((Object[]) doubleArr, false);
    }

    @Override
    public ResponseWriter writeJsonArray(Boolean... boolArr) throws UnifyException {
        return writeJsonArray((Object[]) boolArr, false);
    }

    @Override
    public ResponseWriter writeJsonArray(Collection<?> col) throws UnifyException {
        buf.append('[');
        if (col != null) {
            boolean appendSym = false;
            for (Object val : col) {
                if (appendSym) {
                    buf.append(',');
                } else {
                    appendSym = true;
                }

                if (val == null) {
                    buf.append(val);
                } else {
                    if (val instanceof Number || val instanceof Boolean) {
                        buf.append(val);
                    } else {
                        writeJsonQuote(String.valueOf(val));
                    }
                }
            }
        }
        buf.append(']');
        return this;
    }

    @Override
    public ResponseWriter writeJsonPatternObject(Pattern[] pa) throws UnifyException {
        buf.append("[");
        boolean appendSym = false;
        for (Pattern p : pa) {
            if (appendSym) {
                buf.append(',');
            } else {
                appendSym = true;
            }
            buf.append("{\"flag\":").append(p.isFiller()).append(",\"length\":").append(p.getPattern().length())
                    .append(",\"target\":");
            if (p.isFiller()) {
                writeJsonQuote(p.getPattern());
            } else {
                buf.append('"').append(p.getTarget()).append('"');
            }
            buf.append("}");
        }
        buf.append("]");
        return this;
    }

    @Override
    public ResponseWriter writeJsonDateTimeFormatObject(DateTimeFormat[] dateTimeFormat) throws UnifyException {
        buf.append("[");
        boolean appendSym = false;
        for (DateTimeFormat dtf : dateTimeFormat) {
            if (appendSym) {
                buf.append(',');
            } else {
                appendSym = true;
            }

            if (dtf == null) {
                buf.append("null");
            } else {
                buf.append("{\"flag\":");
                List<? extends Listable> listableList = dtf.getList();
                if (listableList != null) {
                    buf.append(true).append(",\"min\":").append(0).append(",\"max\":").append(listableList.size() - 1)
                            .append(",\"list\":[");
                    boolean appendSym1 = false;
                    for (Listable listable : listableList) {
                        if (appendSym1) {
                            buf.append(',');
                        } else {
                            appendSym1 = true;
                        }
                        writeJsonQuote(listable.getListKey());
                    }
                    buf.append(']');
                } else {
                    int[] range = dtf.getRange();
                    buf.append(false).append(",\"min\":").append(range[0]).append(",\"max\":").append(range[1]);
                }
                buf.append("}");
            }
        }
        buf.append("]");
        return this;
    }

    @Override
    public ResponseWriter writeJsonQuote(String string) throws UnifyException {
        buf.appendJsonQuoted(string);
        return this;
    }

    @Override
    public ResponseWriter writeJsonQuote(WebStringWriter lsw) throws UnifyException {
        buf.appendJsonQuoted(lsw);
        return this;
    }

    @Override
    public ResponseWriter writeJsonPageNameAliasesArray() throws UnifyException {
        buf.append('[');
        Map<String, Set<String>> childAliasesMap = getRequestContextUtil().getRequestPageNameAliases();
        if (childAliasesMap != null) {
            boolean appendSym = false;
            for (Map.Entry<String, Set<String>> entry : childAliasesMap.entrySet()) {
                if (appendSym) {
                    buf.append(',');
                } else {
                    appendSym = true;
                }
                buf.append("{\"pn\":\"").append(entry.getKey()).append("\",");
                buf.append("\"aliases\":");
                writeJsonArray(entry.getValue());
                buf.append('}');
            }
        }
        buf.append(']');
        return this;
    }

    @Override
    public ResponseWriter writeJsonPathVariable(String name, String path) throws UnifyException {
        useSecondary(128);
        writeContextURL(path);
        WebStringWriter pathLsw = discardSecondary();

        buf.append("\"").append(name).append("\":");
        buf.appendJsonQuoted(pathLsw);
        return this;
    }

    @Override
    public ResponseWriter writeJsonPanel(Panel panel, boolean innerOnly) throws UnifyException {
        useSecondary();
        if (innerOnly) {
            writeInnerStructureAndContent(panel);
        } else {
            writeStructureAndContent(panel);
        }
        WebStringWriter htmlLsw = discardSecondary();

        useSecondary();
        writeBehaviour(panel);
        WebStringWriter scriptLsw = discardSecondary();

        buf.append("{\"target\":\"").append(panel.getId()).append('"');
        buf.append(",\"html\":");
        buf.appendJsonQuoted(htmlLsw);
        buf.append(",\"script\":");
        buf.appendJsonQuoted(scriptLsw);
        buf.append('}');
        return this;
    }

    @Override
    public ResponseWriter writeJsonSection(Widget widget, String sectionPageName) throws UnifyException {
        WidgetWriter widgetWriter = (WidgetWriter) getWriter(widget);
        useSecondary();
        widgetWriter.writeSectionStructureAndContent(this, widget, sectionPageName);
        WebStringWriter htmlLsw = discardSecondary();

        useSecondary();
        widgetWriter.writeSectionBehavior(this, widget, sectionPageName);
        WebStringWriter scriptLsw = discardSecondary();

        buf.append("{\"target\":\"").append(sectionPageName).append('"');
        buf.append(",\"html\":");
        buf.appendJsonQuoted(htmlLsw);
        buf.append(",\"script\":");
        buf.appendJsonQuoted(scriptLsw);
        buf.append('}');
        return this;
    }

    @Override
    public ResponseWriter writeContextURL(String path, String... pathElement) throws UnifyException {
        RequestContext requestContext = getRequestContext();
        if (requestContextUtil.isRemoteViewer()) {
            buf.append(getSessionContext().getUriBase());
        }

        buf.append(requestContext.getContextPath());
        buf.append(path);
        for (String element : pathElement) {
            buf.append(element);
        }
        buf.append('?').append(RequestParameterConstants.PAGE_INDICATOR).append("=true");
        return this;
    }

    @Override
    public ResponseWriter writeContextResourceURL(String path, String contentType, String resourceName)
            throws UnifyException {
        writeContextResourceURL(path, contentType, resourceName, null, false, false);
        return this;
    }

    @Override
    public ResponseWriter writeContextResourceURL(String path, String contentType, String resourceName, String scope,
            boolean attachment, boolean clearOnRead) throws UnifyException {
        writeContextURL(path);

        PageManager pageManager = getPageManager();
        buf.append('&').append(pageManager.getPageName("resourceName")).append("=")
                .append(encodeURLParameter(expandThemeTag(resourceName)));
        if (StringUtils.isNotBlank(contentType)) {
            buf.append('&').append(pageManager.getPageName("contentType")).append("=")
                    .append(encodeURLParameter(contentType));
        }

        if (StringUtils.isNotBlank(scope)) {
            buf.append('&').append(pageManager.getPageName("scope")).append("=").append(encodeURLParameter(scope));
        }

        if (attachment) {
            buf.append('&').append(pageManager.getPageName("attachment")).append("=").append(attachment);
        }

        if (clearOnRead) {
            buf.append('&').append(pageManager.getPageName("clearOnRead")).append("=").append(clearOnRead);
        }

        if (requestContextUtil.isRemoteViewer()) {
            buf.append('&').append(RequestParameterConstants.REMOTE_VIEWER).append("=")
                    .append(requestContextUtil.getRemoteViewer());
        }
        return this;
    }

    @Override
    public ResponseWriter writeURLParameter(String name, String value) throws UnifyException {
        buf.append('&').append(getPageManager().getPageName(name)).append("=").append(encodeURLParameter(value));
        return this;
    }

    @Override
    public ResponseWriter writeFileImageContextURL(String src) throws UnifyException {
        writeContextResourceURL("/resource/file", MimeType.IMAGE.template(), src);
        return this;
    }

    @Override
    public ResponseWriter writeScopeImageContextURL(String imageName) throws UnifyException {
        writeContextResourceURL("/resource/scope", MimeType.IMAGE.template(), imageName);
        return this;
    }

    @Override
    public ResponseWriter writeCommandURL() throws UnifyException {
        writeContextURL(getRequestContextUtil().getResponsePathParts().getPathId(), "/command");
        return this;
    }

    @Override
    public ResponseWriter writeCommandURL(String pageControllerName) throws UnifyException {
        if (QueryUtils.isValidStringCriteria(pageControllerName)) {
            writeContextURL(pageControllerName, "/command");
        } else {
            writeCommandURL();
        }
        return this;
    }

    @Override
    public String expandThemeTag(String resouceName) throws UnifyException {
        String themePath = null;
        if (getSessionContext().isUserLoggedIn()) {
            themePath = getSessionContext().getUserToken().getThemePath();
        }

        return WebUtils.expandThemeTag(resouceName, themePath);
    }

    @Override
    public ResponseWriter writeNameFormatRegex(boolean underscore, boolean dollar, boolean period, boolean dash)
            throws UnifyException {
        buf.append("/^[\\\\w");
        if (underscore) {
            buf.append("\\\\_");
        }

        if (dollar) {
            buf.append("\\\\$");
        }

        if (period) {
            buf.append("\\\\.");
        }

        if (dash) {
            buf.append("-");
        }
        buf.append("]*$/");
        return this;
    }

    @Override
    public ResponseWriter writeIdentifierFormatRegex() throws UnifyException {
        buf.append("/^([_a-zA-Z][_a-zA-Z0-9]*)?$/");
        return this;
    }

    @Override
    public ResponseWriter writeWordFormatRegex() throws UnifyException {
        buf.append("/^[a-zA-Z]*$/");
        return this;
    }

    @Override
    public ResponseWriter writeNumberFormatRegex(NumberSymbols numberSymbols, int precision, int scale,
            boolean acceptNegative, boolean useGrouping) throws UnifyException {
        buf.append("/^");
        if (acceptNegative) {
            appendOptionalFormattingRegex(numberSymbols.getNegativePrefix(), numberSymbols.getPositivePrefix());
        } else {
            appendOptionalFormattingRegex(numberSymbols.getPositivePrefix());
        }

        if (scale > 0 && !NumberType.INTEGER.equals(numberSymbols.getNumberType())) {
            precision = precision - scale;
        }

        String digit = escapeSpecial("d");
        int groupSize = numberSymbols.getGroupSize();
        if (precision > 0) {
            if (useGrouping) {
                int fullGroupCount = precision / groupSize;
                int remainder = precision % groupSize;
                if (remainder > 0) {
                    appendRangeOption(digit, remainder);
                }
                if (fullGroupCount > 0) {
                    buf.append("(");
                    appendOptionalFormattingRegex(String.valueOf(numberSymbols.getGroupingSeparator()));
                    appendRangeOption(digit, groupSize);
                    buf.append("){0,").append(fullGroupCount).append('}');
                }
            } else {
                appendRangeOption(digit, precision);
            }
        } else {
            if (useGrouping) {
                appendRangeOption(digit, groupSize);
                buf.append("(");
                appendOptionalFormattingRegex(String.valueOf(numberSymbols.getGroupingSeparator()));
                appendRangeOption(digit, groupSize);
                buf.append(")*");
            } else {
                buf.append("[").append(digit).append("]*");
            }
        }

        if (!NumberType.INTEGER.equals(numberSymbols.getNumberType())) {
            buf.append('(');
            buf.append(escapeSpecial(String.valueOf(numberSymbols.getDecimalSeparator())));
            if (scale > 0) {
                appendRangeOption(digit, scale);
            } else {
                buf.append("[").append(digit).append("]*");
            }
            buf.append(")?");
        }

        if (acceptNegative) {
            appendOptionalFormattingRegex(numberSymbols.getNegativeSuffix(), numberSymbols.getPositiveSuffix());
        } else {
            appendOptionalFormattingRegex(numberSymbols.getPositiveSuffix());
        }
        buf.append("$/");
        return this;
    }

    @Override
    public boolean isEmpty() {
        return buf.length() == 0;
    }

    @Override
    public WebStringWriter getStringWriter() {
        return buf;
    }

    @Override
    public void writeTo(Writer writer) throws UnifyException {
        try {
            buf.writeTo(writer);
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
    }

    @Override
    public void useSecondary() {
        useSecondary(initialBufferCapacity);
    }

    @Override
    public void useSecondary(int initialCapacity) {
        secordaryList.add(buf);
        buf = new WebStringWriter(initialCapacity);
    }

    @Override
    public WebStringWriter discardSecondary() {
        WebStringWriter discLsw = buf;
        buf = secordaryList.remove(secordaryList.size() - 1);
        return discLsw;
    }

    @Override
    public void reset(Map<Class<? extends UplComponent>, UplComponentWriter> writers) {
        this.writers = writers;
        if (buf == null || secordaryList == null || !buf.isEmpty() || !secordaryList.isEmpty()) {
            buf = new WebStringWriter(initialBufferCapacity);
            secordaryList = new ArrayList<WebStringWriter>();
        }
    }

    @Override
    public boolean isTableMode() {
        return tableMode;
    }

    @Override
    public void setTableMode(boolean parentStyleMode) {
        this.tableMode = parentStyleMode;
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private UplComponentWriter getWriter(UplComponent component) throws UnifyException {
        UplComponentWriter writer = writers.get(component.getClass());
        if (writer == null) {
            throw new UnifyException(UnifyWebErrorConstants.UPLCOMPONENT_NO_WRITER, component.getName());
        }
        return writer;
    }

    private PageManager getPageManager() throws UnifyException {
        return (PageManager) getComponent(WebApplicationComponents.APPLICATION_PAGEMANAGER);
    }

    private RequestContextUtil getRequestContextUtil() throws UnifyException {
        return (RequestContextUtil) getComponent(WebApplicationComponents.APPLICATION_REQUESTCONTEXTUTIL);
    }

    private String encodeURLParameter(String parameter) throws UnifyException {
        try {
            return URLEncoder.encode(parameter, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.UTIL_ERROR);
        }
    }
    
    private ResponseWriter writeJsonArray(Object[] arr, boolean quote) throws UnifyException {
        buf.append('[');
        if (arr != null) {
            if (quote) {
                boolean appendSym = false;
                for (Object val : arr) {
                    if (appendSym) {
                        buf.append(',');
                    } else {
                        appendSym = true;
                    }
                    if (val == null) {
                        buf.append(val);
                    } else {
                        writeJsonQuote(String.valueOf(val));
                    }
                }
            } else {
                boolean appendSym = false;
                for (Object val : arr) {
                    if (appendSym) {
                        buf.append(',');
                    } else {
                        appendSym = true;
                    }
                    
                    buf.append(val);
                }
            }
        }
        buf.append(']');
        return this;
    }

    private void appendRangeOption(String pattern, int range) {
        buf.append("[").append(pattern).append(")]{0,").append(range).append('}');
    }

    private void appendOptionalFormattingRegex(String string) {
        int len = 0;
        if (string != null && (len = string.length()) > 0) {
            buf.append('(');
            boolean appendSym = false;
            for (int i = 1; i <= len; i++) {
                if (appendSym)
                    buf.append('|');
                else
                    appendSym = true;
                buf.append(escapeSpecial(string.substring(0, i)));
            }
            buf.append(")?");
        }
    }

    private void appendOptionalFormattingRegex(String... strings) {
        List<String> sbList = new ArrayList<String>();
        Set<String> testSet = new HashSet<String>();
        for (String string : strings) {
            if (!testSet.contains(string)) {
                useSecondary(128);
                appendOptionalFormattingRegex(string);
                WebStringWriter psb = discardSecondary();
                if (psb.length() > 0) {
                    sbList.add(psb.toString());
                }
                testSet.add(string);
            }
        }

        if (!sbList.isEmpty()) {
            buf.append('(');
            boolean appendSym = false;
            for (String string : sbList) {
                if (appendSym)
                    buf.append('|');
                else
                    appendSym = true;
                buf.append(string);
            }
            buf.append(')');
        }
    }

    private String escapeSpecial(String string) {
        StringBuilder sb = new StringBuilder();
        escapeSpecial(sb, string);
        return sb.toString();
    }

    private void escapeSpecial(StringBuilder sb, String string) {
        int len = string.length();
        for (int i = 0; i < len; i++) {
            char ch = string.charAt(i);
            switch (ch) {
                case 'd':
                case '\\':
                case '[':
                case ']':
                case '(':
                case ')':
                case '|':
                case '$':
                case '.':
                case ',':
                    sb.append("\\\\");
                    sb.append(ch);
                    break;
                default:
                    sb.append(ch);
            }
        }
    }

}
