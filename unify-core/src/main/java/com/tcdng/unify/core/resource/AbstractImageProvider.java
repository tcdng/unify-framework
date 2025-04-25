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
package com.tcdng.unify.core.resource;

import com.tcdng.unify.convert.util.ConverterUtils;
import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ThemeManager;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.util.IOUtils;

/**
 * Convenient abstract base class for image providers.
 * 
 * @author The Code Department
 * @since 4.1
 */
public abstract class AbstractImageProvider extends AbstractUnifyComponent implements ImageProvider {

	@Configurable("web/images/blank.png")
	private String defaultSrc;

	@Configurable
	private ThemeManager themeManager;

	@Override
	public final byte[] provideAsByteArray(String name) throws UnifyException {
		byte[] arr = doProvideAsByteArray(name);
		return arr == null ? IOUtils.readFileResourceInputStream(themeManager.expandThemeTag(name))
				: (arr.length == 0 ? IOUtils.readFileResourceInputStream(defaultSrc) : arr);
	}

	@Override
	public final String provideAsBase64String(String name) throws UnifyException {
		try {
			return ConverterUtils.convert(String.class, provideAsByteArray(name));
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throwOperationErrorException(e);
		}

		return null;
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected abstract byte[] doProvideAsByteArray(String name) throws UnifyException;
}
