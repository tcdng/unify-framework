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
 * Used for generating a forward to path response.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("forwardresponse")
@UplAttributes({ @UplAttribute(name = "path", type = String.class),
        @UplAttribute(name = "pathBinding", type = String.class) })
public class ForwardResponse extends AbstractJsonPageControllerResponse {

    public ForwardResponse() {
        super("forwardHdl");
    }

    @Override
    public void doGenerate(ResponseWriter writer, PageController pageController) throws UnifyException {
        String path = getUplAttribute(String.class, "path");
        if (StringUtils.isBlank(path)) {
            String pathBinding = getUplAttribute(String.class, "pathBinding");
            path = (String) ReflectUtils.getNestedBeanProperty(pageController, pathBinding);
        }
        logDebug("Preparing forward response:controller = [{0}],  path = [{1}]", pageController.getName(), path);
        writer.write(",");
        writer.writeJsonPathVariable("loadDocument", path);
    }
}
