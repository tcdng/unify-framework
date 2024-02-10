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
package com.tcdng.unify.core.report;

import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyException;

/**
 * Report processing component with processing applied to a report just before
 * generation. Typically used to add or manipulate report parameters at runtime.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface ReportProcessor extends UnifyComponent {

    /**
     * Processes supplied report.
     * 
     * @param report
     *            the report to process
     * @throws UnifyException
     *             if an error occurs
     */
    void process(Report report) throws UnifyException;
}
