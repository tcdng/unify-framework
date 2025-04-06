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
package com.tcdng.unify.convert.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

import com.tcdng.unify.common.constants.EnumConst;
import com.tcdng.unify.common.util.EnumUtils;
import com.tcdng.unify.convert.converters.BigDecimalConverter;
import com.tcdng.unify.convert.converters.BooleanConverter;
import com.tcdng.unify.convert.converters.ByteArrayConverter;
import com.tcdng.unify.convert.converters.ByteConverter;
import com.tcdng.unify.convert.converters.CharacterConverter;
import com.tcdng.unify.convert.converters.Converter;
import com.tcdng.unify.convert.converters.ConverterFormatter;
import com.tcdng.unify.convert.converters.DateConverter;
import com.tcdng.unify.convert.converters.DoubleConverter;
import com.tcdng.unify.convert.converters.FloatConverter;
import com.tcdng.unify.convert.converters.IntegerConverter;
import com.tcdng.unify.convert.converters.JodaLocalDateConverter;
import com.tcdng.unify.convert.converters.JodaLocalDateTimeConverter;
import com.tcdng.unify.convert.converters.LocalDateConverter;
import com.tcdng.unify.convert.converters.LocalDateTimeConverter;
import com.tcdng.unify.convert.converters.LongConverter;
import com.tcdng.unify.convert.converters.ShortConverter;
import com.tcdng.unify.convert.converters.StringConverter;

