/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.core.util;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import com.tcdng.unify.common.annotation.AutoDetect;
import com.tcdng.unify.common.constants.UnifyStaticSettings;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyComponentSettings;
import com.tcdng.unify.core.UnifyContainerConfig;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Configuration;
import com.tcdng.unify.core.annotation.Singleton;
import com.tcdng.unify.core.application.ApplicationAuxiliaryVersion;
import com.tcdng.unify.core.util.xml.AliasConfig;
import com.tcdng.unify.core.util.xml.AliasesConfig;
import com.tcdng.unify.core.util.xml.ComponentConfig;
import com.tcdng.unify.core.util.xml.ComponentsConfig;
import com.tcdng.unify.core.util.xml.PropertiesConfig;
import com.tcdng.unify.core.util.xml.PropertyConfig;
import com.tcdng.unify.core.util.xml.UnifyConfig;

/**
 * Provides utility methods for reading framework configuration.
 * 
 * @author The Code Department
 * @since 4.1
 */
public final class UnifyConfigUtils {

	private static final String WORKING_PATH_PLACEHOLDER = "{{working-path}}";

	private UnifyConfigUtils() {

	}

	public static final String resolveConfigFileToEnvironment(String configFile, String environment) {
		int extIndex = configFile.lastIndexOf('.');
		return configFile.substring(0, extIndex) + "-" + environment + configFile.substring(extIndex);
	}

	/**
	 * Reads component configuration from an XML file.
	 * 
	 * @param uccb          the configuration object to read into.
	 * @param xmlFile       the XML configuration file to read
	 * @param workingFolder optional working folder
	 * @throws UnifyException if an error occurs
	 */
	public static void readConfigFromXml(UnifyContainerConfig.Builder uccb, File xmlFile, String workingFolder)
			throws UnifyException {
		UnifyConfigUtils.readXmlConfigurationObject(uccb, xmlFile, workingFolder);
	}

	/**
	 * Reads component configuration from an XML input stream.
	 * 
	 * @param uccb           the configuration object to read into.
	 * @param xmlInputStream the XML configuration input stream to read
	 * @param workingFolder  optional working folder
	 * @throws UnifyException if an error occurs
	 */
	public static void readConfigFromXml(UnifyContainerConfig.Builder uccb, InputStream xmlInputStream,
			String workingFolder) throws UnifyException {
		UnifyConfigUtils.readXmlConfigurationObject(uccb, xmlInputStream, workingFolder);
	}

	/**
	 * Reads component configuration from an XML string.
	 * 
	 * @param uccb          the configuration object to read into.
	 * @param xmlConfig     the XML configuration to read
	 * @param workingFolder optional working folder
	 * @throws UnifyException if an error occurs
	 */
	public static void readConfigFromXml(UnifyContainerConfig.Builder uccb, String xmlConfig, String workingFolder)
			throws UnifyException {
		UnifyConfigUtils.readXmlConfigurationObject(uccb, xmlConfig, workingFolder);
	}

	/**
	 * Reads configuration information from type repository.
	 * 
	 * @param uccb           the configuration object to read into.
	 * @param typeRepository the type repository
	 * @param packages       the packages to search. This parameter is optional.
	 * @throws UnifyException if an error occurs
	 */
	public static void readConfigFromTypeRepository(UnifyContainerConfig.Builder uccb, TypeRepository typeRepository,
			String... packages) throws UnifyException {
		// Auxiliary version
		String auxiliaryVersion = "0.0";
		List<Class<? extends ApplicationAuxiliaryVersion>> auxiliaryVersionList = typeRepository
				.getAnnotatedClasses(ApplicationAuxiliaryVersion.class, AutoDetect.class, packages);
		if (auxiliaryVersionList.size() > 1) {
			throw new UnifyException(UnifyCoreErrorConstants.MULTIPLE_AUXILIARY_VERSIONS_DETECTED);
		}

		if (!DataUtils.isBlank(auxiliaryVersionList)) {
			ApplicationAuxiliaryVersion version = ReflectUtils.newInstance(auxiliaryVersionList.get(0));
			auxiliaryVersion = version.getAuxiliaryVersion();
		}
		uccb.auxiliaryVersion(auxiliaryVersion);

		// Static settings
		List<Class<? extends UnifyStaticSettings>> settingsList = typeRepository
				.getAnnotatedClasses(UnifyStaticSettings.class, AutoDetect.class, packages);
		for (Class<? extends UnifyStaticSettings> type : settingsList) {
			uccb.addStaticSettings(ReflectUtils.newInstance(type));
		}

		// Components
		List<Class<? extends UnifyComponent>> list = typeRepository.getAnnotatedClasses(UnifyComponent.class,
				Component.class, packages);
		for (Class<? extends UnifyComponent> type : list) {
			String componentName = UnifyConfigUtils.getComponentName(type);
			String description = UnifyConfigUtils.getDescription(type);
			boolean singleton = true;
			Singleton sa = type.getAnnotation(Singleton.class);
			if (sa != null) {
				singleton = sa.value();
			}

			uccb.addComponentConfig(componentName, description, type, singleton,
					UnifyConfigUtils.readComponentSettings(type));
		}
	}

