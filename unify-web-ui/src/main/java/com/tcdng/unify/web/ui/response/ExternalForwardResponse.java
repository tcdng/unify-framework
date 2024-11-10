/*
 * Copyright 2018-2024 The Code Department.
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
package com.tcdng.unify.web.ui.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.constant.RequestParameterConstants;
import com.tcdng.unify.web.ui.AbstractPageControllerResponse;
import com.tcdng.unify.web.ui.util.WebUtils;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.PlainHtml;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Used for generating a external forward to path response.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("externalforwardresponse")
@UplAttributes({
		@UplAttribute(name = "path", type = String.class),
		@UplAttribute(name = "pathBinding", type = String.class),
		@UplAttribute(name = "paramBinding", type = String.class) })
public class ExternalForwardResponse extends AbstractPageControllerResponse {

	@Override
	public void generate(ResponseWriter writer, Page page) throws UnifyException {
		final String pathBinding = getUplAttribute(String.class, "pathBinding");
		String path = !StringUtils.isBlank(pathBinding)
				? (String) ReflectUtils.getNestedBeanProperty(page.getPageBean(), pathBinding)
				: null;
		if (StringUtils.isBlank(path)) {
			path = getUplAttribute(String.class, "path");
		}

		final String paramBinding = getUplAttribute(String.class, "paramBinding");
		final String param = !StringUtils.isBlank(paramBinding)
				? (String) ReflectUtils.getNestedBeanProperty(page.getPageBean(), paramBinding)
				: null;

		path = !StringUtils.isBlank(param)
				? WebUtils.addParameterToPath(path, RequestParameterConstants.EXTERNAL_FORWARD, param)
				: path;

		logDebug("External forward response: path = [{0}]", path);
		PlainHtml plainHtml = (PlainHtml) page;
		plainHtml.setScripts("window.location.assign(\"" + path + "\");");
		writer.writeStructureAndContent(plainHtml);
		writer.writeBehavior(plainHtml);
	}
}
