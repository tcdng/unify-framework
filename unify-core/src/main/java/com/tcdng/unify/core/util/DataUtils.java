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
package com.tcdng.unify.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.PrettyPrint;
import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.convert.converters.ConverterFormatter;
import com.tcdng.unify.convert.util.ConverterUtils;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.ColumnType;
import com.tcdng.unify.core.constant.DataType;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.convert.ByteArrayConverter;
import com.tcdng.unify.core.convert.ClassConverter;
import com.tcdng.unify.core.convert.IndexedTargetConverter;
import com.tcdng.unify.core.convert.MoneyConverter;
import com.tcdng.unify.core.convert.PeriodConverter;
import com.tcdng.unify.core.convert.StringConverter;
import com.tcdng.unify.core.convert.UplElementReferencesConverter;
import com.tcdng.unify.core.convert.UploadedFileConverter;
import com.tcdng.unify.core.criterion.Order;
import com.tcdng.unify.core.data.IndexedTarget;
import com.tcdng.unify.core.data.Input;
import com.tcdng.unify.core.data.Money;
import com.tcdng.unify.core.data.Period;
import com.tcdng.unify.core.data.UploadedFile;
import com.tcdng.unify.core.format.DateTimeFormatter;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.input.BigDecimalInput;
import com.tcdng.unify.core.input.BooleanInput;
import com.tcdng.unify.core.input.ByteArrayInput;
import com.tcdng.unify.core.input.ByteInput;
import com.tcdng.unify.core.input.CharacterInput;
import com.tcdng.unify.core.input.DateInput;
import com.tcdng.unify.core.input.DoubleInput;
import com.tcdng.unify.core.input.FloatInput;
import com.tcdng.unify.core.input.IntegerInput;
import com.tcdng.unify.core.input.LongInput;
import com.tcdng.unify.core.input.MoneyInput;
import com.tcdng.unify.core.input.ShortInput;
import com.tcdng.unify.core.input.StringInput;
import com.tcdng.unify.core.list.ZeroParams;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.json.JsonBigDecimalArrayConverter;
import com.tcdng.unify.core.util.json.JsonBigDecimalConverter;
import com.tcdng.unify.core.util.json.JsonBooleanArrayConverter;
import com.tcdng.unify.core.util.json.JsonBooleanConverter;
import com.tcdng.unify.core.util.json.JsonByteArrayConverter;
import com.tcdng.unify.core.util.json.JsonCharacterConverter;
import com.tcdng.unify.core.util.json.JsonDateArrayConverter;
import com.tcdng.unify.core.util.json.JsonDateConverter;
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
 * @author The Code Department
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

    static {
        ConverterUtils.registerConverter(byte[].class, new ByteArrayConverter());
        ConverterUtils.registerConverter(Money.class, new MoneyConverter());
        ConverterUtils.registerConverter(Period.class, new PeriodConverter());
        ConverterUtils.registerConverter(String.class, new StringConverter());
        ConverterUtils.registerConverter(UplElementReferences.class, new UplElementReferencesConverter());
        ConverterUtils.registerConverter(UploadedFile.class, new UploadedFileConverter());
        ConverterUtils.registerConverter(Class.class, new ClassConverter());
        ConverterUtils.registerConverter(IndexedTarget.class, new IndexedTargetConverter());
    }

    private static final Map<Class<?>, Class<? extends Input>> classToInputMap;

    static {
        Map<Class<?>, Class<? extends Input>> map = new HashMap<Class<?>, Class<? extends Input>>();
        map.put(boolean.class, BooleanInput.class);
        map.put(Boolean.class, BooleanInput.class);
        map.put(byte.class, ByteInput.class);
        map.put(Byte.class, ByteInput.class);
        map.put(byte[].class, ByteArrayInput.class);
        map.put(char.class, CharacterInput.class);
        map.put(Character.class, CharacterInput.class);
        map.put(short.class, ShortInput.class);
        map.put(Short.class, ShortInput.class);
        map.put(int.class, IntegerInput.class);
        map.put(Integer.class, IntegerInput.class);
        map.put(long.class, LongInput.class);
        map.put(Long.class, LongInput.class);
        map.put(float.class, FloatInput.class);
        map.put(Float.class, FloatInput.class);
        map.put(double.class, DoubleInput.class);
        map.put(Double.class, DoubleInput.class);
        map.put(BigDecimal.class, BigDecimalInput.class);
        map.put(Date.class, DateInput.class);
        map.put(Money.class, MoneyInput.class);
        map.put(String.class, StringInput.class);
        classToInputMap = Collections.unmodifiableMap(map);
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

    private static final Map<Class<?>, JsonValueConverter> jsonConverterMap;

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
        map.put(Date.class, new JsonDateConverter());
        map.put(Date[].class, new JsonDateArrayConverter());
        jsonConverterMap = Collections.unmodifiableMap(map);
    }

    private DataUtils() {

    }
    
    public static void registerDefaultFormatters(DateTimeFormatter defaultDateTimeFormatter) {
        ConverterUtils.registerDefaultFormatters(defaultDateTimeFormatter);
    }

    public static DateTimeFormatter getDefaultDateTimeFormatter() {
        return (DateTimeFormatter) ConverterUtils.getDefaultDateTimeFormatter();
    }

    /**
     * Checks if two objects are equal.
     * 
     * @param a
     *          the first object
     * @param b
     *          the second object
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
     * Returns the column type of a field.
     * 
     * @param field
     *              the supplied field
     * @return The column type
     * @throws UnifyException
     *                        if field is not annotated with {@link Column}
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
     *              the data type
     * @return the equivalent data type
     * @throws UnifyException
     *                        if data type is unsupported
     */
    public static DataType getDataType(Class<?> clazz) throws UnifyException {
        DataType dataType = classToDataTypeMap.get(clazz);
        if (dataType == null) {
            if (EnumConst.class.isAssignableFrom(clazz)) {
                return DataType.STRING;
            }
            throw new UnifyException(UnifyCoreErrorConstants.RECORD_UNSUPPORTED_PROPERTY_TYPE, clazz);
        }
        return dataType;
    }

    /**
     * Returns the data type equivalence of supplied class name.
     * 
     * @param className
     *                  the class name
     * @return the equivalent data type
     * @throws UnifyException
     *                        if data type is unsupported
     */
    public static DataType getDataType(String className) throws UnifyException {
        return DataUtils.getDataType(ReflectUtils.classForName(className));
    }

    /**
     * Finds the data type equivalence of supplied class.
     * 
     * @param clazz
     *              the data type
     * @return the equivalent data type if found otherwise null
     * @throws UnifyException
     *                        if an error occurs
     */
    public static DataType findDataType(Class<?> clazz) throws UnifyException {
        DataType dataType = classToDataTypeMap.get(clazz);
        if (dataType == null) {
            if (EnumConst.class.isAssignableFrom(clazz)) {
                return DataType.STRING;
            }
        }

        return dataType;
    }

    /**
     * Finds the data type equivalence of supplied class name.
     * 
     * @param className
     *                  the class name
     * @return the equivalent data type if found otherwise null
     * @throws UnifyException
     *                        if an error occurs
     */
    public static DataType findDataType(String className) throws UnifyException {
        return DataUtils.findDataType(ReflectUtils.classForName(className));
    }

    /**
     * Returns the column type equivalence of supplied data type.
     * 
     * @param clazz
     *              the data type
     * @return the equivalent column type
     * @throws UnifyException
     *                        if data type is unsupported
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
     * Checks if supplied column type is mapped to a Java class.
     * 
     * @param type
     *             the column type to check
     * @return true if mapped otherwise false
     */
    public static boolean isMappedColumnType(ColumnType type) {
        if (ColumnType.ENUMCONST.equals(type)) {
            return true;
        }

        if (classToColumnMap.containsValue(type)) {
            return true;
        }

        return false;
    }

    /**
     * Returns the column type equivalence of supplied data type.
     * 
     * @param className
     *                  the data type
     * @return the equivalent column type
     * @throws UnifyException
     *                        if data type is unsupported
     */
    public static ColumnType getColumnType(String className) throws UnifyException {
        Class<?> clazz = ReflectUtils.classForName(className);
        return DataUtils.getColumnType(clazz);
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
     *              the data type
     */
    public static boolean isNumberType(Class<?> clazz) {
        return Number.class.isAssignableFrom(ConverterUtils.getWrapperClass(clazz));
    }

    /**
     * Tests if data type of supplied class name is a number type.
     * 
     * @param className
     *                  the data type class name
     */
    public static boolean isNumberType(String className) {
        try {
            return Number.class.isAssignableFrom(ConverterUtils.getWrapperClass(ReflectUtils.classForName(className)));
        } catch (UnifyException e) {
        }

        return false;
    }

    public static Object getNestedBeanProperty(Object bean, String longPropertyName) throws UnifyException {
        try {
            if (longPropertyName.indexOf('.') >= 0) {
                String[] properties = longPropertyName.split("\\.");
                int len = properties.length - 1;
                int j = 0;
                for (; j < len && bean != null; j++) {
                    bean = ReflectUtils.getGetterInfo(bean.getClass(), properties[j]).getGetter().invoke(bean);
                }

                if (bean != null) {
                    return ReflectUtils.getBeanProperty(bean, properties[j]);
                }
            }

            return ReflectUtils.getBeanProperty(bean, longPropertyName);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                    longPropertyName);
        }
    }

    public static <T> T getNestedBeanProperty(Class<T> targetClazz, Object bean, String longPropertyName)
            throws UnifyException {
        try {
            if (longPropertyName.indexOf('.') >= 0) {
                String[] properties = longPropertyName.split("\\.");
                int len = properties.length - 1;
                int j = 0;
                for (; j < len && bean != null; j++) {
                    bean = ReflectUtils.getGetterInfo(bean.getClass(), properties[j]).getGetter().invoke(bean);
                }

                if (bean != null) {
                    return DataUtils.getBeanProperty(targetClazz, bean, properties[j]);
                }
            }

            return DataUtils.getBeanProperty(targetClazz, bean, longPropertyName);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass(),
                    longPropertyName);
        }
    }

    public static <T> T getBeanProperty(Class<T> targetClazz, Object bean, String propertyName) throws UnifyException {
        try {
            return ConverterUtils.convert(targetClazz, ReflectUtils.getBeanProperty(bean, propertyName));
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.COMPONENT_OPERATION_ERROR, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Input<T> newInput(Class<T> type, String name, String description, String editor,
            boolean mandatory) throws UnifyException {
        Class<? extends Input> inputClass = classToInputMap.get(type);
        return (Input<T>) ReflectUtils.newInstance(inputClass,
                new Class<?>[] { String.class, String.class, String.class, boolean.class }, name, description, editor,
                mandatory);
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
     * Converts value to array of specified type.
     * 
     * @param clazz
     *              the array type
     * @param value
     *              the value to convert
     * @return the conversion result
     * @throws UnifyException
     *                        if an error occurs
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
     *                  the length to split
     * @param blockSize
     *                  the block size
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
     * Sorts a list by order
     * 
     * @param list
     *                  the list to sort
     * @param beanClass
     *                  the bean type
     * @param order
     *                  the sorting order
     * @throws UnifyException
     *                        if an error occurs
     */
	public static <T> void sort(List<?> list, Class<T> beanClass, Order order) throws UnifyException {
		if (order != null) {
			for (Order.Part part : order.getParts()) {
				DataUtils.sort(list, beanClass, part.getField(), part.isAscending());
			}
		}
	}

    /**
     * Sorts a list by bean property ascending.
     * 
     * @param list
     *                  the list to sort
     * @param beanClass
     *                  the bean type
     * @param property
     *                  the bean property to sort with
     * @throws UnifyException
     *                        if an error occurs
     */
    public static <T> void sortAscending(List<?> list, Class<T> beanClass, String property) throws UnifyException {
        DataUtils.sort(list, beanClass, property, true);
    }

    /**
     * Sorts a list by bean property descending.
     * 
     * @param list
     *                  the list to sort
     * @param beanClass
     *                  the bean type
     * @param property
     *                  the bean property to sort with
     * @throws UnifyException
     *                        if an error occurs
     */
    public static <T> void sortDescending(List<?> list, Class<T> beanClass, String property) throws UnifyException {
        DataUtils.sort(list, beanClass, property, false);
    }

    /**
     * Compares two comparable value for sorting ascending.
     * 
     * @param value1
     *               the first value
     * @param value2
     *               the second value
     * @return an integer representing the comparison result
     */
    public static int compareForSortAscending(Comparable<Object> value1, Comparable<Object> value2) {
        return DataUtils.compareForSort(value1, value2, true);
    }

    /**
     * Compares two comparable value for sorting descending.
     * 
     * @param value1
     *               the first value
     * @param value2
     *               the second value
     * @return an integer representing the comparison result
     */
    public static int compareForSortDescending(Comparable<Object> value1, Comparable<Object> value2) {
        return DataUtils.compareForSort(value1, value2, false);
    }

    /**
     * Sets nested bean property by long name.
     * 
     * @param bean
     *                     the bean object
     * @param propertyName
     *                     the nested property long name
     * @param value
     *                     the value to set
     * @throws UnifyException
     *                        if an error occurs
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
     *                         the bean object
     * @param longPropertyName
     *                         the nested property long name
     * @param formatter
     *                         applied before setting value. Can be null
     * @param value
     *                         the value to set
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void setNestedBeanProperty(Object bean, String longPropertyName, Object value, Formatter<?> formatter)
            throws UnifyException {
        try {
            if (longPropertyName.indexOf('.') >= 0) {
                String[] properties = longPropertyName.split("\\.");
                int len = properties.length - 1;
                int j = 0;
                for (; j < len && bean != null; j++) {
                    bean = ReflectUtils.getGetterInfo(bean.getClass(), properties[j]).getGetter().invoke(bean);
                }

                if (bean != null) {
                    DataUtils.setBeanProperty(bean, properties[j], value, formatter);
                }
            } else {
                DataUtils.setBeanProperty(bean, longPropertyName, value, formatter);
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
     *                 the bean to populate
     * @param valueMap
     *                 the values for population by property names
     * @throws UnifyException
     *                        if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static void populateBean(Object bean, Map<String, Object> valueMap) throws UnifyException {
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            try {
                GetterSetterInfo setterInfo = ReflectUtils.getSetterInfo(bean.getClass(), entry.getKey());
                Class<?> type = setterInfo.getType();
                Object value = entry.getValue();
                if (setterInfo.isParameterArgumented0()) {
                    value = ConverterUtils.convert((Class<? extends Collection>) type, setterInfo.getArgumentType0(), value);
                } else {
                    value = ConverterUtils.convert(type, value);
                    if (value == null) {
                        value = ConverterUtils.getNullValue(type);
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
     *                     the bean object
     * @param propertyName
     *                     the property to set
     * @param value
     *                     the value to set
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void setBeanProperty(Object bean, String propertyName, Object value) throws UnifyException {
        setBeanProperty(bean, propertyName, value, null);
    }

    /**
     * Sets the value of the property of a bean using supplied formatter.
     * 
     * @param bean
     *                     the bean object
     * @param propertyName
     *                     the property to set
     * @param value
     *                     the value to set
     * @param              formatter,
     *                     if any, applied before setting value
     * @throws UnifyException
     *                        if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static void setBeanProperty(Object bean, String propertyName, Object value, Formatter<?> formatter)
            throws UnifyException {
        try {
            GetterSetterInfo setterInfo = ReflectUtils.getSetterInfo(bean.getClass(), propertyName);
            Class<?> type = setterInfo.getType();
            if (!Object.class.equals(type)) {
                if (setterInfo.isParameterArgumented0()) {
                    value = ConverterUtils.convert((Class<? extends Collection>) type, setterInfo.getArgumentType0(), value,
                            formatter);
                } else {
                    value = ConverterUtils.convert(type, value, formatter);
                    if (value == null) {
                        value = ConverterUtils.getNullValue(type);
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
     * Clears all the properties if a bean.
     * 
     * @param bean
     *             the bean object
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void clearAllBeanProperties(Object bean) throws UnifyException {
        if (bean != null) {
            try {
                for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterMap(bean.getClass()).values()) {
                    if (getterSetterInfo.isSetter()) {
                        Object nullVal = ConverterUtils.getNullValue(getterSetterInfo.getType());
                        getterSetterInfo.getSetter().invoke(bean, nullVal);
                    }
                }
            } catch (UnifyException e) {
                throw e;
            } catch (Exception e) {
                throw new UnifyException(e, UnifyCoreErrorConstants.REFLECT_REFLECTION_ERROR, bean.getClass());
            }
        }
    }
   
    /**
     * Reads a JSON array.
     * 
     * @param type
     *               the data type
     * @param json
     *               the JSON array string
     * @return array
     * @throws UnifyException
     *                        if an error occurs
     */
    public static <T> T[] arrayFromJsonString(Class<T[]> type, String json) throws UnifyException {
        return DataUtils.arrayFromJsonReader(type, new StringReader(json));
    }

    /**
     * Reads a JSON array.
     * 
     * @param type
     *               the data type
     * @param reader
     *               the JSON reader
     * @return array
     * @throws UnifyException
     *                        if an error occurs
     */
    public static <T> T[] arrayFromJsonReader(Class<T[]> type, Reader reader) throws UnifyException {
        try {
          return (T[]) DataUtils.fromJsonReader(type, reader);
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
     *               bean to read to
     * @param json
     *               the JSON string
     * @throws UnifyException
     *                        if an error occurs
     */
    public static <T> T  fromJsonString(Class<T> clazz, String json) throws UnifyException {
        return DataUtils.fromJsonReader(clazz, new StringReader(json));
    }

    /**
     * Reads a JSON object. Has no support for collections.
     * 
     * @param object
     *                    bean to read to
     * @param inputStream
     *                    the JSON input stream
     * @param charset
     *                    optional character set
     * @throws UnifyException
     *                        if an error occurs
     */
    public static <T> T fromJsonInputStream(Class<T> clazz, InputStream inputStream, Charset charset) throws UnifyException {
        if (charset == null) {
            return DataUtils.fromJsonReader(clazz, new BufferedReader(new InputStreamReader(inputStream)));
        }

        return DataUtils.fromJsonReader(clazz, new BufferedReader(new InputStreamReader(inputStream, charset)));
    }

    /**
     * Reads a JSON object. Has no support for collections.
     * 
     * @param clazz
     *               bean type
     * @param reader
     *               the JSON reader
     * @throws UnifyException
     *                        if an error occurs
     */
    public static <T> T fromJsonReader(Class<T> clazz, Reader reader) throws UnifyException {
        try {
            return DataUtils.getObjectFromJsonValue(clazz, null, Json.parse(reader));
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T getObjectFromJsonValue(Class<T> clazz, Class<?> argClass, JsonValue jsonValue) throws Exception{
        if (jsonValue == null || jsonValue.isNull() ) {
            return (T) ConverterUtils.getNullValue(clazz);
        }
        
        JsonValueConverter<T> converter = jsonConverterMap.get(clazz);
        if (converter != null) {
            return converter.read(clazz, jsonValue);
        }
        
        if (clazz.isEnum()) {
            Class<? extends Enum> enumClass = (Class<? extends Enum>) clazz;
            try {
                return (T) Enum.valueOf(enumClass, jsonValue.asString());
            } catch (Exception e) {
            }
            
            return null;
        }
        
        // Array
        if(clazz.isArray()) {
            Class<?> compClass = clazz.getComponentType();
            if (jsonValue.isArray()) {
                JsonArray jsonArray =  jsonValue.asArray();
                int len = jsonArray.size();
                Object arr = Array.newInstance(compClass, len);
                for(int i = 0; i < len; i++) {
                    Array.set(arr, i, DataUtils.getObjectFromJsonValue(compClass, null, jsonArray.get(i)));
                }
                
                return (T) arr;
            }

            Object arr = Array.newInstance(compClass, 1);
            Array.set(arr, 0, DataUtils.getObjectFromJsonValue(compClass, null, jsonValue));
            
            return (T) arr;
        }
        
        // Collection
        if (Collection.class.isAssignableFrom(clazz)) {
            Collection<Object> result = ReflectUtils.newInstance(
                    ConverterUtils.getCollectionConcreteType((Class<? extends Collection>) clazz));
            if (jsonValue.isArray()) {
                JsonArray jsonArray =  jsonValue.asArray();
                int len = jsonArray.size();
                for(int i = 0; i < len; i++) {
                    result.add(DataUtils.getObjectFromJsonValue(argClass, null, jsonArray.get(i)));
                }
            } else {
                result.add(DataUtils.getObjectFromJsonValue(argClass, null, jsonValue));
            }
            
            return (T) result;
        }
        
        // Map TODO
        
        // Bean
        JsonObject jsonObject = jsonValue.asObject();
        T bean = ReflectUtils.newInstance(clazz);
        if(jsonObject.size() > 0) {
            Map<String, GetterSetterInfo> accessors = ReflectUtils.getGetterSetterMap(clazz);
            for (String name : accessors.keySet()) {
                GetterSetterInfo gInfo = accessors.get(name);
                if (!gInfo.isSetter() || !gInfo.isProperty()) {
                    continue;
                }
                
                JsonValue jsonVal = jsonObject.get(name);
                Class<?> type = gInfo.getType();
                if (Object.class.equals(type)) {
                    Object inst = gInfo.getGetter().invoke(bean);
                    if (inst != null) {
                        type = inst.getClass();
                    }
                }
                
                Object val = getObjectFromJsonValue(type, gInfo.getArgumentType0(), jsonVal);
                gInfo.getSetter().invoke(bean, val);
            }
        }
        
        return bean;
    }

    /**
     * Writes a JSON object to an output stream. Has no support for collections.
     * 
     * @param object
     *                     the object to write
     * @param outputStream
     *                     the output stream to write to
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void writeJsonObject(Object object, OutputStream outputStream) throws UnifyException {
        writeJsonObject(object, outputStream, null, PrintFormat.NONE);
    }

    /**
     * Writes a JSON object to an output stream. Has no support for collections.
     * 
     * @param object
     *                     the object to write
     * @param outputStream
     *                     the output stream to write to
     * @param printFormat
     *                     formatting type
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void writeJsonObject(Object object, OutputStream outputStream, PrintFormat printFormat)
            throws UnifyException {
        writeJsonObject(object, outputStream, null, printFormat);
    }

    /**
     * Writes a JSON object to an output stream. Has no support for collections.
     * 
     * @param object
     *                     the object to write
     * @param outputStream
     *                     the output stream to write to
     * @param charset
     *                     optional character set
     * @param printFormat
     *                     formatting type
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void writeJsonObject(Object object, OutputStream outputStream, Charset charset,
            PrintFormat printFormat) throws UnifyException {
        if (charset == null) {
            DataUtils.writeJsonObject(object, new BufferedWriter(new OutputStreamWriter(outputStream)), printFormat);
            return;
        }

        DataUtils.writeJsonObject(object, new BufferedWriter(new OutputStreamWriter(outputStream, charset)),
                printFormat);
    }

    /**
     * Writes a JSON object to a writer. Has no support for collections.
     * 
     * @param object
     *                    the object to write
     * @param writer
     *                    the writer to write to
     * @param printFormat
     *                    formatting type
     * @throws UnifyException
     *                        if an error occurs
     */
    public static void writeJsonObject(Object object, Writer writer, PrintFormat printFormat) throws UnifyException {
        try {
            JsonValue jsonValue = DataUtils.getJsonValueFromObject(object);
            switch (printFormat) {
                case PRETTY:
                    jsonValue.writeTo(writer, PrettyPrint.PRETTY_PRINT);
                    break;
                case NONE:
                default:
                    jsonValue.writeTo(writer);
                    break;
            }
            writer.flush();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    public static String asJsonString(Object obj, PrintFormat format) throws UnifyException {
        try {
            StringWriter writer = new StringWriter();
            DataUtils.writeJsonObject(obj, writer, format);
            return writer.toString();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    private static JsonValue getJsonValueFromObject(Object obj) throws Exception{
        if (obj == null) {
            return Json.NULL;
        }
        
        JsonValueConverter<?> converter = jsonConverterMap.get(obj.getClass());
        if (converter != null) {
            return converter.write(obj);
        }
        
        if (obj.getClass().isEnum()) {            
            return Json.value(((Enum) obj).name());
        }
        
        // Array
        if(obj.getClass().isArray()) {
            JsonArray array = Json.array();
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                array.add(getJsonValueFromObject(Array.get(obj, i)));
            }
            
            return array;
        }
        
        // Collection
        if (Collection.class.isAssignableFrom(obj.getClass())) {
            JsonArray jsonArray = Json.array();
            for (Object val : (Collection<?>) obj) {
                jsonArray.add(getJsonValueFromObject(val));
            }
            
            return jsonArray;
        }
        
        // Map TODO
        
        // Bean
        JsonObject jsonObject = Json.object();
        Map<String, GetterSetterInfo> accessors = ReflectUtils.getGetterSetterMap(obj.getClass());
        for (String name : accessors.keySet()) {
            GetterSetterInfo gInfo = accessors.get(name);
            if (!gInfo.isGetter() || !gInfo.isProperty()) {
                continue;
            }
            
            Object val = gInfo.getGetter().invoke(obj);
            jsonObject.add(name, getJsonValueFromObject(val));
        }
        
        return jsonObject;
    }

    public static <T> String asJsonArrayString(T[] array) throws UnifyException {
        try {
            JsonValueConverter<?> converter = jsonConverterMap.get(array.getClass());
            return converter.write(array).toString();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }
    
    public static <T> T[] toArray(Class<T> clazz, Set<T> set) {
    	return DataUtils.toArray(clazz, (List<T>) (set != null ? new ArrayList<T>(set) : null));
    }


    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Class<T> clazz, List<T> list) {
        if (list != null) {
            return list.toArray((T[]) Array.newInstance(clazz, list.size()));
        }

        return null;
    }

    /**
     * Returns true if supplied array is null or is empty.
     * 
     * @param arr
     *            the array to check
     */
    public static <T> boolean isBlank(T[] arr) {
        return arr == null || arr.length == 0;
    }

    /**
     * Returns true if supplied array is not null and is not empty.
     * 
     * @param arr
     *            the array to check
     */
    public static <T> boolean isNotBlank(T[] arr) {
        return arr != null && arr.length != 0;
    }

    /**
     * Returns true if supplied collection is null or is empty.
     * 
     * @param col
     *            the collection to check
     */
    public static boolean isBlank(Collection<?> col) {
        return col == null || col.isEmpty();
    }

    /**
     * Returns true if supplied collection is not null and is not empty.
     * 
     * @param col
     *            the collection to check
     */
    public static boolean isNotBlank(Collection<?> col) {
        return col != null && !col.isEmpty();
    }

    /**
     * Returns true if supplied map is null or is empty.
     * 
     * @param map
     *            the map to check
     */
    public static boolean isBlank(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Returns true if supplied map is not null and is not empty.
     * 
     * @param map
     *            the map to check
     */
    public static boolean isNotBlank(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static <T> List<T> unmodifiableList(List<T> list) {
        if (isBlank(list)) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(list);
    }

    public static <T> List<T> unmodifiableList(Collection<T> collection) {
        if (isBlank(collection)) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(new ArrayList<T>(collection));
    }

    public static <T> List<T> unmodifiableList(T[] list) {
        if (list == null || list.length == 0) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(Arrays.asList(list));
    }

    public static <T> List<T> unmodifiableValuesList(Map<?, T> map) {
        if (isBlank(map)) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(new ArrayList<T>(map.values()));
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
     * Converts supplied value to target type.
     * 
     * @param targetClazz
     *                    the target type
     * @param value
     *                    the value to convert
     * @return the converted value
     * @throws UnifyException
     *                   if an error occurs
     */
    public static <T> T convert(Class<T> targetClazz, Object value) throws UnifyException {
        try {
            return ConverterUtils.convert(targetClazz, value);
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    /**
     * Converts supplied value to target type.
     * 
     * @param targetClazz
     *                    the target type
     * @param value
     *                    the value to convert
     * @param formatter
     *                    the conversion formatter. Can be null
     * @return the converted value
     * @throws UnifyException
     *                   if an error occurs
     */
    public static <T> T convert(Class<T> targetClazz, Object value, ConverterFormatter<?> formatter) throws UnifyException {
        try {
            return ConverterUtils.convert(targetClazz, value, formatter);
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    /**
     * Converts supplied value to target collection type.
     * 
     * @param collectionClazz
     *                        the collection type
     * @param dataClass
     *                        the collection data type
     * @param val
     *                        the value to convert
     * @return the converted value
     * @throws UnifyException
     *                   if an error occurs
     */
    public static <T, U extends Collection<T>> U convert(Class<U> collectionClazz, Class<T> dataClass, Object val)
            throws UnifyException {
        try {
            return ConverterUtils.convert(collectionClazz, dataClass, val);
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    /**
     * Converts supplied value to target collection type.
     * 
     * @param collectionClazz
     *                        the collection type
     * @param dataClass
     *                        the collection data type
     * @param val
     *                        the value to convert
     * @param formatter
     *                        the conversion formatter. Can be null
     * @return the converted value
     * @throws UnifyException
     *                   if an error occurs
     */
    public static <T, U extends Collection<T>> U convert(Class<U> collectionClazz, Class<T> dataClass, Object val,
            ConverterFormatter<?> formatter) throws UnifyException {
        try {
            return ConverterUtils.convert(collectionClazz, dataClass, val, formatter);
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }
    }

    /**
     * Creates a new instance of a data object populated with supplied parameters.
     * 
     * @param type
     *               the data object type
     * @param params
     *               the parameters to construct the data object
     * @return the new instance of data object except for {@link ZeroParams} type
     *         where the same instance is always returned
     * @throws UnifyException
     *                        if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T constructDataObject(Class<T> type, Object... params) throws UnifyException {
        T paramObject = null;
        try {
            if (ZeroParams.class.equals(type)) {
                paramObject = (T) ZeroParams.INSTANCE;
            } else {
                List<Class<?>> paramTypeList = ReflectUtils.getLargestConstructorParameters(type);
                int len = paramTypeList.size();
                if (len == 1 && paramTypeList.get(0).isArray()) {
                    Object initParam = ConverterUtils.convert(paramTypeList.get(0), params);
                    paramObject = ReflectUtils.newInstanceFromLargestConstructor(type, initParam);
                } else {
                    // Fit parameters
                    Object[] initParam = new Object[len];
                    for (int i = 0; i < len && i < params.length; i++) {
                        initParam[i] = ConverterUtils.convert(paramTypeList.get(i), params[i]);
                    }

                    paramObject = ReflectUtils.newInstanceFromLargestConstructor(type, initParam);
                }
            }
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.DATAUTIL_ERROR, e);
        }

        return paramObject;
    }

	/**
	 * Removes duplicates from a list (unordered)
	 * 
	 * @param list the list
	 * @return new list with duplicates removed
	 */
	public static List<String> removeDuplicatesUnordered(List<String> list) {
		if (list != null) {
			return new ArrayList<String>(new HashSet<String>(list));
		}

		return list;
	}

	/**
	 * Removes duplicates from a list (ordered)
	 * 
	 * @param list the list
	 * @return new list with duplicates removed
	 */
	public static List<String> removeDuplicatesOrdered(List<String> list) {
		if (list != null) {
			return new ArrayList<String>(new LinkedHashSet<String>(list));
		}

		return list;
	}

	/**
	 * Adds two decimal numbers with null values replace with zero
	 * 
	 * @param first  first number
	 * @param second second number
	 * @return
	 */
	public static BigDecimal add(BigDecimal first, BigDecimal second) {
		if (first != null) {
			if (second != null) {
				return first.add(second);
			}

			return first;
		}

		return second != null ? second : BigDecimal.ZERO;
	}
   
    @SuppressWarnings("unchecked")
    private static <T> void sort(List<?> list, Class<T> beanClass, String property, boolean ascending)
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

    private static int compareForSort(Comparable<Object> value1, Comparable<Object> value2, boolean ascending) {
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

    private static final Map<String, Comparator<?>> comparatorMap = new HashMap<String, Comparator<?>>();

    private static final Object COMPARATORMAP_LOCK = new Object();

    private static class ObjectByFieldComparator<T> implements Comparator<T> {

        private Method getter;

        private boolean ascending;

        public ObjectByFieldComparator(Method getter, boolean ascending) {
            this.getter = getter;
            this.ascending = ascending;
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
}
