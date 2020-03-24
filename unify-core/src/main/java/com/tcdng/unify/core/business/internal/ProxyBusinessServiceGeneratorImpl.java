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
package com.tcdng.unify.core.business.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyComponentContext;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyPluginInfo;
import com.tcdng.unify.core.annotation.Broadcast;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Expirable;
import com.tcdng.unify.core.annotation.PluginType;
import com.tcdng.unify.core.annotation.Synchronized;
import com.tcdng.unify.core.annotation.Taskable;
import com.tcdng.unify.core.annotation.TransactionAttribute;
import com.tcdng.unify.core.annotation.Transactional;
import com.tcdng.unify.core.business.BusinessLogicInput;
import com.tcdng.unify.core.business.BusinessLogicOutput;
import com.tcdng.unify.core.business.BusinessLogicUnit;
import com.tcdng.unify.core.business.BusinessService;
import com.tcdng.unify.core.constant.DeploymentMode;
import com.tcdng.unify.core.runtime.RuntimeJavaClassManager;
import com.tcdng.unify.core.system.ClusterService;
import com.tcdng.unify.core.util.NameUtils;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Default implementation proxy business service generator.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_PROXYBUSINESSSERVICEGENERATOR)
public class ProxyBusinessServiceGeneratorImpl extends AbstractUnifyComponent implements ProxyBusinessServiceGenerator {

    @Configurable
    private RuntimeJavaClassManager runtimeJavaClassManager;

    @Configurable("proxy")
    private String proxyPackageExtension;

    @Configurable("Proxy")
    private String proxyClassExtension;

    @Configurable
    private boolean logSource;

    private Map<String, ProxyBusinessServiceMethodAnnotationInfo> annotationInfoBySignature;

    public ProxyBusinessServiceGeneratorImpl() {
        annotationInfoBySignature = new HashMap<String, ProxyBusinessServiceMethodAnnotationInfo>();
    }

    @Override
    public String generateProxyBusinessServiceName(Class<? extends BusinessService> businessServiceClazz)
            throws UnifyException {
        return businessServiceClazz.getPackage().getName() + "." + proxyPackageExtension + "."
                + businessServiceClazz.getSimpleName() + proxyClassExtension;
    }

    @Override
    public String generateProxyBusinessServiceSimpleName(Class<? extends BusinessService> businessServiceClazz)
            throws UnifyException {
        return businessServiceClazz.getSimpleName() + proxyClassExtension;
    }

    @Override
    public String generateProxyBusinessServiceSource(String name, Class<? extends BusinessService> businessServiceClazz,
            Map<String, List<UnifyPluginInfo>> pluginsBySocketMap, DeploymentMode deploymentMode)
            throws UnifyException {
        String simpleName = generateProxyBusinessServiceSimpleName(businessServiceClazz);
        String packageName = businessServiceClazz.getPackage().getName() + "." + proxyPackageExtension;

        ReflectUtils.assertPublicConcreteNonFinal(businessServiceClazz);

        ReflectUtils.assertInterface(businessServiceClazz, BusinessService.class);
        boolean isUseCsService = !ApplicationComponents.APPLICATION_CLUSTERSERVICE.equals(name);

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n");
        sb.append('\n');
        sb.append("public class ").append(simpleName).append(" extends ").append(businessServiceClazz.getName())
                .append(" {\n");

        // Initialize method should be non-final
        Method initMethod = ReflectUtils.getMethod(businessServiceClazz, "initialize", UnifyComponentContext.class);
        ReflectUtils.assertOverridable(initMethod);

        Transactional clazzTa = businessServiceClazz.getAnnotation(Transactional.class);
        Method[] methods = businessServiceClazz.getMethods();

        // Identify locks and extract relayed annotation information
        Map<Method, String> methodLockMap = new HashMap<Method, String>();
        String syncLockBase = simpleName;
        for (Method method : methods) {
            Synchronized syna = method.getAnnotation(Synchronized.class);
            if (syna != null) {
                String lock = syncLockBase + '.' + syna.value();
                methodLockMap.put(method, lock);
            }

            Taskable ta = method.getAnnotation(Taskable.class);
            Expirable ea = method.getAnnotation(Expirable.class);
            if (ta != null || ea != null) {
                annotationInfoBySignature.put(ReflectUtils.getMethodSignature(name, method),
                        new ProxyBusinessServiceMethodAnnotationInfo(ta, ea));
            }
        }

        if (isUseCsService) {
            sb.append("\tprivate ").append(ClusterService.class.getCanonicalName()).append(" csService;\n");
        }

        // Write methods
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if ("tm".equals(method.getName()) && parameterTypes.length == 0) {
                continue;
            }

            int modifiers = method.getModifiers();
            boolean hasUnifyException = false;
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            for (Class<?> exceptionType : exceptionTypes) {
                if (UnifyException.class.equals(exceptionType)) {
                    hasUnifyException = true;
                    break;
                }
            }

            Transactional ta = method.getAnnotation(Transactional.class);
            if (ta == null) {
                if (clazzTa != null) {
                    if (!hasUnifyException || isNotValidTransactionMethod(modifiers))
                        continue;
                    ta = clazzTa;
                }
            } else {
                if (isNotValidTransactionMethod(modifiers)) {
                    throw new UnifyException(UnifyCoreErrorConstants.REFLECT_METHOD_WITH_UNSUPORTED_MODIFIERS, method);
                }
                if (!hasUnifyException) {
                    throw new UnifyException(UnifyCoreErrorConstants.MODULE_TRANSACTIONAL_MUST_THROW_EXCEPTION, method,
                            businessServiceClazz);
                }
            }

            boolean isBroadcast = method.getAnnotation(Broadcast.class) != null;
            boolean isTransactional = ta != null;
            boolean isSynchronized = isUseCsService && methodLockMap.containsKey(method);
            if (!isTransactional && !isSynchronized && !isBroadcast) {
                // No need to override method if it is not transactional or not
                // synchronized or not broadcast
                // Move to next method
                continue;
            }

            // Build method
            sb.append("\tpublic ");
            sb.append(method.getReturnType().getCanonicalName()).append(" ").append(method.getName());

            // Append parameters
            StringBuilder callParams = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                    callParams.append(", ");
                }

