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
package com.tcdng.unify.core.business.internal;

import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyPluginInfo;
import com.tcdng.unify.core.business.BusinessModule;

/**
 * Proxy business module generator by extension class generator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ProxyBusinessModuleGenerator extends ProxyBusinessModuleMethodRelay {

	/**
	 * Generates a name for proxy version of a business module type.
	 * 
	 * @param businessModuleClazz
	 *            the business module type
	 * @return the generated simple name
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String generateProxyBusinessModuleName(Class<? extends BusinessModule> businessModuleClazz) throws UnifyException;

	/**
	 * Generates a simple name for proxy version of a business module type.
	 * 
	 * @param businessModuleClazz
	 *            the business module type
	 * @return the generated simple name
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String generateProxyBusinessModuleSimpleName(Class<? extends BusinessModule> businessModuleClazz)
			throws UnifyException;

	/**
	 * Generates the java source code for proxy version of a business module type.
	 * 
	 * @param name
	 *            the component name
	 * @param businessModuleClazz
	 *            the business module type
	 * @param pluginsBySocketMap
	 *            plug-ins by socket name
	 * @param clusterMode
	 *            Flag indicates generation should be done for cluster mode
	 * @return the generated java code
	 * @throws UnifyException
	 *             if an error occurs
	 */
	String generateProxyBusinessModuleSource(String name, Class<? extends BusinessModule> businessModuleClazz,
			Map<String, List<UnifyPluginInfo>> pluginsBySocketMap, boolean clusterMode) throws UnifyException;

	/**
	 * Generates, compiles and loads a business module proxy class.
	 * 
	 * @param name
	 *            the component name
	 * @param businessModuleClazz
	 *            the business module type
	 * @param pluginsBySocketMap
	 *            plug-ins by socket name
	 * @return the proxy class
	 * @throws UnifyException
	 *             if an error occurs
	 */
	Class<? extends BusinessModule> generateCompileLoadProxyBusinessModuleClass(String name,
			Class<? extends BusinessModule> businessModuleClazz, Map<String, List<UnifyPluginInfo>> pluginsBySocketMap)
			throws UnifyException;

	/**
	 * Returns relayed annotation information for supplied method signature.
	 * 
	 * @param methodSignature
	 *            the method signature to search with
	 * @return info object if found otherwise null
	 * @throws UnifyException
	 *             if an error occurs
	 */
	ProxyBusinessModuleMethodAnnotationInfo getProxyBusinessModuleMethodAnnotationInfo(String methodSignature)
			throws UnifyException;
}
