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
package com.tcdng.unify.web.ui.response;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.TargetPath;
import com.tcdng.unify.web.ui.AbstractJsonPageControllerResponse;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.ResponseWriter;

/**
 * Used for generating a command post to path response.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("commandpostresponse")
public class CommandPostResponse extends AbstractJsonPageControllerResponse {

    public CommandPostResponse() {
        super("commandPostHdl", false);
    }

    @Override
    protected void doGenerate(ResponseWriter writer, Page page) throws UnifyException {
        logDebug("Preparing command post response: path ID = [{0}]", page.getPathId());
        TargetPath targetPath = getRequestContextUtil().getCommandResponsePath();
        if(targetPath != null) {
            if (StringUtils.isNotBlank(targetPath.getPath())) {
                writer.write(",");
                writer.writeJsonPathVariable("postPath", targetPath.getPath());
            }
            
            if (StringUtils.isNotBlank(targetPath.getTarget())) {
                writer.write(",\"target\":").writeJsonQuote(targetPath.getTarget());
            }
        }
    }
}
