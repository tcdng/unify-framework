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
package com.tcdng.unify.web.ui.data;

import java.util.List;

/**
 * Link category information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class LinkCategoryInfo {

	private String name;

	private String caption;

	private String path;

	private List<LinkInfo> linkInfoList;

	public LinkCategoryInfo(String name, String caption, String path, List<LinkInfo> linkInfoList) {
		this.name = name;
		this.caption = caption;
		this.path = path;
		this.linkInfoList = linkInfoList;
	}

	public String getName() {
		return name;
	}

	public String getCaption() {
		return caption;
	}

	public String getPath() {
		return path;
	}

	public List<LinkInfo> getLinkInfoList() {
		return linkInfoList;
	}

}
