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
package com.tcdng.unify.web.ui.widget.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.AbstractSingleObjectValueStore;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.font.FontSymbolManager;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractValueListMultiControl;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.EventHandler;

/**
 * Represents a table with support for pagination, multiple selection and
 * windowing.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-table")
@UplAttributes({ @UplAttribute(name = "bodyStyle", type = String.class),
        @UplAttribute(name = "selectBinding", type = String.class),
        @UplAttribute(name = "summarySrc", type = String.class),
        @UplAttribute(name = "summaryProcList", type = String[].class),
        @UplAttribute(name = "summaryDependentList", type = UplElementReferences.class),
        @UplAttribute(name = "contentDependentList", type = UplElementReferences.class),
        @UplAttribute(name = "multiSelDependentList", type = UplElementReferences.class),
        @UplAttribute(name = "selDependentList", type = UplElementReferences.class),
        @UplAttribute(name = "multiSelect", type = boolean.class),
        @UplAttribute(name = "multiSelectCheckboxes", type = boolean.class, defaultVal = "true"),
        @UplAttribute(name = "pagination", type = boolean.class),
        @UplAttribute(name = "rowSelectable", type = boolean.class),
        @UplAttribute(name = "serialNumbers", type = boolean.class),
        @UplAttribute(name = "serialNumberSymbol", type = String.class, defaultVal = "*"),
        @UplAttribute(name = "windowed", type = boolean.class),
        @UplAttribute(name = "headerEllipsis", type = boolean.class),
        @UplAttribute(name = "contentEllipsis", type = boolean.class),
        @UplAttribute(name = "rowEventHandler", type = EventHandler[].class) })
public class Table extends AbstractValueListMultiControl<Table.Row, Object> {

    private static final int DEFAULT_ITEMS_PER_PAGE = 50;

    @Configurable
    private FontSymbolManager fontSymbolManager;

    private Control viewIndexCtrl;

    private int viewIndex;

    private Control currentPageCtrl;

    private int currentPage;

    private Control itemsPerPageCtrl;

    private int itemsPerPage;

    private Control columnIndexCtrl;

    private int columnIndex;

    private Control sortDirectionCtrl;

    private boolean sortDirection;

    private boolean sortable;

    private int actualItemsPerPage;

    private int totalPages;

    private int pageItemIndex;

    private int pageItemCount;

    private int totalItemCount;

    private Control multiSelectCtrl;

    private Control ascendingImageCtrl;

    private Control descendingImageCtrl;

    private List<Column> columnList;

    private int pageSelectedRowCount;

    private int selectedRowCount;

    private int naviPageStart;

    private int naviPageStop;

    private int visibleColumnCount;

    private List<String> contentDependentList;

    private List<String> selDependentList;

    private List<String> multiSelDependentList;

    private List<String> summaryDependentList;

    private String dataGroupId;

    public void setFontSymbolManager(FontSymbolManager fontSymbolManager) {
        this.fontSymbolManager = fontSymbolManager;
    }

    @Override
    public void populate(DataTransferBlock transferBlock) throws UnifyException {
        if (transferBlock != null) {
            DataTransferBlock childBlock = transferBlock.getChildBlock();
            ChildWidgetInfo childWidgetInfo = getChildWidgetInfo(childBlock.getId());
            Control control = (Control) childWidgetInfo.getWidget();

            if (childWidgetInfo.isExternal()) {
                control.setValueStore(getValueList().get(childBlock.getItemIndex()).getRowValueStore());
            } else {
                if (control == multiSelectCtrl) {
                    control.setValueStore(getValueList().get(childBlock.getItemIndex()).getRowValueStore());
                }
            }

            control.populate(childBlock);

            if (columnIndex >= 0 && (control == columnIndexCtrl || control == sortDirectionCtrl)) {
                getColumnList().get(columnIndex).setAscending(!sortDirection);
            }
        }
    }

    public void reset() throws UnifyException {
        currentPage = 0;
        for (Column column : getColumnList()) {
            if (column.isVisible()) {
                column.setAscending(true);
            }
        }
    }

    public void clear() throws UnifyException {
        invalidateValueList();
    }

    public String getBodyStyle() throws UnifyException {
        return getUplAttribute(String.class, "bodyStyle");
    }

    public String getSummarySrc() throws UnifyException {
        return getUplAttribute(String.class, "summarySrc");
    }

    public String[] getSummaryProcList() throws UnifyException {
        return getUplAttribute(String[].class, "summaryProcList");
    }

    public boolean isPagination() throws UnifyException {
        return getUplAttribute(boolean.class, "pagination");
    }

    public boolean isRowSelectable() throws UnifyException {
        return getUplAttribute(boolean.class, "rowSelectable");
    }

    public boolean isMultiSelect() throws UnifyException {
        return getUplAttribute(boolean.class, "multiSelect");
    }

    public boolean isShowMultiSelectCheckboxes() throws UnifyException {
        return getUplAttribute(boolean.class, "multiSelectCheckboxes");
    }

    public boolean isWindowed() throws UnifyException {
        return getUplAttribute(boolean.class, "windowed") || isPagination();
    }

    public boolean isSerialNumbers() throws UnifyException {
        return getUplAttribute(boolean.class, "serialNumbers");
    }

    public String getSerialNumberSymbol() throws UnifyException {
        return getUplAttribute(String.class, "serialNumberSymbol");
    }
    
    public boolean isSortable() throws UnifyException {
        return sortable;
    }

    public boolean isHeaderEllipsis() throws UnifyException {
        return getUplAttribute(boolean.class, "headerEllipsis");
    }

    public boolean isContentEllipsis() throws UnifyException {
        return getUplAttribute(boolean.class, "contentEllipsis");
    }

    public String getDataGroupId() {
        return dataGroupId;
    }

    public List<String> getContentDependentList() throws UnifyException {
        if (contentDependentList == null) {
            contentDependentList = getPageNames(getUplAttribute(UplElementReferences.class, "contentDependentList"));
        }
        return contentDependentList;
    }

    public List<String> getSelDependentList() throws UnifyException {
        if (selDependentList == null) {
            selDependentList = getPageNames(getUplAttribute(UplElementReferences.class, "selDependentList"));
        }
        return selDependentList;
    }

    public List<String> getSummaryDependentList() throws UnifyException {
        if (summaryDependentList == null) {
            summaryDependentList = getPageNames(getUplAttribute(UplElementReferences.class, "summaryDependentList"));
        }
        return summaryDependentList;
    }

    public List<String> getMultiSelDependentList() throws UnifyException {
        if (multiSelDependentList == null) {
            multiSelDependentList = getPageNames(getUplAttribute(UplElementReferences.class, "multiSelDependentList"));
        }
        return multiSelDependentList;
    }

    @Action
    public void sort() throws UnifyException {
        if (columnIndex >= 0) {
            ColumnState columnState = getColumnList().get(columnIndex);
            List<Row> list = getValueList();
            if (columnState != null && list != null && !list.isEmpty()) {
                Collections.sort(list, new RowComparator(columnState.getFieldName(), sortDirection));
                reIndex();

                // Sort original list to
                List<?> items = (List<?>) getValue();
                if (sortDirection) {
                    DataUtils.sortAscending(items, items.get(0).getClass(), columnState.getFieldName());
                } else {
                    DataUtils.sortDescending(items, items.get(0).getClass(), columnState.getFieldName());
                }
            }
        }
    }

    @Action
    public void shift() throws UnifyException {
        doShift();
        reIndex();
    }

    @Action
    public void delete() throws UnifyException {
        getValueList().remove(getViewIndex());
        reIndex();
    }
    
    public List<String> getColumnPropertyList() throws UnifyException {
        List<String> propertyList = new ArrayList<String>();
        for (ChildWidgetInfo childWidgetInfo : getChildWidgetInfos()) {
            if (childWidgetInfo.isExternal()) {
                propertyList.add(childWidgetInfo.getWidget().getBinding());
            }
        }
        return propertyList;
    }

    public List<? extends ColumnState> getColumnStates() throws UnifyException {
        return getColumnList();
    }

    public Control getViewIndexCtrl() {
        return viewIndexCtrl;
    }

    public Control getItemsPerPageCtrl() {
        return itemsPerPageCtrl;
    }

    public Control getCurrentPageCtrl() {
        return currentPageCtrl;
    }

    public String getSelectAllId() throws UnifyException {
        if (multiSelectCtrl != null) {
            multiSelectCtrl.setValueStore(null);
            return multiSelectCtrl.getPrefixedId("sela_");
        }
        return null;
    }

    public String getSelectGroupId() throws UnifyException {
        return getPrefixedId("sel_");
    }

    public Control getMultiSelectCtrl() {
        return multiSelectCtrl;
    }

    public Control getAscImageCtrl() {
        return ascendingImageCtrl;
    }

    public Control getDescImageCtrl() {
        return descendingImageCtrl;
    }

    public Control getColumnIndexCtrl() {
        return columnIndexCtrl;
    }

    public Control getSortDirectionCtrl() {
        return sortDirectionCtrl;
    }

    public String getRowId() throws UnifyException {
        return getPrefixedId("row_");
    }

    public String getShiftDirectionId() throws UnifyException {
        return null;
    }

    public String getShiftTopId() throws UnifyException {
        return null;
    }

    public String getShiftUpId() throws UnifyException {
        return null;
    }

    public String getShiftDownId() throws UnifyException {
        return null;
    }

    public String getShiftBottomId() throws UnifyException {
        return null;
    }

    public String getDeleteId() throws UnifyException {
        return null;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) throws UnifyException {
        this.currentPage = currentPage;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) throws UnifyException {
        this.itemsPerPage = itemsPerPage;
    }

    public String getSortField() throws UnifyException {
        return getColumnList().get(columnIndex).getFieldName();
    }

    public boolean isSortAscending() {
        return sortDirection;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public void setColumnVisible(String shortName, boolean visible) throws UnifyException {
        for (Column column : getColumnList()) {
            if (shortName.equals(column.getShortName())) {
                column.setVisible(visible);
                break;
            }
        }
    }

    public boolean isSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(boolean sortDirection) {
        this.sortDirection = sortDirection;
    }

    public void clearPageSelectedRowCount() {
        pageSelectedRowCount = 0;
    }

    public void incrementPageSelectedRowCount() {
        pageSelectedRowCount++;
    }

    public int getPageSelectedRowCount() {
        return pageSelectedRowCount;
    }

    public void clearVisibleColumnCount() {
        visibleColumnCount = 0;
    }

    public void incrementVisibleColumnCount() {
        visibleColumnCount++;
    }

    public int getSelectedRows() {
        return selectedRowCount;
    }

    public Object getSelectedItem() throws UnifyException {
        if (viewIndex >= 0) {
            List<Row> rowList = getValueList();
            if (rowList != null && viewIndex < rowList.size()) {
                return rowList.get(viewIndex).getItem();
            }
        }

        return null;
    }

    public Integer[] getSelectedRowIndexes() throws UnifyException {
        getValue();
        List<Row> rowList = getValueList();
        if (rowList == null || rowList.isEmpty()) {
            return DataUtils.ZEROLEN_INTEGER_ARRAY;
        }

        List<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < rowList.size(); i++) {
            if (rowList.get(i).isSelected()) {
                indexList.add(i);
            }
        }
        return DataUtils.toArray(Integer.class, indexList);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getSelectedItems(Class<T> itemClazz) throws UnifyException {
        getValue();
        List<Row> rowList = getValueList();
        if (rowList == null || rowList.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> selectedItems = new ArrayList<T>();
        for (Row row : rowList) {
            if (row.isSelected()) {
                selectedItems.add((T) row.getItem());
            }
        }

        return selectedItems;
    }

    public <T> List<T> getSelectedItemsColumnValues(Class<T> itemColumnClazz, String columnProperty)
            throws UnifyException {
        getValue();
        List<Row> rowList = getValueList();
        if (rowList == null || rowList.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> selectedItems = new ArrayList<T>();
        for (Row row : rowList) {
            if (row.isSelected()) {
                selectedItems.add(row.getRowValueStore().retrieve(itemColumnClazz, columnProperty));
            }
        }

        return selectedItems;
    }

    public int getVisibleColumnCount() {
        return visibleColumnCount;
    }

    public int getViewIndex() {
        return viewIndex;
    }

    public void setViewIndex(int viewIndex) {
        this.viewIndex = viewIndex;
    }

    public boolean isViewIndexAtFirst() {
        return viewIndex == 0;
    }

    public boolean isViewIndexAtLast() {
        return viewIndex >= (totalItemCount - 1);
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPageItemIndex() {
        return pageItemIndex;
    }

    public int getPageItemCount() {
        return pageItemCount;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public int getNaviPageStart() {
        return naviPageStart;
    }

    public int getNaviPageStop() {
        return naviPageStop;
    }

    public void pageCalculations() throws UnifyException {
        List<Row> writeRowList = getValueList();
        if (writeRowList != null) {
            pageCalculations(writeRowList.size());
        } else {
            pageCalculations(0);
        }
        if (isPagination()) {
            naviPageStart = currentPage - 5;
            if (naviPageStart < 0) {
                naviPageStart = 0;
            }
            naviPageStop = naviPageStart + 10;
            if (naviPageStop >= totalPages) {
                if (totalPages == 0) {
                    naviPageStop = 0;
                } else {
                    naviPageStop = totalPages - 1;
                }
            }
        }
    }

    @Override
    public void addPageAliases() throws UnifyException {
        addPageAlias(viewIndexCtrl);

        if (isPagination()) {
            addPageAlias(currentPageCtrl);
            addPageAlias(itemsPerPageCtrl);
        }

        if (isMultiSelect()) {
            addPageAlias(getSelectGroupId());
        }

        if (sortable) {
            addPageAlias(columnIndexCtrl);
            addPageAlias(sortDirectionCtrl);
        }

        if (isContainerEditable()) {
            addPageAlias(getDataGroupId());
        }
    }

    public String getWinId() throws UnifyException {
        return getPrefixedId("win_");
    }

    public String getHeaderId() throws UnifyException {
        return getPrefixedId("hdr_");
    }

    public String getBodyId() throws UnifyException {
        return getPrefixedId("bod_");
    }

    public String getBodyCellId() throws UnifyException {
        return getPrefixedId("bodc_");
    }

    public String getNavId() throws UnifyException {
        return getPrefixedId("nav_");
    }

    public String getNavLeftId() throws UnifyException {
        return getPrefixedId("navl_");
    }

    public String getNavRightId() throws UnifyException {
        return getPrefixedId("navr_");
    }

    @Override
    protected void doOnPageConstruct() throws UnifyException {
        viewIndexCtrl = (Control) addInternalChildWidget("!ui-hidden binding:viewIndex");
        if (isPagination()) {
            currentPageCtrl = (Control) addInternalChildWidget("!ui-hidden binding:currentPage");
            itemsPerPageCtrl = (Control) addInternalChildWidget(
                    "!ui-select styleClass:$e{tpselect} list:itemsperpagelist binding:itemsPerPage");
        }

        if (isMultiSelect()) {
            multiSelectCtrl = (Control) addInternalChildWidget("!ui-checkbox binding:selected");
        }

        getColumnList(); // Do this here to ensure sortable is appropriately set
        if (sortable) {
            if (fontSymbolManager != null) {
                ascendingImageCtrl = (Control) addInternalChildWidget(
                        "!ui-symbol symbol:$s{sort} style:$s{width:16px;height:16px;cursor:pointer;}");
                descendingImageCtrl = (Control) addInternalChildWidget(
                        "!ui-symbol symbol:$s{sort} style:$s{width:16px;height:16px;cursor:pointer;}");
            } else {
                ascendingImageCtrl = (Control) addInternalChildWidget(
                        "!ui-image src:$t{images/ascending.png} style:$s{width:16px;height:16px;cursor:pointer;}");
                descendingImageCtrl = (Control) addInternalChildWidget(
                        "!ui-image src:$t{images/descending.png} style:$s{width:16px;height:16px;cursor:pointer;}");
            }
            columnIndexCtrl = (Control) addInternalChildWidget("!ui-hidden binding:columnIndex", false, true);
            sortDirectionCtrl = (Control) addInternalChildWidget("!ui-hidden binding:sortDirection", false, true);
        }

        dataGroupId = getPrefixedId("data_");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Object> getItemList() throws UnifyException {
        return (List<Object>) getValue();
    }

    @Override
    protected Row newValue(Object item, int index) throws UnifyException {
        return new Row(item, index);
    }

    @Override
    protected void onCreateValueList(List<Row> valueList) throws UnifyException {
        selectedRowCount = 0;
        for (Row row : valueList) {
            if (row.isSelected()) {
                selectedRowCount++;
            }
        }
    }

    protected void doShift() throws UnifyException {

    }

    private void reIndex() throws UnifyException {
        List<Row> list = getValueList();
        if (list != null && !list.isEmpty()) {
            int len = list.size();
            for (int i = 0; i < len; i++) {
                list.get(i).setIndex(i);
            }
        }
    }

    public class Row implements RowState {

        private RowValueStore rowValueStore;

        private Object item;

        private boolean selected;

        public Row(Object item, int index) throws UnifyException {
            this.item = item;
            rowValueStore = new RowValueStore(this, getUplValueMarker(), index);
            String selectBinding = getUplAttribute(String.class, "selectBinding");
            if (selectBinding != null) {
                selected = (Boolean) ReflectUtils.findNestedBeanProperty(item, selectBinding);
                if (selected) {
                    selectedRowCount++;
                }
            }
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        public Object getItem() {
            return item;
        }

        public void setRowValueStore(RowValueStore rowValueStore) {
            this.rowValueStore = rowValueStore;
        }

        public void setIndex(int index) {
            rowValueStore.setDataIndex(index);
        }

        public RowValueStore getRowValueStore() {
            return rowValueStore;
        }

        public void setSelected(boolean selected) throws UnifyException {
            if (this.selected != selected) {
                this.selected = selected;
                String selectBinding = getUplAttribute(String.class, "selectBinding");
                if (selectBinding != null) {
                    DataUtils.setNestedBeanProperty(item, selectBinding, selected);
                }

                if (selected) {
                    selectedRowCount++;
                } else {
                    selectedRowCount--;
                }
            }
        }

    }

    public class RowValueStore extends AbstractSingleObjectValueStore<Row> {

        private RowValueStore(Row row) {
            this(row, null, -1);
        }

        private RowValueStore(Row row, String dataMarker, int dataIndex) {
            super(row, dataMarker, dataIndex);
        }

        @Override
        public boolean isGettable(String name) throws UnifyException {
            if ("selected".equals(name)) {
                return true;
            }

            return isTempValue(name) || ReflectUtils.isGettableField(storage.item.getClass(), name);
        }

        @Override
        public boolean isSettable(String name) throws UnifyException {
            if ("selected".equals(name)) {
                return true;
            }

            return ReflectUtils.isSettableField(storage.item.getClass(), name);
        }

        @Override
        protected Object doRetrieve(String property) throws UnifyException {
            if ("selected".equals(property)) {
                return storage.selected;
            }

            return ReflectUtils.findNestedBeanProperty(storage.item, property);
        }

        @Override
        protected void doStore(String property, Object value, Formatter<?> formatter) throws UnifyException {
            if ("selected".equals(property)) {
                storage.setSelected(DataUtils.convert(Boolean.class, value, formatter));
                return;
            }

            DataUtils.setNestedBeanProperty(storage.item, property, value, formatter);
        }

        public Object getItem() {
            return storage.item;
        }
    }

    public class Column implements ColumnState {

        private Control control;

        private String strippedStyle;

        private boolean ascending;

        public Column(Control control) {
            this.control = control;
        }

        @Override
        public boolean isSortable() throws UnifyException {
            return control.getUplAttribute(boolean.class, "sortable");
        }

        public void setAscending(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public boolean isAscending() {
            return ascending;
        }

        @Override
        public String getFieldName() throws UnifyException {
            return control.getBinding();
        }

        public Control getControl() {
            return control;
        }

        public String getShortName() throws UnifyException {
            return control.getShortName();
        }

        public void setVisible(boolean visible) {
            control.setVisible(visible);
        }

        public boolean isVisible() throws UnifyException {
            return control.isVisible();
        }

        public boolean isColumnSelectSummary() throws UnifyException {
            return control.getColumnSelectSummary();
        }

        public String getStrippedStyle() throws UnifyException {
            if (strippedStyle == null) {
                String style = control.getColumnStyle();
                int index = 0;
                if (StringUtils.isNotBlank(style) && (index = style.indexOf("width:")) >= 0) {
                    int endIndex = style.indexOf(';', index);
                    if (endIndex > index) {
                        return strippedStyle = (style.substring(0, index) + style.substring(endIndex + 1)).trim();
                    }
                }

                strippedStyle = "";
            }

            return strippedStyle;
        }

        public boolean isWithStrippedStyle() throws UnifyException {
            return StringUtils.isNotBlank(getStrippedStyle());
        }
    }

    private class RowComparator implements Comparator<Row> {

        private String property;

        private boolean ascending;

        public RowComparator(String property, boolean ascending) {
            this.property = property;
            this.ascending = ascending;
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compare(Row row1, Row row2) {
            try {
                if (ascending) {
                    return DataUtils.compareForSortAscending(
                            (Comparable<Object>) row1.getRowValueStore().retrieve(property),
                            (Comparable<Object>) row2.getRowValueStore().retrieve(property));
                }

                return DataUtils.compareForSortDescending(
                        (Comparable<Object>) row1.getRowValueStore().retrieve(property),
                        (Comparable<Object>) row2.getRowValueStore().retrieve(property));
            } catch (UnifyException e) {
            }
            return 0;
        }

    }

    public List<Column> getColumnList() throws UnifyException {
        if (columnList == null) {
            columnList = new ArrayList<Column>();
            for (ChildWidgetInfo childWidgetInfo : getChildWidgetInfos()) {
                if (childWidgetInfo.isExternal()) {
                    Column column = new Column((Control) childWidgetInfo.getWidget());
                    if (column.isSortable()) {
                        sortable = true;
                    }
                    columnList.add(column);
                }
            }
            columnList = Collections.unmodifiableList(columnList);
        }
        return columnList;
    }

    private void pageCalculations(int itemCount) throws UnifyException {
        if (itemCount == 0) {
            currentPage = 0;
            totalPages = 0;
            pageItemIndex = 0;
            pageItemCount = 0;
            totalItemCount = 0;
            actualItemsPerPage = DEFAULT_ITEMS_PER_PAGE;
            itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
            return;
        }

        if (!isPagination()) {
            currentPage = 0;
            totalPages = 1;
            pageItemIndex = 0;
            pageItemCount = itemCount;
            totalItemCount = itemCount;
            actualItemsPerPage = itemCount;
            itemsPerPage = itemCount;
            return;
        }

        totalItemCount = itemCount;
        if (itemsPerPage == 0) {
            itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
        }
        actualItemsPerPage = itemsPerPage;
        if (actualItemsPerPage < 0) { // Allow less than. Negative means
                                      // all items (single page)
            actualItemsPerPage = totalItemCount;
        }

        // Calculate total pages and normalize current page
        totalPages = totalItemCount / actualItemsPerPage;
        int lastItemCount = totalItemCount % actualItemsPerPage;
        if (lastItemCount > 0) {
            totalPages++;
        } else {
            lastItemCount = actualItemsPerPage;
        }

        if (currentPage < 0) {
            currentPage = 0;
        } else if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }

        // Load current page
        pageItemIndex = currentPage * actualItemsPerPage;
        pageItemCount = actualItemsPerPage;
        if (currentPage == (totalPages - 1)) { // Current page is last
                                               // page
            pageItemCount = lastItemCount;
        }
    }
}
