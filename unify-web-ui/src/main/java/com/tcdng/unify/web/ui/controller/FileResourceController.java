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
package com.tcdng.unify.web.ui.controller;

import java.io.InputStream;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.file.FileResourceProvider;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.ui.AbstractPageResourceController;

/**
 * Resource controller for fetching file resources from application real path,
 * class-loader path or exact path.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("/resource/file")
public class FileResourceController extends AbstractPageResourceController {

    @Configurable
    private FileResourceProvider fileResourceProvider;

    public FileResourceController() {
        super(Secured.FALSE);
    }

    public FileResourceController(Secured secured) {
        super(secured);
    }

    @Override
    public void prepareExecution() throws UnifyException {
        setContentDisposition(getResourceName());
    }

    @Override
    public void execute(OutputStream out) throws UnifyException {
        InputStream in = null;
        try {
            in = getInputStream();
            IOUtils.writeAll(out, in);
        } finally {
            IOUtils.close(in);
        }
    }

    protected InputStream getInputStream() throws UnifyException {
        InputStream in = null;
        if (fileResourceProvider != null) {
            in = fileResourceProvider.openFileResourceInputStream("/resource/file", getResourceName());
        }

        if (in == null) {
            return IOUtils.openFileResourceInputStream(getResourceName(), getUnifyComponentContext().getWorkingPath());
        }

        return in;
    }

}
