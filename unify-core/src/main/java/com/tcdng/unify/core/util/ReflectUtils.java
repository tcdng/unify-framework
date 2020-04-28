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
package com.tcdng.unify.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.WrappedData;

/**
 * Provides utility methods for reflection.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class ReflectUtils {

    // TODO Clear classes for dynamic class loaders to prevent leakage.

    private static final FactoryMap<Class<?>, Map<String, Field>> declaredFieldMap;

    private static final FactoryMap<Class<?>, Map<String, GetterSetterInfo>> caseSensitiveGetterSetterMap;

    private static final FactoryMap<Class<?>, List<GetterSetterInfo>> caseSensitiveGetterSetterList;

    private static final FactoryMap<Class<?>, List<String>> beanFieldNamesMap;

    private static final FactoryMap<Class<?>, List<String>> beanNestedFieldNamesMap;

    private static final FactoryMap<Class<?>, ConstructorInfo> largeConstructorMap;

    private static final Map<String, Class<?>> primitiveToClassMap = new HashMap<String, Class<?>>();

    static {
        primitiveToClassMap.put("boolean", boolean.class);
        primitiveToClassMap.put("short", short.class);
        primitiveToClassMap.put("int", int.class);
        primitiveToClassMap.put("long", long.class);
        primitiveToClassMap.put("float", float.class);
        primitiveToClassMap.put("double", double.class);
        primitiveToClassMap.put("byte", byte.class);
        primitiveToClassMap.put("char", char.class);
    }

    static {
        declaredFieldMap = new FactoryMap<Class<?>, Map<String, Field>>() {
            @Override
            protected Map<String, Field> create(Class<?> clazz, Object... params) throws Exception {
                Map<String, Field> map = new LinkedHashMap<String, Field>();
                do {
                    for (Field field : clazz.getDeclaredFields()) {
                        if (!map.containsKey(field.getName())) {
                            field.setAccessible(true);
                            map.put(field.getName(), field);
                        }
                    }
                } while ((clazz = clazz.getSuperclass()) != null);
                return Collections.unmodifiableMap(map);
            }
        };

        caseSensitiveGetterSetterList = new FactoryMap<Class<?>, List<GetterSetterInfo>>() {

            @Override
            protected List<GetterSetterInfo> create(Class<?> beanClass, Object... params) throws Exception {
                Map<String, GetterSetterInfo> map = caseSensitiveGetterSetterMap.get(beanClass);
                List<GetterSetterInfo> list = new ArrayList<GetterSetterInfo>();
                list.addAll(map.values());
                Collections.sort(list, new Comparator<GetterSetterInfo>() {

                    @Override
                    public int compare(GetterSetterInfo gsi0, GetterSetterInfo gsi1) {
                        String name0 = gsi0.getName();
                        String name1 = gsi1.getName();
                        if (name0 == null) {
                            if (name1 != null) {
                                return -1;
                            }
                        } else {
                            if (name1 == null) {
                                return 1;
                            }
                        }

                        return name0.compareTo(name1);
                    }
                });
                return Collections.unmodifiableList(list);
            }

        };

        caseSensitiveGetterSetterMap = new FactoryMap<Class<?>, Map<String, GetterSetterInfo>>() {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            protected Map<String, GetterSetterInfo> create(Class<?> beanClass, Object... params) throws Exception {
                Map<String, GetterSetterInfo> map = new LinkedHashMap<String, GetterSetterInfo>();
                Method[] methods = beanClass.getMethods();
                Set<String> fieldNames = declaredFieldMap.get(beanClass).keySet();
                for (Method method : methods) {
                    if (method.isBridge() || method.isSynthetic()) {
                        continue;
                    }

                    String name = method.getName();
                    if (!void.class.equals(method.getReturnType()) && method.getParameterTypes().length == 0) {
                        boolean isIs = false;
                        if ((name.length() > 3 && (name.startsWith("get"))
                                || (isIs = name.length() > 2 && name.startsWith("is")))) {
                            int index = 3;
                            if (isIs) {
                                index = 2;
                            }

                            if (Character.isUpperCase(name.charAt(index))) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(Character.toLowerCase(name.charAt(index)));
                                sb.append(name.substring(index + 1));
                                String fieldName = sb.toString();
                                GetterSetterInfo gsInfo = map.get(fieldName);
                                Class<?> argumentType = ReflectUtils.getArgumentType(method.getGenericReturnType(), 0);
                                boolean isField = fieldNames.contains(fieldName);
                                if (gsInfo == null) {
                                    if ("getData".equals(name) && WrappedData.class.isAssignableFrom(beanClass)) {
                                        WrappedData<?> wrappedBean =
                                                ReflectUtils.newInstance((Class<? extends WrappedData>) beanClass);
                                        map.put(fieldName, new GetterSetterInfo(fieldName, method, null,
                                                wrappedBean.getDataType(), null, false));
                                    } else {
                                        map.put(fieldName, new GetterSetterInfo(fieldName, method, null,
                                                method.getReturnType(), argumentType, isField));
                                    }
                                } else {
                                    if (!gsInfo.getType().equals(method.getReturnType())
                                            || ((gsInfo.getArgumentType() != null)
                                                    && !gsInfo.getArgumentType().equals(argumentType))) {
                                        throw new UnifyException(
                                                UnifyCoreErrorConstants.REFLECTUTIL_INCOMPATIBLE_GETTER_SETTER,
                                                fieldName, beanClass);
                                    }
                                    map.put(fieldName, new GetterSetterInfo(fieldName, method, gsInfo.getSetter(),
                                            method.getReturnType(), argumentType, isField));
                                }
                            }
                        }
                    } else if (void.class.equals(method.getReturnType()) && method.getParameterTypes().length == 1) {
                        if (name.length() > 3 && Character.isUpperCase(name.charAt(3)) && name.startsWith("set")) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(Character.toLowerCase(name.charAt(3)));
                            sb.append(name.substring(4));
                            String fieldName = sb.toString();
                            boolean isField = fieldNames.contains(fieldName);
                            GetterSetterInfo gsInfo = map.get(fieldName);
                            Class<?> argumentType =
                                    ReflectUtils.getArgumentType(method.getGenericParameterTypes()[0], 0);
                            if (gsInfo == null) {
                                map.put(fieldName, new GetterSetterInfo(fieldName, null, method,
                                        method.getParameterTypes()[0], argumentType, isField));
                            } else {
                                if (!gsInfo.getType().equals(method.getParameterTypes()[0])
                                        || ((gsInfo.getArgumentType() != null)
                                                && !gsInfo.getArgumentType().equals(argumentType))) {
                                    throw new UnifyException(
                                            UnifyCoreErrorConstants.REFLECTUTIL_INCOMPATIBLE_GETTER_SETTER, fieldName,
                                            beanClass);
                                }
                                map.put(fieldName, new GetterSetterInfo(fieldName, gsInfo.getGetter(), method,
                                        method.getParameterTypes()[0], argumentType, isField));
                            }
                        }
                    }
                }

                return Collections.unmodifiableMap(map);
            }
        };

        beanFieldNamesMap = new FactoryMap<Class<?>, List<String>>() {

            @Override
            protected List<String> create(Class<?> beanClass, Object... params) throws Exception {
                List<String> names = new ArrayList<String>();
                for (Field field : declaredFieldMap.get(beanClass).values()) {
                    String name = field.getName();
                    GetterSetterInfo gsi = caseSensitiveGetterSetterMap.get(beanClass).get(name);
                    if (gsi != null && gsi.isGetter() && gsi.isSetter()) {
                        names.add(name);
                    }
                }

                Collections.sort(names);
                return Collections.unmodifiableList(names);
            }

        };

        beanNestedFieldNamesMap = new FactoryMap<Class<?>, List<String>>() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            protected List<String> create(Class<?> beanClass, Object... params) throws Exception {
                List<String> names = new ArrayList<String>();
                for (String name : beanFieldNamesMap.get(beanClass)) {
                    GetterSetterInfo gsi = caseSensitiveGetterSetterMap.get(beanClass).get(name);
                    for (String childName : beanNestedFieldNamesMap.get(gsi.getType())) {
                        names.add(name + '.' + childName);
                    }

                    names.add(name);
                }

                if (WrappedData.class.isAssignableFrom(beanClass)) {
                    WrappedData<?> wrappedBean = ReflectUtils.newInstance((Class<? extends WrappedData>) beanClass);
                    Class<?> argumentType = wrappedBean.getDataType();
                    for (String childName : beanNestedFieldNamesMap.get(argumentType)) {
                        names.add("data." + childName);
                    }
                }

                Collections.sort(names);
                return Collections.unmodifiableList(names);
            }

        };

        largeConstructorMap = new FactoryMap<Class<?>, ConstructorInfo>() {

            @Override
            protected ConstructorInfo create(Class<?> clazz, Object... params) throws Exception {
                Constructor<?> largeConstructor = null;
                Class<?>[] largeParamTypes = null;
                for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    if (largeParamTypes == null || largeParamTypes.length < paramTypes.length) {
                        largeConstructor = constructor;
                        largeParamTypes = paramTypes;
                    }
                }

                return new ConstructorInfo(largeConstructor,
                        Collections.unmodifiableList(Arrays.asList(largeParamTypes)));
            }

        };
    }

    private ReflectUtils() {

    }

    /**
     * Gets the value of a bean property.
     * 
     * @param bean
     *            the bean object
     * @param propertyName
     *            the bean property
     * @return the value of the bean property
     * @throws UnifyException
     *             if an error occurs
     */
    public static Object getBeanProperty(Object bean, String propertyName) throws UnifyException {
        try {
            return caseSensitiveGetterSetterMap.get(bean.getClass()).get(propertyName).getGetter().invoke(bean);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                    propertyName);
        }
    }

    /**
     * Gets the value of a nested bean property. Does a deep get by iterating
     * through bean property values with bean for new iteration equal to value from
     * previous iteration. For instance if we have a bean that encapsulates another
     * 
     * <pre>
     *     <code>
     *     class Address {
     *       String line1;
     *      
     *       String line2;
     *       ...
     *       (setter and getters)
     *     }
     *     
     *     class Customer {
     *       String name;
     *       
     *       String telephone;
     *       
     *       Address address;
     *       ...
     *       (setter and getters)
     *     }
     *     </code>
     * </pre>
     * 
     * and we want to get line1 of customer address. We use the long name
     * <em>"address.line1"</em> like
     * 
     * <pre>
     *     <code>
     *       Customer customer = ...
     *       String customerAddressLine1 = Reflection.getBeanLongProperty(customer, "address.line1");
     *     </code>
     * </pre>
     * 
     * @param bean
     *            the bean object
     * @param longPropertyName
     *            the nested bean property long name
     * @return the value of the bean property
     * @throws UnifyException
     *             if an error occurs
     */
    public static Object getNestedBeanProperty(Object bean, String longPropertyName) throws UnifyException {
        try {
            String[] properties = longPropertyName.split("\\.");
            for (int i = 0; i < properties.length && bean != null; i++) {
                bean = caseSensitiveGetterSetterMap.get(bean.getClass()).get(properties[i]).getGetter().invoke(bean);
            }
            return bean;
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                    longPropertyName);
        }
    }

    /**
     * Finds the value of a nested bean property. Operates the same way as
     * {@link #getNestedBeanProperty(Object, String)} except returns a null, instead
     * of throwing an exception, when bean does not have nested property with
     * supplied long name
     * 
     * @param bean
     * @param longPropertyName
     * @return
     * @throws UnifyException
     */
    public static Object findNestedBeanProperty(Object bean, String longPropertyName) throws UnifyException {
        try {
            String[] properties = longPropertyName.split("\\.");
            for (int i = 0; i < properties.length && bean != null; i++) {
                GetterSetterInfo getterSetterInfo =
                        caseSensitiveGetterSetterMap.get(bean.getClass()).get(properties[i]);
                if (getterSetterInfo == null || !getterSetterInfo.isGetter()) {
                    return null;
                }
                bean = getterSetterInfo.getGetter().invoke(bean);
            }
            return bean;
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                    longPropertyName);
        }
    }

    /**
     * Sets the value of the property of a bean.
     * 
     * @param bean
     *            the bean object
     * @param propertyName
     *            the property to set
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if an error occurs
     */
    public static void setBeanProperty(Object bean, String propertyName, Object value) throws UnifyException {
        try {
            GetterSetterInfo setterInfo = ReflectUtils.getSetterInfo(bean.getClass(), propertyName);
            setterInfo.getSetter().invoke(bean, value);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                    propertyName);
        }
    }

    /**
     * Sets nested bean property by long name. Does a deep get until the last bean
     * then sets property which is the last name in supplied long name. For instance
     * if we have a bean that encapsulates another
     * 
     * <pre>
     *     <code>
     *     class Address {
     *       String line1;
     *      
     *       String line2;
     *       ...
     *       (setter and getters)
     *     }
     *     
     *     class Customer {
     *       String name;
     *       
     *       String telephone;
     *       
     *       Address address;
     *       ...
     *       (setter and getters)
     *     }
     *     </code>
     * </pre>
     * 
     * and we want to set line1 of customer address to <em>"24 Parklane"</em>. We
     * use the long name <em>"address.line1"</em> like
     * 
     * <pre>
     *     <code>
     *       Customer customer = ...
     *       Reflection.setLongNameBeanProperty(customer, "address.line1", "24 Parklane");
     *     </code>
     * </pre>
     * 
     * @param bean
     *            the bean object
     * @param longPropertyName
     *            the nested property long name
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if an error occurs
     */
    public static void setNestedBeanProperty(Object bean, String longPropertyName, Object value) throws UnifyException {
        try {
            String[] properties = longPropertyName.split("\\.");
            int len = properties.length - 1;
            int j = 0;
            for (; j < len && bean != null; j++) {
                bean = ReflectUtils.getGetterInfo(bean.getClass(), properties[j]).getGetter().invoke(bean);
            }

            if (bean != null) {
                ReflectUtils.setBeanProperty(bean, properties[j], value);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                    longPropertyName);
        }
    }

    /**
     * Returns the getter information for a bean property.
     * 
     * @param beanClass
     *            the bean class
     * @param propertyName
     *            the property
     * @return the method
     * @throws UnifyException
     *             if an error occurs
     */
    public static GetterSetterInfo getGetterInfo(Class<?> beanClass, String propertyName) throws UnifyException {
        GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClass).get(propertyName);
        if (getterSetterInfo == null || !getterSetterInfo.isGetter()) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_NO_GETTER, beanClass, propertyName);
        }
        return getterSetterInfo;
    }

    /**
     * Returns the setter information for a bean property.
     * 
     * @param beanClass
     *            the bean class
     * @param propertyName
     *            the property
     * @return the method
     * @throws UnifyException
     *             if an error occurs
     */
    public static GetterSetterInfo getSetterInfo(Class<?> beanClass, String propertyName) throws UnifyException {
        GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClass).get(propertyName);
        if (getterSetterInfo == null || !getterSetterInfo.isSetter()) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_NO_SETTER, beanClass, propertyName);
        }
        return getterSetterInfo;
    }

    public static GetterSetterInfo getGetterSetterInfo(Class<?> beanClass, String propertyName) throws UnifyException {
        GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClass).get(propertyName);
        if (getterSetterInfo == null || !getterSetterInfo.isGetter()) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_NO_GETTER, beanClass, propertyName);
        }

        if (!getterSetterInfo.isSetter()) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_NO_SETTER, beanClass, propertyName);
        }
        return getterSetterInfo;
    }

    /**
     * Returns all getter/setter information for a bean class.
     * 
     * @param beanClass
     *            the bean class
     * @return a list of getter/setter method info
     * @throws UnifyException
     *             if an error occurs
     */
    public static List<GetterSetterInfo> getGetterSetterList(Class<?> beanClass) throws UnifyException {
        return caseSensitiveGetterSetterList.get(beanClass);
    }

    /**
     * Returns all getter/setter information for a bean class.
     * 
     * @param beanClass
     *            the bean class
     * @return a map of getter/setter method info by property name
     * @throws UnifyException
     *             if an error occurs
     */
    public static Map<String, GetterSetterInfo> getGetterSetterMap(Class<?> beanClass) throws UnifyException {
        return caseSensitiveGetterSetterMap.get(beanClass);
    }

    /**
     * Returns a list of all the bean compliant field names of a supplied type.
     * 
     * @param beanClass
     *            the type
     * @throws UnifyException
     *             if an error occurs
     */
    public static List<String> getBeanCompliantFieldNames(String beanClass) throws UnifyException {
        return beanFieldNamesMap.get(ReflectUtils.getClassForName(beanClass));
    }

    /**
     * Returns a list of all the bean compliant field names of a supplied type.
     * 
     * @param beanClass
     *            the bean type
     * @throws UnifyException
     *             if an error occurs
     */
    public static List<String> getBeanCompliantFieldNames(Class<?> beanClass) throws UnifyException {
        return beanFieldNamesMap.get(beanClass);
    }

    /**
     * Returns a list of all the bean compliant nested field names of a supplied
     * type.
     * 
     * @param beanClass
     *            the type
     * @throws UnifyException
     *             if an error occurs
     */
    public static List<String> getBeanCompliantNestedFieldNames(String beanClass) throws UnifyException {
        return beanNestedFieldNamesMap.get(ReflectUtils.getClassForName(beanClass));
    }

    /**
     * Returns a list of all the bean compliant nested field names of a supplied
     * type.
     * 
     * @param beanClass
     *            the bean type
     * @throws UnifyException
     *             if an error occurs
     */
    public static List<String> getBeanCompliantNestedFieldNames(Class<?> beanClass) throws UnifyException {
        return beanNestedFieldNamesMap.get(beanClass);
    }

    /**
     * Asserts a supplied type is public, concrete and non-final.
     * 
     * @param clazz
     *            the class to assert
     * @throws UnifyException
     *             if supplied type is not public nor concrete or is final
     */
    public static void assertPublicConcreteNonFinal(Class<?> clazz) throws UnifyException {
        int modifiers = clazz.getModifiers();
        if ((Modifier.PUBLIC & modifiers) == 0
                || ((Modifier.FINAL | Modifier.ABSTRACT | Modifier.INTERFACE) & modifiers) != 0) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_CLASS_IS_NOT_PUBLIC_CONCRETE_NONFINAL, clazz);
        }
    }

    public static void assertBeanComplaintField(Class<?> beanClass, String fieldName) throws UnifyException {
        GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClass).get(fieldName);
        if (getterSetterInfo == null || !getterSetterInfo.isGetter() || !getterSetterInfo.isSetter()) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_CLASS_FIELD_NOT_BEAN_COMPLIANT, fieldName,
                    beanClass);
        }
    }

    /**
     * Assert a non-static and non-final field.
     * 
     * @param field
     *            the field to test
     * @throws UnifyException
     *             if field is static or final
     */
    public static void assertNonStaticNonFinal(Field field) throws UnifyException {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_FIELD_WITH_UNSUPORTED_MODIFIERS, field);
        }
    }

    /**
     * Asserts that a method can be overridden.
     * 
     * @param method
     *            the method to test
     * @throws UnifyException
     *             if method can not be overriden
     */
    public static void assertOverridable(Method method) throws UnifyException {
        int modifiers = method.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isFinal(modifiers)) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_METHOD_WITH_UNSUPORTED_MODIFIERS, method);
        }
    }

    /**
     * Asserts that class has specific runtime annotation.
     * 
     * @param clazz
     *            the type
     * @param annotClazz
     *            the annotation type
     * @throws UnifyException
     *             if class has no such annotation
     */
    public static void assertAnnotation(Class<?> clazz, Class<? extends Annotation> annotClazz) throws UnifyException {
        if (!clazz.isAnnotationPresent(annotClazz)) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_ANNOTATION_REQUIRED, annotClazz, clazz);
        }
    }

    /**
     * Asserts the a type has a particular interface.
     * 
     * @param clazz
     *            the type
     * @param intefaceClazz
     *            the interface type
     * @throws UnifyException
     *             if type does not have the specific interface
     */
    public static void assertInterface(Class<?> clazz, Class<?> intefaceClazz) throws UnifyException {
        if (!isInterface(clazz, intefaceClazz)) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_INTERFACES_REQUIRED, intefaceClazz, clazz);
        }
    }

    /**
     * Tests if a type has a specific interface.
     * 
     * @param clazz
     *            the type
     * @param intefaceClazz
     *            the interface
     */
    public static boolean isInterface(Class<?> clazz, Class<?> intefaceClazz) {
        while (clazz != null) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfaceClaz : interfaces) {
                if (intefaceClazz.isAssignableFrom(interfaceClaz)) {
                    return true;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    /**
     * Returns method by name and signature for supplied type.
     * 
     * @param clazz
     *            the type
     * @param name
     *            the method name
     * @param parameterTypes
     *            the method parameter types
     * @throws UnifyException
     *             if an error occurs
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws UnifyException {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_CLASS_UNKNOWN_UNACCESSIBLE_METHOD, clazz, name);
        }
    }

    /**
     * Computes the hash code of an object.
     * 
     * @param object
     *            the object
     * @return the computed hash code
     */
    @SuppressWarnings("unchecked")
    public static int beanHashCode(Object object) {
        return ReflectUtils.beanHashCode(object, (Set<String>) Collections.EMPTY_SET);
    }

    /**
     * Computes the hash code of an object.
     * 
     * @param object
     *            the object
     * @param ignore
     *            Fields to ignore otherwise all fields are considered
     * @return the computed hash code
     */
    public static int beanHashCode(Object object, Set<String> ignore) {
        try {
            final int prime = 31;
            int result = 1;
            if (ignore.isEmpty()) {
                for (GetterSetterInfo getterSetterInfo : caseSensitiveGetterSetterMap.get(object.getClass()).values()) {
                    if (getterSetterInfo.isGetter()) {
                        Object value = getterSetterInfo.getGetter().invoke(object);
                        result = prime * result + ((value == null) ? 0 : value.hashCode());
                    }
                }
            } else {
                Map<String, GetterSetterInfo> map = caseSensitiveGetterSetterMap.get(object.getClass());
                for (Map.Entry<String, GetterSetterInfo> entry : map.entrySet()) {
                    if (!ignore.contains(entry.getKey())) {
                        GetterSetterInfo getterSetterInfo = entry.getValue();
                        if (getterSetterInfo != null && getterSetterInfo.isGetter()) {
                            Object value = getterSetterInfo.getGetter().invoke(object);
                            result = prime * result + ((value == null) ? 0 : value.hashCode());
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Compares two objects.
     * 
     * @param a
     *            object to compare
     * @param b
     *            object to compare
     * @return true value if a equals b
     */
    public static boolean objectEquals(Object a, Object b) {
        if (a == b) {
            return true;
        }

        if (b == null) {
            return false;
        }

        if (a.getClass() != b.getClass()) {
            return false;
        }

        return a.equals(b);
    }

    /**
     * Compares two bean objects.
     * 
     * @param a
     *            object to compare
     * @param b
     *            object to compare
     * @param ignore
     *            Fields to ignore otherwise all fields are considered
     * @return true value if a equals b
     */
    public static boolean beanEquals(Object a, Object b, String... ignore) {
        if (a == b) {
            return true;
        }

        if (b == null) {
            return false;
        }

        if (a.getClass() != b.getClass()) {
            return false;
        }

        Set<String> ignoreSet = new HashSet<String>();
        Collections.addAll(ignoreSet, ignore);
        return ReflectUtils.innerBeanEquals(a, b, ignoreSet);
    }

    /**
     * Compares two bean objects.
     * 
     * @param a
     *            object to compare
     * @param b
     *            object to compare
     * @param ignore
     *            Fields to ignore otherwise all fields are considered
     * @return true value if a equals b
     */
    public static boolean beanEquals(Object a, Object b, Set<String> ignore) {
        if (a == b) {
            return true;
        }

        if (b == null) {
            return false;
        }

        if (a.getClass() != b.getClass()) {
            return false;
        }

        return ReflectUtils.innerBeanEquals(a, b, ignore);
    }

    /**
     * Returns a new shallow copy of supplied bean.
     * 
     * @param source
     *            the supplied object
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T shallowBeanCopy(T source) throws UnifyException {
        try {
            T result = (T) source.getClass().newInstance();
            ReflectUtils.shallowBeanCopy(result, source);
            return result;
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            Class<?> clazz = null;
            if (source != null) {
                clazz = source.getClass();
            }
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INSTANTIATION_ERROR, clazz);
        }
    }

    /**
     * Does a shallow copy of the values of source bean into destination bean.
     * 
     * @param destination
     *            the destination object
     * @param source
     *            the source object
     * @return true if source is copied into destination
     * @throws UnifyException
     *             if an error occurs
     */
    public static boolean shallowBeanCopy(Object destination, Object source) throws UnifyException {
        try {
            if (destination == source) {
                return true;
            }

            if (destination == null) {
                return false;
            }

            if (destination.getClass() != source.getClass()) {
                return false;
            }

            for (String fieldName : ReflectUtils.getBeanCompliantNestedFieldNames(destination.getClass())) {
                ReflectUtils.setNestedBeanProperty(destination, fieldName,
                        ReflectUtils.getNestedBeanProperty(source, fieldName));
            }

            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Does a shallow copy of the values of specific fields of source object into
     * destination object.
     * 
     * @param destination
     *            the destination object
     * @param source
     *            the source object
     * @param fields
     *            the fields to copy
     * @return true if source is copied into destination
     * @throws UnifyException
     *             if an error occurs
     */
    public static boolean shallowBeanCopy(Object destination, Object source, String... fields) throws UnifyException {
        try {
            if (destination == source) {
                return true;
            }

            if (destination == null) {
                return false;
            }

            if (destination.getClass() != source.getClass()) {
                return false;
            }

            for (String fieldName : fields) {
                ReflectUtils.setNestedBeanProperty(destination, fieldName,
                        ReflectUtils.getNestedBeanProperty(source, fieldName));
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Gets all declared fields annotated with an annotation type in a specified
     * class.
     * 
     * @param clazz
     *            the class
     * @param annotClazz
     *            The annotation type
     * @return the declared annotated fields
     * @throws UnifyException
     *             if an error occurs
     */
    public static Field[] getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotClazz)
            throws UnifyException {
        List<Field> fieldList = new ArrayList<Field>();
        for (Field field : declaredFieldMap.get(clazz).values()) {
            if (field.isAnnotationPresent(annotClazz)) {
                fieldList.add(field);
            }
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }

    /**
     * Returns a declared field with name and annotated with an annotation type in a
     * specified class.
     * 
     * @param name
     *            the field name
     * @param clazz
     *            the type
     * @param annotClazz
     *            the annotation type
     * @throws UnifyException
     *             if an error occurs
     */
    public static Field getAnnotatedField(Class<?> clazz, String name, Class<? extends Annotation> annotClazz)
            throws UnifyException {
        Field field = declaredFieldMap.get(clazz).get(name);
        if (field != null && field.isAnnotationPresent(annotClazz)) {
            return field;
        }
        throw new UnifyException(UnifyCoreErrorConstants.REFLECT_ANNOTATED_FIELD_NOT_FOUND, name, clazz);
    }

    /**
     * Returns a declared class field with specified name.
     * 
     * @param clazz
     *            the class
     * @param name
     *            the field name
     * @return the field that matches supplied name iterating from subclass to super
     *         class
     * @throws UnifyException
     *             if declared field with name is not found
     */
    public static Field getField(Class<?> clazz, String name) throws UnifyException {
        Field field = declaredFieldMap.get(clazz).get(name);
        if (field == null) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_FIELD_UNKNOWN, clazz, name);
        }
        return field;
    }

    /**
     * Returns nested field type.
     * 
     * @param beanClazz
     *            the base bean class
     * @param name
     *            the nested name
     * @throws UnifyException
     *             if an error occurs
     */
    public static Class<?> getNestedFieldType(Class<?> beanClazz, String name) throws UnifyException {
        String[] names = name.split("\\.");
        for (int i = 0; i < names.length; i++) {
            GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClazz).get(names[i]);
            beanClazz = getterSetterInfo.getType();
        }
        return beanClazz;
    }

    /**
     * Returns true if a nested field of a class is gettable.
     * 
     * @param beanClazz
     *            the bean class
     * @param name
     *            the name of the field
     * @throws UnifyException
     *             if an error occurs
     */
    public static boolean isGettableField(Class<?> beanClazz, String name) throws UnifyException {
        if (name.indexOf('.') > 0) {
            String[] names = name.split("\\.");
            for (int i = 0; i < names.length; i++) {
                GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClazz).get(names[i]);
                if (getterSetterInfo == null || !getterSetterInfo.isGetter()) {
                    return false;
                }
                beanClazz = getterSetterInfo.getType();
            }
            return true;
        }
        GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClazz).get(name);
        return getterSetterInfo != null && getterSetterInfo.isGetter();
    }

    /**
     * Returns true if a nested field of a class is settanle.
     * 
     * @param beanClazz
     *            the bean class
     * @param name
     *            the name of the field
     * @throws UnifyException
     *             if an error occurs
     */
    public static boolean isSettableField(Class<?> beanClazz, String name) throws UnifyException {
        if (name.indexOf('.') > 0) {
            String[] names = name.split("\\.");
            int i = 0;
            for (; i < names.length - 1; i++) {
                GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClazz).get(names[i]);
                if (getterSetterInfo == null || !getterSetterInfo.isGetter()) {
                    return false;
                }
                beanClazz = getterSetterInfo.getType();
            }

            GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClazz).get(names[i]);
            if (getterSetterInfo == null || !getterSetterInfo.isSetter()) {
                return false;
            }

            return true;
        }
        GetterSetterInfo getterSetterInfo = caseSensitiveGetterSetterMap.get(beanClazz).get(name);
        return getterSetterInfo != null && getterSetterInfo.isSetter();
    }

    /**
     * Returns a required class annotation.
     * 
     * @param clazz
     *            the type
     * @param annotClazz
     *            the annotation
     * @return the annotation
     * @throws UnifyException
     *             if class does not have specified annotation
     */
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotClazz) throws UnifyException {
        T t = clazz.getAnnotation(annotClazz);
        if (t == null) {
            throw new UnifyException(UnifyCoreErrorConstants.REFLECT_ANNOTATION_REQUIRED, annotClazz, clazz);
        }
        return t;
    }

    /**
     * Returns class for supplied name.
     * 
     * @param className
     *            the name of the class
     * @throws UnifyException
     *             if an error occurs
     */
    public static Class<?> getClassForName(String className) throws UnifyException {
        try {
            Class<?> clazz = primitiveToClassMap.get(className);
            if (clazz != null) {
                return clazz;
            }

            return Class.forName(className);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, ReflectUtils.class);
        }
    }

    /**
     * Returns a casted new instance of specific type.
     * 
     * @param clazz
     *            the type to cast new instance to
     * @param className
     *            the name of the class
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz, String className) throws UnifyException {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INSTANTIATION_ERROR, clazz);
        }
    }

    /**
     * Returns a new instance of specific type.
     * 
     * @param clazz
     *            the type
     * @throws UnifyException
     *             if an error occurs
     */
    public static <T> T newInstance(Class<T> clazz) throws UnifyException {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INSTANTIATION_ERROR, clazz);
        }
    }

    /**
     * Returns a new instance of specific type using supplied parameters.
     * 
     * @param clazz
     *            the instance type
     * @param parameterTypes
     *            the constructor parameter types
     * @param parameters
     *            the parameters
     * @return the instance
     * @throws UnifyException
     *             if an error occurs
     */
    public static <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object... parameters)
            throws UnifyException {
        try {
            return clazz.getDeclaredConstructor(parameterTypes).newInstance(parameters);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INSTANTIATION_ERROR, clazz);
        }
    }

    /**
     * Returns a new instance of specific type using supplied largest constructor
     * and parameters.
     * 
     * @param clazz
     *            the instance type
     * @param parameters
     *            the parameters
     * @return the instance
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstanceFromLargestConstructor(Class<T> clazz, Object... parameters) throws UnifyException {
        try {
            return (T) largeConstructorMap.get(clazz).getConstructor().newInstance(parameters);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_INSTANTIATION_ERROR, clazz);
        }
    }

    /**
     * Returns the largest constructor parameters for supplied class.
     * 
     * @param clazz
     *            the class to check
     * @throws UnifyException
     *             if an error occurs
     */
    public static <T> List<Class<?>> getLargestConstructorParameters(Class<T> clazz) throws UnifyException {
        return largeConstructorMap.get(clazz).getParams();
    }

    /**
     * Gets class hierarchy list from super class down to supplied sub class.
     * 
     * @param clazz
     *            the type
     * @return the class hierarchy list
     */
    public static List<Class<?>> getClassHierachyList(Class<?> clazz) {
        List<Class<?>> classHierachyList = new ArrayList<Class<?>>();
        while (clazz != null) {
            classHierachyList.add(0, clazz);
            clazz = clazz.getSuperclass();
        }
        return classHierachyList;
    }

    /**
     * Returns annotated constant strings for a specified type.
     * 
     * @param constantsClass
     *            the type
     * @param annotationClass
     *            the annotation type
     * @return the map of string constants to annotation
     * @throws UnifyException
     *             if an error occurs
     */
    public static <T extends Annotation> Map<String, T> getStringConstantFieldAnnotations(Class<?> constantsClass,
            Class<? extends T> annotationClass) throws UnifyException {
        Map<String, T> resultMap = new LinkedHashMap<String, T>();
        Field[] fields = ReflectUtils.getAnnotatedFields(constantsClass, annotationClass);
        for (Field field : fields) {
            String constantString = ReflectUtils.getPublicStaticStringConstant(field);
            resultMap.put(constantString, field.getAnnotation(annotationClass));
        }
        return resultMap;
    }

    /**
     * Gets the string value of a constant field.
     * 
     * @param longFieldName
     *            the long java name of the field
     * @throws UnifyException
     *             if an error occurs
     */
    public static String getPublicStaticStringConstant(String longFieldName) throws UnifyException {
        try {
            int index = longFieldName.lastIndexOf('.');
            Class<?> clazz = Class.forName(longFieldName.substring(0, index));
            Field field = clazz.getField(longFieldName.substring(index + 1));
            return ReflectUtils.getPublicStaticStringConstant(field);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, ReflectUtils.class);
        }
    }

    public static String getMethodSignature(String typeName, Method method) {
        StringBuilder sb = new StringBuilder();
        if (typeName != null) {
            sb.append(typeName).append('.');
        }

        sb.append(method.getName()).append('(');
        boolean isAppendSymbol = false;
        for (Class<?> type : method.getParameterTypes()) {
            if (isAppendSymbol) {
                sb.append(',');
            } else {
                isAppendSymbol = true;
            }
            sb.append(type.getSimpleName());
        }
        sb.append(')');
        return sb.toString();
    }

    public static Class<?> getArgumentType(Type type, int index) throws UnifyException {
        if (type instanceof ParameterizedType) {
            Type argType = ((ParameterizedType) type).getActualTypeArguments()[index];
            if (argType instanceof WildcardType) {
                argType = ((WildcardType) argType).getUpperBounds()[0];
            }
            if (argType instanceof Class) {
                return (Class<?>) argType;
            }
        }
        return null;
    }

    private static boolean innerBeanEquals(Object a, Object b, Set<String> ignore) {
        try {
            if (ignore.isEmpty()) {
                for (GetterSetterInfo getterSetterInfo : caseSensitiveGetterSetterMap.get(a.getClass()).values()) {
                    if (getterSetterInfo.isGetter()) {
                        Method getter = getterSetterInfo.getGetter();
                        Object valueA = getter.invoke(a);
                        Object valueB = getter.invoke(b);
                        if (valueA == null) {
                            if (valueB != null) {
                                return false;
                            }
                        } else if (!valueA.equals(valueB)) {
                            return false;
                        }
                    }
                }
            } else {
                Map<String, GetterSetterInfo> map = caseSensitiveGetterSetterMap.get(a.getClass());
                for (Map.Entry<String, GetterSetterInfo> entry : map.entrySet()) {
                    if (!ignore.contains(entry.getKey())) {
                        GetterSetterInfo getterSetterInfo = entry.getValue();
                        if (getterSetterInfo != null && getterSetterInfo.isGetter()) {
                            Method getter = getterSetterInfo.getGetter();
                            Object valueA = getter.invoke(a);
                            Object valueB = getter.invoke(b);
                            if (valueA == null) {
                                if (valueB != null) {
                                    return false;
                                }
                            } else if (!valueA.equals(valueB)) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    private static String getPublicStaticStringConstant(Field field) throws UnifyException {
        int modifiers = field.getModifiers();
        if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                && String.class.equals(field.getType())) {
            try {
                return (String) field.get(null);
            } catch (Exception e) {
            }
        }
        throw new UnifyException(UnifyCoreErrorConstants.REFLECT_FIELD_IS_NOT_PUBLIC_NONFINAL, field);
    }

    private static class ConstructorInfo {

        private Constructor<?> constructor;

        private List<Class<?>> params;

        public ConstructorInfo(Constructor<?> constructor, List<Class<?>> params) {
            this.constructor = constructor;
            this.params = params;
        }

        public Constructor<?> getConstructor() {
            return constructor;
        }

        public List<Class<?>> getParams() {
            return params;
        }
    }
}
