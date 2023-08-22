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
package com.tcdng.unify.web.ui.widget.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.constant.ClosePageMode;
import com.tcdng.unify.web.ui.PageAttributeConstants;
import com.tcdng.unify.web.ui.PageBean;
import com.tcdng.unify.web.ui.PagePathInfoRepository;
import com.tcdng.unify.web.ui.response.HintUserResponse;
import com.tcdng.unify.web.ui.response.LoadContentResponse;
import com.tcdng.unify.web.ui.widget.AbstractContentPanel;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * Panel used for holding document content. Designed to work with
 * {@link LoadContentResponse} and {@link HintUserResponse}
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-contentpanel")
@UplAttributes({ @UplAttribute(name = "documentPath", type = String.class, mandatory = true),
		@UplAttribute(name = "paths", type = String[].class), @UplAttribute(name = "pathsBinding", type = String.class),
		@UplAttribute(name = "stickyPaths", type = boolean.class),
		@UplAttribute(name = "stickyPathsBinding", type = String.class),
		@UplAttribute(name = "tabbed", type = boolean.class),
		@UplAttribute(name = "tabbedBinding", type = String.class),
		@UplAttribute(name = "titleUppercase", type = boolean.class),
		@UplAttribute(name = "titleUppercaseBinding", type = String.class),
		@UplAttribute(name = "titlebar", type = boolean.class),
		@UplAttribute(name = "sidebar", type = UplElementReferences.class) })
public class ContentPanelImpl extends AbstractContentPanel {

	@Configurable
	private PagePathInfoRepository pathInfoRepository;

	private Map<String, ContentInfo> contentByPathIdMap;

	private List<ContentInfo> contentList;

	private int contentIndex;

	public ContentPanelImpl() {
		contentByPathIdMap = new HashMap<String, ContentInfo>();
		contentList = new ArrayList<ContentInfo>();
	}

	public void setPathInfoRepository(PagePathInfoRepository pathInfoRepository) {
		this.pathInfoRepository = pathInfoRepository;
	}

	public String getDocumentPath() throws UnifyException {
		return getUplAttribute(String.class, "documentPath");
	}

	public String[] getPaths() throws UnifyException {
		return getUplAttribute(String[].class, "paths", "pathsBinding");
	}

	public boolean isStickyPaths() throws UnifyException {
		return getUplAttribute(boolean.class, "stickyPaths", "stickyPathsBinding");
	}

	public boolean isTabbed() throws UnifyException {
		return getUplAttribute(boolean.class, "tabbed", "tabbedBinding");
	}

	public boolean isTitleBar() throws UnifyException {
		return getUplAttribute(boolean.class, "titlebar");
	}

	public boolean isTitleUppercase() throws UnifyException {
		return getUplAttribute(boolean.class, "titleUppercase", "titleUppercaseBinding");
	}

	public String getHintPanelId() throws UnifyException {
		return getPrefixedId("hint_");
	}

	@Override
	public String getBaseContentId() throws UnifyException {
		return getPrefixedId("base_");
	}