/**
 * Conversion utility methods for data manipulation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public final class ConverterUtils {

	public static final Object[] ZEROLEN_OBJECT_ARRAY = new Object[0];

	private static ConverterFormatter<?> defaultDateTimeFormatter;

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
        map.put(org.joda.time.LocalDate.class, new JodaLocalDateConverter());
        map.put(org.joda.time.LocalDateTime.class, new JodaLocalDateTimeConverter());
        map.put(java.time.LocalDate.class, new LocalDateConverter());
        map.put(java.time.LocalDateTime.class, new LocalDateTimeConverter());
		map.put(String.class, new StringConverter());
		classToConverterMap = map; 
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

	private static final Map<Class<? extends Collection>, Class<? extends Collection>> collectionInterfaceToClassMap;

	static {
		Map<Class<? extends Collection>, Class<? extends Collection>> map = new HashMap<Class<? extends Collection>, Class<? extends Collection>>();
		map.put(Collection.class, ArrayList.class);
		map.put(List.class, ArrayList.class);
		map.put(Set.class, HashSet.class);
		map.put(Queue.class, LinkedList.class);
		map.put(Deque.class, LinkedList.class);
		collectionInterfaceToClassMap = Collections.unmodifiableMap(map);
	}

	private ConverterUtils() {

	}

	public static void registerConverter(Class<?> clazz, Converter<?> converter) {
		classToConverterMap.put(clazz, converter);
	}

	public static void registerDefaultFormatters(ConverterFormatter defaultDateTimeFormatter) {
		ConverterUtils.defaultDateTimeFormatter = defaultDateTimeFormatter;
	}

	public static ConverterFormatter getDefaultDateTimeFormatter() {
		return defaultDateTimeFormatter;
	}

	/**
	 * Converts supplied value to target type.
	 * 
	 * @param targetClazz the target type
	 * @param value       the value to convert
	 * @return the converted value
	 * @throws Exception if an error occurs
	 */
	public static <T> T convert(Class<T> targetClazz, Object value) throws Exception {
		return ConverterUtils.convert(targetClazz, value, null);
	}

	/**
	 * Converts supplied value to target type.
	 * 
	 * @param targetClazz the target type
	 * @param value       the value to convert
	 * @param formatter   the conversion formatter. Can be null
	 * @return the converted value
	 * @throws Exception if an error occurs
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convert(Class<T> targetClazz, Object value, ConverterFormatter<?> formatter) throws Exception {
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

			Object values = ConverterUtils.getValueObjectArray(value, formatter);
			if (targetClazz.isAssignableFrom(values.getClass())) {
				return (T) values;
			}

			Class<?> targetClass = targetClazz.getComponentType();
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
			Object values = ConverterUtils.getValueObjectArray(value, formatter);
			int length = Array.getLength(values);
			Collection<Object> result = ConverterUtils
					.getCollectionConcreteType((Class<? extends Collection>) targetClazz).newInstance();
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
			} else if (targetClazz.isEnum()) {
				if (value instanceof String) {
					Class<? extends Enum> enumClass = (Class<? extends Enum>) targetClazz;
					result = Enum.valueOf(enumClass, (String) value);
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
	}

	/**
	 * Converts supplied value to target collection type.
	 * 
	 * @param collectionClazz the collection type
	 * @param dataClass       the collection data type
	 * @param val             the value to convert
	 * @return the converted value
	 * @throws Exception if an error occurs
	 */
	public static <T, U extends Collection<T>> U convert(Class<U> collectionClazz, Class<T> dataClass, Object val)
			throws Exception {
		return ConverterUtils.convert(collectionClazz, dataClass, val, null);
	}

	/**
	 * Converts supplied value to target collection type.
	 * 
	 * @param collectionClazz the collection type
	 * @param dataClass       the collection data type
	 * @param val             the value to convert
	 * @param formatter       the conversion formatter. Can be null
	 * @return the converted value
	 * @throws Exception if an error occurs
	 */
	@SuppressWarnings("unchecked")
	public static <T, U extends Collection<T>> U convert(Class<U> collectionClazz, Class<T> dataClass, Object val,
			ConverterFormatter<?> formatter) throws Exception {
		if (val == null) {
			return null;
		}

		Class<?> valClass = val.getClass();
		if (collectionClazz.isAssignableFrom(valClass)) {
			Collection collection = (Collection) val;
			if (!collection.isEmpty()) {
				Iterator it = collection.iterator();
				while (it.hasNext()) {
					Object elem = it.next();
					if (elem != null) {
						if (dataClass.isAssignableFrom(elem.getClass())) {
							return (U) val;
						} else {
							break;
						}
					}
				}
			} else {
				return (U) val;
			}
		}

		if (val instanceof String) {
			List<String> list = Arrays.asList(((String) val).split(","));
			if (String.class.equals(dataClass)) {
				return (U) list;
			}

			val = list;
			valClass = val.getClass();
		}

		Collection<Object> result = ConverterUtils.getCollectionConcreteType(collectionClazz).newInstance();
		if (valClass.isArray()) {
			int length = Array.getLength(val);
			if (dataClass.isAssignableFrom(ConverterUtils.getWrapperClass(valClass.getComponentType()))) {
				for (int i = 0; i < length; i++) {
					result.add(Array.get(val, i));
				}
			} else {
				if (EnumConst.class.isAssignableFrom(dataClass)) {
					for (int i = 0; i < length; i++) {
						result.add(EnumUtils.fromCode((Class<? extends EnumConst>) dataClass,
								String.valueOf(Array.get(val, i))));
					}
				} else if (dataClass.isEnum()) {
					Class<? extends Enum> enumClass = (Class<? extends Enum>) dataClass;
					for (int i = 0; i < length; i++) {
						result.add(Enum.valueOf(enumClass, String.valueOf(Array.get(val, i))));
					}
				} else {
					Converter<?> converter = classToConverterMap.get(dataClass);
					if (converter != null) {
						for (int i = 0; i < length; i++) {
							result.add(converter.convert(Array.get(val, i), formatter));
						}
					} else {
						for (int i = 0; i < length; i++) {
							result.add(Array.get(val, i));
						}
					}
				}
			}
		} else if (Collection.class.isAssignableFrom(valClass)) {
			if (EnumConst.class.isAssignableFrom(dataClass)) {
				for (Object object : (Collection<Object>) val) {
					result.add(EnumUtils.fromCode((Class<? extends EnumConst>) dataClass, String.valueOf(object)));
				}
			} else if (dataClass.isEnum()) {
				Class<? extends Enum> enumClass = (Class<? extends Enum>) dataClass;
				for (Object _val : (Collection<Object>) val) {
					if (_val instanceof String) {
						result.add(Enum.valueOf(enumClass, (String) _val));
					}
				}
			} else {
				Converter<?> converter = classToConverterMap.get(dataClass);
				if (converter != null) {
					for (Object object : (Collection<Object>) val) {
						result.add(converter.convert(object, formatter));
					}
				} else {
					for (Object object : (Collection<Object>) val) {
						result.add(object);
					}
				}
			}
		} else {
			if (EnumConst.class.isAssignableFrom(dataClass)) {
				Object resultItem = EnumUtils.fromCode((Class<? extends EnumConst>) dataClass, String.valueOf(val));
				if (resultItem == null) {
					resultItem = EnumUtils.fromName((Class<? extends EnumConst>) dataClass, String.valueOf(val));
				}

				result.add(resultItem);
			} else if (dataClass.isEnum()) {
				Class<? extends Enum> enumClass = (Class<? extends Enum>) dataClass;
				result.add(Enum.valueOf(enumClass, String.valueOf(val)));
			} else {
				Converter<?> converter = classToConverterMap.get(dataClass);
				if (converter != null) {
					result.add(converter.convert(val, formatter));
				} else {
					result.add(val);
				}
			}
		}
		return (U) result;
	}

	/**
	 * Returns the wrapper class for a data type.
	 * 
	 * @param clazz the data type
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
	 * @param clazz the data type
	 * @return the name of the wrapper class
	 */
	public static String getWrapperClassName(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return classToWrapperMap.get(clazz).getName();
		}
		return clazz.getName();
	}

	/**
	 * Gets the null value of a data type.
	 * 
	 * @param clazz the data type
	 * @return the null value
	 */
	public static Object getNullValue(Class<?> clazz) {
		return classToNullMap.get(clazz);
	}

	/**
	 * Returns a concrete type for a collection type.
	 * 
	 * @param clazz the collection type
	 * @throws Exception if an error occurs
	 */
	public static Class<? extends Collection> getCollectionConcreteType(Class<? extends Collection> clazz)
			throws Exception {
		Class<? extends Collection> result = clazz;
		if (clazz.isInterface()) {
			result = collectionInterfaceToClassMap.get(clazz);
			if (result == null) {
				throw new Exception("Unsupported collection type [" + clazz + "]");
			}
		}
		return result;
	}

	public static Date getFromJodaLocalDate(org.joda.time.LocalDate date) {
    	return date.toDate();
    }
    
	public static Date getFromJodaLocalDateTime(org.joda.time.LocalDateTime date) {
    	return date.toDate();
    }
   
	public static Date getFromLocalDate(LocalDate date) {
    	return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
	public static Date getFromLocalDateTime(LocalDateTime date) {
    	return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }
	
	public static org.joda.time.LocalDate getJodaLocalDate(Date date) {
    	return new org.joda.time.LocalDate(date);
    }
    
	public static org.joda.time.LocalDateTime getJodaLocalDateTime(Date date) {
    	return new org.joda.time.LocalDateTime(date);
    }
   
	public static LocalDate getLocalDate(Date date) {
    	return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
	public static LocalDateTime getLocalDateTime(Date date) {
    	return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

	private static Object getValueObjectArray(Object value, ConverterFormatter<?> formatter) throws Exception {
		if (value.getClass().isArray()) {
			return value;
		}

		if (value instanceof String) {
			return formatter != null && formatter.isArrayFormat() ? formatter.parse((String) value)
					: ((String) value).split(",");
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
	}

}
