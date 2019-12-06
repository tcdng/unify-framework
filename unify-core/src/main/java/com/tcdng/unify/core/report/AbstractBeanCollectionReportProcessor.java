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
package com.tcdng.unify.core.report;

import java.util.List;

import com.tcdng.unify.core.UnifyException;

/**
 * Convenient abstract bean collection report processor.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractBeanCollectionReportProcessor<T> extends AbstractReportProcessor {

    @Override
    public void process(Report report) throws UnifyException {
        doProcess(report);
        List<T> beanCollection = getBeanCollection(report.getReportParameters());
        report.setBeanCollection(beanCollection);
    }

    protected abstract void doProcess(Report report) throws UnifyException;

    protected abstract List<T> getBeanCollection(ReportParameters reportParameters) throws UnifyException;
}
