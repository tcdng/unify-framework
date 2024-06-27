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
import com.tcdng.unify.web.ui.AbstractPageControllerResponse;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.PlainHtml;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Used for generating a plain HTML response.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("plainhtmlresponse")
@UplAttributes({ @UplAttribute(name = "html", type = String.class),
		@UplAttribute(name = "htmlBinding", type = String.class) })
public class PlainHtmlResponse extends AbstractPageControllerResponse {

	@Override
	public void generate(ResponseWriter writer, Page page) throws UnifyException {
		String html = getUplAttribute(String.class, "html");
		if (StringUtils.isBlank(html)) {
			String htmlBinding = getUplAttribute(String.class, "htmlBinding");
			html = (String) ReflectUtils.getNestedBeanProperty(page.getPageBean(), htmlBinding);
		}

		logDebug("Plain HTML response...");
		PlainHtml plainHtml = (PlainHtml) page;
		plainHtml.setBodyContent(html);
		writer.writeStructureAndContent(plainHtml);
		writer.writeBehavior(plainHtml);
	}
}
