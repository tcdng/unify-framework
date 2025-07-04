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

package com.tcdng.unify.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.util.RealPathUtils;

/**
 * Plain real path resource controller.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("/plain/resource/realpath")
public class PlainRealPathResourceController extends AbstractPlainResourceController {

    private File file;

    private String subfolder;
   
    public PlainRealPathResourceController() {

    }

    public PlainRealPathResourceController(String subfolder) {
        this.subfolder = subfolder;
    }

    @Override
    protected void prepareExecution(ClientRequest request) throws UnifyException {
        super.prepareExecution(request);
        String resourceName = getResourceName();
        if (!StringUtils.isBlank(subfolder)) {
            resourceName = subfolder + resourceName;
        } else {
            RealPathUtils.checkAccessibleRealPath(resourceName);
        }
        
        file = new File(IOUtils.buildFilename(getUnifyComponentContext().getWorkingPath(), resourceName));
        if (file.exists()) {
            setContentLength(file.length());
        }
    }

    @Override
    protected InputStream getInputStream() throws UnifyException {
        if (file != null && file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throwOperationErrorException(e);
            }
        }
        return null;
    }

}
