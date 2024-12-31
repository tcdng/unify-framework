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

import java.io.InputStream;
import java.io.OutputStream;

import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.file.FileResourceProvider;
import com.tcdng.unify.core.util.FileUtils;
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
	public String execute(OutputStream out) throws UnifyException {
		String contentType = null;
		ResInputStream rin = null;
		try {
			rin = getInputStream();
			if (rin != null && rin.isPresent()) {
				IOUtils.writeAll(out, rin.getIn());
			}
		} finally {
			if (rin != null) {
				contentType = rin.getContentType();
				IOUtils.close(rin.getIn());
			}
		}

		return contentType;
	}

	@Override
	public boolean isRefererRequired() {
		return false;
	}

	protected ResInputStream getInputStream() throws UnifyException {
		InputStream in = null;
		final String resourceName = getResourceName();
		String contentType = null;
		if (fileResourceProvider != null) {
			in = fileResourceProvider.openFileResourceInputStream("/resource/file", resourceName);
		}

		if (in == null) {
			try {
				final String workingPath = getUnifyComponentContext().getWorkingPath();
				final String _resourceName = getThemeExtendedFileName(resourceName, workingPath);
				in = IOUtils.openFileResourceInputStream(_resourceName, workingPath);
			} catch (UnifyException e) {
				logError(e);
				contentType = MimeType.TEXT_HTML.template();
				in = IOUtils.openClassLoaderResourceInputStream("/web/404.html");
			}
		}

		return new ResInputStream(in, contentType);
	}

	private String getThemeExtendedFileName(final String fileName, final String workingPath) throws UnifyException {
		final String theme = getContainerSetting(String.class, UnifyCorePropertyConstants.APPLICATION_THEME);
		return FileUtils.detectPresentAndGetThemeFileName(fileName, theme, workingPath);
	}

	protected class ResInputStream {

		private final InputStream in;

		private final String contentType;

		public ResInputStream(InputStream in, String contentType) {
			this.in = in;
			this.contentType = contentType;
		}

		public ResInputStream(InputStream in) {
			this.in = in;
			this.contentType = null;
		}

		public InputStream getIn() {
			return in;
		}

		public String getContentType() {
			return contentType;
		}

		public boolean isPresent() {
			return in != null;
		}
	}
}
