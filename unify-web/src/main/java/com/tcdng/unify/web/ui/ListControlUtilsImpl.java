/*
 * Copyright 2018 The Code Department
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.LocaleType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.ListData;
import com.tcdng.unify.core.data.Listable;
import com.tcdng.unify.core.list.ListCommand;
import com.tcdng.unify.core.list.SearchProvider;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.util.HtmlUtils;

/**
 * Default implementation of list control utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_LISTCONTROLUTIL)
public class ListControlUtilsImpl extends AbstractUnifyComponent implements ListControlUtils {

	@Configurable(WebApplicationComponents.APPLICATION_PAGEMANAGER)
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
	public ListControlJsonData getListControlJsonData(ListControl listControl, boolean indexes, boolean keys,
			boolean labels) throws UnifyException {
		List<? extends Listable> listableList = listControl.getListables();
		int length = listableList.size();
		String value = null;
		if (!listControl.isMultiple()) {
			value = listControl.getStringValue();
		}

		int valueIndex = -1;
		String valueLabel = null;
		StringBuilder isb = new StringBuilder();
		StringBuilder ksb = new StringBuilder();
		StringBuilder lsb = new StringBuilder();
		boolean appendSym = false;
		isb.append('[');
		ksb.append('[');
		lsb.append('[');
		for (int i = 0; i < length; i++) {
			if (appendSym) {
				if (indexes) {
					isb.append(',');
				}
				if (keys) {
					ksb.append(',');
				}
				if (labels) {
					lsb.append(',');
				}
			} else {
				appendSym = true;
			}

			if (indexes) {
				isb.append('"').append(listControl.getNamingIndexedId(i)).append('"');
			}

			Listable listable = listableList.get(i);
			String key = listable.getListKey();
			String description = HtmlUtils.getStringWithHtmlEscape(listable.getListDescription());
			if (key.equals(value)) {
				valueIndex = i;
				valueLabel = description;
			}

			if (keys) {
				ksb.append('"').append(key).append('"');
			}

			if (labels) {
				lsb.append('"').append(description).append('"');
			}
		}
		isb.append(']');
		ksb.append(']');
		lsb.append(']');

		return new ListControlJsonData(isb.toString(), ksb.toString(), lsb.toString(), valueLabel, valueIndex,
				listableList.size());
	}

	@Override
	public List<? extends Listable> getListables(ListControl listControl) throws UnifyException {
		String listName = listControl.getList();
		List<? extends Listable> list = getList(LocaleType.SESSION, listName, resolveParams(listControl));
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

				return newList;
			}
		}
		return list;
	}

	@Override
	public Map<String, String> getListMap(ListControl listControl) throws UnifyException {
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
				Map<String, String> map = new HashMap<String, String>();
				for (Listable listable : list) {
					String key = listable.getListKey();
					String description = listable.getListDescription();
					if (keyProperty != null) {
						key = String.valueOf(ReflectUtils.getBeanProperty(listable, keyProperty));
					}

					if (descProperty != null) {
						description = String.valueOf(ReflectUtils.getBeanProperty(listable, descProperty));
					}

					map.put(key, description);
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
				params[i] = panel.getValue(Object.class, listParams[i]);
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