	/**
	 * Reads a component's configurable properties (settings).
	 * 
	 * @param type the component type
	 * @return the component settings
	 * @throws UnifyException if an error occurs
	 */
	public static UnifyComponentSettings readComponentSettings(Class<? extends UnifyComponent> type)
			throws UnifyException {
		UnifyComponentSettings.Builder ub = new UnifyComponentSettings.Builder();
		Set<String> names = new HashSet<String>();
		Field[] fields = ReflectUtils.getAnnotatedFields(type, Configurable.class);
		for (Field field : fields) {
			Configurable ca = field.getAnnotation(Configurable.class);
			names.add(field.getName());
			ub.setProperty(field.getName(), UnifyConfigUtils.getConfigurableValue(ca), ca.resolve(), ca.hidden());
		}

		// Set configurable fields overriding same fields if present
		for (Class<?> claz : ReflectUtils.getClassHierachyList(type)) {
			Configuration cas = claz.getAnnotation(Configuration.class);
			if (cas != null) {
				for (Configurable ca : cas.value()) {
					String property = AnnotationUtils.getAnnotationString(ca.property());
					if (!names.contains(property)) {
						throw new UnifyException(UnifyCoreErrorConstants.PROPERTY_IS_NOT_CONFIGURABLE, property, claz);
					}

					ub.setProperty(property, UnifyConfigUtils.getConfigurableValue(ca), ca.resolve(), ca.hidden());
				}
			}
		}

		return ub.build();
	}

	/**
	 * Resolves configuration overrides.
	 * 
	 * @param map                the target type map
	 * @param overrideSuffixList the override suffix list
	 */
	public static <T> Map<String, String> resolveConfigurationOverrides(Map<String, T> map,
			List<String> overrideSuffixList) {
		Map<String, String> resolutionMap = new HashMap<String, String>();
		if (overrideSuffixList != null && !overrideSuffixList.isEmpty()) {
			for (int i = overrideSuffixList.size() - 1; i >= 0; i--) {
				String suffix = overrideSuffixList.get(i);
				for (String typeName : map.keySet()) {
					T result = map.get(typeName);
					int index = typeName.lastIndexOf('_');
					if (index > 0 && typeName.lastIndexOf(suffix) == (index + 1)) {
						map.remove(typeName);
						if (result != null) {
							String name = typeName.substring(0, index);
							resolutionMap.put(typeName, name);
							map.put(name, result);
						}
					}
				}
			}
		}
		return resolutionMap;
	}

	public static String getComponentName(Class<? extends UnifyComponent> clazz) throws UnifyException {
		String name = null;
		Component ca = clazz.getAnnotation(Component.class);
		if (ca != null) {
			name = AnnotationUtils.getAnnotationString(ca.value());
			if (StringUtils.isBlank(name)) {
				name = AnnotationUtils.getAnnotationString(ca.name());
			} else if (StringUtils.isNotBlank(AnnotationUtils.getAnnotationString(ca.name()))) {
				throw new UnifyException(UnifyCoreErrorConstants.ANNOTATION_BAD_ATTRIBUTE_COMBINATION, "value", "name",
						Component.class, clazz);
			}

			if (StringUtils.isBlank(name)) {
				throw new UnifyException(UnifyCoreErrorConstants.ANNOTATION_MUST_SPECIFY_ATTRIBUTE_OF_TWO, "value",
						"name", Component.class, clazz);
			}
		}
		return name;
	}

