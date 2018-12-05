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
package com.tcdng.unify.web.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.PageController;
import com.tcdng.unify.web.ui.ResponseWriter;

/**
 * Used for generating a post to path response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("postresponse")
@UplAttributes({ @UplAttribute(name = "path", type = String.class),
		@UplAttribute(name = "pathProperty", type = String.class),
		@UplAttribute(name = "pathRequestAttribute", type = String.class) })
public class PostResponse extends AbstractJsonPageControllerResponse {

	public PostResponse() {
		super("postHdl");
	}

	@Override
	protected void doGenerate(ResponseWriter writer, PageController pageController) throws UnifyException {
		String path = getUplAttribute(String.class, "path");
		if (StringUtils.isBlank(path)) {
			String pathProperty = getUplAttribute(String.class, "pathProperty");
			if (!StringUtils.isBlank(pathProperty)) {
				path = (String) ReflectUtils.getNestedBeanProperty(pageController, pathProperty);
			}
		}

		if (StringUtils.isBlank(path)) {
			String pathRequestAttribute = getUplAttribute(String.class, "pathRequestAttribute");
			if (!StringUtils.isBlank(pathRequestAttribute)) {
				path = (String) getRequestAttribute(pathRequestAttribute);
			}
		}

		logDebug("Preparing post response: controller = [{0}], path = [{1}]", pageController.getName(), path);
		if (!StringUtils.isBlank(path)) {
			writer.write(",");
			writer.writeJsonPathVariable("postPath", path);
		} else {
			writer.write(",\"postPath\":\"content_open\"");
		}

	}
}
