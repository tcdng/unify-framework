/*
 * Copyright 2018 The Code Department
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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.tcdng.unify.core.list.ListManager;
import com.tcdng.unify.core.logging.Logger;
import com.tcdng.unify.core.message.ResourceBundles;
import com.tcdng.unify.core.upl.UplComponent;

/**
 * The component context class.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UnifyComponentContext {

    private ApplicationContext applicationContext;

    private RequestContextManager requestContextManager;

    private ListManager listManager;

    private Logger logger;

    private String name;

    public UnifyComponentContext(ApplicationContext applicationContext, Logger logger, String name) {
        this.applicationContext = applicationContext;
        this.logger = logger;
        this.name = name;
    }

    /**
     * Gets the component name.
     * 
     * @return the component name
     */
    public String getName() {
        return name;
    }

    /**
     * Sends a message to the application container.
     * 
     * @param command
     *            the command to send
     * @param params
     *            the command parameters
     * @throws UnifyException
     *             if an error occurs
     */
    public void sendCommand(String command, String... params) throws UnifyException {
        applicationContext.getContainer().command(command, params);
    }

    /**
     * Gets the context's container information.
     * 
     * @return the container information object
     * @throws UnifyException
     *             if an error occurs
     */
    public UnifyContainerInfo getContainerInfo() throws UnifyException {
        return applicationContext.getContainer().getInfo();
    }

    /**
     * Gets component by name.
     * 
     * @param name
     *            the component name
     * @return the component with name
     * @throws UnifyException
     *             If component is unknown. If an error occurs
     */
    public UnifyComponent getComponent(String name) throws UnifyException {
        return applicationContext.getContainer().getComponent(name);
    }

    /**
     * Gets a component by name using alternate settings. Applies to non-singletons
     * only..
     * 
     * @param name
     *            the component name
     * @param altSettings
     *            the alternate settings
     * @return the component
     * @throws UnifyException
     *             if container is not started. If component with name is unknown.
     *             If component is a singleton. If component instantiation error
     *             occurs.
     */
    public UnifyComponent getComponent(String name, UnifyComponentSettings altSettings) throws UnifyException {
        return applicationContext.getContainer().getComponent(name, altSettings);
    }

    /**
     * Returns true if component with name exists in context.
     * 
     * @param name
     *            the component name
     * @throws UnifyException
     *             If component an error occurs.
     */
    public boolean isComponent(String name) throws UnifyException {
        return applicationContext.getContainer().isComponent(name);
    }

    /**
     * Gets a UPL component instance using supplied descriptor.
     * 
     * @param locale
     *            the locale
     * @param descriptor
     *            the UPL descriptor
     * @param cached
     *            the cached flag.
     * @return If the cached flag is set, the container returns a cached instance
     *         with the same descriptor and locale if found. Otherwise, a new
     *         instance is returned.
     * @throws UnifyException
     *             if an error occurs
     */
    public UplComponent getUplComponent(Locale locale, String descriptor, boolean cached) throws UnifyException {
        return applicationContext.getContainer().getUplComponent(locale, descriptor, cached);
    }

    /**
     * Gets a UPL component using UPL attributes key.
     * 
     * @param locale
     *            the locale
     * @param attributesKey
     *            the UPL attributes ID
     * @return a UPL component
     * @throws UnifyException
     *             if an error occurs
     */
    public UplComponent getUplComponent(Locale locale, String attributesKey) throws UnifyException {
        return applicationContext.getContainer().getUplComponent(locale, attributesKey);
    }

    /**
     * Gets the component configuration of component with specified name.
     * 
     * @param name
     *            the component name
     * @return the component configuration otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    public UnifyComponentConfig getComponentConfig(String name) throws UnifyException {
        return applicationContext.getContainer().getComponentConfig(name);
    }

    /**
     * Gets all component names of particular types in scope.
     * 
     * @param type
     *            the component type
     * @return the list of names
     * @throws UnifyException
     *             if an error occurs
     */
    public List<String> getComponentNames(Class<? extends UnifyComponent> type) throws UnifyException {
        return applicationContext.getContainer().getComponentNames(type);
    }

    /**
     * Gets component configuration for types the extend or implement specified
     * types.
     * 
     * @param type
     *            the component type
     * @return the component types
     * @throws UnifyException
     *             if an error occurs
     */
    public List<UnifyComponentConfig> getComponentConfigs(Class<? extends UnifyComponent> type) throws UnifyException {
        return applicationContext.getContainer().getComponentConfigs(type);
    }

    /**
     * Returns classes of a particular type annotated with a specific type of
     * annotation.
     * 
     * @param classType
     *            the annotated class type
     * @param annotationClass
     *            the annotation
     * @param basePackages
     *            packages to restrict search to. This parameter is optional.
     * @return list of annotated classes
     * @throws UnifyException
     *             if an error occurs
     */
    public <T> List<Class<? extends T>> getAnnotatedClasses(Class<T> classType,
            Class<? extends Annotation> annotationClass, String... basePackages) throws UnifyException {
        return applicationContext.getContainer().getAnnotatedClasses(classType, annotationClass);
    }

    /**
     * Gets current thread request context object.
     * 
     * @return the request context
     * @throws UnifyException
     *             if an error occurs
     */
    public RequestContext getRequestContext() throws UnifyException {
        return getRequestContextManager().getRequestContext();
    }

    /**
     * Gets session context object.
     * 
     * @return the session context
     * @throws UnifyException
     *             if an error occurs
     */
    public SessionContext getSessionContext() throws UnifyException {
        return getRequestContextManager().getRequestContext().getSessionContext();
    }

    /**
     * Sets an attribute in application context.
     * 
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value to set
     * @throws UnifyException
     *             if an error occurs
     */
    public void setApplicationAttribute(String name, Object value) throws UnifyException {
        applicationContext.setAttribute(name, value);
    }

    /**
     * Gets an attribute value from application context.
     * 
     * @param name
     *            the attribute name
     * @return the attribute value
     * @throws UnifyException
     *             if an error occurs
     */
    public Object getApplicationAttribute(String name) throws UnifyException {
        return applicationContext.getAttribute(name);
    }

    /**
     * Removes an attribute value from application context.
     * 
     * @param name
     *            the attribute name
     * @return the attribute value
     * @throws UnifyException
     *             if an error occurs
     */
    public Object removeApplicationAttribute(String name) throws UnifyException {
        return applicationContext.removeAttribute(name);
    }

    /**
     * Removes attribute values from application context.
     * 
     * @param names
     *            the attribute names
     * @throws UnifyException
     *             if an error occurs
     */
    public void removeApplicationAttributes(String... names) throws UnifyException {
        applicationContext.removeAttributes(names);
    }

    /**
     * Checks if application context has an attribute.
     * 
     * @param name
     *            the attribute name
     * @return a true value if attribute exists in application context otherwise
     *         false
     * @throws UnifyException
     *             if an error occurs
     */
    public boolean isApplicationAttribute(String name) throws UnifyException {
        return applicationContext.isAttribute(name);
    }

    /**
     * Gets application locale
     * 
     * @return the application local
     * @throws UnifyException
     *             if an error occurs
     */
    protected Locale getApplicationLocale() throws UnifyException {
        return applicationContext.getApplicationLocale();
    }

    /**
     * Gets the application banner ASCII text
     * 
     * @return the application banner text as a list of strings
     * @throws UnifyException
     *             if an error occurs
     */
    protected List<String> getApplicationBanner() throws UnifyException {
        return applicationContext.getApplicationBanner();
    }

    /**
     * Returns privilege settings for supplied privilege code and current session
     * role.
     * 
     * @param privilege
     *            the privilege to test
     * @return the privilege settings
     * @throws UnifyException
     *             if an error occurs
     */
    public PrivilegeSettings getRolePrivilegeSettings(String privilege) throws UnifyException {
        UserToken userToken = getSessionContext().getUserToken();
        if (userToken != null) {
            return applicationContext.getPrivilegeSettings(userToken.getRoleCode(), privilege);
        }
        return applicationContext.getPrivilegeSettings(null, privilege);
    }

    /**
     * Returns privilege codes for supplied privilege category and current session
     * role.
     * 
     * @param privilegeCategoryCode
     *            the privilege category code
     * @return set of privilege codes
     * @throws UnifyException
     *             if an error occurs
     */
    public Set<String> getRolePrivilegeCodes(String privilegeCategoryCode) throws UnifyException {
        return applicationContext.getPrivilegeCodes(getSessionContext().getUserToken().getRoleCode(),
                privilegeCategoryCode);
    }

    /**
     * Checks if current session role has privilege.
     * 
     * @param privilegeCategoryCode
     *            the privilege category code
     * @param privilegeCode
     *            the privilege code
     * @return a true value is current session role has privilege otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    public boolean isRolePrivilege(String privilegeCategoryCode, String privilegeCode) throws UnifyException {
        UserToken userToken = getSessionContext().getUserToken();
        if (userToken != null && userToken.getRoleCode() != null) {
            Set<String> privileges = getRolePrivilegeCodes(privilegeCategoryCode);
            if (privileges != null) {
                return privileges.contains(privilegeCode);
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if application context has role attributes loaded for the supplied
     * role code.
     * 
     * @param roleCode
     *            the role code
     * @return true if context has attributes for role
     */
    public boolean isRoleAttributes(String roleCode) {
        return applicationContext.isRoleAttributes(roleCode);
    }

    /**
     * Returns workflow step codes that are associated with role of current user.
     * 
     * @return a set of workflow step codes
     * @throws UnifyException if an error occurs
     */
    public Set<String> getCurrentUserRoleStepCodes() throws UnifyException {
        UserToken userToken = getSessionContext().getUserToken();
        if (userToken != null) {
            return getRoleStepCodes(userToken.getRoleCode());
        }

        return Collections.emptySet();
    }

    /**
     * Returns workflow step codes that are associated with role.
     * 
     * @param roleCode
     *            the role code
     * @return a set of workflow step codes
     */
    public Set<String> getRoleStepCodes(String roleCode) {
        return applicationContext.getStepCodes(roleCode);
    }

    /**
     * Sets attributes for specified role.
     * 
     * @param roleCode
     *            the role code
     * @param roleAttributes
     *            the attributes to load.
     */
    public void setRoleAttributes(String roleCode, RoleAttributes roleAttributes) {
        applicationContext.setRoleAttributes(roleCode, roleAttributes);
    }

    /**
     * Tries to grab the cluster master synchronization lock.
     * 
     * @return a true value is lock is obtained otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    public boolean grabClusterMasterLock() throws UnifyException {
        return applicationContext.getContainer().grabClusterMasterLock();
    }

    /**
     * Tries to grab a cluster synchronization lock. Lock must be released after use
     * with {@link #releaseClusterLock(String)}
     * 
     * @param lockName
     *            the lock name
     * @return a true value is lock is obtained otherwise false
     * @throws UnifyException
     *             if an error occurs
     */
    public boolean grabClusterLock(String lockName) throws UnifyException {
        return applicationContext.getContainer().grabClusterLock(lockName);
    }

    /**
     * Releases a cluster synchronization lock.
     * 
     * @param lockName
     *            the lock name
     * @return a true value if lock was released
     * @throws UnifyException
     *             if an error occurs
     */
    public boolean releaseClusterLock(String lockName) throws UnifyException {
        return applicationContext.getContainer().releaseClusterLock(lockName);
    }

    /**
     * Broadcasts a cluster command to other nodes.
     * 
     * @param command
     *            the command to braodcast
     * @param params
     *            the command parameters
     * @throws UnifyException
     *             if an error occurs
     */
    public void broadcastToOtherNodes(String command, String... params) throws UnifyException {
        applicationContext.getContainer().broadcastToOtherNodes(command, params);
    }

    /**
     * Broadcasts attribute to all sessions in this node.
     * 
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value. A null value clears attribute.
     * @throws UnifyException
     *             if an error occurs
     */
    public void broadcastToSessions(String name, Object value) throws UnifyException {
        applicationContext.getContainer().broadcastToSessions(name, value);
    }

    /**
     * Broadcasts attribute to specific application session context in this node.
     * 
     * @param sessionId
     *            the session ID
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value. A null value clears attribute.
     * @throws UnifyException
     *             if an error occurs
     */
    public void broadcastToSession(String sessionId, String name, Object value) throws UnifyException {
        applicationContext.getContainer().broadcastToSession(sessionId, name, value);
    }

    public List<UnifyStaticSettings> getStaticSettings() {
        return applicationContext.getContainer().getStaticSettings();
    }

    public ResourceBundles getMessages() throws UnifyException {
        return applicationContext.getContainer().getMessages();
    }

    public ListManager getListManager() throws UnifyException {
        if (listManager == null) {
            listManager = (ListManager) applicationContext.getContainer()
                    .getComponent(ApplicationComponents.APPLICATION_LISTMANAGER);
        }
        return listManager;
    }

    public Logger getLogger() throws UnifyException {
        return logger;
    }

    public String getLineSeparator() throws UnifyException {
        return applicationContext.getLineSeparator();
    }

    public String getNodeId() throws UnifyException {
        return applicationContext.getContainer().getNodeId();
    }

    public String getInstanceCode() throws UnifyException {
        return applicationContext.getContainer().getInstanceCode();
    }

    public String getInstanceName() throws UnifyException {
        return applicationContext.getContainer().getInstanceName();
    }

    public String getDeploymentVersion() throws UnifyException {
        return applicationContext.getContainer().getDeploymentVersion();
    }

    public Object getContainerSetting(String name) throws UnifyException {
        return applicationContext.getContainer().getSetting(name);
    }

    public String getWorkingPath() throws UnifyException {
        return applicationContext.getContainer().getWorkingPath();
    }

    public boolean isProductionMode() throws UnifyException {
        return applicationContext.getContainer().isProductionMode();
    }

    public boolean isDeploymentMode() throws UnifyException {
        return applicationContext.getContainer().isDeploymentMode();
    }

    public boolean isClusterMode() throws UnifyException {
        return applicationContext.getContainer().isClusterMode();
    }

    private RequestContextManager getRequestContextManager() throws UnifyException {
        if (requestContextManager == null) {
            requestContextManager = (RequestContextManager) applicationContext.getContainer()
                    .getComponent(ApplicationComponents.APPLICATION_REQUESTCONTEXTMANAGER);
        }
        return requestContextManager;
    }
}
