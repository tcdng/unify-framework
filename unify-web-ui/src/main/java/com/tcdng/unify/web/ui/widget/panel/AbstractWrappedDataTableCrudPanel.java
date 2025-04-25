/*
 * Copyright (c) 2018-2025 The Code Department.
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

package com.tcdng.unify.web.ui.widget.panel;

import com.tcdng.unify.common.database.Entity;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.core.data.WrappedData;

/**
 * Abstract wrapped data table CRUD panel.
 * 
 * @author The Code Department
 * @since 4.1
 */
@UplBinding("web/panels/upl/wrappeddatatablecrudpanel.upl")
public abstract class AbstractWrappedDataTableCrudPanel<T extends Entity, U extends WrappedData<T>>
        extends AbstractTableCrudPanel<T> {

    public AbstractWrappedDataTableCrudPanel(Class<T> entityClass, String titleKey) {
        super(entityClass, titleKey);
    }

    public AbstractWrappedDataTableCrudPanel(Class<T> entityClass, String titleKey, boolean searchOnSwitchState) {
        super(entityClass, titleKey, searchOnSwitchState);
    }

    @Override
    protected T doPrepareCreateRecord() throws UnifyException {
        U wrappedRecord = doPrepareCreateWrappedRecord();
        getCrudData().setWrappedRecord(wrappedRecord);
        return wrappedRecord.getData();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doCreateRecord() throws UnifyException {
        doCreateRecord((U) getCrudData().getWrappedRecord());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doUpdateRecord() throws UnifyException {
        doUpdateRecord((U) getCrudData().getWrappedRecord());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doDeleteRecord() throws UnifyException {
        doDeleteRecord((U) getCrudData().getWrappedRecord());
    }

    @Override
    protected T loadSelectedRecordForView() throws UnifyException {
        T record = super.loadSelectedRecordForView();
        getCrudData().setWrappedRecord(doFindWrappedRecord(record.getId()));
        return record;
    }

    /**
     * Finds a wrapped record using supplied record ID.
     * 
     * @param id
     *            the ID to use
     * @return the wrapped record
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract U doFindWrappedRecord(Object id) throws UnifyException;

    /**
     * Prepares a new wrapped record.
     * 
     * @return the prepared wrapped record
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract U doPrepareCreateWrappedRecord() throws UnifyException;

    /**
     * Creates a record using wrapped record.
     * 
     * @param wrappedRecord
     *            the record to use
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract void doCreateRecord(U wrappedRecord) throws UnifyException;

    /**
     * Updates a record using wrapped record.
     * 
     * @param wrappedRecord
     *            the record to use
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract void doUpdateRecord(U wrappedRecord) throws UnifyException;

    /**
     * Deletes a record using wrapped record.
     * 
     * @param wrappedRecord
     *            the record to use
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract void doDeleteRecord(U wrappedRecord) throws UnifyException;

}