                sb.append(parameterTypes[i].getCanonicalName()).append(" p").append(i);
                callParams.append(" p").append(i);
            }
            sb.append(") ");

            // Append method exceptions
            if (exceptionTypes.length > 0) {
                sb.append("throws ");
                for (int i = 0; i < exceptionTypes.length; i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    if (exceptionTypes[i].equals(UnifyException.class)) {
                        hasUnifyException = true;
                    }
                    sb.append(exceptionTypes[i].getCanonicalName());
                }
            }
            sb.append("{\n");

            // Transaction boundary start
            boolean nonVoidReturn = !method.getReturnType().equals(void.class);
            if (nonVoidReturn) {
                sb.append("\t\t").append(method.getReturnType().getCanonicalName()).append(" result;\n");
            }

            if (isTransactional) {
                sb.append("\t\ttm().beginTransaction(").append(TransactionAttribute.class.getName()).append('.')
                        .append(ta.value()).append(");\n");
                sb.append("\t\ttry{\n");
            }

            // Synchronization boundary start
            String extraTab = "";
            if (isSynchronized) {
                if (isTransactional) {
                    extraTab = "\t\t\t";
                }
                String lock = methodLockMap.get(method);
                sb.append(extraTab).append("\t\t\t\tcsService.beginSynchronization(\"").append(lock).append("\");\n");
                sb.append(extraTab).append("\t\t\t\t\ttry{\n");
            }

            // Pre-logic plug-ins
            List<UnifyPluginInfo> pluginInfoList =
                    pluginsBySocketMap.get(ReflectUtils.getMethodSignature(name, method));
            boolean isPlugin = pluginInfoList != null && !pluginInfoList.isEmpty();
            if (isPlugin) {
                sb.append("\t\t\t").append(BusinessLogicInput.class.getCanonicalName()).append(" blin = new ")
                        .append(BusinessLogicInput.class.getCanonicalName()).append("();\n");
                for (int i = 0; i < parameterTypes.length; i++) {
                    sb.append("\t\t\tblin.setParameter(\"p").append(i).append("\", p").append(i).append(");\n");
                }
                sb.append("\t\t\t").append(BusinessLogicOutput.class.getCanonicalName()).append(" blout = new ")
                        .append(BusinessLogicOutput.class.getCanonicalName()).append("();\n");
                for (UnifyPluginInfo upi : pluginInfoList) {
                    if (PluginType.PRE_LOGIC.equals(upi.getPluginType())) {
                        sb.append("\t\t\t((").append(BusinessLogicUnit.class.getCanonicalName())
                                .append(") this.getComponent(\"").append(upi.getPluginName())
                                .append("\")).execute(blin, blout);\n");
                    }
                }
            }

            // Invoke proxied service
            sb.append("\t\t\t");
            if (nonVoidReturn) {
                sb.append("result = ");
            }
            sb.append("super.").append(method.getName()).append("(").append(callParams).append(");\n");

            // Post-logic plug-ins
            if (isPlugin) {
                if (nonVoidReturn) {
                    sb.append("\t\t\tblin.setParameter(\"r0\", result);\n");
                }
                for (UnifyPluginInfo upi : pluginInfoList) {
                    if (PluginType.POST_LOGIC.equals(upi.getPluginType())) {
                        sb.append("\t\t\t((").append(BusinessLogicUnit.class.getCanonicalName())
                                .append(") this.getComponent(\"").append(upi.getPluginName())
                                .append("\")).execute(blin, blout);\n");
                    }
                }
            }

            // Broadcast if necessary
            if (isBroadcast) {
                sb.append("\t\t\tthis.getUnifyComponentContext().broadcastToOtherNodes(\"")
                        .append(NameUtils.getComponentMethodName(name, method.getName())).append("\", p0);\n");
            }

            // Synchronization boundary end
            if (isSynchronized) {
                String lock = methodLockMap.get(method);
                sb.append(extraTab).append("\t\t\t\t}finally{\n");
                sb.append(extraTab).append("\t\t\t\t\tcsService.endSynchronization(\"").append(lock).append("\");\n");
                sb.append(extraTab).append("\t\t\t\t}\n");
            }

            // Transaction boundary end
            if (isTransactional) {
                sb.append("\t\t} ");
                for (Class<?> exceptionType : exceptionTypes) {
                    sb.append("catch(").append(exceptionType.getCanonicalName()).append(" e) {\n");
                    sb.append("\t\t\ttm().setRollback();\n");
                    sb.append("\t\t\tthrow e;\n");
                    sb.append("\t\t} ");
                }
                sb.append("catch(RuntimeException e) {\n");
                sb.append("\t\t\ttm().setRollback();\n");
                sb.append("\t\t\tthrow e;\n");
                sb.append("\t\t} finally {\n");
                sb.append("\t\t\ttm().endTransaction();\n");
                sb.append("\t\t}\n");
            }

            if (nonVoidReturn) {
                sb.append("\t\t return result;\n");
            }
            sb.append("\t}\n\n");
        }

        if (isUseCsService) {
            sb.append("\tprotected void onInitialize() throws ").append(UnifyException.class.getCanonicalName())
                    .append(" {\n");
            sb.append("\t\tsuper.onInitialize();\n");
            sb.append("\t\tthis.csService = (").append(ClusterService.class.getCanonicalName())
                    .append(")this.getUnifyComponentContext().getComponent(\"")
                    .append(ApplicationComponents.APPLICATION_CLUSTERSERVICE).append("\");\n");
            sb.append("\t}\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends BusinessService> generateCompileLoadProxyBusinessServiceClass(String name,
            Class<? extends BusinessService> businessServiceClazz,
            Map<String, List<UnifyPluginInfo>> pluginsBySocketMap) throws UnifyException {
        boolean isClusterMode = isClusterMode();
        try {
            DeploymentMode deploymentMode = DeploymentMode.STANDALONE;
            if (isClusterMode) {
                deploymentMode = DeploymentMode.CLUSTER;
            }

            String source =
                    generateProxyBusinessServiceSource(name, businessServiceClazz, pluginsBySocketMap, deploymentMode);
            if (logSource) {
                logDebug("Generated source for [{0}]. Cluster mode: [{1}], Source:\n [{2}]", businessServiceClazz,
                        isClusterMode, source);
            }

            return (Class<? extends BusinessService>) runtimeJavaClassManager
                    .compileAndLoadJavaClass(generateProxyBusinessServiceName(businessServiceClazz), source);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPILER_CLASSLOAD_ERROR);
        }
    }

    @Override
    public ProxyBusinessServiceMethodAnnotationInfo getProxyBusinessServiceMethodAnnotationInfo(String methodSignature)
            throws UnifyException {
        return annotationInfoBySignature.get(methodSignature);
    }

    @Override
    public Taskable getTaskable(String signature) throws UnifyException {
        ProxyBusinessServiceMethodAnnotationInfo mpaInfo = annotationInfoBySignature.get(signature);
        if (mpaInfo != null) {
            return mpaInfo.getTaskable();
        }
        return null;
    }

    @Override
    public Expirable getExpirable(String signature) throws UnifyException {
        ProxyBusinessServiceMethodAnnotationInfo mpaInfo = this.annotationInfoBySignature.get(signature);
        if (mpaInfo != null) {
            return mpaInfo.getExpirable();
        }
        return null;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    protected boolean isNotValidTransactionMethod(int modifiers) {
        return Modifier.isAbstract(modifiers) || Modifier.isFinal(modifiers) || !Modifier.isPublic(modifiers);
    }
}
