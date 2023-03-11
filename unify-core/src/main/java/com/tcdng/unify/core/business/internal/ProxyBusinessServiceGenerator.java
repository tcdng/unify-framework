/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.core.business.internal;

import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyPluginInfo;
import com.tcdng.unify.core.business.BusinessService;
import com.tcdng.unify.core.constant.DeploymentMode;

/**
 * Proxy business service generator by extension class generator.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ProxyBusinessServiceGenerator extends ProxyBusinessServiceMethodRelay {

    /**
     * Generates a name for proxy version of a business service type.
     * 
     * @param businessServiceClazz
     *            the business service type
     * @return the generated simple name
     * @throws UnifyException
     *             if an error occurs
     */
    String generateProxyBusinessServiceName(Class<? extends BusinessService> businessServiceClazz)
            throws UnifyException;

    /**
     * Generates a simple name for proxy version of a business service type.
     * 
     * @param businessServiceClazz
     *            the business service type
     * @return the generated simple name
     * @throws UnifyException
     *             if an error occurs
     */
    String generateProxyBusinessServiceSimpleName(Class<? extends BusinessService> businessServiceClazz)
            throws UnifyException;

    /**
     * Generates the java source code for proxy version of a business service type.
     * 
     * @param name
     *            the component name
     * @param businessServiceClazz
     *            the business service type
     * @param pluginsBySocketMap
     *            plug-ins by socket name
     * @param deploymentMode
     *            deployment mode for which code generation should be done
     * @return the generated java code
     * @throws UnifyException
     *             if an error occurs
     */
    String generateProxyBusinessServiceSource(String name, Class<? extends BusinessService> businessServiceClazz,
            Map<String, List<UnifyPluginInfo>> pluginsBySocketMap, DeploymentMode deploymentMode) throws UnifyException;

    /**
     * Generates, compiles and loads a business service proxy class.
     * 
     * @param name
     *            the component name
     * @param businessServiceClazz
     *            the business service type
     * @param pluginsBySocketMap
     *            plug-ins by socket name
     * @return the proxy class
     * @throws UnifyException
     *             if an error occurs
     */
    Class<? extends BusinessService> generateCompileLoadProxyBusinessServiceClass(String name,
            Class<? extends BusinessService> businessServiceClazz,
            Map<String, List<UnifyPluginInfo>> pluginsBySocketMap) throws UnifyException;

    /**
     * Returns relayed annotation information for supplied method signature.
     * 
     * @param methodSignature
     *            the method signature to search with
     * @return info object if found otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    ProxyBusinessServiceMethodAnnotationInfo getProxyBusinessServiceMethodAnnotationInfo(String methodSignature)
            throws UnifyException;
}
