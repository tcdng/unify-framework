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
package com.tcdng.unify.web.ui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.common.data.Listable;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.LocaleType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.list.ListCommand;
import com.tcdng.unify.core.list.SearchProvider;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.html.HtmlUtils;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;

/**
 * Default implementation of list control utilities.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(WebUIApplicationComponents.APPLICATION_LISTCONTROLUTIL)
public class ListControlUtilsImpl extends AbstractUnifyComponent implements ListControlUtils {

	@Configurable
	private PageManager pageManager;

	private FactoryMap<String, ListInfo> listInfoMap;

	public ListControlUtilsImpl() {
		listInfoMap = new FactoryMap<String, ListInfo>() {

			@Override
			protected ListInfo create(String listName, Object... params) throws Exception {
				if (isComponent(listName)) {
					ListCommand<?> listCommand = (ListCommand<?>) getComponent(listName);
					if (listCommand instanceof SearchProvider) {
						SearchProvider sp = (SearchProvider) listCommand;
						return new ListInfo(sp.getKeyProperty(), sp.getDescProperty());
					}
				}

				return ListInfo.BLANK;
			}

		};
	}

	@Override
	public ListControlInfo getListControlInfo(ListControl listControl, Formatter<Object> formatter)
			throws UnifyException {
		List<? extends Listable> listableList = listControl.getListables();
		int len = listableList.size();
		String[] selectIds = new String[len];
		String[] keys = new String[len];
		String[] labels = new String[len];
		for (int i = 0; i < len; i++) {
			Listable listable = listableList.get(i);
			keys[i] = listable.getListKey();
			if (formatter != null && formatter.isLabelFormat()) {
				labels[i] = listControl.isHtmlEscape()
						? HtmlUtils.getStringWithHtmlEscape(formatter.format(listable.getListDescription()))
						: formatter.format(listable.getListDescription());
			} else {
				labels[i] = listControl.isHtmlEscape()
						? HtmlUtils.getStringWithHtmlEscape(listable.getListDescription())
						: listable.getListDescription();
			}
			labels[i] = resolveSessionMessage(labels[i]);
			selectIds[i] = listControl.getNamingIndexedId(i);
		}

		return new ListControlInfo(selectIds, keys, labels);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends Listable> getListables(ListControl listControl) throws UnifyException {
		String reqId = "reqList_" + listControl.getId();
		List<? extends Listable> list = (List<? extends Listable>) getRequestAttribute(reqId);
		if (list == null) {
			String listName = listControl.getList();
			list = getList(LocaleType.SESSION, listName, resolveParams(listControl));
			if (!list.isEmpty()) {
				ListInfo listInfo = listInfoMap.get(listName);
				String keyProperty = listInfo.getListKey();
				if (keyProperty == null) {
					keyProperty = listControl.getListKey();
				}

				String descProperty = listInfo.getListDescription();
				if (descProperty == null) {
					descProperty = listControl.getListDescription();
				}

				if (keyProperty != null || descProperty != null) {
					List<ListData> newList = new ArrayList<ListData>();
					for (Listable listable : list) {
						String key = listable.getListKey();
						String description = listable.getListDescription();
						if (keyProperty != null) {
							key = String.valueOf(ReflectUtils.getBeanProperty(listable, keyProperty));
						}

						if (descProperty != null) {
							description = String.valueOf(ReflectUtils.getBeanProperty(listable, descProperty));
						}

						newList.add(new ListData(key, description));
					}

					list = newList;
				}
			}

			setRequestAttribute(reqId, list);
		}

		return list;
	}

	@Override
	public Map<String, Listable> getListMap(ListControl listControl) throws UnifyException {
		String listName = listControl.getList();
		ListInfo listInfo = listInfoMap.get(listName);
		String keyProperty = listInfo.getListKey();
		if (keyProperty == null) {
			keyProperty = listControl.getListKey();
		}

		String descProperty = listInfo.getListDescription();
		if (descProperty == null) {
			descProperty = listControl.getListDescription();
		}

		if (keyProperty != null || descProperty != null) {
			List<? extends Listable> list = getList(LocaleType.SESSION, listName, resolveParams(listControl));
			if (!list.isEmpty()) {
				Map<String, Listable> map = new HashMap<String, Listable>();
				for (Listable listable : list) {
					String key = listable.getListKey();
					String description = listable.getListDescription();
					if (keyProperty != null) {
						key = String.valueOf(ReflectUtils.getBeanProperty(listable, keyProperty));
					}

					if (descProperty != null) {
						description = String.valueOf(ReflectUtils.getBeanProperty(listable, descProperty));
					}

					map.put(key, new ListData(key, description));
				}

				return map;
			}
			return Collections.emptyMap();

		}

		return getListMap(LocaleType.SESSION, listControl.getList(), resolveParams(listControl));
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected Object[] resolveParams(ListControl listControl) throws UnifyException {
		String[] listParams = listControl.getListParams();
		if (listParams == null || listParams.length == 0) {
			return DataUtils.ZEROLEN_OBJECT_ARRAY;
		}

		Object[] params = null;
		switch (listControl.getListParamType()) {
		case IMMEDIATE:
			params = listParams;
			break;
		case PANEL:
			params = new Object[listParams.length];
			Panel panel = listControl.getPanel();
			for (int i = 0; i < params.length; i++) {
				params[i] = listControl.getValue(listParams[i]);
				if (params[i] == null) {
					params[i] = panel.getValue(Object.class, listParams[i]);
				}
			}
			break;
		case CONTROL:
		default:
			params = new Object[listParams.length];
			for (int i = 0; i < params.length; i++) {
				params[i] = listControl.getValue(listParams[i]);
			}
			break;
		}

		return params;
	}

	private static class ListInfo {
		public static final ListInfo BLANK = new ListInfo();

		private String listKey;

		private String listDescription;

		public ListInfo() {

		}

		public ListInfo(String listKey, String listDescription) {
			this.listKey = listKey;
			this.listDescription = listDescription;
		}

		public String getListKey() {
			return listKey;
		}

		public String getListDescription() {
			return listDescription;
		}
	}
}
