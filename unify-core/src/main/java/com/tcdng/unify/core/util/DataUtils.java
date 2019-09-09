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
package com.tcdng.unify.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.EnumConst;
import com.tcdng.unify.core.convert.BigDecimalConverter;
import com.tcdng.unify.core.convert.BooleanConverter;
import com.tcdng.unify.core.convert.ByteArrayConverter;
import com.tcdng.unify.core.convert.ByteConverter;
import com.tcdng.unify.core.convert.CharacterConverter;
import com.tcdng.unify.core.convert.ClassConverter;
import com.tcdng.unify.core.convert.Converter;
import com.tcdng.unify.core.convert.DateConverter;
import com.tcdng.unify.core.convert.DoubleConverter;
import com.tcdng.unify.core.convert.FloatConverter;
import com.tcdng.unify.core.convert.IntegerConverter;
import com.tcdng.unify.core.convert.LongConverter;
import com.tcdng.unify.core.convert.MoneyConverter;
import com.tcdng.unify.core.convert.ShortConverter;
import com.tcdng.unify.core.convert.StringConverter;
import com.tcdng.unify.core.convert.UplElementReferencesConverter;
import com.tcdng.unify.core.convert.UploadedFileConverter;
import com.tcdng.unify.core.data.Input;
import com.tcdng.unify.core.data.Money;
import com.tcdng.unify.core.data.UploadedFile;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.list.ZeroParams;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.json.JsonBigDecimalArrayConverter;
import com.tcdng.unify.core.util.json.JsonBigDecimalConverter;
import com.tcdng.unify.core.util.json.JsonBooleanArrayConverter;
import com.tcdng.unify.core.util.json.JsonBooleanConverter;
import com.tcdng.unify.core.util.json.JsonByteArrayConverter;
import com.tcdng.unify.core.util.json.JsonCharacterConverter;
import com.tcdng.unify.core.util.json.JsonDoubleArrayConverter;
import com.tcdng.unify.core.util.json.JsonDoubleConverter;
import com.tcdng.unify.core.util.json.JsonFloatArrayConverter;
import com.tcdng.unify.core.util.json.JsonFloatConverter;
import com.tcdng.unify.core.util.json.JsonIntArrayConverter;
import com.tcdng.unify.core.util.json.JsonIntConverter;
import com.tcdng.unify.core.util.json.JsonLongArrayConverter;
import com.tcdng.unify.core.util.json.JsonLongConverter;
import com.tcdng.unify.core.util.json.JsonShortArrayConverter;
import com.tcdng.unify.core.util.json.JsonShortConverter;
import com.tcdng.unify.core.util.json.JsonStringArrayConverter;
import com.tcdng.unify.core.util.json.JsonStringConverter;
import com.tcdng.unify.core.util.json.JsonValueConverter;

