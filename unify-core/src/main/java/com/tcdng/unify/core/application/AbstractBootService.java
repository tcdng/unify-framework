/*
 * Copyright 2018-2020 The Code Department.
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

import java.util.List;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.constant.ForceConstraints;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.database.DataSourceManager;
import com.tcdng.unify.core.database.DataSourceManagerOptions;
import com.tcdng.unify.core.database.sql.SqlDataSource;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.VersionUtils;

/**
 * Convenient base class for boot business service.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractBootService<T extends FeatureDefinition> extends AbstractBusinessService
        implements BootService {

    private static final String BOOT_DEPLOYMENT_LOCK = "bootdeployment-lock";
    
    @Configurable(ApplicationComponents.APPLICATION_DATASOURCEMANAGER)
    private DataSourceManager dataSourceManager;

    private List<StartupShutdownHook> startupShutdownHooks;

    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void startup() throws UnifyException {
        logInfo("Initializing datasources...");
        DataSourceManagerOptions options = new DataSourceManagerOptions(PrintFormat.NONE,
                ForceConstraints.fromBoolean(!getContainerSetting(boolean.class,
                        UnifyCorePropertyConstants.APPLICATION_FOREIGNKEY_EASE, false)));
        List<String> datasources = getApplicationDataSources();
        for (String datasource : datasources) {
            dataSourceManager.initDataSource(datasource, options);
        }

        if (isDeploymentMode()) {
            Feature deploymentFeature = getFeature("deploymentVersion", "0.0");
            boolean isDataSourcesManaged = false;
            if (deploymentFeature == null) {
                // Blank database. Manage data sources first time.
                logInfo("Managing datasources...");
                for (String datasource : datasources) {
                    dataSourceManager.manageDataSource(datasource, options);
                }

                deploymentFeature = getFeature("deploymentVersion", "0.0");
                isDataSourcesManaged = true;
            }
            
            beginClusterLock(BOOT_DEPLOYMENT_LOCK);
            try {
                logInfo("Checking application version information...");
                deploymentFeature = getFeature("deploymentVersion", "0.0");
                String lastDeploymentVersion = deploymentFeature.getValue();
                String versionToDeploy = getDeploymentVersion();
                boolean isDeployNewVersion = VersionUtils.isNewerVersion(versionToDeploy, lastDeploymentVersion);
                if (!isDataSourcesManaged) {
                    // If not already managed, manage data sources if not production mode or if
                    // deploying new version
                    if (!isProductionMode() || isDeployNewVersion) {
                        logInfo("Managing datasources...");
                        for (String datasource : datasources) {
                            dataSourceManager.manageDataSource(datasource, options);
                        }
                    }
                }

                // Do installation only if deployment version is new
                if (isDeployNewVersion) {
                    logInfo("Installing newer application version {0}. Current version is {1}.", versionToDeploy,
                            lastDeploymentVersion);

                    BootInstallationInfo<T> bootInstallationInfo = prepareBootInstallation();
                    if (bootInstallationInfo.isInstallers() && bootInstallationInfo.isFeatures()) {
                        for (String installerName : bootInstallationInfo.getFeatureInstallerNames()) {
                            FeatureInstaller<T> installer = (FeatureInstaller<T>) getComponent(installerName);
                            installer.installFeatures(bootInstallationInfo.getFeatures());
                        }
                    }

                    // Update last deployment feature
                    Feature lastDeploymentFeature = getFeature("lastDeploymentVersion", "0.0");
                    lastDeploymentFeature.setValue(lastDeploymentVersion);
                    db().updateByIdVersion(lastDeploymentFeature);

                    // Update current current deployment feature
                    deploymentFeature.setValue(versionToDeploy);
                    db().updateByIdVersion(deploymentFeature);
                } else {
                    if (lastDeploymentVersion.equals(versionToDeploy)) {
                        logInfo("Application deployment version {0} is current.", versionToDeploy);
                    } else {
                        logInfo("Application deployment version {0} is old. Current version is {1}.", versionToDeploy,
                                lastDeploymentVersion);
                    }
                }
            } finally {
                endClusterLock(BOOT_DEPLOYMENT_LOCK);
            }
        }

        startupShutdownHooks = getStartupShutdownHooks();
        if (DataUtils.isNotBlank(startupShutdownHooks)) {
            for (StartupShutdownHook startupShutdownHook : startupShutdownHooks) {
                startupShutdownHook.onApplicationStartup();
            }
        }

        onStartup();
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

    protected abstract void onStartup() throws UnifyException;

    protected abstract void onShutdown() throws UnifyException;

    private List<String> getApplicationDataSources() throws UnifyException {
        List<String> appDataSourceNames = getComponentNames(SqlDataSource.class);
        appDataSourceNames.remove(ApplicationComponents.APPLICATION_DYNAMICSQLDATASOURCE);
        return appDataSourceNames;
    }
    
    private Feature getFeature(String code, String defaultVal) throws UnifyException {
        try {
            grabClusterMasterLock(); // Check integrity of cluster lock table
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
            commitTransactions();
        }

        return null;
    }
}
