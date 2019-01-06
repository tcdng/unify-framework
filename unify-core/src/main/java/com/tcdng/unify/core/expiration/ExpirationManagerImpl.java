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
package com.tcdng.unify.core.expiration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyComponentConfig;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.Expirable;
import com.tcdng.unify.core.annotation.Periodic;
import com.tcdng.unify.core.annotation.PeriodicType;
import com.tcdng.unify.core.business.internal.ProxyBusinessServiceMethodRelay;
import com.tcdng.unify.core.task.TaskMonitor;
import com.tcdng.unify.core.util.AnnotationUtils;
import com.tcdng.unify.core.util.CalendarUtils;
import com.tcdng.unify.core.util.ReflectUtils;

/**
 * Default implementation of expiration manager.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_EXPIRATIONMANAGER)
public class ExpirationManagerImpl extends AbstractUnifyComponent implements ExpirationManager {

    @Configurable
    private ProxyBusinessServiceMethodRelay proxyMethodRelay;

    private List<ObservedExpirableInfo> expirablesList;

    public ExpirationManagerImpl() {
        expirablesList = new ArrayList<ObservedExpirableInfo>();
    }

    @Periodic(PeriodicType.NORMAL)
    public synchronized void removeExpiredExpirables(TaskMonitor taskMonitor) throws UnifyException {
        Date now = new Date();
        for (ObservedExpirableInfo observedExpirableInfo : expirablesList) {
            try {
                if (observedExpirableInfo.isExpired(now)) {
                    logDebug("Invoking expiration method [{1}] on component [{0}]...",
                            observedExpirableInfo.getExpirable().getName(),
                            observedExpirableInfo.getMethod().getName());
                    observedExpirableInfo.getMethod().invoke(observedExpirableInfo.getExpirable());
                    observedExpirableInfo.reset();
                }
            } catch (Exception e) {
                logError(e);
            }
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {
        for (UnifyComponentConfig unifyComponentConfig : getComponentConfigs(UnifyComponent.class)) {
            for (Method method : unifyComponentConfig.getType().getMethods()) {
                Expirable ea = method.getAnnotation(Expirable.class);
                if (ea == null) {
                    ea = proxyMethodRelay
                            .getExpirable(ReflectUtils.getMethodSignature(unifyComponentConfig.getName(), method));
                }

                if (ea != null) {
                    if (!unifyComponentConfig.isSingleton()) {
                        throw new UnifyException(UnifyCoreErrorConstants.EXPIRABLE_METHOD_SINGLETON_ONLY,
                                method.getName(), unifyComponentConfig.getName());
                    }

                    if (method.getParameterTypes().length != 0) {
                        throw new UnifyException(UnifyCoreErrorConstants.EXPIRABLE_METHOD_NO_PARAMS, method.getName(),
                                unifyComponentConfig.getName());
                    }

                    int cycle = ea.cycleInSec();
                    String setting = AnnotationUtils.getAnnotationString(ea.cycleInSecSetting());
                    if (setting != null) {
                        cycle = getContainerSetting(int.class, setting, ea.cycleInSec());
                    }

                    UnifyComponent expirable = getComponent(unifyComponentConfig.getName());
                    expirablesList.add(new ObservedExpirableInfo(expirable, method, cycle));
                }
            }
        }
    }

    @Override
    protected void onTerminate() throws UnifyException {
        expirablesList.clear();
    }

    private class ObservedExpirableInfo {

        private UnifyComponent expirable;

        private Method method;

        private int cycle;

        private Date expiryDate;

        public ObservedExpirableInfo(UnifyComponent expirable, Method method, int cycle) {
            this.expirable = expirable;
            this.method = method;
            this.cycle = cycle;
            reset();
        }

        public UnifyComponent getExpirable() {
            return expirable;
        }

        public Method getMethod() {
            return method;
        }

        public boolean isExpired(Date now) {
            return now.after(expiryDate);
        }

        public void reset() {
            expiryDate = CalendarUtils.getNowWithOffset(cycle);
        }
    }
}