/**
 * Provides utility methods for data manipulation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public final class DataUtils {

    public static final Object[] ZEROLEN_OBJECT_ARRAY = new Object[0];

    public static final String[] ZEROLEN_STRING_ARRAY = new String[0];

    public static final Long[] ZEROLEN_LONG_ARRAY = new Long[0];

    public static final Integer[] ZEROLEN_INTEGER_ARRAY = new Integer[0];

    public static final byte[] ZEROLEN_BYTE_ARRAY = new byte[0];

    public static final String EMPTY_STRING = "";

    private static final Map<Class<?>, DataType> classToDataTypeMap;

    static {
        Map<Class<?>, DataType> map = new HashMap<Class<?>, DataType>();
        map.put(byte[].class, DataType.BLOB);
        map.put(Boolean.class, DataType.BOOLEAN);
        map.put(boolean.class, DataType.BOOLEAN);
        map.put(char.class, DataType.CHAR);
        map.put(Character.class, DataType.CHAR);
        map.put(Date.class, DataType.DATE);
        map.put(BigDecimal.class, DataType.DECIMAL);
        map.put(Double.class, DataType.DOUBLE);
        map.put(double.class, DataType.DOUBLE);
        map.put(Float.class, DataType.FLOAT);
        map.put(float.class, DataType.FLOAT);
        map.put(Integer.class, DataType.INTEGER);
        map.put(int.class, DataType.INTEGER);
        map.put(Long.class, DataType.LONG);
        map.put(long.class, DataType.LONG);
        map.put(Short.class, DataType.SHORT);
        map.put(short.class, DataType.SHORT);
        map.put(String.class, DataType.STRING);
        classToDataTypeMap = Collections.unmodifiableMap(map);
    }

    private static final Map<Class<?>, ColumnType> classToColumnMap;

    static {
        Map<Class<?>, ColumnType> map = new HashMap<Class<?>, ColumnType>();
        map.put(byte[].class, ColumnType.BLOB);
        map.put(Boolean.class, ColumnType.BOOLEAN);
        map.put(Boolean[].class, ColumnType.BOOLEAN_ARRAY);
        map.put(boolean.class, ColumnType.BOOLEAN);
        map.put(char.class, ColumnType.CHARACTER);
        map.put(Character.class, ColumnType.CHARACTER);
        map.put(Date.class, ColumnType.DATE);
        map.put(BigDecimal.class, ColumnType.DECIMAL);
        map.put(Double.class, ColumnType.DOUBLE);
        map.put(Double[].class, ColumnType.DOUBLE_ARRAY);
        map.put(double.class, ColumnType.DOUBLE);
        map.put(Float.class, ColumnType.FLOAT);
        map.put(Float[].class, ColumnType.FLOAT_ARRAY);
        map.put(float.class, ColumnType.FLOAT);
        map.put(Integer.class, ColumnType.INTEGER);
        map.put(Integer[].class, ColumnType.INTEGER_ARRAY);
        map.put(int.class, ColumnType.INTEGER);
        map.put(Long.class, ColumnType.LONG);
        map.put(Long[].class, ColumnType.LONG_ARRAY);
        map.put(long.class, ColumnType.LONG);
        map.put(Short.class, ColumnType.SHORT);
        map.put(Short[].class, ColumnType.SHORT_ARRAY);
        map.put(short.class, ColumnType.SHORT);
        map.put(String.class, ColumnType.STRING);
        map.put(String[].class, ColumnType.STRING_ARRAY);
        classToColumnMap = Collections.unmodifiableMap(map);
    }

    private static final Map<Class<?>, Converter<?>> classToConverterMap;

    static {
        Map<Class<?>, Converter<?>> map = new HashMap<Class<?>, Converter<?>>();
        map.put(boolean.class, new BooleanConverter());// ByteArrayConverter
        map.put(Boolean.class, new BooleanConverter());
        map.put(byte.class, new ByteConverter());
        map.put(Byte.class, new ByteConverter());
        map.put(byte[].class, new ByteArrayConverter());
        map.put(char.class, new CharacterConverter());
        map.put(Character.class, new CharacterConverter());
        map.put(short.class, new ShortConverter());
        map.put(Short.class, new ShortConverter());
        map.put(int.class, new IntegerConverter());
        map.put(Integer.class, new IntegerConverter());
        map.put(long.class, new LongConverter());
        map.put(Long.class, new LongConverter());
        map.put(float.class, new FloatConverter());
        map.put(Float.class, new FloatConverter());
        map.put(double.class, new DoubleConverter());
        map.put(Double.class, new DoubleConverter());
        map.put(BigDecimal.class, new BigDecimalConverter());
        map.put(Date.class, new DateConverter());
        map.put(Money.class, new MoneyConverter());
        map.put(String.class, new StringConverter());
        map.put(UplElementReferences.class, new UplElementReferencesConverter());
        map.put(UploadedFile.class, new UploadedFileConverter());
        map.put(Class.class, new ClassConverter());
        classToConverterMap = Collections.unmodifiableMap(map);
    }

    private static final Map<Class<?>, Object> classToNullMap;

    static {
        Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
        map.put(boolean.class, false);
        map.put(byte.class, Byte.valueOf((byte) 0));
        map.put(char.class, Character.valueOf((char) 0));
        map.put(short.class, Short.valueOf((short) 0));
        map.put(int.class, Integer.valueOf(0));
        map.put(long.class, Long.valueOf(0));
        map.put(float.class, Float.valueOf((float) 0.0));
        map.put(double.class, Double.valueOf(0.0));
        classToNullMap = Collections.unmodifiableMap(map);
    }

    private static final Map<Class<?>, Class<?>> classToWrapperMap;

    static {
        Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
        map.put(boolean.class, Boolean.class);
        map.put(char.class, Character.class);
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
        classToWrapperMap = Collections.unmodifiableMap(map);
    }

    private static final Map<Class<?>, String> typeToFormatterMap;

    static {
        Map<Class<?>, String> map = new HashMap<Class<?>, String>();
        map.put(Short.class, "!integerformat");
        map.put(short.class, "!integerformat");
        map.put(Integer.class, "!integerformat");
        map.put(int.class, "!integerformat");
        map.put(Long.class, "!longformat");
        map.put(long.class, "!longformat");
        map.put(Float.class, "!decimalformat");
        map.put(float.class, "!decimalformat");
        map.put(Double.class, "!decimalformat");
        map.put(double.class, "!decimalformat");
        map.put(BigDecimal.class, "!decimalformat");
        map.put(Date.class, "!dateformat");
        typeToFormatterMap = Collections.unmodifiableMap(map);
    }

    private static final Map<Class<? extends Collection>, Class<? extends Collection>> collectionInterfaceToClassMap;

    static {
        Map<Class<? extends Collection>, Class<? extends Collection>> map =
                new HashMap<Class<? extends Collection>, Class<? extends Collection>>();
        map.put(Collection.class, ArrayList.class);
        map.put(List.class, ArrayList.class);
        map.put(Set.class, HashSet.class);
        map.put(Queue.class, LinkedList.class);
        map.put(Deque.class, LinkedList.class);
        collectionInterfaceToClassMap = Collections.unmodifiableMap(map);
    }

    private static final Map<Class<?>, JsonValueConverter> jsonConverrerMap;

    static {
        Map<Class<?>, JsonValueConverter> map = new HashMap<Class<?>, JsonValueConverter>();
        map.put(Boolean.class, new JsonBooleanConverter());
        map.put(Boolean[].class, new JsonBooleanArrayConverter());
        map.put(boolean.class, new JsonBooleanConverter());
        map.put(byte[].class, new JsonByteArrayConverter());
        map.put(char.class, new JsonCharacterConverter());
        map.put(Character.class, new JsonCharacterConverter());
        map.put(BigDecimal.class, new JsonBigDecimalConverter());
        map.put(BigDecimal[].class, new JsonBigDecimalArrayConverter());
        map.put(Double.class, new JsonDoubleConverter());
        map.put(Double[].class, new JsonDoubleArrayConverter());
        map.put(double.class, new JsonDoubleConverter());
        map.put(Float.class, new JsonFloatConverter());
        map.put(Float[].class, new JsonFloatArrayConverter());
        map.put(float.class, new JsonFloatConverter());
        map.put(Integer.class, new JsonIntConverter());
        map.put(Integer[].class, new JsonIntArrayConverter());
        map.put(int.class, new JsonIntConverter());
        map.put(Long.class, new JsonLongConverter());
        map.put(Long[].class, new JsonLongArrayConverter());
        map.put(long.class, new JsonLongConverter());
        map.put(Short.class, new JsonShortConverter());
        map.put(Short[].class, new JsonShortArrayConverter());
        map.put(short.class, new JsonShortConverter());
        map.put(String.class, new JsonStringConverter());
        map.put(String[].class, new JsonStringArrayConverter());
        jsonConverrerMap = Collections.unmodifiableMap(map);
    }

    private DataUtils() {

    }

    /**
     * Checks if two objects are equal.
     * 
     * @param a
     *            the first object
     * @param b
     *            the second object
     * @return a true value of both parameters are equal
     */
    public static boolean equals(Object a, Object b) {
        if (a == b) {
            return true;
        }

        if (a == null) {
            return false;
        }

        return a.equals(b);
    }

    /**
     * Returns a concrete type for a collection type.
     * 
     * @param clazz
     *            the collection type
     * @throws UnifyException
     *             if an error occurs
     */
    public static Class<? extends Collection> getCollectionConcreteType(Class<? extends Collection> clazz)
            throws UnifyException {
        Class<? extends Collection> result = clazz;
        if (clazz.isInterface()) {
            result = collectionInterfaceToClassMap.get(clazz);
            if (result == null) {
                throw new UnifyException(UnifyCoreErrorConstants.UNSUPPORTED_COLLECTIONTYPE, clazz);
            }
        }
        return result;
    }

    /**
     * Returns the column type of a field.
     * 
     * @param field
     *            the supplied field
     * @return The column type
     * @throws UnifyException
     *             if field is not annotated with {@link Column}
     */
    public static ColumnType getColumnType(Field field) throws UnifyException {
        Column ca = field.getAnnotation(Column.class);
        if (ca != null) {
            if (ColumnType.AUTO.equals(ca.type())) {
                return DataUtils.getColumnType(field.getType());
            }
            return ca.type();
        }

        return DataUtils.getColumnType(field.getType());
    }

    /**
     * Returns the data type equivalence of supplied data type.
     * 
     * @param clazz
     *            the data type
     * @return the equivalent data type
     * @throws UnifyException
     *             if data type is unsupported
     */
    public static DataType getDataType(Class<?> clazz) throws UnifyException {
        DataType dataType = classToDataTypeMap.get(clazz);

        if (dataType == null) {
            throw new UnifyException(UnifyCoreErrorConstants.RECORD_UNSUPPORTED_PROPERTY_TYPE, clazz);
        }
        return dataType;
    }

    /**
     * Finds the data type equivalence of supplied data type.
     * 
     * @param clazz
     *            the data type
     * @return the equivalent data type if found otherwise null
     * @throws UnifyException
     *             if an error occurs
     */
    public static DataType findDataType(Class<?> clazz) throws UnifyException {
        return classToDataTypeMap.get(clazz);
    }

    /**
     * Returns the column type equivalence of supplied data type.
     * 
     * @param clazz
     *            the data type
     * @return the equivalent column type
     * @throws UnifyException
     *             if data type is unsupported
     */
    public static ColumnType getColumnType(Class<?> clazz) throws UnifyException {
        ColumnType columnType = classToColumnMap.get(clazz);

        if (columnType == null) {
            if (EnumConst.class.isAssignableFrom(clazz)) {
                return ColumnType.ENUMCONST;
            }

            throw new UnifyException(UnifyCoreErrorConstants.RECORD_UNSUPPORTED_PROPERTY_TYPE, clazz);
        }
        return columnType;
    }

    /**
     * Returns the column type equivalence of supplied data type.
     * 
     * @param className
     *            the data type
     * @return the equivalent column type
     * @throws UnifyException
     *             if data type is unsupported
     */
    public static ColumnType getColumnType(String className) throws UnifyException {
        Class<?> clazz = ReflectUtils.getClassForName(className);
        return DataUtils.getColumnType(clazz);
    }

    /**
     * Returns the wrapper class for a data type.
     * 
     * @param clazz
     *            the data type
     * @return the wrapper class if found, otherwise the supplied data type is
     *         returned
     */
    public static Class<?> getWrapperClass(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return classToWrapperMap.get(clazz);
        }
        return clazz;
    }

    /**
     * Returns the name of the wrapper class for a data type.
     * 
     * @param clazz
     *            the data type
     * @return the name of the wrapper class
     */
    public static String getWrapperClassName(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return classToWrapperMap.get(clazz).getName();
        }
        return clazz.getName();
    }

    public static String getDefaultFormatterDescriptor(Class<?> clazz) {
        return typeToFormatterMap.get(clazz);
    }

    public static <T> List<T> getNewArrayList(Class<T> type) {
        return new ArrayList<T>();
    }

    /**
     * Tests if a supplied data type is a number type.
     * 
     * @param clazz
     *            the data type
     */
    public static boolean isNumberType(Class<?> clazz) {
        return Number.class.isAssignableFrom(DataUtils.getWrapperClass(clazz));
    }

    /**
     * Tests if data type of supplied class name is a number type.
     * 
     * @param className
     *            the data type class name
     */
    public static boolean isNumberType(String className) throws UnifyException {
        return Number.class.isAssignableFrom(DataUtils.getWrapperClass(ReflectUtils.getClassForName(className)));
    }

    /**
     * Gets the null value of a data type.
     * 
     * @param clazz
     *            the data type
     * @return the null value
     */
    public static Object getNullValue(Class<?> clazz) {
        return classToNullMap.get(clazz);
    }

    public static <T> T getBeanProperty(Class<T> targetClazz, Object bean, String propertyName) throws UnifyException {
        return DataUtils.convert(targetClazz, ReflectUtils.getBeanProperty(bean, propertyName), null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] getBeanPropertyArray(Class<T> targetClazz, List<?> objectList, String propertyName)
            throws UnifyException {
        T[] result = (T[]) Array.newInstance(targetClazz, objectList.size());
        for (int i = 0; i < result.length; i++) {
            result[i] = (T) ReflectUtils.getBeanProperty(objectList.get(i), propertyName);
        }
        return result;
    }

    public static Object[] getArrayFromList(List<?> list) throws UnifyException {
        if (list != null) {
            if (list.size() > 0) {
                return list.toArray((Object[]) Array.newInstance(list.get(0).getClass(), list.size()));
            }

            return DataUtils.ZEROLEN_OBJECT_ARRAY;
        }

        return null;
    }

    /**
     * Converts supplied value to target type.
     * 
     * @param targetClazz
     *            the target type
     * @param value
     *            the value to convert
     * @param formatter
     *            the conversion formatter. Can be null
     * @return the converted value
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Class<T> targetClazz, Object value, Formatter<?> formatter) throws UnifyException {
        try {
            if (value == null) {
                return (T) classToNullMap.get(targetClazz);
            }

            if (targetClazz.equals(value.getClass())) {
                if (formatter != null && String.class.equals(targetClazz)) {
                    return (T) classToConverterMap.get(targetClazz).convert(value, formatter);
                }

                return (T) value;
            }

            if (targetClazz.isAssignableFrom(value.getClass())) {
                if (formatter != null) {
                    return (T) classToConverterMap.get(targetClazz).convert(value, formatter);
                }

                return (T) value;
            }

            if (targetClazz.isArray()) {
                if (byte[].class.equals(targetClazz)) {
                    return (T) classToConverterMap.get(targetClazz).convert(value, formatter);
                }

                Class<?> targetClass = targetClazz.getComponentType();
                Object values = DataUtils.getValueObjectArray(value);
                int length = Array.getLength(values);
                Object result = Array.newInstance(targetClass, length);
                Object nullValue = classToNullMap.get(targetClass);
                if (EnumConst.class.isAssignableFrom(targetClass)) {
                    for (int i = 0; i < length; i++) {
                        Object arrValue = Array.get(values, i);
                        if (arrValue == null) {
                            arrValue = nullValue;
                        } else if (!targetClass.isAssignableFrom(arrValue.getClass())) {
                            arrValue = EnumUtils.fromCode((Class<? extends EnumConst>) targetClass,
                                    String.valueOf(arrValue));
                        }
                        Array.set(result, i, arrValue);
                    }
                } else {
                    Converter<?> converter = classToConverterMap.get(targetClass);
                    for (int i = 0; i < length; i++) {
                        Object arrValue = Array.get(values, i);
                        if (arrValue == null) {
                            arrValue = nullValue;
                        } else if (!targetClass.isAssignableFrom(arrValue.getClass())) {
                            arrValue = converter.convert(arrValue, formatter);
                        }
                        Array.set(result, i, arrValue);
                    }
                }
                return (T) result;
            } else if (Collection.class.isAssignableFrom(targetClazz)) {
                Object values = DataUtils.getValueObjectArray(value);
                int length = Array.getLength(values);
                Collection<Object> result = ReflectUtils
                        .newInstance(DataUtils.getCollectionConcreteType((Class<? extends Collection>) targetClazz));
                for (int i = 0; i < length; i++) {
                    result.add(Array.get(values, i));
                }
                return (T) result;
            } else {
                Object result = null;
                if (EnumConst.class.isAssignableFrom(targetClazz)) {
                    String valueStr = null;
                    if (value.getClass().isArray() && Array.getLength(value) == 1) {
                        valueStr = String.valueOf(Array.get(value, 0));
                    } else {
                        valueStr = String.valueOf(value);
                    }

                    result = EnumUtils.fromCode((Class<? extends EnumConst>) targetClazz, valueStr);
                    if (result == null) {
                        result = EnumUtils.fromName((Class<? extends EnumConst>) targetClazz, valueStr);
                    }
                } else {
                    if (value.getClass().isArray() && Array.getLength(value) == 1) {
                        result = classToConverterMap.get(targetClazz).convert(Array.get(value, 0), formatter);
                    } else {
                        result = classToConverterMap.get(targetClazz).convert(value, formatter);
                    }
                }

                if (result == null) {
                    result = classToNullMap.get(targetClazz);
                }
                return (T) result;
            }
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATA_CONVERSION_ERROR, targetClazz, value);
        }
    }

    /**
     * Converts supplied value to target collection type.
     * 
     * @param collectionClazz
     *            the collection type
     * @param dataClass
     *            the collection data type
     * @param value
     *            the value to convert
     * @param formatter
     *            the conversion formatter. Can be null
     * @return the converted value
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T, U extends Collection<T>> U convert(Class<U> collectionClazz, Class<T> dataClass, Object value,
            Formatter<?> formatter) throws UnifyException {
        try {
            if (value == null) {
                return null;
            }

            Class<?> valueClass = value.getClass();
            if (collectionClazz.isAssignableFrom(valueClass)) {
                Collection collection = (Collection) value;
                if (!collection.isEmpty()) {
                    Iterator it = collection.iterator();
                    while (it.hasNext()) {
                        Object elem = it.next();
                        if (elem != null) {
                            if (dataClass.isAssignableFrom(elem.getClass())) {
                                return (U) value;
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                    return (U) value;
                }
            }

            Collection<Object> result = ReflectUtils.newInstance(DataUtils.getCollectionConcreteType(collectionClazz));
            if (valueClass.isArray()) {
                int length = Array.getLength(value);
                if (dataClass.equals(valueClass.getComponentType())) {
                    for (int i = 0; i < length; i++) {
                        result.add(Array.get(value, i));
                    }
                } else {
                    if (EnumConst.class.isAssignableFrom(dataClass)) {
                        for (int i = 0; i < length; i++) {
                            result.add(EnumUtils.fromCode((Class<? extends EnumConst>) dataClass,
                                    String.valueOf(Array.get(value, i))));
                        }
                    } else {
                        Converter<?> converter = classToConverterMap.get(dataClass);
                        for (int i = 0; i < length; i++) {
                            result.add(converter.convert(Array.get(value, i), formatter));
                        }
                    }
                }
            } else if (Collection.class.isAssignableFrom(valueClass)) {
                if (EnumConst.class.isAssignableFrom(dataClass)) {
                    for (Object object : (Collection<Object>) value) {
                        result.add(EnumUtils.fromCode((Class<? extends EnumConst>) dataClass, String.valueOf(object)));
                    }
                } else {
                    Converter<?> converter = classToConverterMap.get(dataClass);
                    for (Object object : (Collection<Object>) value) {
                        result.add(converter.convert(object, formatter));
                    }
                }
            } else {
                if (EnumConst.class.isAssignableFrom(dataClass)) {
                    Object resultItem =
                            EnumUtils.fromCode((Class<? extends EnumConst>) dataClass, String.valueOf(value));
                    if (resultItem == null) {
                        resultItem = EnumUtils.fromName((Class<? extends EnumConst>) dataClass, String.valueOf(value));
                    }

                    result.add(resultItem);
                } else {
                    result.add(classToConverterMap.get(dataClass).convert(value, formatter));
                }
            }
            return (U) result;

        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.DATA_CONVERSION_ERROR, collectionClazz, value);
        }
    }

    /**
     * Converts value to array of specified type.
     * 
     * @param clazz
     *            the array type
     * @param value
     *            the value to convert
     * @return the conversion result
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] getValueArray(Class<T> clazz, Object value) throws UnifyException {
        try {
            if (value == null || value.getClass().isArray()) {
                return (T[]) value;
            }
            List<T> values = new ArrayList<T>();
            if (value instanceof Map) {
                value = ((Map<?, T>) value).values();
            }
            if (value instanceof Collection) {
                for (T subValues : (Collection<T>) value) {
                    if (subValues != null) {
                        if (subValues.getClass().isArray()) {
                            for (T subValue : (T[]) subValues) {
                                values.add(subValue);
                            }
                        } else {
                            values.add(subValues);
                        }
                    }
                }
            } else {
                values.add((T) value);
            }
            return values.toArray((T[]) Array.newInstance(clazz, values.size()));
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.CONVERTER_EXCEPTION);
        }
    }

    /**
     * Splits a length into blocks. Returns an array whose length is the number of
     * blocks with each element equal to the length of the corresponding block. For
     * instance if we want to split 235 into blocks of 100, we can write
     * 
     * <pre>
     *     <code>
     *      int[] block = DataUtils.splitToBlocks(235, 100);
     *      //block.length = 3
     *      //block[0] = 100
     *      //block[1] = 100
     *      //block[2] = 35
     *     </code>
     * </pre>
     * 
     * @param length
     *            the length to split
     * @param blockSize
     *            the block size
     * @return array containing split information.
     */
    public static int[] splitToBlocks(int length, int blockSize) {
        int[] blocks = null;
        if (blockSize <= 0) {
            blocks = new int[1];
            blocks[0] = length;
        } else {
            int blockCount = length / blockSize;
            int remainder = length % blockSize;

            if (remainder > 0) {
                blocks = new int[blockCount + 1];
                Arrays.fill(blocks, blockSize);
                blocks[blockCount] = remainder;
            } else {
                blocks = new int[blockCount];
                Arrays.fill(blocks, blockSize);
            }
        }
        return blocks;
    }

    /**
     * Sorts a list by bean property.
     * 
     * @param list
     *            the list to sort
     * @param beanClass
     *            the bean type
     * @param property
     *            the bean property to sort with
     * @param ascending
     *            the sort direction. A true value means sort ascending
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> void sort(List<?> list, Class<T> beanClass, String property, boolean ascending)
            throws UnifyException {
        String key = beanClass.getName() + '.' + property + '.' + ascending;
        Comparator<T> comparator = (Comparator<T>) comparatorMap.get(key);
        if (comparator == null) {
            synchronized (COMPARATORMAP_LOCK) {
                comparator = (Comparator<T>) comparatorMap.get(key);
                if (comparator == null) {
                    GetterSetterInfo getterSetterInfo = ReflectUtils.getGetterInfo(beanClass, property);
                    comparator = new ObjectByFieldComparator<T>(getterSetterInfo.getGetter(), ascending);
                    comparatorMap.put(key, comparator);
                }
            }
        }
        Collections.sort((List<T>) list, comparator);
    }

    /**
     * Compares two comparable value for sorting.
     * 
     * @param value1
     *            the first value
     * @param value2
     *            the second value
     * @param ascending
     *            the sort direction
     * @return an integer representing the comparision result
     */
    public static int compareForSort(Comparable<Object> value1, Comparable<Object> value2, boolean ascending) {
        if (value1 == value2) {
            return 0;
        }

        if (ascending) {
            if (value1 == null)
                return -1;
            if (value2 == null)
                return 1;
            return value1.compareTo(value2);
        } else {
            if (value1 == null)
                return 1;
            if (value2 == null)
                return -1;
            return -value1.compareTo(value2);
        }
    }

    /**
     * Returns input holder values by name.
     * 
     * @param parameterList
     *            the parameter holder list
     * @return map of values by name
     */
    public static Map<String, Object> getInputHolderNameValueMap(List<Input> parameterList) throws UnifyException {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Input parameterHolder : parameterList) {
            result.put(parameterHolder.getName(), parameterHolder.getTypeValue());
        }
        return result;
    }

    /**
     * Sets nested bean property by long name.
     * 
     * @param bean
     *            the bean object
     * @param propertyName
     *            the nested property long name
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if an error occurs
     */
    public static void setNestedBeanProperty(Object bean, String propertyName, Object value) throws UnifyException {
        DataUtils.setNestedBeanProperty(bean, propertyName, value, null);
    }

    /**
     * Sets nested bean property by long name with supplied formatter. Does a deep
     * get until the last bean then sets property which is the last name in supplied
     * long name. For instance if we have a bean that encapsulates another
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
     * @param formatter
     *            applied before setting value. Can be null
     * @param value
     *            the value to set
     * @throws UnifyException
     *             if an error occurs
     */
    public static void setNestedBeanProperty(Object bean, String longPropertyName, Object value, Formatter<?> formatter)
            throws UnifyException {
        try {
            String[] properties = longPropertyName.split("\\.");
            int len = properties.length - 1;
            int j = 0;
            for (; j < len && bean != null; j++) {
                bean = ReflectUtils.getGetterInfo(bean.getClass(), properties[j]).getGetter().invoke(bean);
            }

            if (bean != null) {
                DataUtils.setBeanProperty(bean, properties[j], value, formatter);
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                    longPropertyName);
        }
    }

    /**
     * Populates a bean with supplied values.
     * 
     * @param bean
     *            the bean to populate
     * @param valueMap
     *            the values for population by property names
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static void populateBean(Object bean, Map<String, Object> valueMap) throws UnifyException {
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            try {
                GetterSetterInfo setterInfo = ReflectUtils.getSetterInfo(bean.getClass(), entry.getKey());
                Class<?> type = setterInfo.getType();
                Object value = entry.getValue();
                if (setterInfo.isParameterArgumented()) {
                    value = DataUtils.convert((Class<? extends Collection>) type, setterInfo.getArgumentType(), value,
                            null);
                } else {
                    value = DataUtils.convert(type, value, null);
                    if (value == null) {
                        value = DataUtils.getNullValue(type);
                    }
                }
                setterInfo.getSetter().invoke(bean, value);
            } catch (UnifyException e) {
                throw e;
            } catch (Exception e) {
                throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                        entry.getKey());
            }
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
        setBeanProperty(bean, propertyName, value, null);
    }

    /**
     * Sets the value of the property of a bean using supplied formatter.
     * 
     * @param bean
     *            the bean object
     * @param propertyName
     *            the property to set
     * @param value
     *            the value to set
     * @param formatter,
     *            if any, applied before setting value
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static void setBeanProperty(Object bean, String propertyName, Object value, Formatter<?> formatter)
            throws UnifyException {
        try {
            GetterSetterInfo setterInfo = ReflectUtils.getSetterInfo(bean.getClass(), propertyName);
            Class<?> type = setterInfo.getType();
            if (!Object.class.equals(type)) {
                if (setterInfo.isParameterArgumented()) {
                    value = DataUtils.convert((Class<? extends Collection>) type, setterInfo.getArgumentType(), value,
                            formatter);
                } else {
                    value = DataUtils.convert(type, value, formatter);
                    if (value == null) {
                        value = DataUtils.getNullValue(type);
                    }
                }
            }
            setterInfo.getSetter().invoke(bean, value);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                    propertyName);
        }
    }

    /**
     * Reads a JSON object. Has no support for collections.
     * 
     * @param type
     *            the object type
     * @param json
     *            the JSON string
     * @return instance of object
     * @throws UnifyException
     *             if an error occurs
     */
    public static <T> T readJsonObject(Class<T> type, String json) throws UnifyException {
        return DataUtils.readJsonObject(type, new StringReader(json));
    }

    /**
     * Reads a JSON object. Has no support for collections.
     * 
     * @param type
     *            the object type
     * @param inputStream
     *            the JSON input stream
     * @param charset
     *            optional character set
     * @return instance of object
     * @throws UnifyException
     *             if an error occurs
     */
    public static <T> T readJsonObject(Class<T> type, InputStream inputStream, Charset charset) throws UnifyException {
        if (charset == null) {
            return DataUtils.readJsonObject(type, new BufferedReader(new InputStreamReader(inputStream)));
        }

        return DataUtils.readJsonObject(type, new BufferedReader(new InputStreamReader(inputStream, charset)));
    }

    /**
     * Reads a JSON object. Has no support for collections.
     * 
     * @param type
     *            the object type
     * @param reader
     *            the JSON reader
     * @return instance of object
     * @throws UnifyException
     *             if an error occurs
     */
    public static <T> T readJsonObject(Class<T> type, Reader reader) throws UnifyException {
        try {
            return DataUtils.readJsonObject(type.newInstance(), Json.parse(reader).asObject());
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    /**
     * Reads a JSON object. Has no support for collections.
     * 
     * @param object
     *            bean to read to
     * @param json
     *            the JSON string
     * @throws UnifyException
     *             if an error occurs
     */
    public static void readJsonObject(Object object, String json) throws UnifyException {
        DataUtils.readJsonObject(object, new StringReader(json));
    }

    /**
     * Reads a JSON object. Has no support for collections.
     * 
     * @param object
     *            bean to read to
     * @param inputStream
     *            the JSON input stream
     * @param charset
     *            optional character set
     * @throws UnifyException
     *             if an error occurs
     */
    public static void readJsonObject(Object object, InputStream inputStream, Charset charset) throws UnifyException {
        if (charset == null) {
            DataUtils.readJsonObject(object, new BufferedReader(new InputStreamReader(inputStream)));
            return;
        }

        DataUtils.readJsonObject(object, new BufferedReader(new InputStreamReader(inputStream, charset)));
    }

    /**
     * Reads a JSON object. Has no support for collections.
     * 
     * @param object
     *            bean to read to
     * @param reader
     *            the JSON reader
     * @throws UnifyException
     *             if an error occurs
     */
    public static void readJsonObject(Object object, Reader reader) throws UnifyException {
        try {
            DataUtils.readJsonObject(object, Json.parse(reader).asObject());
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    /**
     * Writes a JSON object to an output stream. Has no support for collections.
     * 
     * @param object
     *            the object to write
     * @param outputStream
     *            the output stream to write to
     * @param charset
     *            optional character set
     * @throws UnifyException
     *             if an error occurs
     */
    public static void writeJsonObject(Object object, OutputStream outputStream, Charset charset)
            throws UnifyException {
        if (charset == null) {
            DataUtils.writeJsonObject(object, new BufferedWriter(new OutputStreamWriter(outputStream)));
            return;
        }

        DataUtils.writeJsonObject(object, new BufferedWriter(new OutputStreamWriter(outputStream, charset)));
    }

    /**
     * Writes a JSON object to a writer. Has no support for collections.
     * 
     * @param object
     *            the object to write
     * @param writer
     *            the writer to write to
     * @throws UnifyException
     *             if an error occurs
     */
    public static void writeJsonObject(Object object, Writer writer) throws UnifyException {
        try {
            JsonObject jsonObject = Json.object();
            DataUtils.writeJsonObject(object, jsonObject);
            jsonObject.writeTo(writer);
            writer.flush();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    public static String writeJsonObject(Object object) throws UnifyException {
        try {
            JsonObject jsonObject = Json.object();
            DataUtils.writeJsonObject(object, jsonObject);
            return jsonObject.toString();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    /**
     * Returns true if supplied collection is null or is empty, otherwise a null is
     * returned
     * 
     * @param coll
     *            the collection to check
     */
    public static boolean isBlank(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static <T> List<T> unmodifiableList(List<T> list) {
        if (isBlank(list)) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(list);
    }

    public static <T> List<T> unmodifiableList(T[] list) {
        if (list == null || list.length == 0) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(Arrays.asList(list));
    }

    public static <T> Set<T> unmodifiableSet(Set<T> list) {
        if (isBlank(list)) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(list);
    }

    public static <T, U> Map<T, U> unmodifiableMap(Map<T, U> map) {
        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(map);
    }

    /**
     * Creates a new instance of a data object populated with supplied parameters.
     * 
     * @param type
     *            the data object type
     * @param params
     *            the parameters to construct the data object
     * @return the new instance of data object except for {@link ZeroParams} type
     *         where the same instance is always returned
     * @throws UnifyException
     *             if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T constructDataObject(Class<T> type, Object... params) throws UnifyException {
        T paramObject = null;
        if (ZeroParams.class.equals(type)) {
            paramObject = (T) ZeroParams.INSTANCE;
        } else {
            List<Class<?>> paramTypeList = ReflectUtils.getLargestConstructorParameters(type);
            int len = paramTypeList.size();
            if (len == 1 && paramTypeList.get(0).isArray()) {
                Object initParam = DataUtils.convert(paramTypeList.get(0), params, null);
                paramObject = ReflectUtils.newInstanceFromLargestConstructor(type, initParam);
            } else {
                // Fit parameters
                Object[] initParam = new Object[len];
                for (int i = 0; i < len && i < params.length; i++) {
                    initParam[i] = DataUtils.convert(paramTypeList.get(i), params[i], null);
                }

                paramObject = ReflectUtils.newInstanceFromLargestConstructor(type, initParam);
            }
        }

        return paramObject;
    }

    @SuppressWarnings("unchecked")
    private static <T> T readJsonObject(T object, JsonObject jsonObject) throws UnifyException, Exception {
        Map<String, GetterSetterInfo> mutatorMap = ReflectUtils.getGetterSetterMap(object.getClass());
        for (Member member : jsonObject) {
            GetterSetterInfo sInfo = mutatorMap.get(member.getName());
            if (sInfo == null) {
                throw new Exception(
                        "Type " + object.getClass() + " has no mutator/accesor for member " + member.getName());
            }

            if (!sInfo.isProperty()) {
                throw new Exception(
                        "Type " + object.getClass() + " has no matching bean property for member " + member.getName());
            }

            Class<?> paramType = sInfo.getType();
            JsonValue value = member.getValue();
            if (Object.class.equals(paramType)) {
                if (!sInfo.isGetter()) {
                    throw new Exception("Type " + object.getClass() + " has no accesor for member " + member.getName());
                }

                Object implObject = sInfo.getGetter().invoke(object);
                DataUtils.readJsonObject(implObject, value.asObject());
            } else {
                if (!sInfo.isSetter()) {
                    throw new Exception("Type " + object.getClass() + " has no mutator for member " + member.getName());
                }

                JsonValueConverter<?> converter = jsonConverrerMap.get(paramType);
                if (converter == null) {
                    if (Collection.class.isAssignableFrom(paramType)) {
                        JsonArray array = value.asArray();
                        Collection<Object> result = ReflectUtils.newInstance(
                                DataUtils.getCollectionConcreteType((Class<? extends Collection>) paramType));
                        if (sInfo.isParameterArgumented()) {
                            Class<?> componentType = sInfo.getArgumentType();
                            converter = jsonConverrerMap.get(sInfo.getArgumentType());
                            if (converter == null) {
                                for (int i = 0; i < array.size(); i++) {
                                    result.add(DataUtils.readJsonObject(componentType.newInstance(),
                                            array.get(i).asObject()));
                                }
                            } else {
                                for (int i = 0; i < array.size(); i++) {
                                    result.add(converter.read(array.get(i)));
                                }
                            }
                        }

                        sInfo.getSetter().invoke(object, (Object) result);
                    } else if (paramType.isArray()) {
                        JsonArray array = value.asArray();
                        Class<?> componentType = paramType.getComponentType();
                        converter = jsonConverrerMap.get(componentType);
                        Object[] valueArray = (Object[]) Array.newInstance(componentType, array.size());
                        if (converter == null) {
                            for (int i = 0; i < valueArray.length; i++) {
                                valueArray[i] =
                                        DataUtils.readJsonObject(componentType.newInstance(), array.get(i).asObject());
                            }
                        } else {
                            for (int i = 0; i < valueArray.length; i++) {
                                valueArray[i] = converter.read(array.get(i));
                            }
                        }

                        sInfo.getSetter().invoke(object, (Object) valueArray);
                    } else {
                        sInfo.getSetter().invoke(object,
                                DataUtils.readJsonObject(paramType.newInstance(), value.asObject()));
                    }
                } else {
                    sInfo.getSetter().invoke(object, converter.read(value));
                }
            }
        }
        return object;
    }

    private static <T> void writeJsonObject(T object, JsonObject jsonObject) throws UnifyException, Exception {
        if (object != null) {
            Map<String, GetterSetterInfo> accessorMap = ReflectUtils.getGetterSetterMap(object.getClass());
            for (String name : accessorMap.keySet()) {
                GetterSetterInfo gInfo = accessorMap.get(name);
                if (!gInfo.isGetter() || !gInfo.isProperty()) {
                    continue;
                }

                Object value = gInfo.getGetter().invoke(object);
                if (value != null) {
                    Class<?> returnType = gInfo.getType();
                    if (Object.class.equals(returnType)) {
                        JsonObject jsonValue = Json.object();
                        DataUtils.writeJsonObject(value, jsonValue);
                        jsonObject.add(name, jsonValue);
                    } else {
                        JsonValueConverter<?> converter = jsonConverrerMap.get(returnType);
                        if (converter == null) {
                            if (Collection.class.isAssignableFrom(value.getClass())) {
                                JsonArray array = (JsonArray) Json.array();
                                if (gInfo.isParameterArgumented()) {
                                    converter = jsonConverrerMap.get(gInfo.getArgumentType());
                                }

                                if (converter == null) {
                                    for (Object obj : (Collection<?>) value) {
                                        JsonObject jsonValue = Json.object();
                                        DataUtils.writeJsonObject(obj, jsonValue);
                                        array.add(jsonValue);
                                    }
                                } else {
                                    for (Object obj : (Collection<?>) value) {
                                        array.add(converter.write(obj));
                                    }
                                }

                                jsonObject.add(name, array);
                            } else if (value.getClass().isArray()) {
                                JsonArray array = (JsonArray) Json.array();
                                Class<?> componentType = value.getClass().getComponentType();
                                converter = jsonConverrerMap.get(componentType);
                                int length = Array.getLength(value);
                                if (converter == null) {
                                    for (int i = 0; i < length; i++) {
                                        JsonObject jsonValue = Json.object();
                                        DataUtils.writeJsonObject(Array.get(value, i), jsonValue);
                                        array.add(jsonValue);
                                    }
                                } else {
                                    for (int i = 0; i < length; i++) {
                                        array.add(converter.write(Array.get(value, i)));
                                    }
                                }

                                jsonObject.add(name, array);
                            } else {
                                JsonObject jsonValue = Json.object();
                                DataUtils.writeJsonObject(value, jsonValue);
                                jsonObject.add(name, jsonValue);
                            }
                        } else {
                            jsonObject.add(name, converter.write(value));
                        }
                    }
                }
            }
        }
    }

    private static final Map<String, Comparator<?>> comparatorMap = new HashMap<String, Comparator<?>>();

    private static final Object COMPARATORMAP_LOCK = new Object();

    private static class ObjectByFieldComparator<T> implements Comparator<T> {

        private Method getter;

        private boolean ascending;

        public ObjectByFieldComparator(Method getter, boolean ascending) {
            this.getter = getter;
            this.ascending = ascending;
            Class<?> fieldType = getter.getReturnType();
            if (fieldType.isPrimitive()) {
                fieldType = classToWrapperMap.get(fieldType);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compare(T object1, T object2) {
            try {
                Object value1 = this.getter.invoke(object1);
                Object value2 = this.getter.invoke(object2);
                return DataUtils.compareForSort((Comparable<Object>) value1, (Comparable<Object>) value2, ascending);
            } catch (Exception e) {
            }
            return 0;
        }
    }

    private static Object getValueObjectArray(Object value) throws UnifyException {
        try {
            if (value.getClass().isArray()) {
                return value;
            }
            List<Object> values = new ArrayList<Object>();
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).values();
            } else if (value instanceof Collection) {
                for (Object subValues : (Collection<?>) value) {
                    if (subValues != null) {
                        if (subValues.getClass().isArray()) {
                            int length = Array.getLength(subValues);
                            for (int i = 0; i < length; i++) {
                                values.add(Array.get(subValues, i));
                            }
                        } else {
                            values.add(subValues);
                        }
                    }
                }
            } else {
                values.add(value);
            }
            return values.toArray(new Object[values.size()]);
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.CONVERTER_EXCEPTION);
        }
    }
}
