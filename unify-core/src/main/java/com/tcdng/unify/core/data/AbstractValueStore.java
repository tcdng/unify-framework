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
package com.tcdng.unify.core.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.GetterSetterInfo;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Abstract value store.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractValueStore implements ValueStore {

	private Formats.Instance formatsInstance;
	
    private String dataPrefix;
    
    private ValueStorePolicy policy;
    
    private Map<String, Object> savedValues;

    private ValueStoreReader reader;

    private ValueStoreWriter writer;
    
    private int dataIndex;
    
    public AbstractValueStore(int dataIndex) {
    	this.dataIndex = dataIndex;
    }
    
    @Override
	public void setReadFormats(Formats formats) {
		this.formatsInstance = formats != null ? formats.createInstance() : null;
	}

	@Override
    public String getDataPrefix() {
        return dataPrefix;
    }

    @Override
    public void setDataPrefix(String dataPrefix) {
        this.dataPrefix = dataPrefix;
    }

    @Override
    public final void setDataIndex(int dataIndex) {
        savedValues = null;
        this.dataIndex = dataIndex;
    }

    @Override
    public final int getDataIndex() {
        return dataIndex;
    }

    @Override
	public void reset() {
    	dataIndex = -1;
	}

	@Override
	public boolean next() {
		dataIndex++;
		return dataIndex < size();
	}

	@Override
    public void setPolicy(ValueStorePolicy policy) {
        this.policy = policy;
    }

    @Override
    public boolean isNull(String name) throws UnifyException {
        return retrieve(name) == null;
    }

    @Override
    public boolean isNotNull(String name) throws UnifyException {
        return retrieve(name) != null;
    }

    @Override
	public String retrieveAsString(String name) throws UnifyException {
		Object val = retrieve(name);
		if (val != null) {
			return formatsInstance != null? formatsInstance.format(val) : String.valueOf(val);
		}
		
		return null;
	}

	@Override
	public int compare(ValueStore valSource) throws UnifyException {
		for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(getDataClass())) {
			if (getterSetterInfo.isGetterSetter()) {
				String fieldName = getterSetterInfo.getName();
				if (!DataUtils.equals(retrieve(fieldName), valSource.retrieve(fieldName))) {
					return 1;
				}
			}
		}

		return 0;
	}

	@Override
	public int compare(ValueStore valSource, String... inclusionFieldNames) throws UnifyException {
        Set<String> inclusion = new HashSet<String>(Arrays.asList(inclusionFieldNames));
        return compare(valSource, inclusion);
	}

	@Override
	public int compare(ValueStore valSource, Collection<String> inclusionFieldNames) throws UnifyException {
        for (String fieldName: inclusionFieldNames) {
            if (!DataUtils.equals(retrieve(fieldName), valSource.retrieve(fieldName))) {
                return 1;
            }
        }
        
		return 0;
	}

	@Override
	public int compare(ValueStore valSource, Map<String, String> inclusionMapping) throws UnifyException {
		for (Map.Entry<String, String> entry : inclusionMapping.entrySet()) {
			if (!DataUtils.equals(retrieve(entry.getKey()), valSource.retrieve(entry.getValue()))) {
				return 1;
			}
		}

		return 0;
	}

	@Override
    public Audit diff(ValueStore newSource) throws UnifyException {
        Audit.Builder ab = Audit.newBuilder();
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(getDataClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                Object oldVal = retrieve(fieldName);
                Object newVal = newSource.retrieve(fieldName);
                if (!DataUtils.equals(oldVal, newVal)) {
                    ab.addItem(fieldName, oldVal, newVal);
                }
            }
        }

        return ab.build();
    }

    @Override
    public Audit diff(ValueStore newSource, String... inclusionFieldNames) throws UnifyException {
        Set<String> inclusion = new HashSet<String>(Arrays.asList(inclusionFieldNames));
        return diff(newSource, inclusion);
    }

    @Override
    public Audit diff(ValueStore newSource, Collection<String> inclusionFieldNames) throws UnifyException {
        Audit.Builder ab = Audit.newBuilder();
        for (String fieldName: inclusionFieldNames) {
            Object oldVal = retrieve(fieldName);
            Object newVal = newSource.retrieve(fieldName);
            if (!DataUtils.equals(oldVal, newVal)) {
                ab.addItem(fieldName, oldVal, newVal);
            }
        }

        return ab.build();
    }

    @Override
    public void copy(ValueStore source) throws UnifyException {
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(getDataClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                store(fieldName, source.retrieve(fieldName));
            }
        }
    }

    @Override
    public void copyWithExclusions(ValueStore source, String... exclusionFieldNames) throws UnifyException {
        Set<String> exclusion = new HashSet<String>(Arrays.asList(exclusionFieldNames));
        copyWithExclusions(source, exclusion);
    }

    @Override
    public void copyWithInclusions(ValueStore source, String... inclusionFieldNames) throws UnifyException {
        Set<String> inclusion = new HashSet<String>(Arrays.asList(inclusionFieldNames));
        copyWithInclusions(source, inclusion);
    }

    @Override
    public void copyWithExclusions(ValueStore source, Collection<String> exclusionFieldNames) throws UnifyException {
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(getDataClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                if (!exclusionFieldNames.contains(fieldName)) {
                    store(fieldName, source.retrieve(fieldName));
                }
            }
        }
    }

    @Override
    public void copyWithInclusions(ValueStore source, Collection<String> inclusionFieldNames) throws UnifyException {
        for (GetterSetterInfo getterSetterInfo : ReflectUtils.getGetterSetterList(getDataClass())) {
            if (getterSetterInfo.isGetterSetter()) {
                String fieldName = getterSetterInfo.getName();
                if (inclusionFieldNames.contains(fieldName)) {
                    store(fieldName, source.retrieve(fieldName));
                }
            }
        }
    }

    @Override
    public void save(String... fields) throws UnifyException {
        if (savedValues == null) {
            savedValues = new HashMap<String, Object>();
        }
        
        for (String fieldName: fields) {
            savedValues.put(fieldName, retrieve(fieldName));
        }
    }

    @Override
    public void save(Collection<String> fields) throws UnifyException {
        if (savedValues == null) {
            savedValues = new HashMap<String, Object>();
        }
        
        for (String fieldName: fields) {
            savedValues.put(fieldName, retrieve(fieldName));
        }
    }

    @Override
    public void restore() throws UnifyException {
        if (savedValues != null) {
            for (Map.Entry<String, Object> entry: savedValues.entrySet()) {
                store(entry.getKey(), entry.getValue());
            }
        }
        
        savedValues = null;
    }

    @Override
    public ValueStoreReader getReader() {
        if (reader == null) {
            synchronized (this) {
                if (reader == null) {
                    reader = new ValueStoreReaderImpl(this);
                }
            }
        }

        return reader;
    }

    @Override
    public ValueStoreWriter getWriter() {
        if (writer == null) {
            synchronized (this) {
                if (writer == null) {
                    writer = new ValueStoreWriterImpl(this);
                }
            }
        }

        return writer;
    }

    protected ValueStorePolicy getPolicy() {
        return policy;
    }
    
    protected abstract Class<?> getDataClass() throws UnifyException;
    
    private class ValueStoreWriterImpl implements ValueStoreWriter{
        
        private ValueStore valueStore;

        public ValueStoreWriterImpl(ValueStore valueStore) {
            this.valueStore = valueStore;
        }

        @Override
        public void writeScratch(String fieldName, Object value) throws UnifyException {
            valueStore.setTempValue(fieldName, value);
        }

        @Override
        public void write(String fieldName, Object value) throws UnifyException {
            valueStore.store(fieldName, value);
        }

        @Override
        public void write(String fieldName, Object value, Formatter<?> formatter) throws UnifyException {
            valueStore.store(fieldName, value, formatter);
        }
        
        @Override
		public ValueStore getValueStore() {
			return valueStore;
		}

		@Override
        public Object getValueObject() {
            return valueStore.getValueObject();
        }

        @Override
        public Object getTempValue(String name) throws UnifyException {
            return valueStore.getTempValue(name);
        }

        @Override
        public <T> T getTempValue(Class<T> type, String name) throws UnifyException {
            return valueStore.getTempValue(type, name);
        }

        @Override
        public void setTempValue(String name, Object value) throws UnifyException {
            valueStore.setTempValue(name, value);
        }

        @Override
        public boolean isTempValue(String name) {
            return valueStore.isTempValue(name);
        }
    }

    private class ValueStoreReaderImpl implements ValueStoreReader {
        
        private ValueStore valueStore;
        
        public ValueStoreReaderImpl(ValueStore valueStore) {
            this.valueStore = valueStore;
        }

        @Override
		public void setReadFormats(Formats formats) {
        	valueStore.setReadFormats(formats);
		}

		@Override
        public boolean isNull(String name) throws UnifyException {
        	return valueStore.isNull(name);
        }

        @Override
        public boolean isNotNull(String name) throws UnifyException {
        	return valueStore.isNotNull(name);
        }

        @Override
        public Object readScratch(String fieldName) throws UnifyException {
            return valueStore.getTempValue(fieldName);
        }

        @Override
        public <T> T readScratch(Class<T> type, String fieldName) throws UnifyException {
            return valueStore.getTempValue(type, fieldName);
        }

        @Override
		public String readAsString(String fieldName) throws UnifyException {
            return valueStore.retrieveAsString(fieldName);
		}

		@Override
        public Object read(String fieldName) throws UnifyException {
            return valueStore.retrieve(fieldName);
        }

        @Override
        public <T> T read(Class<T> type, String fieldName) throws UnifyException {
            return valueStore.retrieve(type, fieldName);
        }
        
        @Override
		public <T> T read(Class<T> type, String fieldName, Formatter<?> formatter) throws UnifyException {
            return valueStore.retrieve(type, fieldName, formatter);
		}

		@Override
        public ValueStore getValueStore() {
            return valueStore;
        }
        
        @Override
        public Object getValueObject() {
            return valueStore.getValueObject();
        }

        @Override
		public void reset() {
        	valueStore.reset();
		}

		@Override
		public boolean next() {
			return valueStore.next();
		}

		@Override
        public int getDataIndex() {
        	return valueStore.getDataIndex();
        }

        @Override
        public void setDataIndex(int dataIndex) {
        	valueStore.setDataIndex(dataIndex);
        }

        @Override
		public int size() {
			return valueStore.size();
		}

		@Override
        public Object getTempValue(String name) throws UnifyException {
            return valueStore.getTempValue(name);
        }

        @Override
        public <T> T getTempValue(Class<T> type, String name) throws UnifyException {
            return valueStore.getTempValue(type, name);
        }

        @Override
        public void setTempValue(String name, Object value) throws UnifyException {
            valueStore.setTempValue(name, value);
        }

        @Override
        public boolean isTempValue(String name) {
            return valueStore.isTempValue(name);
        }
    }

}
