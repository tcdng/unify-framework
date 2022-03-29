/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.core.upl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.constant.ClientPlatform;

/**
 * Default implementation of UPL component writer manager.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_UPLCOMPONENTWRITERMANAGER)
public class UplComponentWriterManagerImpl extends AbstractUnifyComponent implements UplComponentWriterManager {

    private Map<ClientPlatform, Map<Class<? extends UplComponent>, UplComponentWriter>> writersByPlatform;

    public UplComponentWriterManagerImpl() {
        writersByPlatform =
                new ConcurrentHashMap<ClientPlatform, Map<Class<? extends UplComponent>, UplComponentWriter>>();
    }

    @Override
    public Map<Class<? extends UplComponent>, UplComponentWriter> getWriters(ClientPlatform platform)
            throws UnifyException {
        Map<Class<? extends UplComponent>, UplComponentWriter> writers = writersByPlatform.get(platform);
        if (writers == null) {
            return writersByPlatform.get(ClientPlatform.DEFAULT);
        }
        return writers;
    }

    @Override
    protected void onInitialize() throws UnifyException {
        writersByPlatform.put(ClientPlatform.DEFAULT, new HashMap<Class<? extends UplComponent>, UplComponentWriter>());
        List<UnifyComponentConfig> writerConfigList = getComponentConfigs(UplComponentWriter.class);
        for (UnifyComponentConfig config : writerConfigList) {
            Class<? extends UnifyComponent> writerType = config.getType();
            Writes wa = writerType.getAnnotation(Writes.class);
            if (wa != null) {
                ClientPlatform platform = wa.target();
                UplComponentWriter writer = (UplComponentWriter) getComponent(config.getName());
                Map<Class<? extends UplComponent>, UplComponentWriter> writers = writersByPlatform.get(platform);
                if (writers == null) {
                    writers = new HashMap<Class<? extends UplComponent>, UplComponentWriter>();
                    writersByPlatform.put(platform, writers);
                }

                Class<? extends UplComponent> uplType = wa.value();
                if (writers.containsKey(uplType)) {
                    throw new UnifyException(UnifyCoreErrorConstants.CONFLICTING_UPLCOMPONENT_WRITERS, uplType,
                            platform, writer.getClass(), writers.get(uplType).getClass());
                }

                writers.put(uplType, writer);
            }
        }

        // Expand to concrete UPL component types
        for (ClientPlatform platform : writersByPlatform.keySet()) {
            Map<Class<? extends UplComponent>, UplComponentWriter> writers =
                    this.expandToConcreteUplTypes(writersByPlatform.get(platform));
            writersByPlatform.put(platform, writers);
        }

        // Set defaults for other platforms
        Map<Class<? extends UplComponent>, UplComponentWriter> defaultWriters =
                writersByPlatform.get(ClientPlatform.DEFAULT);
        for (Map<Class<? extends UplComponent>, UplComponentWriter> writers : writersByPlatform.values()) {
            if (writers != defaultWriters) {
                for (Class<? extends UplComponent> uplType : defaultWriters.keySet()) {
                    if (!writers.containsKey(uplType)) {
                        writers.put(uplType, defaultWriters.get(uplType));
                    }
                }
            }
        }

        // Calcify
        for (ClientPlatform platform : writersByPlatform.keySet()) {
            writersByPlatform.put(platform, Collections.unmodifiableMap(writersByPlatform.get(platform)));
        }
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    @SuppressWarnings("unchecked")
    private Map<Class<? extends UplComponent>, UplComponentWriter> expandToConcreteUplTypes(
            Map<Class<? extends UplComponent>, UplComponentWriter> writers) throws UnifyException {
        Map<Class<? extends UplComponent>, UplComponentWriter> newWriters =
                new HashMap<Class<? extends UplComponent>, UplComponentWriter>();
        for (Class<? extends UplComponent> uplType : writers.keySet()) {
            UplComponentWriter writer = writers.get(uplType);
            List<UnifyComponentConfig> uplTypeConfigList = getComponentConfigs(uplType);
            if (uplTypeConfigList.size() == 1) {
                newWriters.put((Class<? extends UplComponent>) uplTypeConfigList.get(0).getType(), writer);
            } else {
                for (UnifyComponentConfig eUplTypeConfig : uplTypeConfigList) {
                    Class<? extends UplComponent> exUplType = (Class<? extends UplComponent>) eUplTypeConfig.getType();
                    if (!newWriters.containsKey(exUplType)) {
                        boolean isQualified = true;
                        for (Class<? extends UplComponent> refUplType : writers.keySet()) {
                            if (uplType != refUplType && refUplType.isAssignableFrom(exUplType)) {
                                if (uplType.isAssignableFrom(refUplType)) {
                                    isQualified = false;
                                    break;
                                }
                            }
                        }

                        if (isQualified) {
                            newWriters.put(exUplType, writer);
                        }
                    }
                }
            }

        }
        return newWriters;
    }

}
