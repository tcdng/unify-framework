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
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.data.PackableDocConfig.FieldConfig;
import com.tcdng.unify.core.data.PackableDocRWConfig.FieldMapping;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils.StringToken;

/**
 * A packable document.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class PackableDoc implements Serializable {

    private static final long serialVersionUID = 8352270606932913113L;

    public transient static final String RESERVED_EXT_FIELD = "resrvExt";

    private Map<String, Object> values;

    private transient PackableDocConfig config;

    private transient Map<String, Object> oldValues;

    private transient Object resrvExt;

    private transient boolean auditable;

    private transient boolean changed;
    
    public PackableDoc(PackableDocConfig config) {
        this(config, false);
    }

    public PackableDoc(PackableDocConfig config, boolean auditable) {
        this(config, new HashMap<String, Object>(), auditable);
    }

    private PackableDoc(PackableDocConfig config, Map<String, Object> values, boolean auditable) {
        this.values = values;
        setup(config, auditable);
    }

    public PackableDoc preset() throws UnifyException {
        for (FieldConfig fc : config.getFieldConfigs()) {
            if (fc.isComplex()) {
                values.put(fc.getName(), new PackableDoc(fc.getPackableDocConfig(), auditable).preset());
            }
        }

        return this;
    }

    public static PackableDoc unpack(PackableDocConfig config, byte[] packedDoc) throws UnifyException {
        return PackableDoc.unpack(config, packedDoc, false);
    }

    public static PackableDoc unpack(PackableDocConfig config, byte[] packedDoc, boolean auditable)
            throws UnifyException {
        PackableDoc pd = IOUtils.streamFromBytes(PackableDoc.class, packedDoc);
        pd.setup(config, auditable);
        return pd;
    }

    public byte[] pack() throws UnifyException {
        return IOUtils.streamToBytes(this);
    }

    public String describe(List<StringToken> itemDescFormat) throws UnifyException {
        return PackableDoc.describe(itemDescFormat, this);
    }

    public static String describe(List<StringToken> itemDescFormat, PackableDoc packableDoc) throws UnifyException {
        if (itemDescFormat.isEmpty()) {
            return DataUtils.EMPTY_STRING;
        }

        StringBuilder sb = new StringBuilder();
        for (StringToken stringToken : itemDescFormat) {
            if (stringToken.isParam()) {
                sb.append(packableDoc.readFieldValue(String.class, stringToken.getToken()));
            } else {
                sb.append(stringToken.getToken());
            }
        }

        return sb.toString();
    }

    public void readFrom(PackableDocRWConfig rwConfig, Object bean) throws UnifyException {
        if (bean == null) {
            throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_CANT_READ_FROM_NULL);
        }

        for (FieldMapping fMapping : rwConfig.getFieldMappings()) {
            writeFieldObject(fMapping, this, ReflectUtils.getNestedBeanProperty(bean, fMapping.getBeanFieldName()));
        }
    }

    public void writeTo(PackableDocRWConfig rwConfig, Object bean) throws UnifyException {
        if (bean == null) {
            throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_CANT_WRITE_TO_NULL);
        }

        for (FieldMapping fMapping : rwConfig.getFieldMappings()) {
            DataUtils.setNestedBeanProperty(bean, fMapping.getBeanFieldName(), readFieldObject(fMapping, this), null);
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

    public Object getResrvExt() {
        return resrvExt;
    }

    public void setResrvExt(Object resrvExt) {
        this.resrvExt = resrvExt;
    }

    public boolean isAuditable() {
        return auditable;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Set<String> getFieldNames() {
        return config.getFieldNames();
    }

    public Class<?> getFieldType(String name) throws UnifyException {
        return config.getFieldConfig(name).getType();
    }

    public Object readFieldValue(String name) throws UnifyException {
        if (RESERVED_EXT_FIELD.equals(name)) {
            return resrvExt;
        }
        
        config.getFieldConfig(name);
        return values.get(name);
    }

    public <T> T readFieldValue(Class<T> type, String name) throws UnifyException {
        return DataUtils.convert(type, readFieldValue(name), null);
    }

    public Object readFieldValue(PackableDocRWConfig rwConfig, String name) throws UnifyException {
        FieldConfig fc = config.getFieldConfig(name);
        if (!fc.isComplex()) {
            throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_FIELD_NOT_COMPLEX, name);
        }

        FieldMapping fMapping = rwConfig.getFieldMapping(name);
        if (!fMapping.isComplex()) {
            throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_FIELDMAPPING_NOT_COMPLEX,
                    fMapping.getDocFieldName(), fMapping.getBeanFieldName());
        }

        return readFieldObject(fMapping, this);
    }

    public void writeFieldValue(String name, Object value) throws UnifyException {
        writeFieldValue(name, value, null);
    }

    public void writeFieldValue(String name, Object value, Formatter<?> formatter) throws UnifyException {
        if (RESERVED_EXT_FIELD.equals(name)) {
            resrvExt = value;
            return;
        }
        
        FieldConfig fc = config.getFieldConfig(name);
        if (fc.isComplex()) {
            throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_FIELD_COMPLEX_DIRECT_WRITE, name);
        }

        values.put(name, DataUtils.convert(fc.getType(), value, formatter));
        changed = true;
    }

    public void writeFieldValue(PackableDocRWConfig rwConfig, String name, Object value) throws UnifyException {
        FieldConfig fc = config.getFieldConfig(name);
        if (!fc.isComplex()) {
            throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_FIELD_NOT_COMPLEX, name);
        }

        FieldMapping fMapping = rwConfig.getFieldMapping(name);
        if (!fMapping.isComplex()) {
            throw new UnifyException(UnifyCoreErrorConstants.DOCUMENT_FIELDMAPPING_NOT_COMPLEX,
                    fMapping.getDocFieldName(), fMapping.getBeanFieldName());
        }

        writeFieldObject(fMapping, this, value);
    }

    public boolean isField(String name) {
        return PackableDoc.RESERVED_EXT_FIELD.equals(name) || config.getFieldNames().contains(name);
    }

    public int getFieldCount() {
        return config.getFieldCount();
    }

    private void setup(PackableDocConfig config, boolean auditable) {
        this.config = config;
        this.auditable = auditable;
        changed = false;

        for (FieldConfig fc : config.getFieldConfigs()) {
            if (!values.containsKey(fc.getName())) {
                values.put(fc.getName(), null);
            } else {
                if (fc.isComplex()) {
                    Object val = values.get(fc.getName());
                    if (val != null) {
                        if (val.getClass().isArray()) {
                            PackableDoc[] pd = (PackableDoc[]) val;
                            for (int i = 0; i < pd.length; i++) {
                                if (pd[i] != null) {
                                    pd[i].setup(fc.getPackableDocConfig(), auditable);
                                }
                            }
                        } else {
                            PackableDoc pd = (PackableDoc) val;
                            pd.setup(fc.getPackableDocConfig(), auditable);
                        }
                    }
                }
            }
        }

        if (auditable) {
            oldValues = new HashMap<String, Object>(values);
        }
    }

    private Object readObject(PackableDocRWConfig rwConfig, PackableDoc pd) throws UnifyException {
        Object bean = null;
        if (pd != null) {
            bean = ReflectUtils.newInstance(rwConfig.getBeanType());
            for (FieldMapping fMapping : rwConfig.getFieldMappings()) {
                Object val = readFieldObject(fMapping, pd);
                if (val != null) {
                    DataUtils.setNestedBeanProperty(bean, fMapping.getBeanFieldName(), val);
                }
            }
        }

        return bean;
    }

    private Object readFieldObject(FieldMapping fMapping, PackableDoc pd) throws UnifyException {
        Object val = pd.values.get(fMapping.getDocFieldName());
        if (val != null) {
            if (fMapping.isComplex()) {
                if (val.getClass().isArray()) {
                    PackableDoc[] pdArray = (PackableDoc[]) val;
                    Object[] beans = new Object[pdArray.length];
                    for (int i = 0; i < pdArray.length; i++) {
                        beans[i] = readObject(fMapping.getPackableDocRWConfig(), pdArray[i]);
                    }

                    val = beans;
                } else {
                    val = readObject(fMapping.getPackableDocRWConfig(), (PackableDoc) val);
                }
            }

        }

        return val;
    }

    private void writeFieldObject(FieldMapping fMapping, PackableDoc pd, Object val) throws UnifyException {
        FieldConfig fc = pd.config.getFieldConfig(fMapping.getDocFieldName());
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
                    pd.values.put(fc.getName(), fpd);
                } else {
                    PackableDoc fpd = new PackableDoc(fpdConfig, auditable);
                    fpd.readFrom(fMapping.getPackableDocRWConfig(), val);
                    pd.values.put(fc.getName(), fpd);
                }
            } else {
                pd.values.put(fc.getName(), DataUtils.convert(fc.getType(), val, null));
            }
        } else {
            pd.values.put(fc.getName(), null);
        }
    }
}