	public String getBodyPanelId() throws UnifyException {
		return getPrefixedId("body_");
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

	@Override
	public void clearPages() throws UnifyException {
		contentByPathIdMap.clear();
		contentList.clear();
		contentIndex = 0;
	}

	@Override
	public Page getCurrentPage() {
		return contentList.get(contentIndex).getPage();
	}

	@Override
	public void addContent(Page page) throws UnifyException {
		ContentInfo contentInfo = contentByPathIdMap.get(page.getPathId());
		if (contentInfo != null) {
			contentIndex = contentInfo.getPageIndex();
			return;
		}

		untabbedClear();
		contentIndex = contentList.size();
		contentInfo = new ContentInfo(page, contentIndex);
		contentList.add(contentInfo);
		contentByPathIdMap.put(page.getPathId(), contentInfo);
	}

	@Override
	public String insertContent(Page page) throws UnifyException {
		ContentInfo contentInfo = contentByPathIdMap.get(page.getPathId());
		if (contentInfo != null) {
			contentIndex = contentInfo.getPageIndex();
			return null;
		}

		untabbedClear();

		String replacedPathId = null;
		final int len = contentList.size();
		if (len <= 1) {
			contentIndex = len;
			contentInfo = new ContentInfo(page, contentIndex);
			contentList.add(contentInfo);
		} else {
			replacedPathId = contentList.get(contentIndex).getPathId();
			contentInfo = new ContentInfo(page, contentIndex);
			contentList.add(contentIndex, contentInfo);
			for (int i = contentIndex + 1; i <= len; i++) {
				contentList.get(i).incPageIndex();
			}
		}

		contentByPathIdMap.put(page.getPathId(), contentInfo);
		return replacedPathId;
	}

	private void untabbedClear() throws UnifyException {
		if (!isTabbed()) {
			// Discard old pages
			for (ContentInfo oldContentInfo : contentList) {
				getSessionContext().removeAttribute(oldContentInfo.getPage().getPathId());
			}

			// Clear content list
			contentList.clear();
			contentByPathIdMap.clear();
		}
	}

	@Override
	public List<String> evaluateRemoveContent(Page page, ClosePageMode closePageMode) throws UnifyException {
		List<String> toRemovePathIdList = new ArrayList<String>();
		if (closePageMode == null) {
			closePageMode = ClosePageMode.CLOSE;
		}

		boolean removeSrc = false;
		switch (closePageMode) {
		case CLOSE:
			removeSrc = true;
			break;
		case CLOSE_ALL:
			removeSrc = true;
		case CLOSE_OTHERS:
			// Close others
			final int stickyCount = isStickyPaths() && getPaths() != null ? getPaths().length : 1;
			List<ContentInfo> refContentList = new ArrayList<ContentInfo>(contentList);
			for (int i = 0; i < refContentList.size(); i++) {
				Page refPage = refContentList.get(i).getPage();
				if (i < stickyCount) {
					if (refPage == page) {
						removeSrc = false;
					}
				} else {
					if (refPage != page) {
						toRemovePathIdList.add(refPage.getPathId());
					}
				}
			}
			break;
		default:
			break;
		}

		if (removeSrc) {
			toRemovePathIdList.add(page.getPathId());
		}

		return toRemovePathIdList;
	}

	@Override
	public void removeContent(List<String> toRemovePathIdList) throws UnifyException {
		for (String removePathId : toRemovePathIdList) {
			ContentInfo contentInfo = contentByPathIdMap.remove(removePathId);
			if (contentInfo == null) {
				// TODO throw some exception here
				continue;
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

		Page currentPage = getCurrentPage();
		if (currentPage != null) {
			currentPage.setAttribute(PageAttributeConstants.OTHER_PAGE_CLOSED_DETECTED, Boolean.TRUE);
		}
	}

	public class ContentInfo {

		private Page page;

		private int pageIndex;

		public ContentInfo(Page page, int pageIndex) throws UnifyException {
			this.page = page;
			this.pageIndex = pageIndex;
		}

		public String getColorScheme() throws UnifyException {
			return pathInfoRepository.getPagePathInfo(page).getColorScheme();
		}

		public String getOpenPath() throws UnifyException {
			return pathInfoRepository.getPagePathInfo(page).getOpenPagePath();
		}

		public String getClosePath() throws UnifyException {
			return pathInfoRepository.getPagePathInfo(page).getClosePagePath();
		}

		public String getSavePath() throws UnifyException {
			return pathInfoRepository.getPagePathInfo(page).getSavePagePath();
		}

		public boolean isRemoteSave() throws UnifyException {
			return pathInfoRepository.getPagePathInfo(page).isRemoteSave();
		}

		public ControllerPathParts getPathParts() throws UnifyException {
			return pathInfoRepository.getControllerPathParts(page);
		}

		public Page getPage() {
			return page;
		}

		public PageBean getPageBean() throws UnifyException {
			return page.getPageBean();
		}

		public String getPathId() {
			return page.getPathId();
		}

		public int getPageIndex() {
			return pageIndex;
		}

		public void decPageIndex() {
			pageIndex--;
		}

		public void incPageIndex() {
			pageIndex++;
		}

		@Override
		public String toString() {
			return StringUtils.toXmlString(this);
		}

	}
}
