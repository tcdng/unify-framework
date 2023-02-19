/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.TypeRepository;
import com.tcdng.unify.core.util.TypeUtils;
import com.tcdng.unify.core.util.UnifyConfigUtils;

/**
 * Unify application class.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class Unify {

    private static final Logger LOGGER = Logger.getLogger(Unify.class.getName());

    private static UnifyContainer uc;

    public static void main(String[] args) {
        if (args.length == 0) {
            LOGGER.log(Level.SEVERE, "Operation argument is required");
            System.exit(1);
        }

        String operation = args[0];
        String workingFolder = null;
        String configFile = null;
        short port = 0;
        boolean restrictedJARMode = false;

        for (int i = 1; i <= (args.length - 2); i += 2) {
            if ("-w".equals(args[i])) {
                workingFolder = args[i + 1];
            } else if ("-p".equals(args[i])) {
                port = Short.valueOf(args[i + 1]);
            } else if ("-c".equals(args[i])) {
                configFile = args[i + 1];
            } else if ("-j".equals(args[i])) {
                restrictedJARMode = Boolean.parseBoolean(args[i + 1]);
            } else {
                Unify.doHelp();
            }
        }

        if ("startup".equalsIgnoreCase(operation)) {
            Unify.doStartup(workingFolder, configFile, null, port, false);
        } else if ("install".equalsIgnoreCase(operation)) {
            Unify.doStartup(workingFolder, configFile, null, port, true);
        } else if ("install-onejar-fat".equalsIgnoreCase(operation)) {
            if (restrictedJARMode) {
                IOUtils.enterRestrictedJARMode();
            }
            
            URL[] _baseUrls = Unify.getOneJarFatBaseUrls();
            Unify.doStartup(workingFolder, configFile, _baseUrls, port, true);
        } else if ("install-spring-fat".equalsIgnoreCase(operation)) {
            if (restrictedJARMode) {
                IOUtils.enterRestrictedJARMode();
            }
            
            URL[] _baseUrls = Unify.getSpringFatBaseUrls();
            Unify.doStartup(workingFolder, configFile, _baseUrls, port, true);
        } else if ("help".equalsIgnoreCase(operation)) {
            Unify.doHelp();
        } else {
            LOGGER.log(Level.SEVERE, "Unknown operation - " + operation);
            System.exit(1);
        }
    }

    public static synchronized void startup(String workingFolder, short preferredPort, URL... baseUrls)
            throws UnifyException {
        Unify.doStartup(workingFolder, null, baseUrls, preferredPort, true);
    }

    public static synchronized UnifyContainer startup(UnifyContainerEnvironment uce, UnifyContainerConfig ucc)
            throws UnifyException {
        if (uc != null) {
            throw new UnifyException(UnifyCoreErrorConstants.CONTAINER_IN_RUNTIME);
        }

        try {
            uc = new UnifyContainer();
            uc.startup(uce, ucc);
        } catch (UnifyException e) {
            uc = null;
            throw e;
        } catch (Exception e) {
            uc = null;
            throw new UnifyException(e, UnifyCoreErrorConstants.CONTAINER_STARTUP_ERROR);
        }

        return uc;
    }

    public static synchronized void shutdown(String accessKey) throws UnifyException {
        Unify.getContainer();
        if (!uc.getAccessKey().equals(accessKey)) {
            throw new UnifyException(UnifyCoreErrorConstants.INVALID_CONTAINER_RUNTIME_ACCESSKEY);
        }
        uc.shutdown();
        uc = null;
    }

    private static UnifyContainer getContainer() throws UnifyException {
        if (uc == null) {
            throw new UnifyException(UnifyCoreErrorConstants.NO_CONTAINER_IN_RUNTIME);
        }

        return uc;
    }

    private static void doStartup(String workingFolder, String configFile, URL[] baseUrls, short preferredPort,
            boolean deploymentMode) {
        // Java 9 an 10 temp fix for jaxb binding and warnings
        // This is a temporary fix and should be removed and resolved with jaxb-api
        // 2.3.1 when moving to minimum Java 9
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
        LogManager.getLogManager().reset();

        if (workingFolder == null || workingFolder.isEmpty()) {
            workingFolder = System.getProperty("user.dir");
        }

        UnifyContainerEnvironment uce = null;
        UnifyContainerConfig.Builder uccb = UnifyContainerConfig.newBuilder();
        try {
            LOGGER.log(Level.INFO, "Scanning classpath type repository...");
            TypeRepository tr = TypeUtils.getTypeRepositoryFromClasspath(baseUrls);
            uce = new UnifyContainerEnvironment(tr, workingFolder);
            UnifyConfigUtils.readConfigFromTypeRepository(uccb, tr);
            uccb.deploymentMode(deploymentMode);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed scanning classpath type repository.", e);
            e.printStackTrace();
            System.exit(1);
        }

        InputStream xmlInputStream = null;
        if (configFile == null || configFile.isEmpty()) {
            configFile = UnifyCoreConstants.CONFIGURATION_FILE;
        }

        final String environment = System.getProperty("unify.environment");
        if (!StringUtils.isBlank(environment)) {
            LOGGER.log(Level.INFO, "Environment specification detected...");
            LOGGER.log(Level.INFO, "Resolving container configuration file for environment...");
        	int extIndex = configFile.lastIndexOf('.');
        	configFile = configFile.substring(0, extIndex) + "-" + environment + configFile.substring(extIndex);
        }
        
        try {
            LOGGER.log(Level.INFO, "Reading container configuration file [{0}]...", configFile);
            xmlInputStream = IOUtils.openFileResourceInputStream(configFile, workingFolder);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "Unable to open configuration file - " + IOUtils.buildFilename(workingFolder, configFile), e);
            e.printStackTrace();
            System.exit(1);
        }

        try {
            UnifyConfigUtils.readConfigFromXml(uccb, xmlInputStream, workingFolder);
        } catch (UnifyException e) {
            IOUtils.close(xmlInputStream);
            LOGGER.log(Level.SEVERE,
                    "Failed reading configuration file - " + IOUtils.buildFilename(workingFolder, configFile), e);
            e.printStackTrace();
            System.exit(1);
        } finally {
            IOUtils.close(xmlInputStream);
        }

        try {
            if (preferredPort > 0) {
                uccb.preferredPort(preferredPort);
            }

            UnifyContainerConfig ucc = uccb.build();
            Unify.startup(uce, ucc);
        } catch (UnifyException e) {
            LOGGER.log(Level.SEVERE, "Error initializing Unify container.", e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void doHelp() {

    }
    
    private static URL[] getOneJarFatBaseUrls() {
        List<URL> baseUrls = new ArrayList<URL>();
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = cl.getResources("META-INF/MANIFEST.MF");
            String base = null;
            List<String[]> partList = new ArrayList<String[]>();
            while (urls.hasMoreElements()) {
                String url = urls.nextElement().toString();
                if (url.startsWith("jar:file:")) {
                    String[] parts = url.split("\\!/");
                    if (parts.length == 2) {
                        if (base == null || base.length() < parts[0].length()) {
                            base = parts[0];
                        }
                    }

                    partList.add(parts);
                }
            }

            if (base != null) {
                for (String[] parts : partList) {
                    if (parts.length == 3) {
                        baseUrls.add(new URL(base + "!/" + parts[1]));
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error resolving packaged JARs.", e);
            e.printStackTrace();
       }

        return baseUrls.isEmpty() ? null : baseUrls.toArray(new URL[baseUrls.size()]);
    }
    
    private static URL[] getSpringFatBaseUrls() {
        List<URL> baseUrls = new ArrayList<URL>();
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = cl.getResources("META-INF/MANIFEST.MF");
            String base = null;
            List<String[]> partList = new ArrayList<String[]>();
            while (urls.hasMoreElements()) {
                String url = urls.nextElement().toString();
                if (url.startsWith("jar:file:")) {
                    String[] parts = url.split("\\!/");
                    if (parts.length == 2) {
                        if (base == null || base.length() < parts[0].length()) {
                            base = parts[0];
                        }
                    }

                    partList.add(parts);
                }
            }

            if (base != null) {
                String classPath = base.substring("jar:".length());
                baseUrls.add(new URL(classPath));
                for (String[] parts : partList) {
                    if (parts.length == 3) {
                        baseUrls.add(new URL(base + "!/" + parts[1]));
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error resolving packaged JARs.", e);
            e.printStackTrace();
       }

        return baseUrls.isEmpty() ? null : baseUrls.toArray(new URL[baseUrls.size()]);
    }
}
