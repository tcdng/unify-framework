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
package com.tcdng.unify.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.PackableDocConfig.FieldConfig;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.GetterSetterInfo;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * A packable document.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PackableDoc implements Serializable {

    private static final long serialVersionUID = 8352270606932913113L;

    public transient static final String RESERVED_EXT_FIELD = "resrvExt";

    private Object id;

    private Map<String, Object> values;

    private transient PackableDocConfig config;

    private transient Map<String, Object> oldValues;

    private transient Object resrvExt;

    private transient boolean auditable;

    private transient boolean updated;

    public PackableDoc(PackableDocConfig config) {
        this(config, false);
    }

    public PackableDoc(PackableDocConfig config, boolean auditable) {
        this(config, new HashMap<String, Object>(), auditable);
    }

    private PackableDoc(PackableDocConfig config, Map<String, Object> values, boolean auditable) {
        this.values = values;
        construct(config, auditable);
    }

    public static PackableDoc unpack(PackableDocConfig config, byte[] packedDoc) throws UnifyException {
        return PackableDoc.unpack(config, packedDoc, false);
    }

    public static PackableDoc unpack(PackableDocConfig config, byte[] packedDoc, boolean auditable)
            throws UnifyException {
        PackableDoc pd = IOUtils.streamFromBytes(PackableDoc.class, packedDoc);
        pd.construct(config, auditable);
        return pd;
    }

    public byte[] pack() throws UnifyException {
        return IOUtils.streamToBytes(this);
    }

    public String getConfigName() {
        return config.getName();
    }

    public Object read(String fieldName) throws UnifyException {
        if (RESERVED_EXT_FIELD.equals(fieldName)) {
            return resrvExt;
        }

        config.getFieldConfig(fieldName);
        return values.get(fieldName);
    }

    @SuppressWarnings("unchecked")
    public <T> Object read(Class<T> type, String fieldName) throws UnifyException {
        Object val = read(fieldName);
        if (val != null && !RESERVED_EXT_FIELD.equals(fieldName)) {
            FieldConfig fc = config.getFieldConfig(fieldName);
            if (fc.isList()) {
                List<T> valList = DataUtils.getNewArrayList(type);
                for (Object aVal : (List<Object>) val) {
                    valList.add(convertTo(type, fc, aVal));
                }

                return valList;
            }

            return convertTo(type, fc, val);
        }

        return val;
    }

    public void readFrom(Object bean) throws UnifyException {
        if (bean == null) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_CANT_READ_FROM_NULL);
        }

        BeanMappingConfig beanMappingConfig = config.getBeanMapping(bean.getClass());
        if (!beanMappingConfig.getBeanClass().isAssignableFrom(bean.getClass())) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_INCOMPATIBLE_BEANCONFIG, config.getName(),
                    bean.getClass());
        }

        for (String beanProperty : beanMappingConfig.getBeanProperties()) {
            write(beanMappingConfig.getMappedField(beanProperty),
                    ReflectUtils.getNestedBeanProperty(bean, beanProperty), null);
        }
    }

    public void write(String fieldName, Object val) throws UnifyException {
        write(fieldName, val, null);
    }

    @SuppressWarnings("unchecked")
    public void write(String fieldName, Object val, Formatter<?> formatter) throws UnifyException {
        if (RESERVED_EXT_FIELD.equals(fieldName)) {
            resrvExt = val;
            return;
        }

        FieldConfig fc = config.getFieldConfig(fieldName);
        if (val != null) {
            if (fc.isList()) {
                List<?> valList = null;
                if (fc.isComplex()) {
                    valList = DataUtils.getNewArrayList(PackableDoc.class);
                } else {
                    valList = DataUtils.getNewArrayList(fc.getDataType());
                }

                for (Object aVal : (List<Object>) val) {
                    ((List<Object>) valList).add(convertFrom(fc, aVal, formatter));
                }

                val = valList;
            } else {
                val = convertFrom(fc, val, formatter);
            }
        }

        values.put(fieldName, val);
        updated = true;
    }

    public void writeTo(Object bean) throws UnifyException {
        if (bean == null) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_CANT_WRITE_TO_NULL);
        }

        BeanMappingConfig beanMappingConfig = config.getBeanMapping(bean.getClass());
        if (!beanMappingConfig.getBeanClass().isAssignableFrom(bean.getClass())) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_INCOMPATIBLE_BEANCONFIG, config.getName(),
                    bean.getClass());
        }

        for (String beanProperty : beanMappingConfig.getBeanProperties()) {
            FieldConfig fc = config.getFieldConfig(beanMappingConfig.getMappedField(beanProperty));
            GetterSetterInfo gsInfo = ReflectUtils.getGetterSetterInfo(bean.getClass(), beanProperty);
            Class<?> type = gsInfo.getType();
            if (gsInfo.isParameterArgumented()) {
                type = gsInfo.getArgumentType();
            }

            DataUtils.setNestedBeanProperty(bean, beanProperty, read(type, fc.getFieldName()), null);
        }
    }

    public PackableDocAudit audit() throws UnifyException {
        if (!auditable) {
            throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_PACKABLE_NOT_AUDITABLE);
        }

        List<PackableDocAudit.TrailItem> items = new ArrayList<PackableDocAudit.TrailItem>();
        for (String fieldName : config.getFieldNames()) {
            Object oldVal = oldValues.get(fieldName);
            Object val = values.get(fieldName);
            if (oldVal == val) {
                continue;
            }

            if (oldVal != null) {
                if (oldVal.equals(val)) {
                    continue;
                }
            }

            items.add(new PackableDocAudit.TrailItem(fieldName, oldVal, val));
        }

        return new PackableDocAudit(items);
    }

    public PackableDocConfig getConfig() {
        return config;
    }

    public boolean isField(String fieldName) {
        return PackableDoc.RESERVED_EXT_FIELD.equals(fieldName) || config.getFieldNames().contains(fieldName);
    }

    public int getFieldCount() {
        return config.getFieldCount();
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getResrvExt() {
        return resrvExt;
    }

    public void setResrvExt(Object resrvExt) {
        this.resrvExt = resrvExt;
    }

    public boolean isAuditable() {
        return auditable;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void clearUpdated() {
        this.updated = false;
    }

    private void construct(PackableDocConfig config, boolean auditable) {
        this.config = config;
        this.auditable = auditable;

        for (FieldConfig fc : config.getFieldConfigs()) {
            if (!values.containsKey(fc.getFieldName())) {
                if (fc.isList()) {
                    if (fc.isComplex()) {
                        values.put(fc.getFieldName(), DataUtils.getNewArrayList(PackableDoc.class));
                    } else {
                        values.put(fc.getFieldName(), DataUtils.getNewArrayList(fc.getDataType()));
                    }
                } else {
                    if (fc.isComplex()) {
                        values.put(fc.getFieldName(), new PackableDoc(fc.getPackableDocConfig(), auditable));
                    } else {
                        values.put(fc.getFieldName(), null);
                    }
                }
            }
        }

        if (auditable) {
            oldValues = new HashMap<String, Object>(values);
        }

        updated = false;
    }

    private <T> T convertTo(Class<T> type, FieldConfig fc, Object val) throws UnifyException {
        if (fc.isComplex()) {
            T bean = ReflectUtils.newInstance(type);
            ((PackableDoc) val).writeTo(bean);
            return bean;
        }

        return DataUtils.convert(type, val, null);
    }

    private Object convertFrom(FieldConfig fc, Object val, Formatter<?> formatter) throws UnifyException {
        if (fc.isComplex()) {
            PackableDoc pd = new PackableDoc(fc.getPackableDocConfig(), auditable);
            pd.readFrom(val);
            return pd;
        }

        return DataUtils.convert(fc.getDataType(), val, formatter);
    }
}
