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
package com.tcdng.unify.web.ui.writer.control;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.web.ui.Control;
import com.tcdng.unify.web.ui.EventHandler;
import com.tcdng.unify.web.ui.PushType;
import com.tcdng.unify.web.ui.ResponseWriter;
import com.tcdng.unify.web.ui.Widget;
import com.tcdng.unify.web.ui.control.ColumnState;
import com.tcdng.unify.web.ui.control.Table;
import com.tcdng.unify.web.ui.control.Table.Column;
import com.tcdng.unify.web.ui.control.Table.Row;
import com.tcdng.unify.web.ui.writer.AbstractControlWriter;
import com.tcdng.unify.web.util.HtmlUtils;

/**
 * Table writer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Writes(Table.class)
@Component("table-writer")
public class TableWriter extends AbstractControlWriter {

    private static final String SELECT_CLASSNAME_BASE = "tsel";

    @Override
    protected void doWriteStructureAndContent(ResponseWriter writer, Widget widget) throws UnifyException {
        Table table = (Table) widget;
        table.pageCalculations();
        writer.write("<div");
        if (table.isContentEllipsis()) {
            writeTagStyleClass(writer, table, "ui-table-cellipsis");
        } else {
            writeTagStyleClass(writer, table);
        }
        writeTagStyle(writer, table);
        writer.write(">");
        if (table.isWindowed()) {
            // String id = table.getId();
            writer.write("<div");
            writeTagId(writer, table.getWinId());
            writeTagStyleClass(writer, "twin");
            writeTagStyle(writer, "display:table;table-layout:fixed;width:100%;height:100%;");
            writer.write(">");

            // Header
            writer.write("<div style=\"display:table-row;\"><div style=\"display:table-cell;\">");
            writer.write(
                    "<div><table  class=\"thframe\" style=\"table-layout:fixed;width:100%;\"><tr><td><div style=\"overflow:hidden;\">");
            writer.write("<div");
            writeTagId(writer, table.getHeaderId());
            writeTagStyle(writer, "position:relative;");
            writer.write(">");
            writer.write("<table  class=\"thead\" style=\"table-layout:fixed;width:100%;\">");
            writeHeaderRow(writer, table);
            writer.write("</table></div>");
            writer.write("</div></td><td style=\"width:17px;\"></td></tr></table></div>");
            writer.write("</div></div>");

            // Body
            writer.write("<div style=\"display:table-row;height:100%;\">");
            writer.write("<div");
            writeTagId(writer, table.getBodyCellId());
            writeTagStyle(writer, "height:100%;");
            writer.write(">");
            writer.write("<div");
            writeTagId(writer, table.getBodyId());
            writeTagStyleClass(writer, "tbody");
            writeTagStyle(writer, table.getBodyStyle());
            writer.write(">");
            writer.write("<span><table");
            writeTagId(writer, table);
            writeTagStyleClass(writer, "ttable tfixed");
            writeTagStyle(writer, "display:none;");
            writer.write(">");
            writeBodyRows(writer, table);
            writer.write("</table></span></div>");
            writer.write("</div></div>");

            if (table.isPagination()) {
                // Pagination
                writer.write("<div style=\"display:table-row;\"><div style=\"display:table-cell;\">");
                writePaginationRow(writer, table);
                writer.write("</div></div>");
            }
            writer.write("</div>");
        } else {
            writer.write("<div>");
            writer.write("<div><table");
            writeTagId(writer, table);
            writeTagStyleClass(writer, "ttable");
            writer.write(">");
            writeHeaderRow(writer, table);
            writeBodyRows(writer, table);
            writer.write("</table></div>");
            if (table.isPagination()) {
                writePaginationRow(writer, table);
            }
            writer.write("</div>");
        }

        if (table.isMultiSelect()) {
            writeHiddenPush(writer, table.getSelectGroupId(), PushType.GROUP);
        }

        if (table.isContainerEditable()) {
            writeHiddenPush(writer, table.getDataGroupId(), PushType.GROUP);
        }

        writer.writeStructureAndContent(table.getViewIndexCtrl());
        if (table.isSortable()) {
            table.setColumnIndex(-1);
            writer.writeStructureAndContent(table.getColumnIndexCtrl());
            writer.writeStructureAndContent(table.getSortDirectionCtrl());
        }
        writer.write("</div>");
    }

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        super.doWriteBehavior(writer, widget);
        Table table = (Table) widget;

        // Internal control behavior
        Control itemsPerPageCtrl = table.getItemsPerPageCtrl();
        if (itemsPerPageCtrl != null) {
            writer.writeBehaviour(itemsPerPageCtrl);
        }

        // External control behavior
        List<Row> writeRowList = table.getValueList();
        int index = table.getPageItemIndex();
        int lastIndex = index + table.getPageItemCount();
        for (; index < lastIndex; index++) {
            ValueStore itemValueStore = writeRowList.get(index).getItemValueStore();

            for (Column column : table.getColumnList()) {
                if (column.isVisible()) {
                    Control control = column.getControl();
                    control.setValueStore(itemValueStore);
                    writer.writeBehaviour(control);
                }
            }
        }

        // Row behavior if any
        EventHandler[] rowEventHandlers = table.getUplAttribute(EventHandler[].class, "rowEventHandler");
        if (rowEventHandlers != null) {
            for (EventHandler rowEventHandler : rowEventHandlers) {
                writer.writeBehavior(rowEventHandler, table.getRowId());
            }
        }

        // Append table rigging
        writer.write("ux.rigTable({");
        writer.write("\"pId\":\"").write(table.getId()).write('"');
        writer.write(",\"pContId\":\"").write(table.getContainerId()).write('"');
        writer.write(",\"pCmdURL\":\"");
        writer.writeCommandURL();
        writer.write('"');
        writer.write(",\"pIdxCtrlId\":\"").write(table.getViewIndexCtrl().getId()).write('"');
        writer.write(",\"pBaseIdx\":").write(table.getPageItemIndex());
        writer.write(",\"pSelectable\":").write(table.isRowSelectable());
        if (table.isRowSelectable()) {
            writer.write(",\"pSelClassNm\":\"").write(getSelectClassName()).write("\"");
            writer.write(",\"pSelDepList\":");
            writer.writeJsonStringArray(table.getSelDependentList());
        }
        writer.write(",\"pWindowed\":").write(table.isWindowed());
        writer.write(",\"pPagination\":").write(table.isPagination());
        writer.write(",\"pItemCount\":").write(table.getPageItemCount());
        if (table.isPagination()) {
            writer.write(",\"pCurrPgCtrlId\":\"").write(table.getCurrentPageCtrl().getId()).write('"');
            writer.write(",\"pItemPerPgCtrlId\":\"").write(table.getItemsPerPageCtrl().getId()).write('"');
            writer.write(",\"pCurrPage\":").write(table.getCurrentPage());
            writer.write(",\"pPageCount\":").write(table.getTotalPages());
            writer.write(",\"pNaviStart\":").write(table.getNaviPageStart());
            writer.write(",\"pNaviStop\":").write(table.getNaviPageStop());
        }

        if (table.getPageItemCount() <= 0) {
            writer.write(",\"pConDepList\":");
            writer.writeJsonStringArray(table.getContentDependentList());
        }

        writer.write(",\"pMultiSel\":").write(table.isMultiSelect());
        if (table.isMultiSelect()) {
            writer.write(",\"pSelAllId\":\"").write(table.getSelectAllId()).write('"');
            writer.write(",\"pSelGrpId\":\"").write(table.getSelectGroupId()).write('"');
            writer.write(",\"pVisibleSel\":").write(table.getPageSelectedRowCount());
            writer.write(",\"pHiddenSel\":").write(table.getSelectedRows() - table.getPageSelectedRowCount());
            writer.write(",\"pMultiSelDepList\":");
            writer.writeJsonStringArray(table.getMultiSelDependentList());
        }

        boolean shiftable = table.getShiftDirectionId() != null;
        writer.write(",\"pShiftable\":").write(shiftable);
        if (shiftable) {
            writer.write(",\"pShiftDirId\":\"").write(table.getShiftDirectionId()).write('"');
            writer.write(",\"pShiftTopId\":\"").write(table.getShiftTopId()).write('"');
            writer.write(",\"pShiftUpId\":\"").write(table.getShiftUpId()).write('"');
            writer.write(",\"pShiftDownId\":\"").write(table.getShiftDownId()).write('"');
            writer.write(",\"pShiftBottomId\":\"").write(table.getShiftBottomId()).write('"');
        }

        writer.write(",\"pSortable\":").write(table.isSortable());
        if (table.isSortable()) {
            writer.write(",\"pColIdxId\":\"").write(table.getColumnIndexCtrl().getId()).write('"');
            writer.write(",\"pSortDirId\":\"").write(table.getSortDirectionCtrl().getId()).write('"');
            writer.write(",\"pSortAscId\":\"").write(table.getAscImageCtrl().getId()).write('"');
            writer.write(",\"pSortDescId\":\"").write(table.getDescImageCtrl().getId()).write('"');
            writer.write(",\"pSortColList\":[");
            boolean appendSym = false;
            List<? extends ColumnState> columnStates = table.getColumnStates();
            for (int i = 0; i < columnStates.size(); i++) {
                ColumnState columnState = columnStates.get(i);
                if (columnState.isSortable()) {
                    if (appendSym)
                        writer.write(',');
                    else
                        appendSym = true;
                    writer.write('{');
                    writer.write("\"idx\":").write(i);
                    writer.write(",\"ascend\":").write(columnState.isAscending());
                    writer.write(",\"field\":\"").write(columnState.getFieldName()).write('"');
                    writer.write('}');
                }
            }
            writer.write(']');
        }
        writer.write("});");

    }

    private void writeHeaderRow(ResponseWriter writer, Table table) throws UnifyException {
        writer.write("<tr>");
        table.clearVisibleColumnCount();
        if (table.getUplAttribute(boolean.class, "serialNumbers")) {
            writer.write("<th class=\"thserialno tth\">*</th>");
            table.incrementVisibleColumnCount();
        }

        if (table.isMultiSelect()) {
            writer.write("<th class=\"thselect tth\">");
            Control multiSelectCtrl = table.getMultiSelectCtrl();
            multiSelectCtrl.setValueStore(null);
            multiSelectCtrl.setGroupId(null);
            writer.writeStructureAndContent(multiSelectCtrl, table.getSelectAllId());
            writer.write("</th>");
            table.incrementVisibleColumnCount();
        }

        boolean isHeaderEllipsis = table.isHeaderEllipsis();
        for (Column column : table.getColumnList()) {
            if (column.isVisible()) {
                Control control = column.getControl();
                writer.write("<th");
                writeTagStyleClass(writer, "tth");
                writeTagStyle(writer, HtmlUtils.extractStyleAttribute(control.getColumnStyle(), "width"));
                if (isHeaderEllipsis) {
                    writer.write("><span class=\"theadtitle theadellipsis\">");
                } else {
                    writer.write("><span class=\"theadtitle\">");
                }

                String caption = control.getCaption();
                if (caption != null) {
                    writer.write(caption);
                } else {
                    writer.writeHtmlFixedSpace();
                }

                if (column.isSortable()) {
                    writer.write("</span>&nbsp;&nbsp;<span class=\"theadwdg\">");
                    Control imageCtrl = null;
                    if (column.isAscending()) {
                        imageCtrl = table.getAscImageCtrl();
                    } else {
                        imageCtrl = table.getDescImageCtrl();
                    }
                    writer.writeStructureAndContent(imageCtrl, imageCtrl.getPrefixedId(column.getFieldName() + '_'));
                    writer.write("</span>");
                }
                writer.write("</th>");
                table.incrementVisibleColumnCount();
            }
        }
        writer.write("</tr>");
    }

    private void writeBodyRows(ResponseWriter writer, Table table) throws UnifyException {
        if (table.getPageItemCount() > 0) {
            List<Row> writeRowList = table.getValueList();
            boolean isWindowed = table.isWindowed();
            int index = table.getPageItemIndex();
            int lastIndex = index + table.getPageItemCount();

            table.clearPageSelectedRowCount();

            boolean isSerialNo = table.isSerialNumbers();
            boolean isMultiSelect = table.isMultiSelect();
            boolean isContainerDisabled = table.isContainerDisabled();
            boolean isContainerEditable = table.isContainerEditable();

            String dataGroupId = null;
            if (isContainerEditable) {
                dataGroupId = table.getDataGroupId();
            }

            Control multiSelectCtrl = null;
            if (isMultiSelect) {
                multiSelectCtrl = table.getMultiSelectCtrl();
                multiSelectCtrl.setGroupId(table.getSelectGroupId());
            }

            // Set column mode
            for (Column column : table.getColumnList()) {
                if (column.isVisible()) {
                    Control control = column.getControl();
                    control.setDisabled(isContainerDisabled);
                    control.setEditable(isContainerEditable);
                    control.setGroupId(dataGroupId);
                }
            }

            // Write rows
            boolean firstRowWrite = true;
            // Enter parent style mode. Column rendering will use table style if supported
            // by column control.
            writer.setParentStyleClassMode(true);
            for (; index < lastIndex; index++) {
                writer.write("<tr");
                if (index % 2 == 0) {
                    writeTagStyleClass(writer, "todd");
                } else {
                    writeTagStyleClass(writer, "teven");
                }

                writeTagName(writer, table.getRowId());
                writer.write(">");

                int columnIndex = 0;
                if (isSerialNo) {
                    writer.write("<td");
                    if (table.isWindowed()) {
                        writeTagStyleClass(writer, "thserialnol");
                    } else {
                        writeTagStyleClass(writer, "thserialno");
                    }
                    writer.write(">");
                    writer.write(index + 1); // Localization?
                    writer.write(".</td>");
                    columnIndex++;
                }

                Row row = writeRowList.get(index);
                if (isMultiSelect) {
                    if (row.isSelected()) {
                        table.incrementPageSelectedRowCount();
                    }

                    multiSelectCtrl.setValueStore(row.getRowValueStore());

                    writer.write("<td");
                    if (table.isWindowed() && columnIndex == 0) {
                        writeTagStyleClass(writer, "thselectl");
                    } else {
                        writeTagStyleClass(writer, "thselect");
                    }
                    
                    writer.write(">");
                    writer.writeStructureAndContent(multiSelectCtrl);
                    writer.write("</td>");
                    columnIndex++;
                }

                ValueStore itemValueStore = row.getItemValueStore();
                for (Column column : table.getColumnList()) {
                    if (column.isVisible()) {
                        Control control = column.getControl();
                        control.setValueStore(itemValueStore);
                        writer.write("<td");
                        // Optimization : Do not set class for each TD element. Set in CSS file only.
                        // writeTagStyleClass(writer, "ttd");
                        // Optimization : write column style information for first row only
                        if (firstRowWrite) {
                            String columnStyle = control.getColumnStyle();
                            if (isWindowed) {
                                writer.write(" style=\"");
                                if (!StringUtils.isBlank(columnStyle)) {
                                    writer.write(columnStyle);
                                }
                                if (columnIndex == 0) {
                                    writer.write("border-left:0px;");
                                }
                                writer.write("\"");
                            } else {
                                writeTagStyle(writer, columnStyle);
                            }
                        }
                        writer.write(">");
                        writer.writeStructureAndContent(control);
                        writer.write("</td>");
                        columnIndex++;
                    }
                }
                writer.write("</tr>");

                firstRowWrite = false;
            }
            // Disable parent style mode
            writer.setParentStyleClassMode(false);

        } else {
            writer.write("<tr class=\"tnoitems ").write(getSelectClassName()).write("\"><td");
            if (!table.isWindowed()) {
                writer.write(" colspan = \"").write(table.getVisibleColumnCount()).write('"');
            }
            writer.write('>');
            writer.write(getSessionMessage("table.no.items.to.display"));
            writer.write("</td></tr>");
        }
    }

    private String getSelectClassName() throws UnifyException {
        UserToken userToken = getUserToken();
        if (userToken != null && !StringUtils.isBlank(userToken.getColorScheme())) {
            return SELECT_CLASSNAME_BASE + userToken.getColorScheme();
        }

        return SELECT_CLASSNAME_BASE;
    }

    private void writePaginationRow(ResponseWriter writer, Table table) throws UnifyException {
        writer.write("<table class=\"tpagn\" style=\"table-layout:fixed;width:100%;\"><tr>");
        writer.write("<td class=\"tpnavleft\">");
        writer.write("<span>");
        if (table.getPageItemCount() > 0) {
            writer.write(getSessionMessage("table.items.of.total.displayed", table.getPageItemIndex() + 1,
                    table.getPageItemIndex() + table.getPageItemCount(), table.getTotalItemCount()));
        } else {
            writer.write(getSessionMessage("table.no.item.displayed"));
        }
        writer.write("</span>");
        writer.write("</td><td class=\"tpnavright\">");
        writer.write("<div>");
        writer.write("<div class=\"tpnavrightsec\">");
        writer.write("<span class=\"tpnavmsg\">").write(getSessionMessage("table.items.per.page")).write("</span>");
        writer.write("</div>");
        writer.write("<div class=\"tpnavrightsec\">");
        writer.writeStructureAndContent(table.getCurrentPageCtrl());
        writer.writeStructureAndContent(table.getItemsPerPageCtrl());
        writer.write("</div>");
        writer.write("<div class=\"tpnavrightsec\">");
        writer.write("<span");
        writeTagId(writer, table.getNavLeftId());
        writeTagStyleClass(writer, "tpnavpage");
        writer.write(">");
        writer.write("&lt;</span>");
        int naviPageStop = table.getNaviPageStop();
        String id = table.getId();
        for (int i = table.getNaviPageStart(); i <= naviPageStop;) {
            writer.write("&nbsp;<span id=\"nav_").write(id).write(i).write('"');
            if (i == table.getCurrentPage()) {
                writeTagStyleClass(writer, "tpnavcurr");
            } else {
                writeTagStyleClass(writer, "tpnavpage");
            }
            if (table.getPageItemCount() > 0) {
                writer.write(">").write(++i).write("</span>");
            } else {
                writer.write(">").write(i++).write("</span>");
            }
        }
        writer.writeHtmlFixedSpace();
        writer.write("<span");
        writeTagId(writer, table.getNavRightId());
        writeTagStyleClass(writer, "tpnavpage");
        writer.write(">");
        writer.write("&gt;</span>");
        writer.write("</div>");
        writer.write("</td>");
        writer.write("</tr></table>");
    }
}
