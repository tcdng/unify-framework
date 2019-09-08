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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.PackableDocConfig.BeanConfig;
import com.tcdng.unify.core.data.PackableDocConfig.FieldConfig;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;
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

    public PackableDoc preset() {
        for (FieldConfig fc : config.getFieldConfigs()) {
            if (fc.isComplex()) {
                Object val = null;
                if (fc.isList()) {
                    val = DataUtils.getNewArrayList(PackableDoc.class);
                } else {
                    val = new PackableDoc(fc.getPackableDocConfig(), auditable).preset();
                }

                values.put(fc.getFieldName(), val);
                updated = true;
            }
        }

        return this;
    }

    public String getConfigName() {
        return config.getName();
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

    public Object read(String fieldName) throws UnifyException {
        if (RESERVED_EXT_FIELD.equals(fieldName)) {
            return resrvExt;
        }

        config.getFieldConfig(fieldName);
        return values.get(fieldName);
    }

    public <T> T read(Class<T> type, String fieldName) throws UnifyException {
        FieldConfig fc = config.getFieldConfig(fieldName);
        if (fc.isList()) {
            return null;
        }
        
        if (fc.isComplex()) {
            
        }
        
        return DataUtils.convert(type, values.get(fieldName), null);
    }

    public <T> List<T> readList(Class<T> type, String fieldName) throws UnifyException {
        FieldConfig fc = config.getFieldConfig(fieldName);
        if (!fc.isList()) {
            return null;
        }
        
        if (fc.isComplex()) {
            
        }
        
        return DataUtils.convert(List.class, type, values.get(fieldName), null);
    }

    public <T> T readComplexField(Class<T> type, String fieldName) throws UnifyException {
        config.getFieldConfig(fieldName);
        return DataUtils.convert(type, values.get(fieldName), null);
    }

    public void readFrom(Object bean) throws UnifyException {
        if (bean == null) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_CANT_READ_FROM_NULL);
        }

        BeanConfig beanConfig = config.getBeanConfig(bean.getClass());
        for (String beanProperty : beanConfig.getBeanProperties()) {
            writeField(beanConfig.getFieldConfig(beanProperty), ReflectUtils.getNestedBeanProperty(bean, beanProperty));
        }
    }

    public void writeField(String fieldName, Object value) throws UnifyException {
        writeField(fieldName, value, null);
    }

    public void writeField(String fieldName, Object value, Formatter<?> formatter) throws UnifyException {
        if (RESERVED_EXT_FIELD.equals(fieldName)) {
            resrvExt = value;
            return;
        }

        FieldConfig fc = config.getFieldConfig(fieldName);
        Object val = DataUtils.convert(fc.getDataType(), value, formatter);
        writeField(fc, val);
    }

    public void writeTo(Object bean) throws UnifyException {
        if (bean == null) {
            throw new UnifyException(UnifyCoreErrorConstants.PACKABLEDOC_CANT_WRITE_TO_NULL);
        }

        BeanConfig beanConfig = config.getBeanConfig(bean.getClass());
        for (String beanProperty : beanConfig.getBeanProperties()) {
            DataUtils.setNestedBeanProperty(bean, beanProperty, readField(beanConfig.getFieldConfig(beanProperty)),
                    null);
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
        if (auditable) {
            oldValues = new HashMap<String, Object>(values);
        }

        updated = false;
    }

    private Object readObject(PackableDocRWConfig rwConfig, PackableDoc pd) throws UnifyException {
        Object bean = null;
        if (pd != null) {
            bean = ReflectUtils.newInstance(rwConfig.getBeanType());
            for (FieldMapping fMapping : rwConfig.getFieldMappings()) {
                Object val = readField(fMapping);
                if (val != null) {
                    DataUtils.setNestedBeanProperty(bean, fMapping.getBeanFieldName(), val);
                }
            }
        }

        return bean;
    }

    private void writeField(FieldMapping fMapping, Object val) throws UnifyException {
        FieldConfig fc = config.getFieldConfig(fMapping.getDocFieldName());
        Object oldValue = values.get(fc.getName());
        Object newValue = null;
        if (val != null) {
            if (fc.isComplex()) {
                PackableDocConfig fpdConfig = fc.getPackableDocConfig();
                if (val.getClass().isArray()) {
                    Object[] beans = DataUtils.convert(Object[].class, val, null);
                    PackableDoc[] fpd = new PackableDoc[beans.length];
                    for (int i = 0; i < beans.length; i++) {
                        if (beans[i] != null) {
                            fpd[i] = new PackableDoc(fpdConfig, auditable);
                            fpd[i].readFrom(fMapping.getPackableDocRWConfig(), beans[i]);
                        }
                    }

                    newValue = fpd;
                } else {
                    PackableDoc fpd = new PackableDoc(fpdConfig, auditable);
                    fpd.readFrom(fMapping.getPackableDocRWConfig(), val);
                    newValue = fpd;
                }
            } else {
                newValue = DataUtils.convert(fc.getDataType(), val, null);
            }
        }

        values.put(fc.getName(), newValue);
        updated |= !DataUtils.equals(oldValue, newValue);
    }
}
