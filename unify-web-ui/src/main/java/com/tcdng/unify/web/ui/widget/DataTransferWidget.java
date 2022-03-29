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
package com.tcdng.unify.web.ui.widget;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.DataTransferBlock;

/**
 * A widget that can transfer data.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface DataTransferWidget extends Widget {

    /**
     * Populates control data value bean property.
     * 
     * @param transferBlock
     *            the transfer block
     * @throws UnifyException
     *             if an error occurs
     */
    void populate(DataTransferBlock transferBlock) throws UnifyException;
}
