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
package com.tcdng.unify.web.ui.panel;

import java.util.ArrayList;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.core.business.GenericService;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.data.WrappedData;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.ui.AbstractPanel;
import com.tcdng.unify.web.ui.control.Table;

/**
 * Abstract base class for simple CRUD panels.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@UplBinding("web/panels/upl/tablecrudpanel.upl")
@UplAttributes({ @UplAttribute(name = "createNext", type = boolean.class, defaultVal = "false") })
public abstract class AbstractTableCrudPanel<T extends Entity> extends AbstractPanel implements TableCrudPanel<T> {

    protected enum FORMMODE {
        CREATE, RETRIEVE, UPDATE, DELETE
    }

    @Configurable
    private GenericService genericService;

    private FORMMODE formMode;

    private CrudData crudData;

    private boolean searchOnSwitchState;

    public AbstractTableCrudPanel(Class<T> entityClass, String title) {
        this(entityClass, title, false);
    }

    public AbstractTableCrudPanel(Class<T> entityClass, String title, boolean searchOnSwitchState) {
        this.searchOnSwitchState = searchOnSwitchState;
        crudData = new CrudData(entityClass, title);
    }

    @Override
    public void onPageConstruct() throws UnifyException {
        super.onPageConstruct();
        setFormMode(FORMMODE.CREATE, null);
    }

    @Override
    public void clear() throws UnifyException {
        setRecordList(null);
        crudData.setParentId(null);
        crudData.getQuery().clear();
    }

    @Override
    public T getRecord(int index) throws UnifyException {
        return crudData.getRecordList().get(index);
    }

    @Override
    public void setRecordList(List<T> recordList) throws UnifyException {
        crudData.setRecordList(recordList);
        Table table = (Table) getWidgetByShortName("simpleCrudTbl");
        table.reset();
    }

    @Override
    public List<T> getRecordList() throws UnifyException {
        return crudData.getRecordList();
    }

    @Override
    @Action
    public void switchState() throws UnifyException {
        super.switchState();

        boolean editable = isContainerEditable() && !isContainerDisabled();

        setVisible("addBtn", editable);
        setVisible("editBtn", editable);
        setVisible("deleteBtn", editable);

        setVisible("createFrmBtn", false);
        setVisible("createNextFrmBtn", false);
        setVisible("saveFrmBtn", false);
        setVisible("deleteFrmBtn", false);
        setVisible("cancelFrmBtn", false);
        setVisible("doneFrmBtn", false);

        switch (formMode) {
            case CREATE:
                setEditable("formPanel", editable);
                setVisible("createFrmBtn", editable);
                setVisible("cancelFrmBtn", true);
                setVisible("createNextFrmBtn", editable && getUplAttribute(boolean.class, "createNext"));
                break;
            case DELETE:
                setEditable("formPanel", false);
                setVisible("deleteFrmBtn", editable);
                setVisible("cancelFrmBtn", true);
                break;
            case UPDATE:
                setEditable("formPanel", editable);
                setVisible("saveFrmBtn", editable);
                setVisible("cancelFrmBtn", true);
                break;
            case RETRIEVE:
            default:
                setEditable("formPanel", false);
                setVisible("doneFrmBtn", true);
                break;
        }

        if (searchOnSwitchState) {
            findRecords();
        }
    }

    @Override
    public void setValueStore(ValueStore valueStore) throws UnifyException {
        if (getValueStore() == null) {
            super.setValueStore(createValueStore(crudData));
        }
    }

    @Action
    public void prepareCreateRecord() throws UnifyException {
        crudData.setRecord(doPrepareCreateRecord());
        setFormMode(FORMMODE.CREATE, "simplecrudpanel.createrecord");
        showPopup();
    }

    @Action
    public void createRecord() throws UnifyException {
        doCreateRecord();
        findRecords();
        hidePopup();
        notifyListeners(TableCrudPanel.Event.CREATE);
    }

    @Action
    public void createNextRecord() throws UnifyException {
        doCreateRecord();
        findRecords();
        prepareCreateRecord();
        refreshTable();
        notifyListeners(TableCrudPanel.Event.CREATE_NEXT);
    }

    @Action
    public void prepareViewRecord() throws UnifyException {
        loadSelectedRecordForView();
        setFormMode(FORMMODE.RETRIEVE, "simplecrudpanel.viewrecord");
        showPopup();
    }

    @Action
    public void prepareUpdateRecord() throws UnifyException {
        loadSelectedRecordForView();
        setFormMode(FORMMODE.UPDATE, "simplecrudpanel.editrecord");
        showPopup();
    }

    @Action
    public void updateRecord() throws UnifyException {
        doUpdateRecord();
        findRecords();
        hidePopup();
        notifyListeners(TableCrudPanel.Event.UPDATE);
    }

    @Action
    public void prepareDeleteRecord() throws UnifyException {
        loadSelectedRecordForView();
        setFormMode(FORMMODE.DELETE, "simplecrudpanel.deleterecord");
        showPopup();
    }

    @Action
    public void deleteRecord() throws UnifyException {
        doDeleteRecord();
        findRecords();
        hidePopup();
        notifyListeners(TableCrudPanel.Event.DELETE);
    }

    @Action
    public void cancel() throws UnifyException {
        hidePopup();
        notifyListeners(TableCrudPanel.Event.CANCEL);
    }

    /**
     * Sets this panel's form mode.
     * 
     * @param formMode
     *            the form mode to set
     * @param captionKey
     *            the form caption key
     * @throws UnifyException
     *             if an error occurs
     */
    protected void setFormMode(FORMMODE formMode, String captionKey) throws UnifyException {
        this.formMode = formMode;
        if (formMode == null) {
            this.formMode = FORMMODE.CREATE;
        }

        if (captionKey != null) {
            String crudTypeTitle = crudData.getTitleKey();
            if (crudTypeTitle != null) {
                crudData.setCaption(getSessionMessage(captionKey, resolveSessionMessage(crudTypeTitle)));
            } else {
                crudData.setCaption(getSessionMessage(captionKey));
            }

            cascadeValueStore();
            switchState();
        }
    }

    protected FORMMODE getFormMode() {
        return formMode;
    }

    protected CrudData getCrudData() throws UnifyException {
        return crudData;
    }

    protected boolean isRecord() throws UnifyException {
        return crudData.getRecord() != null;
    }

    protected void showPopup() throws UnifyException {
        getRequestContextUtil().setRequestPopupName(getWidgetByShortName("simpleCrudPopup").getLongName());
        setCommandResultMapping(ResultMappingConstants.SHOW_POPUP);
    }

    protected void hidePopup() throws UnifyException {
        setCommandResultMapping(ResultMappingConstants.HIDE_POPUP);
    }

    protected void refreshTable() throws UnifyException {
        getRequestContextUtil().setRequestPopupName(getWidgetByShortName("simpleCrudPopup").getLongName());
        setCommandResultMapping(ResultMappingConstants.REFRESH_SHOW_POPUP);
    }

    protected T loadSelectedRecordForView() throws UnifyException {
        Integer idx = getRequestTarget(Integer.class);
        T record = crudData.getRecordList().get(idx);
        crudData.setRecord(record);
        return record;
    }

    protected final void findRecords() throws UnifyException {
        setRecordList(doFindRecords());
    }

    protected List<T> doFindRecords() throws UnifyException {
        doSetSearchFilter(crudData.getQuery());
        return genericService.listAll(crudData.getQuery());
    }

    protected T doPrepareCreateRecord() throws UnifyException {
        return ReflectUtils.newInstance(crudData.getEntityClass());
    }

    protected void doCreateRecord() throws UnifyException {
        genericService.create(crudData.getRecord());
    }

    protected void doUpdateRecord() throws UnifyException {
        genericService.update(crudData.getRecord());
    }

    protected void doDeleteRecord() throws UnifyException {
        T record = crudData.getRecord();
        genericService.delete(record.getClass(), record.getId());
    }

    protected void doSetSearchFilter(Query<T> query) throws UnifyException {
        query.ignoreEmptyCriteria(true);
    }

    protected GenericService getGenericService() {
        return genericService;
    }

    protected class CrudData {

        private Long parentId;

        private String title;

        private String caption;

        private List<T> recordList;

        private T record;

        private WrappedData<T> wrappedRecord;

        private Query<T> query;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public CrudData(Class<T> entityClass, String title) {
            this.title = title;
            recordList = new ArrayList<T>();
            query = new Query(entityClass);
        }

        public Long getParentId() {
            return parentId;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        public String getTitleKey() {
            return title;
        }

        public Query<T> getQuery() {
            return query;
        }

        public Class<T> getEntityClass() {
            return query.getEntityClass();
        }

        public List<T> getRecordList() {
            return recordList;
        }

        public void setRecordList(List<T> recordList) {
            this.recordList = recordList;
        }

        public T getRecord() {
            return record;
        }

        public void setRecord(T record) {
            this.record = record;
        }

        public WrappedData<T> getWrappedRecord() {
            return wrappedRecord;
        }

        public void setWrappedRecord(WrappedData<T> wrappedRecord) {
            this.wrappedRecord = wrappedRecord;
            this.record = wrappedRecord.getData();
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }
    }

}