	public static String getDescription(Class<? extends UnifyComponent> clazz) throws UnifyException {
		Component ca = clazz.getAnnotation(Component.class);
		if (ca != null) {
			return AnnotationUtils.getAnnotationString(ca.description());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static void readXmlConfigurationObject(UnifyContainerConfig.Builder uccb, Object xmlConfigObject,
			String workingFolder) throws UnifyException {
		try {
			UnifyConfig unifyConfig = XmlConfigUtils.readXmlConfig(UnifyConfig.class, xmlConfigObject);
			if (unifyConfig != null) {
				uccb.deploymentVersion(unifyConfig.getVersion());
//                uccb.nodeId(unifyConfig.getNodeId());
				uccb.nodeId(UUID.randomUUID().toString());
				uccb.productionMode(unifyConfig.isProduction());
				uccb.clusterMode(unifyConfig.isCluster());

				PropertiesConfig propertiesConfig = unifyConfig.getPropertiesConfig();
				if (propertiesConfig != null && propertiesConfig.getPropertyConfigList() != null) {
					for (PropertyConfig propertyConfig : propertiesConfig.getPropertyConfigList()) {
						String val = propertyConfig.getValue();
						if (val != null) {
							uccb.setProperty(propertyConfig.getName(),
									UnifyConfigUtils.replacePlaceHolderValues(val, workingFolder));
						} else if (propertyConfig.getValueList() != null) {
							String[] vals = propertyConfig.getValueList()
									.toArray(new String[propertyConfig.getValueList().size()]);
							for (int i = 0; i < vals.length; i++) {
								vals[i] = UnifyConfigUtils.replacePlaceHolderValues(vals[i], workingFolder);
							}

							uccb.setProperty(propertyConfig.getName(), vals);
						}
					}
				}

				Properties appProperties = UnifyConfigUtils.getApplicationProperties(uccb,
						UnifyCorePropertyConstants.APPLICATION_PROPERTIES, workingFolder);
				final Set<String> propertyNames = appProperties.stringPropertyNames();
				for (String property : propertyNames) {
					String val = UnifyConfigUtils.replacePlaceHolderValues(appProperties.getProperty(property),
							workingFolder);
					uccb.setProperty(property, val);
				}

				ComponentsConfig componentsConfig = unifyConfig.getComponentsConfig();
				if (componentsConfig != null && componentsConfig.getComponentConfigList() != null) {
					AliasesConfig aliasesConfig = unifyConfig.getComponentsConfig().getAliasesConfig();
					if (aliasesConfig != null && aliasesConfig.getAliasConfigList() != null) {
						for (AliasConfig aliasConfig : aliasesConfig.getAliasConfigList()) {
							uccb.setAlias(aliasConfig.getName(), aliasConfig.getActualName());
						}
					}

					for (ComponentConfig componentConfig : componentsConfig.getComponentConfigList()) {
						Class<? extends UnifyComponent> type = (Class<? extends UnifyComponent>) ReflectUtils
								.classForName(componentConfig.getClassName());
						String componentName = componentConfig.getName();
						String description = componentConfig.getDescription();
						if (StringUtils.isBlank(componentName)) {
							componentName = UnifyConfigUtils.getComponentName(type);
							description = UnifyConfigUtils.getDescription(type);

							if (StringUtils.isBlank(componentName)) {
								throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_HAS_NO_NAME, type);
							}
						}

						boolean singleton = true; // Default
						Boolean configSingleton = componentConfig.getSingleton();
						if (configSingleton != null) {
							singleton = configSingleton.booleanValue();
						} else {
							Singleton sa = type.getAnnotation(Singleton.class);
							if (sa != null) {
								singleton = sa.value();
							}
						}

						UnifyComponentSettings annotationSettings = UnifyConfigUtils.readComponentSettings(type);
						UnifyComponentSettings.Builder ub = new UnifyComponentSettings.Builder(annotationSettings);
						propertiesConfig = componentConfig.getPropertiesConfig();
						if (propertiesConfig != null && propertiesConfig.getPropertyConfigList() != null) {
							for (PropertyConfig propertyConfig : propertiesConfig.getPropertyConfigList()) {
								String property = propertyConfig.getName();
								String val = propertyConfig.getValue();
								if (val != null) {
									val = UnifyConfigUtils.replacePlaceHolderValues(val, workingFolder);
									ub.setProperty(property, val, false,
											Boolean.TRUE.equals(propertyConfig.getHidden()));
								} else if (propertyConfig.getValueList() != null) {
									String[] vals = propertyConfig.getValueList()
											.toArray(new String[propertyConfig.getValueList().size()]);
									for (int i = 0; i < vals.length; i++) {
										vals[i] = UnifyConfigUtils.replacePlaceHolderValues(vals[i], workingFolder);
									}

									ub.setProperty(property, vals, false,
											Boolean.TRUE.equals(propertyConfig.getHidden()));
								}
							}
						}

						if (!propertyNames.isEmpty()) {
							final String propPrefix = componentName + '.';
							for (String property : propertyNames) {
								if (property.startsWith(propPrefix)) {
									String val = UnifyConfigUtils.replacePlaceHolderValues(
											appProperties.getProperty(property), workingFolder);
									ub.setProperty(property.substring(propPrefix.length()), val);
								}
							}
						}

						uccb.addComponentConfig(componentName, description, type, singleton, true, ub.build());

					}
				}
			}
		} catch (Exception ex) {
			throw new UnifyException(ex, UnifyCoreErrorConstants.CONFIGURATION_READ_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	private static Properties getApplicationProperties(final UnifyContainerConfig.Builder uccb,
			final String propertyName, final String workingFolder) throws UnifyException {
		Properties properties = new Properties();
		List<String> applicationPropFiles = DataUtils.convert(List.class, String.class, uccb.getProperty(propertyName));
		if (!DataUtils.isBlank(applicationPropFiles)) {
			properties = IOUtils.readPropertiesFromFileResources(applicationPropFiles, workingFolder);
		}
		return properties;
	}

	private static Object getConfigurableValue(Configurable ca) {
		Object configuredValue = AnnotationUtils.getAnnotationString(ca.value());
		if (configuredValue == null && (ca.values().length > 0)) {
			return ca.values();
		}
		return configuredValue;
	}

	private static String replacePlaceHolderValues(String val, String workingFolder) {
		if (val != null) {
			int i = val.indexOf(WORKING_PATH_PLACEHOLDER);
			if (i >= 0) {
				return val.substring(0, i) + workingFolder + val.substring(i + WORKING_PATH_PLACEHOLDER.length());
			}
		}

		return val;
	}
}
