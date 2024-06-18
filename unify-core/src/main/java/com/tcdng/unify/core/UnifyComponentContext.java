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
package com.tcdng.unify.core;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.list.ListManager;
import com.tcdng.unify.core.logging.Logger;
import com.tcdng.unify.core.message.ResourceBundles;
import com.tcdng.unify.core.upl.UplComponent;

/**
 * The component context class.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class UnifyComponentContext {

	final private ApplicationContext applicationContext;

	final private Logger logger;

	final private String name;

	private RequestContextManager requestContextManager;

	private ListManager listManager;

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
	 * @param command the command to send
	 * @param params  the command parameters
	 * @throws UnifyException if an error occurs
	 */
	public void sendCommand(String command, String... params) throws UnifyException {
		applicationContext.getContainer().command(command, params);
	}

	/**
	 * Gets the context's container information.
	 * 
	 * @return the container information object
	 * @throws UnifyException if an error occurs
	 */
	public UnifyContainerInfo getContainerInfo() throws UnifyException {
		return applicationContext.getContainer().getInfo();
	}

	/**
	 * Gets a component by type.
	 * 
	 * @param componentType the component type
	 * @return the component
	 * @throws UnifyException if container is not started. If component of type
	 *                        unknown. If multiple implementations of type is found.
	 *                        If component instantiation error occurs.
	 */
	public <T extends UnifyComponent> T getComponent(Class<T> componentType) throws UnifyException {
		return applicationContext.getContainer().getComponent(componentType);
	}

	/**
	 * Gets component by name.
	 * 
	 * @param name the component name
	 * @return the component with name
	 * @throws UnifyException If component is unknown. If an error occurs
	 */
	public UnifyComponent getComponent(String name) throws UnifyException {
		return applicationContext.getContainer().getComponent(name);
	}

	/**
	 * Gets a component by name using alternate settings. Applies to non-singletons
	 * only..
	 * 
	 * @param name        the component name
	 * @param altSettings the alternate settings
	 * @return the component
	 * @throws UnifyException if container is not started. If component with name is
	 *                        unknown. If component is a singleton. If component
	 *                        instantiation error occurs.
	 */
	public UnifyComponent getComponent(String name, UnifyComponentSettings altSettings) throws UnifyException {
		return applicationContext.getContainer().getComponent(name, altSettings);
	}

	/**
	 * Returns true if component with name exists in context.
	 * 
	 * @param name the component name
	 * @throws UnifyException If component an error occurs.
	 */
	public boolean isComponent(String name) throws UnifyException {
		return applicationContext.getContainer().isComponent(name);
	}

	/**
	 * Checks if lock is locked.
	 * 
	 * @param lockName the lock name
	 * @return true if locked otherwise false
	 * @throws Exception if an error occurs
	 */
	public boolean isLocked(String lockName) throws UnifyException {
		return applicationContext.getContainer().isLocked(lockName);
	}
	
	/**
	 * Grabs lock if available.
	 * 
	 * @param lockName the lock name
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	public boolean tryGrabLock(String lockName) throws UnifyException {
		return applicationContext.getContainer().tryGrabLock(lockName);
	}
	
	/**
	 * Grabs lock with no timeout.
	 * @param lockName the lock name
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	public boolean grabLock(String lockName) throws UnifyException {
		return applicationContext.getContainer().grabLock(lockName);
	}

	/**
	 * Grabs lock. Waits for lock to be available.
	 * 
	 * @param lockName the lock name
	 * @param timeout  the timeout (no timeout if negetive or zero)
	 * @return true if lock is grabbed otherwise false
	 * @throws UnifyException if an error occurs
	 */
	public boolean grabLock(String lockName, long timeout) throws UnifyException {
		return applicationContext.getContainer().grabLock(lockName, timeout);
	}

	/**
	 * Releases lock.
	 * 
	 * @param lockName the lock name
	 * @throws UnifyException if an error occurs
	 */
	public void releaseLock(String lockName) throws UnifyException {
		applicationContext.getContainer().releaseLock(lockName);
	}

	/**
	 * Gets a UPL component instance using supplied descriptor.
	 * 
	 * @param locale     the locale
	 * @param descriptor the UPL descriptor
	 * @param cached     the cached flag.
	 * @return If the cached flag is set, the container returns a cached instance
	 *         with the same descriptor and locale if found. Otherwise, a new
	 *         instance is returned.
	 * @throws UnifyException if an error occurs
	 */
	public UplComponent getUplComponent(Locale locale, String descriptor, boolean cached) throws UnifyException {
		return applicationContext.getContainer().getUplComponent(locale, descriptor, cached);
	}

	/**
	 * Gets a UPL component using UPL attributes key.
	 * 
	 * @param locale        the locale
	 * @param attributesKey the UPL attributes ID
	 * @return a UPL component
	 * @throws UnifyException if an error occurs
	 */
	public UplComponent getUplComponent(Locale locale, String attributesKey) throws UnifyException {
		return applicationContext.getContainer().getUplComponent(locale, attributesKey);
	}

	/**
	 * Gets the component configuration of component with specified name.
	 * 
	 * @param name the component name
	 * @return the component configuration otherwise null
	 * @throws UnifyException if an error occurs
	 */
	public UnifyComponentConfig getComponentConfig(String name) throws UnifyException {
		return applicationContext.getContainer().getComponentConfig(name);
	}

	/**
	 * Gets all component names of particular types in scope.
	 * 
	 * @param type the component type
	 * @return the list of names
	 * @throws UnifyException if an error occurs
	 */
	public List<String> getComponentNames(Class<? extends UnifyComponent> type) throws UnifyException {
		return applicationContext.getContainer().getComponentNames(type);
	}

	/**
	 * Gets component configuration for types the extend or implement specified
	 * types.
	 * 
	 * @param type the component type
	 * @return the component types
	 * @throws UnifyException if an error occurs
	 */
	public List<UnifyComponentConfig> getComponentConfigs(Class<? extends UnifyComponent> type) throws UnifyException {
		return applicationContext.getContainer().getComponentConfigs(type);
	}

	/**
	 * Fetches all component instances of a specific type.
	 * 
	 * @param componentType the component type
	 * @return the list of components.
	 * @throws UnifyException if an error occurs
	 */
	protected <T extends UnifyComponent> List<T> getComponents(Class<T> componentType) throws UnifyException {
		return applicationContext.getContainer().getComponents(componentType);
	}

	/**
	 * Returns classes of a particular type annotated with a specific type of
	 * annotation.
	 * 
	 * @param classType       the annotated class type
	 * @param annotationClass the annotation
	 * @param basePackages    packages to restrict search to. This parameter is
	 *                        optional.
	 * @return list of annotated classes
	 * @throws UnifyException if an error occurs
	 */
	public <T> List<Class<? extends T>> getAnnotatedClasses(Class<T> classType,
			Class<? extends Annotation> annotationClass, String... basePackages) throws UnifyException {
		return applicationContext.getContainer().getAnnotatedClasses(classType, annotationClass, basePackages);
	}

	/**
	 * Returns classes of a particular type annotated with a specific type of
	 * annotation.
	 * 
	 * @param classType       the annotated class type
	 * @param annotationClass the annotation
	 * @param excludePackages packages to exclude search from. This parameter is
	 *                        optional.
	 * @return list of annotated classes
	 * @throws UnifyException if an error occurs
	 */
	public <T> List<Class<? extends T>> getAnnotatedClassesExcluded(Class<T> classType,
			Class<? extends Annotation> annotationClass, String... excludePackages) throws UnifyException {
		return applicationContext.getContainer().getAnnotatedClassesExcluded(classType, annotationClass,
				excludePackages);
	}

	/**
	 * Gets current thread request context object.
	 * 
	 * @return the request context
	 * @throws UnifyException if an error occurs
	 */
	public RequestContext getRequestContext() throws UnifyException {
		return getRequestContextManager().getRequestContext();
	}

	/**
	 * Gets session context object.
	 * 
	 * @return the session context
	 * @throws UnifyException if an error occurs
	 */
	public SessionContext getSessionContext() throws UnifyException {
		return getRequestContextManager().getRequestContext().getSessionContext();
	}

	public boolean isWithSessionContext() throws UnifyException {
		return getRequestContextManager().getRequestContext().getSessionContext() != null;
	}
	
	/**
	 * Sets an attribute in application context.
	 * 
	 * @param name  the attribute name
	 * @param value the attribute value to set
	 * @throws UnifyException if an error occurs
	 */
	public void setApplicationAttribute(String name, Object value) throws UnifyException {
		applicationContext.setAttribute(name, value);
	}

	/**
	 * Gets an attribute value from application context.
	 * 
	 * @param name the attribute name
	 * @return the attribute value
	 * @throws UnifyException if an error occurs
	 */
	public Object getApplicationAttribute(String name) throws UnifyException {
		return applicationContext.getAttribute(name);
	}

	/**
	 * Removes an attribute value from application context.
	 * 
	 * @param name the attribute name
	 * @return the attribute value
	 * @throws UnifyException if an error occurs
	 */
	public Object removeApplicationAttribute(String name) throws UnifyException {
		return applicationContext.removeAttribute(name);
	}

	/**
	 * Removes attribute values from application context.
	 * 
	 * @param names the attribute names
	 * @throws UnifyException if an error occurs
	 */
	public void removeApplicationAttributes(String... names) throws UnifyException {
		applicationContext.removeAttributes(names);
	}

	/**
	 * Checks if application context has an attribute.
	 * 
	 * @param name the attribute name
	 * @return a true value if attribute exists in application context otherwise
	 *         false
	 * @throws UnifyException if an error occurs
	 */
	public boolean isApplicationAttribute(String name) throws UnifyException {
		return applicationContext.isAttribute(name);
	}

	/**
	 * Creates a cached formatter component.
	 * 
	 * @param formatterUpl the formatter UPL
	 * @return the formatter object
	 * @throws UnifyException if an error occurs
	 */
	public Formatter<Object> getFormatter(String formatterUpl) throws UnifyException {
		return applicationContext.getFormatter(formatterUpl);
	}
	
	/**
	 * Creates an un-cached formatter component.
	 * 
	 * @param formatterUpl the formatter UPL
	 * @return the formatter object
	 * @throws UnifyException if an error occurs
	 */
	public Formatter<Object> createFormatter(String formatterUpl) throws UnifyException {
		return applicationContext.createFormatter(formatterUpl);
	}

	/**
	 * Gets application locale
	 * 
	 * @return the application locale
	 * @throws UnifyException if an error occurs
	 */
	protected Locale getApplicationLocale() throws UnifyException {
		return applicationContext.getApplicationLocale();
	}

	/**
	 * Gets application time zone
	 * 
	 * @return the application time zone
	 * @throws UnifyException if an error occurs
	 */
	protected TimeZone getApplicationTimeZone() throws UnifyException {
		return applicationContext.getTimeZone();
	}

	/**
	 * Checks if view directive should be ignored.
	 * 
	 * @return true if ignore view directive is set for application otherwise false
	 * @throws UnifyException if an error occurs
	 */
	protected boolean isApplicationIgnoreViewDirective() throws UnifyException {
		return applicationContext.isIgnoreViewDirective();
	}

	/**
	 * Gets the application banner ASCII text
	 * 
	 * @return the application banner text as a list of strings
	 * @throws UnifyException if an error occurs
	 */
	protected List<String> getApplicationBanner() throws UnifyException {
		return applicationContext.getApplicationBanner();
	}

	/**
	 * Returns view directive for supplied privilege code and current session role.
	 * 
	 * @param privilege the privilege to test
	 * @return the privilege settings
	 * @throws UnifyException if an error occurs
	 */
	public ViewDirective getRoleViewDirective(String privilege) throws UnifyException {
		UserToken userToken = getSessionContext().getUserToken();
		if (userToken != null) {
			return applicationContext.getRoleViewDirective(userToken.getRoleCode(), privilege);
		}
		return applicationContext.getRoleViewDirective(null, privilege);
	}

	/**
	 * Returns privilege codes for supplied privilege category and role.
	 * 
	 * @param roleCode              the role code
	 * @param privilegeCategoryCode the privilege category code
	 * @return set of privilege codes
	 * @throws UnifyException if an error occurs
	 */
	public Set<String> getRolePrivilegeCodes(String roleCode, String privilegeCategoryCode) throws UnifyException {
		return applicationContext.getPrivilegeCodes(roleCode, privilegeCategoryCode);
	}

	/**
	 * Returns privilege codes for supplied privilege category and current session
	 * role.
	 * 
	 * @param privilegeCategoryCode the privilege category code
	 * @return set of privilege codes
	 * @throws UnifyException if an error occurs
	 */
	public Set<String> getCurrentRolePrivilegeCodes(String privilegeCategoryCode) throws UnifyException {
		return applicationContext.getPrivilegeCodes(getSessionContext().getUserToken().getRoleCode(),
				privilegeCategoryCode);
	}

	/**
	 * Checks if current session role has privilege.
	 * 
	 * @param privilegeCategoryCode the privilege category code
	 * @param privilegeCode         the privilege code
	 * @return a true value is current session role has privilege otherwise false
	 * @throws UnifyException if an error occurs
	 */
	public boolean isCurrentRolePrivilege(String privilegeCategoryCode, String privilegeCode) throws UnifyException {
		UserToken userToken = getSessionContext().getUserToken();
		if (userToken != null && userToken.getRoleCode() != null) {
			Set<String> privileges = getRolePrivilegeCodes(userToken.getRoleCode(), privilegeCategoryCode);
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
	 * @param roleCode the role code
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
	 * @param roleCode the role code
	 * @return a set of workflow step codes
	 */
	public Set<String> getRoleStepCodes(String roleCode) {
		return applicationContext.getStepCodes(roleCode);
	}

	/**
	 * Sets attributes for specified role.
	 * 
	 * @param roleCode       the role code
	 * @param roleAttributes the attributes to load.
	 */
	public void setRoleAttributes(String roleCode, RoleAttributes roleAttributes) {
		applicationContext.setRoleAttributes(roleCode, roleAttributes);
	}

	/**
	 * Broadcasts a cluster command to other nodes.
	 * 
	 * @param command the command to braodcast
	 * @param params  the command parameters
	 * @throws UnifyException if an error occurs
	 */
	public void broadcastToOtherNodes(String command, String... params) throws UnifyException {
		applicationContext.getContainer().broadcastToOtherNodes(command, params);
	}

	/**
	 * Broadcasts attribute to all sessions in this node.
	 * 
	 * @param name  the attribute name
	 * @param value the attribute value. A null value clears attribute.
	 * @throws UnifyException if an error occurs
	 */
	public void broadcastToSessions(String name, Object value) throws UnifyException {
		applicationContext.getContainer().broadcastToSessions(name, value);
	}

	/**
	 * Broadcasts attribute to specific application session context in this node.
	 * 
	 * @param sessionId the session ID
	 * @param name      the attribute name
	 * @param value     the attribute value. A null value clears attribute.
	 * @throws UnifyException if an error occurs
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

	public String getNodeId() {
		return applicationContext.getContainer().getNodeId();
	}

	public String getRuntimeId() throws UnifyException {
		return applicationContext.getContainer().getRuntimeId();
	}

	public short getPreferredPort() throws UnifyException {
	    return applicationContext.getContainer().getPreferredPort();
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

    public String getAuxiliaryVersion() throws UnifyException {
        return applicationContext.getContainer().getAuxiliaryVersion();
    }

	public Object getContainerSetting(String name) throws UnifyException {
		return applicationContext.getContainer().getSetting(name);
	}

	public String getWorkingPath() throws UnifyException {
		return applicationContext.getContainer().getWorkingPath();
	}
	
    public String getWorkingPathFilename(String relativeFilename) throws UnifyException {
    	return applicationContext.getContainer().getWorkingPathFilename(relativeFilename);
    }

	public boolean isProductionMode() throws UnifyException {
		return applicationContext.getContainer().isProductionMode();
	}

	public boolean isDeploymentMode() throws UnifyException {
		return applicationContext.getContainer().isDeploymentMode();
	}

	public boolean isClusterMode() {
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
