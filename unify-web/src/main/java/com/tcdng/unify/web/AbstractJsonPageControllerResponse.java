/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.web;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.FormatHelper;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Convenient JSON object page controller response.
 * 
 * @author Lateef Ojulari
 * @version 1.0
 */
public abstract class AbstractJsonPageControllerResponse extends AbstractPageControllerResponse {

	private String handlerName;

	public AbstractJsonPageControllerResponse(String handlerName) {
		this.handlerName = handlerName;
	}

	@Override
	public void generate(ResponseWriter writer, PageController pageController) throws UnifyException {
		writer.write("{\"handler\":\"").write(handlerName).write("\"");
		doGenerate(writer, pageController);
		appendOnSaveList(writer);
		writer.write("}");
	}

	protected String getTimestampedResourceName(String resourceName) throws UnifyException {
		return StringUtils.underscore(resourceName) + "_" + getFormatHelper().formatNow(FormatHelper.yyyyMMdd_HHmmss);

	}

	protected abstract void doGenerate(ResponseWriter writer, PageController pageController) throws UnifyException;

	private void appendOnSaveList(ResponseWriter writer) throws UnifyException {
		List<String> saveList = getRequestContextUtil().getOnSaveContentWidgets();
		if (!DataUtils.isBlank(saveList)) {
			writer.write(",\"pSaveList\":");
			writer.writeJsonStringArray(saveList);
		}
	}
}
