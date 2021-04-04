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
package com.tcdng.unify.core;

import java.io.InputStream;
import java.util.logging.LogManager;

import com.tcdng.unify.core.util.CommandInterfaceUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.TypeRepository;
import com.tcdng.unify.core.util.TypeUtils;
import com.tcdng.unify.core.util.UnifyConfigUtils;

/**
 * Unify application class.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class Unify {

    private static UnifyContainer uc;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Operation argument is required");
            System.exit(1);
        }

        String operation = args[0];
        String workingFolder = null;
        String configFile = null;
        String host = UnifyCoreConstants.DEFAULT_UNIFY_HOST;
        short port = UnifyCoreConstants.DEFAULT_COMMAND_PORT;

        for (int i = 1; i <= (args.length - 2); i += 2) {
            if ("-w".equals(args[i])) {
                workingFolder = args[i + 1];
            } else if ("-h".equals(args[i])) {
                host = args[i + 1];
            } else if ("-p".equals(args[i])) {
                port = Short.valueOf(args[i + 1]);
            } else if ("-c".equals(args[i])) {
                configFile = args[i + 1];
            } else {
                Unify.doHelp();
            }
        }

        if ("startup".equalsIgnoreCase(operation)) {
            Unify.doStartup(workingFolder, configFile, false);
        } else if ("install".equalsIgnoreCase(operation)) {
            Unify.doStartup(workingFolder, configFile, true);
        } else if ("shutdown".equalsIgnoreCase(operation)) {
            Unify.doShutdown(host, port);
        } else if ("help".equalsIgnoreCase(operation)) {
            Unify.doHelp();
        } else {
            System.err.println("Unknown operation - " + operation);
            System.exit(1);
        }
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

    private static void doStartup(String workingFolder, String configFile, boolean deploymentMode) {
        // Java 9 an 10 temp fix for jaxb binding and warnings
        // This is a temporary fix and should be removed and resolved with jaxb-api 2.3.1 when moving to minimum Java 9
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
        LogManager.getLogManager().reset();
        
        if (workingFolder == null || workingFolder.isEmpty()) {
            workingFolder = System.getProperty("user.dir");
        }

        UnifyContainerEnvironment uce = null;
        UnifyContainerConfig.Builder uccb = UnifyContainerConfig.newBuilder();
        try {
            TypeRepository tr = TypeUtils.getTypeRepositoryFromClasspath();
            uce = new UnifyContainerEnvironment(tr, workingFolder);
            UnifyConfigUtils.readConfigFromTypeRepository(uccb, tr);
            uccb.deploymentMode(deploymentMode);
        } catch (Exception e) {
            System.err.println("Failed scanning classpath type repository.");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        InputStream xmlInputStream = null;
        if (configFile == null || configFile.isEmpty()) {
            configFile = UnifyCoreConstants.CONFIGURATION_FILE;
        }

        try {
            xmlInputStream = IOUtils.openFileResourceInputStream(configFile, workingFolder);
        } catch (Exception e) {
            System.err
                    .println("Unable to open configuration file - " + IOUtils.buildFilename(workingFolder, configFile));
            e.printStackTrace(System.err);
            System.exit(1);
        }

        try {
            UnifyConfigUtils.readConfigFromXml(uccb, xmlInputStream, workingFolder);
        } catch (UnifyException e) {
            IOUtils.close(xmlInputStream);
            System.err
                    .println("Failed reading configuration file - " + IOUtils.buildFilename(workingFolder, configFile));
            e.printStackTrace(System.err);
            System.exit(1);
        } finally {
            IOUtils.close(xmlInputStream);
        }

        try {
            UnifyContainerConfig ucc = uccb.build();
            Unify.startup(uce, ucc);
        } catch (UnifyException e) {
            System.err.println("Error initializing Unify container.");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static void doShutdown(String host, short port) {
        try {
            System.out.println("Sending shutdown command to container on host '" + host + "' and port " + port + "...");
            CommandInterfaceUtils.sendCommand(host, port, "shutdown", "");
            System.out.println("Shutdown command successfully sent.");
        } catch (Exception e) {
            System.err.println("Error sending shutdown command. Unable to reach container instance running on '" + host
                    + "' and listening for commands on port " + port + ".");
        }
        System.exit(1);
    }

    private static void doHelp() {
        System.out.println("Usage:");

    }
}
