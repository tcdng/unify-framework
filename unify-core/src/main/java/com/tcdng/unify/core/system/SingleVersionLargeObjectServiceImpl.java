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

package com.tcdng.unify.core.system;

import java.util.List;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.system.entities.SingleVersionBlob;
import com.tcdng.unify.core.system.entities.SingleVersionBlobQuery;
import com.tcdng.unify.core.system.entities.SingleVersionClob;
import com.tcdng.unify.core.system.entities.SingleVersionClobQuery;

/**
 * Default implementation of single version large object service.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Transactional
@Component(ApplicationComponents.APPLICATION_SINGLEVERSIONLOBSERVICE)
public class SingleVersionLargeObjectServiceImpl extends AbstractBusinessService
		implements SingleVersionLargeObjectService {

	private static final String STORE_LOB_LOCK = "app::storelob-lock";

	@Override
	@Synchronized(STORE_LOB_LOCK)
	public boolean storeBlob(String applicationName, String categoryName, String objectName, byte[] blob, long version)
			throws UnifyException {
		if (version <= 0) {
			throw new UnifyException(UnifyCoreErrorConstants.INVALID_LARGEOBJECT_VERSION, applicationName, categoryName,
					objectName, version);
		}

		SingleVersionBlob singleVersionBlob = db().find(new SingleVersionBlobQuery()
				.applicationName(applicationName).categoryName(categoryName).objectName(objectName)
				.addSelect("id", "applicationName", "categoryName", "objectName", "version"));
		if (singleVersionBlob == null) {
			singleVersionBlob = new SingleVersionBlob();
			singleVersionBlob.setApplicationName(applicationName);
			singleVersionBlob.setCategoryName(categoryName);
			singleVersionBlob.setObjectName(objectName);
			singleVersionBlob.setLargeObject(blob);
			singleVersionBlob.setVersion(version);
			db().create(singleVersionBlob);
			return true;
		}

		if (singleVersionBlob.getVersion() < version) {
			singleVersionBlob.setLargeObject(blob);
			singleVersionBlob.setVersion(version);
			db().updateById(singleVersionBlob);
			return true;
		}

		return false;
	}

	@Override
	public byte[] retrieveBlob(String applicationName, String categoryName, String objectName) throws UnifyException {
		SingleVersionBlob singleVersionBlob = db().find(new SingleVersionBlobQuery().applicationName(applicationName)
				.categoryName(categoryName).objectName(objectName).addSelect("largeObject"));
		if (singleVersionBlob != null) {
			return singleVersionBlob.getLargeObject();
		}

		return null;
	}

	@Override
	public List<String> retrieveBlobObjectNames(String applicationName, String categoryName) throws UnifyException {
		return db().valueList(String.class, "objectName",
				new SingleVersionBlobQuery().applicationName(applicationName).categoryName(categoryName));
	}

	@Override
	public long getBlobVersion(String applicationName, String categoryName, String objectName) throws UnifyException {
		SingleVersionBlob singleVersionBlob = db().find(new SingleVersionBlobQuery().applicationName(applicationName)
				.categoryName(categoryName).objectName(objectName).addSelect("version"));
		if (singleVersionBlob != null) {
			return singleVersionBlob.getVersion();
		}

		return 0;
	}

	@Override
	@Synchronized(STORE_LOB_LOCK)
	public boolean storeClob(String applicationName, String categoryName, String objectName, String clob, long version)
			throws UnifyException {
		if (version <= 0) {
			throw new UnifyException(UnifyCoreErrorConstants.INVALID_LARGEOBJECT_VERSION, applicationName, categoryName,
					objectName, version);
		}

		SingleVersionClob singleVersionClob = db().find(new SingleVersionClobQuery()
				.applicationName(applicationName).categoryName(categoryName).objectName(objectName)
				.addSelect("id", "applicationName", "categoryName", "objectName", "version"));
		if (singleVersionClob == null) {
			singleVersionClob = new SingleVersionClob();
			singleVersionClob.setApplicationName(applicationName);
			singleVersionClob.setCategoryName(categoryName);
			singleVersionClob.setObjectName(objectName);
			singleVersionClob.setLargeObject(clob);
			singleVersionClob.setVersion(version);
			db().create(singleVersionClob);
			return true;
		}

		if (singleVersionClob.getVersion() < version) {
			singleVersionClob.setLargeObject(clob);
			singleVersionClob.setVersion(version);
			db().updateById(singleVersionClob);
			return true;
		}

		return false;
	}

	@Override
	public String retrieveClob(String applicationName, String categoryName, String objectName) throws UnifyException {
		SingleVersionClob singleVersionClob = db().find(new SingleVersionClobQuery().applicationName(applicationName)
				.categoryName(categoryName).objectName(objectName).addSelect("largeObject"));
		if (singleVersionClob != null) {
			return singleVersionClob.getLargeObject();
		}

		return null;
	}

	@Override
	public List<String> retrieveClobObjectNames(String applicationName, String categoryName) throws UnifyException {
		return db().valueList(String.class, "objectName",
				new SingleVersionClobQuery().applicationName(applicationName).categoryName(categoryName));
	}

	@Override
	public long getClobVersion(String applicationName, String categoryName, String objectName) throws UnifyException {
		SingleVersionClob singleVersionClob = db().find(new SingleVersionClobQuery().applicationName(applicationName)
				.categoryName(categoryName).objectName(objectName).addSelect("version"));
		if (singleVersionClob != null) {
			return singleVersionClob.getVersion();
		}

		return 0;
	}
}
