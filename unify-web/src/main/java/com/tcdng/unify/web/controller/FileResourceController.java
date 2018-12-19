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
package com.tcdng.unify.web.controller;

import java.io.InputStream;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.cache.FileCache;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.web.AbstractResourceController;
import com.tcdng.unify.web.annotation.RequestParameter;

/**
 * Resource controller for fetching file resources from application real path,
 * class-loader path or exact path.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("/resource/file")
public class FileResourceController extends AbstractResourceController {

    private static final long MINIMUM_CACHE_EXPIRY_TIME = 1;

    @Configurable
    private FileCache fileCache;

    @Configurable("300")
    // Expiration in seconds
    private long cacheExpirationTime;

    @RequestParameter
    private boolean cache;

    public FileResourceController() {
        super(false);
    }

    public FileResourceController(boolean secured) {
        super(secured);
    }

    @Override
    public void prepareExecution() throws UnifyException {
        setContentDisposition(getResourceName());
    }

    @Override
    public void execute(OutputStream outputStream) throws UnifyException {
        InputStream inputStream = null;
        try {
            if (cache) {
                inputStream = getCachedResourceInputStream();
            } else {
                inputStream = getInputStream();
            }
            IOUtils.writeAll(outputStream, inputStream);
        } finally {
            IOUtils.close(inputStream);
        }
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    @Override
    protected void onInitialize() throws UnifyException {
        super.onInitialize();
        if (cacheExpirationTime <= 0) {
            cacheExpirationTime = MINIMUM_CACHE_EXPIRY_TIME;
        }
    }

    protected InputStream getInputStream() throws UnifyException {
        return IOUtils.openFileResourceInputStream(getResourceName(), getUnifyComponentContext().getWorkingPath());
    }

    private InputStream getCachedResourceInputStream() throws UnifyException {
        InputStream inputStream = fileCache.getTransformed(getResourceName());
        if (inputStream == null) {
            return fileCache.transformPut(getResourceName(), IOUtils.readAll(getInputStream()), cacheExpirationTime);
        }
        return inputStream;
    }
}
