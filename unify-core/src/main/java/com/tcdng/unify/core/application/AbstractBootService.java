/*
 * Copyright 2018-2019 The Code Department.
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
import java.util.Map;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.StaticList;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.AbstractBusinessService;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.constant.LocaleType;
import com.tcdng.unify.core.database.DataSourceManager;
import com.tcdng.unify.core.database.StaticReference;
import com.tcdng.unify.core.database.StaticReferenceQuery;
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

    @Configurable
    private DataSourceManager dataSourceManager;

    @Configurable(ApplicationComponents.APPLICATION_DATASOURCE)
    private String[] datasources;

    private List<StartupShutdownHook> startupShutdownHooks;

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void startup() throws UnifyException {
        logInfo("Initializing datasources...");
        for (String datasource : datasources) {
            dataSourceManager.initDataSource(datasource);
        }

        if (isDeploymentMode()) {
            Feature deploymentFeature = getFeature("deploymentVersion", "0.0");
            boolean isDataSourcesManaged = false;
            if (deploymentFeature == null) {
                // Blank database. Manage data sources first time.
                manageDataSources();
                deploymentFeature = getFeature("deploymentVersion", "0.0");
                isDataSourcesManaged = true;
            }

            if (grabClusterMasterLock()) {
                logInfo("Checking application version information...");
                deploymentFeature = getFeature("deploymentVersion", "0.0");
                String lastDeploymentVersion = deploymentFeature.getValue();
                String versionToDeploy = getDeploymentVersion();
                boolean isDeployNewVersion = VersionUtils.isNewerVersion(versionToDeploy, lastDeploymentVersion);
                if (!isDataSourcesManaged) {
                    // If not already managed, manage data sources if not production mode or if
                    // deploying new version
                    if (!isProductionMode() || isDeployNewVersion) {
                        manageDataSources();
                    }
                }

                // Do installation only if deployment version is new
                if (isDeployNewVersion) {
                    logInfo("Installing newer application version {0}. Current version is {1}.", versionToDeploy,
                            lastDeploymentVersion);

                    updateStaticReferenceTables();

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

    private void manageDataSources() throws UnifyException {
        logInfo("Managing datasources...");
        for (String datasource : datasources) {
            dataSourceManager.manageDataSource(datasource);
        }
    }

    private void updateStaticReferenceTables() throws UnifyException {
        // Update reference tables
        logDebug("Updating static reference tables...");
        List<Class<? extends EnumConst>> enumConstList = getAnnotatedClasses(EnumConst.class, StaticList.class);

        for (Class<? extends EnumConst> clazz : enumConstList) {
            StaticList ra = clazz.getAnnotation(StaticList.class);
            logDebug("Updating static reference table [{0}]...", ra.value());
            Map<String, String> map = getListMap(LocaleType.APPLICATION, ra.value());
            StaticReferenceQuery query = new StaticReferenceQuery(clazz);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String description = entry.getValue();
                query.clear();
                query.code(entry.getKey());
                StaticReference staticData = db().find(query);
                if (staticData == null) {
                    staticData = new StaticReference(clazz);
                    staticData.setCode(entry.getKey());
                    staticData.setDescription(description);
                    db().create(staticData);
                } else if (!description.equals(staticData.getDescription())) {
                    staticData.setDescription(description);
                    db().updateById(staticData);
                }
            }
        }
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
        }

        return null;
    }
}
