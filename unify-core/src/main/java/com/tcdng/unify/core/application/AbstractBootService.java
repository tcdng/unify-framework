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
package com.tcdng.unify.core.application;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.constant.ForceConstraints;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.database.DataSourceManager;
import com.tcdng.unify.core.database.DataSourceManagerContext;
import com.tcdng.unify.core.database.DataSourceManagerOptions;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.VersionUtils;

/**
 * Convenient base class for boot business service.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractBootService<T extends FeatureDefinition> extends AbstractBusinessService
		implements BootService {

	private static final String BOOT_DEPLOYMENT_LOCK = "app::bootdeployment-lock";

	private static final String BOOT_FEATURE_LOCK = "app::bootfeature-lock";

	@Configurable(ApplicationComponents.APPLICATION_DATASOURCEMANAGER)
	private DataSourceManager dataSourceManager;

	private List<StartupShutdownHook> startupShutdownHooks;

	@Override
	@Transactional
	public void startup() throws UnifyException {
		logInfo("Initializing datasources...");
		final DataSourceManagerContext ctx = new DataSourceManagerContext(new DataSourceManagerOptions(PrintFormat.NONE,
				ForceConstraints.fromBoolean(!getContainerSetting(boolean.class,
						UnifyCorePropertyConstants.APPLICATION_FOREIGNKEY_EASE, false))));
		List<String> datasources = getApplicationDataSources();
		
		
		
		// Initialize data sources with application datasource first
		ctx.setStrictEntitySort(true);
		if (datasources.remove(ApplicationComponents.APPLICATION_DATASOURCE)) {
			datasources.add(0, ApplicationComponents.APPLICATION_DATASOURCE);
		}

		for (String datasource : datasources) {
			dataSourceManager.initDataSource(ctx, datasource);
		}

		// Initialize other data sources with application datasource last
		ctx.setStrictEntitySort(false);
		if (datasources.remove(ApplicationComponents.APPLICATION_DATASOURCE)) {
			datasources.add(ApplicationComponents.APPLICATION_DATASOURCE);
		}

		for (String datasource : datasources) {
			dataSourceManager.initDataSource(ctx, datasource);
		}

		// Deployment
		boolean isDeploymentPerformed = false;
		if (isDeploymentMode()) {
			Feature deploymentFeature = getFeature("deploymentVersion", "0.0");
			Feature auxiliaryFeature = getFeature("auxiliaryVersion", "0.0");
			boolean isDataSourcesManaged = false;
			if (deploymentFeature == null) {
				// Blank database. Manage data sources first time.
				logInfo("Managing datasources...");
				for (String datasource : datasources) {
					dataSourceManager.manageDataSource(ctx, datasource);
				}

				deploymentFeature = getFeature("deploymentVersion", "0.0");
				auxiliaryFeature = getFeature("auxiliaryVersion", "0.0");
				getFeature("deploymentID", UUID.randomUUID().toString());
				getFeature("deploymentInitDate", String.valueOf(new Date().getTime()));
				isDataSourcesManaged = true;
			}

			isDeploymentPerformed = performBootDeployment(ctx, deploymentFeature, auxiliaryFeature,
					isDataSourcesManaged);
		}

		startupShutdownHooks = getStartupShutdownHooks();
		if (DataUtils.isNotBlank(startupShutdownHooks)) {
			for (StartupShutdownHook startupShutdownHook : startupShutdownHooks) {
				startupShutdownHook.onApplicationStartup();
			}
		}

		onStartup(isDeploymentPerformed);
		dataSourceManager.initDelayedDataSource(ctx);
	}

	@Override
	public void shutdown() throws UnifyException {
		onShutdown();

		if (DataUtils.isNotBlank(startupShutdownHooks)) {
			for (StartupShutdownHook startupShutdownHook : startupShutdownHooks) {
				startupShutdownHook.onApplicationShutdown();
			}
		}
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected abstract List<StartupShutdownHook> getStartupShutdownHooks() throws UnifyException;

	protected abstract BootInstallationInfo<T> prepareBootInstallation() throws UnifyException;

	protected abstract void onStartup(boolean isInstallationPerformed) throws UnifyException;

	protected abstract void onShutdown() throws UnifyException;

	private List<String> getApplicationDataSources() throws UnifyException {
		List<String> appDataSourceNames = getComponentNames(SqlDataSource.class);
		appDataSourceNames.remove(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
		return appDataSourceNames;
	}

	@SuppressWarnings("unchecked")
	@Synchronized(BOOT_DEPLOYMENT_LOCK)
	private boolean performBootDeployment(DataSourceManagerContext ctx, Feature deploymentFeature, Feature auxiliaryFeature,
			boolean isDataSourcesManaged) throws UnifyException {
		logInfo("Checking application version information...");
		deploymentFeature = getFeature("deploymentVersion", "0.0");
		String lastDeploymentVersion = deploymentFeature.getValue();
		String versionToDeploy = getDeploymentVersion();
		boolean isDeployNewVersion = VersionUtils.isNewerVersion(versionToDeploy, lastDeploymentVersion);

		auxiliaryFeature = getFeature("auxiliaryVersion", "0.0");
		String lastAuxiliaryVersion = auxiliaryFeature.getValue();
		String auxVersionToDeploy = getAuxiliaryVersion();
		boolean isDeployNewAuxVersion = VersionUtils.isNewerVersion(auxVersionToDeploy, lastAuxiliaryVersion);
		if (!isDataSourcesManaged) {
			// If not already managed, manage data sources if not production mode or if
			// deploying new version
			if (isDeployNewVersion || isDeployNewAuxVersion) {
				logInfo("Managing datasources...");
				DataSourceManagerOptions options = new DataSourceManagerOptions(PrintFormat.NONE,
						ForceConstraints.fromBoolean(!getContainerSetting(boolean.class,
								UnifyCorePropertyConstants.APPLICATION_FOREIGNKEY_EASE, false)));
				DataSourceManagerContext _ctx = new DataSourceManagerContext(ctx, options);
				List<String> datasources = getApplicationDataSources();
				for (String datasource : datasources) {
					dataSourceManager.manageDataSource(_ctx, datasource);
				}
			}
		}

		// Do installation only if deployment version is new
		if (isDeployNewVersion || isDeployNewAuxVersion) {
			if (isDeployNewVersion) {
				logInfo("Installing newer application version {0}. Current version is {1}.", versionToDeploy,
						lastDeploymentVersion);
			} else {
				logInfo("Installing newer application auxiliary version {0}. Current auxiliary  version is {1}.",
						auxVersionToDeploy, lastAuxiliaryVersion);
			}

			BootInstallationInfo<T> bootInstallationInfo = prepareBootInstallation();
			if (bootInstallationInfo.isInstallers() && bootInstallationInfo.isFeatures()) {
				for (String installerName : bootInstallationInfo.getFeatureInstallerNames()) {
					FeatureInstaller<T> installer = (FeatureInstaller<T>) getComponent(installerName);
					installer.installFeatures(bootInstallationInfo.getFeatures());
				}
			}

			if (isDeployNewVersion) {
				// Update last deployment feature
				Feature lastDeploymentFeature = getFeature("lastDeploymentVersion", "0.0");
				lastDeploymentFeature.setValue(lastDeploymentVersion);
				db().updateByIdVersion(lastDeploymentFeature);

				// Update current deployment feature
				deploymentFeature.setValue(versionToDeploy);
				db().updateByIdVersion(deploymentFeature);
			}

			if (isDeployNewAuxVersion) {
				// Update last auxiliary feature
				Feature lastAuxiliaryFeature = getFeature("lastAuxiliaryVersion", "0.0");
				lastAuxiliaryFeature.setValue(lastAuxiliaryVersion);
				db().updateByIdVersion(lastAuxiliaryFeature);

				// Update current auxiliary feature
				auxiliaryFeature.setValue(auxVersionToDeploy);
				db().updateByIdVersion(auxiliaryFeature);
			}
		} else {
			if (lastDeploymentVersion.equals(versionToDeploy)) {
				logInfo("Application deployment version {0} is current.", versionToDeploy);
			} else {
				logInfo("Application deployment version {0} is old. Current version is {1}.", versionToDeploy,
						lastDeploymentVersion);
			}

			if (lastAuxiliaryVersion.equals(auxVersionToDeploy)) {
				logInfo("Application auxiliary version {0} is current.", auxVersionToDeploy);
			} else {
				logInfo("Application auxiliary version {0} is old. Current version is {1}.", auxVersionToDeploy,
						lastAuxiliaryVersion);
			}
		}

		return isDeployNewVersion || isDeployNewAuxVersion;
	}

	@Synchronized(lock = BOOT_FEATURE_LOCK, waitForLock = false)
	private Feature getFeature(String code, String defaultVal) throws UnifyException {
		try {
			Feature feature = db().find(new FeatureQuery().code(code));
			if (feature == null) {
				feature = new Feature();
				feature.setCode(code);
				feature.setValue(defaultVal);
				db().create(feature);
			}
			return feature;
		} catch (UnifyException ue) {
		} finally {
			try {
				commitTransactions();
			} catch (UnifyException e) {
			}
		}

		return null;
	}
}
