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
package com.tcdng.unify.web.ui.controller;

import java.io.OutputStream;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.web.ResourceStreamer;
import com.tcdng.unify.web.annotation.RequestParameter;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.ui.AbstractPageResourceController;

/**
 * Resource controller for fetching resources using streamer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("/resource/streamer")
public class StreamerResourceController extends AbstractPageResourceController {

    @RequestParameter
    private String streamer;

    private ResourceStreamer resourceStreamer;
    
    public StreamerResourceController() {
        super(Secured.FALSE);
    }

	@Override
    public void prepareExecution() throws UnifyException {
        setContentDisposition(getResourceName());
        resourceStreamer = getComponent(ResourceStreamer.class, streamer);
        final int length = resourceStreamer.setResourceName(getResourceName());
        if (length > 0) {
            setContentLength(length);
        }
    }

    @Override
    public String execute(OutputStream outputStream) throws UnifyException {
    	resourceStreamer.stream(outputStream);       
        return null;
    }

	public String getStreamer() {
		return streamer;
	}

	public void setStreamer(String streamer) {
		this.streamer = streamer;
	}

}
