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
package com.tcdng.unify.web.ui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.web.ControllerResponseInfo;
import com.tcdng.unify.web.PageController;
import com.tcdng.unify.web.PageControllerPathInfo;
import com.tcdng.unify.web.response.HintUserResponse;
import com.tcdng.unify.web.response.LoadContentResponse;
import com.tcdng.unify.web.ui.AbstractPanel;
import com.tcdng.unify.web.ui.Page;
import com.tcdng.unify.web.ui.Widget;

/**
 * Panel used for holding document content. Designed to work with
 * {@link LoadContentResponse} and {@link HintUserResponse}
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-contentpanel")
@UplAttributes({ @UplAttribute(name = "path", type = String.class),
        @UplAttribute(name = "pathBinding", type = String.class),
        @UplAttribute(name = "tabbed", type = boolean.class),
        @UplAttribute(name = "titlebar", type = boolean.class),
        @UplAttribute(name = "sidebar", type = UplElementReferences.class) })
public class ContentPanel extends AbstractPanel {

    private Map<String, ContentInfo> contentByBeanIdMap;

    private List<ContentInfo> contentList;

    private int contentIndex;

    public ContentPanel() {
        contentByBeanIdMap = new HashMap<String, ContentInfo>();
        contentList = new ArrayList<ContentInfo>();
    }

    public String getPath() throws UnifyException {
        return getUplAttribute(String.class, "path", "pathBinding");
    }

    public boolean isTabbed() throws UnifyException {
        return getUplAttribute(boolean.class, "tabbed");
    }

    public boolean isTitleBar() throws UnifyException {
        return getUplAttribute(boolean.class, "titlebar");
    }

    public String getHintPanelId() throws UnifyException {
        return getPrefixedId("hint_");
    }

    public String getBusyIndicatorId() throws UnifyException {
        return getPrefixedId("busy_");
    }

    public String getTabItemId(int index) throws UnifyException {
        return getPrefixedId("tabitem_") + index;
    }

    public String getTabItemImgId(int index) throws UnifyException {
        return getPrefixedId("tabimg_") + index;
    }

    public boolean isSidebar() throws UnifyException {
        return getUplAttribute(UplElementReferences.class, "sidebar") != null;
    }

    public Widget getSidebar() throws UnifyException {
        return getWidgetByLongName(getShallowReferencedLongNames("sidebar").get(0));
    }

    public int getPageCount() {
        return contentList.size();
    }

    public int getPageIndex() {
        return contentIndex;
    }

    public ContentInfo getContentInfo(int pageIndex) throws UnifyException {
        return contentList.get(pageIndex);
    }

    public ContentInfo getCurrentContentInfo() {
        return contentList.get(contentIndex);
    }

    public Page getCurrentPage() {
        return contentList.get(contentIndex).getPage();
    }

    public void addContent(PageController pageController) throws UnifyException {
        PageControllerPathInfo pathInfo = pageController.getPathInfo();
        ContentInfo contentInfo = contentByBeanIdMap.get(pathInfo.getId());
        if (contentInfo != null) {
            contentIndex = contentInfo.getPageIndex();
            return;
        }

        contentIndex = contentList.size();
        contentInfo = new ContentInfo(pageController.getName(), pathInfo, pageController.getPage(), contentIndex);
        contentList.add(contentInfo);
        contentByBeanIdMap.put(pathInfo.getId(), contentInfo);
    }

    public void removeContent(PageController pageController) throws UnifyException {
        String beanId = pageController.getSessionId();
        ContentInfo contentInfo = contentByBeanIdMap.remove(beanId);
        if (contentInfo == null) {
            // TODO throw some exception here instead of return
            return;
        }

        int pageIndex = contentInfo.getPageIndex();
        contentList.remove(pageIndex);
        int size = contentList.size();
        for (int i = pageIndex; i < size; i++) {
            contentList.get(i).decPageIndex();
        }

        if (pageIndex <= contentIndex) {
            contentIndex--;
        }
    }

    public class ContentInfo {

        private ControllerResponseInfo respInfo;

        private PageControllerPathInfo pathInfo;

        private Page page;

        private int pageIndex;

        public ContentInfo(String beanName, PageControllerPathInfo pathInfo, Page page, int pageIndex) {
            respInfo = new ControllerResponseInfo(beanName, pathInfo.getId());
            this.pathInfo = pathInfo;
            this.page = page;
            this.pageIndex = pageIndex;
        }

        public ControllerResponseInfo getRespInfo() {
            return respInfo;
        }

        public String getOpenPath() {
            return pathInfo.getOpenPagePath();
        }

        public String getClosePath() {
            return pathInfo.getClosePagePath();
        }

        public String getSavePath() {
            return pathInfo.getSavePagePath();
        }

        public boolean isRemoteSave() {
            return pathInfo.isRemoteSave();
        }

        public Page getPage() {
            return page;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void decPageIndex() {
            pageIndex--;
        }
    }
}
