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
import com.tcdng.unify.web.constant.ClosePageMode;
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
        @UplAttribute(name = "pathBinding", type = String.class), @UplAttribute(name = "tabbed", type = boolean.class),
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

    public String getTabPaneId() throws UnifyException {
        return getPrefixedId("tp_");
    }

    public String getMenuId() throws UnifyException {
        return getPrefixedId("m_");
    }

    public String getMenuBaseId() throws UnifyException {
        return getPrefixedId("mb_");
    }

    public String getMenuCloseId() throws UnifyException {
        return getPrefixedId("mic_");
    }

    public String getMenuCloseOtherId() throws UnifyException {
        return getPrefixedId("mico_");
    }

    public String getMenuCloseAllId() throws UnifyException {
        return getPrefixedId("mica_");
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
        contentInfo = new ContentInfo(pageController, contentIndex);
        contentList.add(contentInfo);
        contentByBeanIdMap.put(pathInfo.getId(), contentInfo);
    }

    public void removeContent(PageController pageController) throws UnifyException {
        ClosePageMode mode = getRequestTarget(ClosePageMode.class);
        if (mode == null) {
            mode = ClosePageMode.CLOSE;
        }
        
        boolean removeSrc = false;
        switch(mode) {
            case CLOSE:
                removeSrc = true;
                break;
            case CLOSE_ALL:
                removeSrc = true;
            case CLOSE_OTHERS:
                // Close others
                List<ContentInfo> refContentList = new ArrayList<ContentInfo>(contentList);
                for(int i = 1; i < refContentList.size(); i++) {
                    PageController refPageController = refContentList.get(i).getPageController();
                    if (refPageController != pageController) {
                        refPageController.closePage();
                        actRemoveContent(refPageController);
                    }                    
                }
                break;
            default:
                break;   
        }
        
        if (removeSrc) {
            actRemoveContent(pageController);
        }
    }
    
    private void actRemoveContent(PageController pageController) throws UnifyException {
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

        private PageController pageController;

        private int pageIndex;

        public ContentInfo(PageController pageController, int pageIndex) throws UnifyException {
            respInfo = new ControllerResponseInfo(pageController.getName(), pageController.getPathInfo().getId());
            this.pageController = pageController;
            this.pageIndex = pageIndex;
        }

        public ControllerResponseInfo getRespInfo() {
            return respInfo;
        }

        public String getColorScheme() throws UnifyException {
            return pageController.getPathInfo().getColorScheme();
        }

        public String getOpenPath() throws UnifyException {
            return pageController.getPathInfo().getOpenPagePath();
        }

        public String getClosePath() throws UnifyException {
            return pageController.getPathInfo().getClosePagePath();
        }

        public String getSavePath() throws UnifyException {
            return pageController.getPathInfo().getSavePagePath();
        }

        public boolean isRemoteSave() throws UnifyException {
            return pageController.getPathInfo().isRemoteSave();
        }

        public Page getPage() {
            return pageController.getPage();
        }

        public PageController getPageController() {
            return pageController;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void decPageIndex() {
            pageIndex--;
        }
    }
}
