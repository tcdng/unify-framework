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
package com.tcdng.unify.web.controller;

import java.io.File;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Resource controller for fetching file downloading resources from application
 * download path.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("/resource/downloadpath")
public class DownloadPathResourceController extends RealPathResourceController {

	@Override
	public void prepareExecution() throws UnifyException {
		super.prepareExecution();
		this.file = new File(IOUtils.buildFilename(this.getUnifyComponentContext().getWorkingPath(),
				"download/" + this.getResourceName()));
		if (this.file.exists()) {
			this.setContentLength(this.file.length());
		}
	}
}
