/*
 * Copyright 2018-2025 The Code Department.
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
package com.tcdng.unify.web.ui.widget.writer.control;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.html.HtmlUtils;
import com.tcdng.unify.core.util.json.JsonWriter;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.PushType;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.ColumnState;
import com.tcdng.unify.web.ui.widget.control.Table;
import com.tcdng.unify.web.ui.widget.control.Table.Column;
import com.tcdng.unify.web.ui.widget.control.Table.Row;
import com.tcdng.unify.web.ui.widget.control.Table.RowValueStore;
import com.tcdng.unify.web.ui.widget.writer.AbstractControlWriter;

/**
 * Table writer.
 * 
 * @author The Code Department
 * @since 4.1
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
			writeTagStyleClassWithTrailingExtraStyleClasses(writer, table, "ui-table-cellipsis");
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

			writer.writeStructureAndContent(table.getBodyYCtrl());
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
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);
		Table table = (Table) widget;

		// Internal control behavior
		Control itemsPerPageCtrl = table.getItemsPerPageCtrl();
		if (itemsPerPageCtrl != null) {
			writer.writeBehavior(itemsPerPageCtrl);
		}

		Control multiSelectCtrl = table.getMultiSelectCtrl();
		if (multiSelectCtrl != null) {
			multiSelectCtrl.setValueStore(null);
			multiSelectCtrl.setGroupId(null);
			writer.writeBehavior(multiSelectCtrl, table.getSelectAllId());
		}

		// External control behavior
		List<Row> writeRowList = table.getValueList();
		int index = table.getPageItemIndex();
		int lastIndex = index + table.getPageItemCount();
		for (; index < lastIndex; index++) {
			ValueStore itemValueStore = writeRowList.get(index).getRowValueStore();

			if (multiSelectCtrl != null) {
				multiSelectCtrl.setValueStore(itemValueStore);
				writer.writeBehavior(multiSelectCtrl);
			}

			for (Column column : table.getColumnList()) {
				if (column.isVisible()) {
					Control control = column.getControl();
					control.setValueStore(itemValueStore);
					writer.writeBehavior(control);
				}
			}
		}

		// Row behavior if any
		EventHandler[] rowEventHandlers = table.getUplAttribute(EventHandler[].class, "rowEventHandler");
		if (rowEventHandlers != null) {
			for (EventHandler rowEventHandler : rowEventHandlers) {
				writer.writeBehavior(rowEventHandler, table.getRowId(), null, null);
			}
		}

		// Get summary
		if (table.isMultiSelect()) {
		}

		// Append table rigging
		writer.beginFunction("ux.rigTable");
		writer.writeParam("pId", table.getId());
		writer.writeParam("pContId", table.getContainerId());
		writer.writeCommandURLParam("pCmdURL");
		writer.writeParam("pIdxCtrlId", table.getViewIndexCtrl().getId());
		writer.writeParam("pBaseIdx", table.getPageItemIndex());
		writer.writeParam("pSelectable", table.isRowSelectable());
		if (table.isRowSelectable()) {
			writer.writeParam("pSelClassNm", getSelectClassName());
			writer.writeParam("pSelDepList", DataUtils.toArray(String.class, table.getSelDependentList()));
		}
		writer.writeParam("pWindowed", table.isWindowed());
		if (table.isWindowed()) {
			writer.writeParam("pBodyYCtrlId", table.getBodyYCtrl().getId());
			writer.writeParam("pBodyY", table.getBodyY());
		}
		
		writer.writeParam("pPagination", table.isPagination());
		writer.writeParam("pItemCount", table.getPageItemCount());
		if (table.isPagination()) {
			writer.writeParam("pCurrPgCtrlId", table.getCurrentPageCtrl().getId());
			writer.writeParam("pItemPerPgCtrlId", table.getItemsPerPageCtrl().getId());
			writer.writeParam("pCurrPage", table.getCurrentPage());
			writer.writeParam("pPageCount", table.getTotalPages());
			writer.writeParam("pNaviStart", table.getNaviPageStart());
			writer.writeParam("pNaviStop", table.getNaviPageStop());
		}

		if (table.getPageItemCount() <= 0) {
			writer.writeParam("pConDepList", DataUtils.toArray(String.class, table.getContentDependentList()));
		}

		writer.writeParam("pMultiSel", table.isMultiSelect());
		if (table.isMultiSelect()) {
			// Normal multi-select details
			if (!table.isRowSelectable()) {
				writer.writeParam("pSelClassNm", getSelectClassName());
			}

			writer.writeParam("pSelAllId", table.getSelectAllId());
			writer.writeParam("pSelGrpId", table.getSelectGroupId());
			writer.writeParam("pVisibleSel", table.getPageSelectedRowCount());
			writer.writeParam("pHiddenSel", table.getSelectedRows() - table.getPageSelectedRowCount());
			writer.writeParam("pMultiSelDepList", DataUtils.toArray(String.class, table.getMultiSelDependentList()));

			// Summary columns
			int summaryColIndex = 1; // Because of multi-select column
			if (table.isSerialNumbers()) {
				summaryColIndex++;
			}

			String summarySrc = table.getSummarySrc();
			if (StringUtils.isNotBlank(summarySrc)) {
				writer.writeParam("pSumSrc", summarySrc);
				writer.writeParam("pSumProcList", table.getSummaryProcList());
				writer.writeParam("pSumDepList", DataUtils.toArray(String.class, table.getSummaryDependentList()));
			}

			JsonWriter jw = new JsonWriter();
			jw.beginArray();
			for (Column column : table.getColumnList()) {
				if (column.isVisible()) {
					if (column.isColumnSelectSummary()) {
						Control control = column.getControl();
						jw.beginObject();
						jw.write("idx", summaryColIndex);
						jw.write("nm", control.getShortName());
						jw.endObject();
					}
					summaryColIndex++;
				}
			}
			jw.endArray();
			writer.writeParam("pSumColList", jw);
		}

		boolean shiftable = table.getShiftDirectionId() != null;
		writer.writeParam("pShiftable", shiftable);
		if (shiftable) {
			writer.writeParam("pShiftDirId", table.getShiftDirectionId());
			writer.writeParam("pShiftTopId", table.getShiftTopId());
			writer.writeParam("pShiftUpId", table.getShiftUpId());
			writer.writeParam("pShiftDownId", table.getShiftDownId());
			writer.writeParam("pShiftBottomId", table.getShiftBottomId());
			writer.writeParam("pDeleteId", table.getDeleteId());
		}

		writer.writeParam("pSortable", table.isSortable());
		if (table.isSortable()) {
			writer.writeParam("pColIdxId", table.getColumnIndexCtrl().getId());
			writer.writeParam("pSortDirId", table.getSortDirectionCtrl().getId());
			writer.writeParam("pSortAscId", table.getAscImageCtrl().getId());
			writer.writeParam("pSortDescId", table.getDescImageCtrl().getId());

			JsonWriter jw = new JsonWriter();
			jw.beginArray();
			List<? extends ColumnState> columnStates = table.getColumnStates();
			for (int i = 0; i < columnStates.size(); i++) {
				ColumnState columnState = columnStates.get(i);
				if (columnState.isSortable()) {
					jw.beginObject();
					jw.write("idx", i);
					jw.write("ascend", columnState.isAscending());
					jw.write("field", columnState.getFieldName());
					jw.endObject();
				}
			}
			jw.endArray();
			writer.writeParam("pSortColList", jw);
		}
		writer.endFunction();

	}

	private void writeHeaderRow(ResponseWriter writer, Table table) throws UnifyException {
		writer.write("<tr>");
		table.clearVisibleColumnCount();
		if (table.isSerialNumbers()) {
			String snSym = table.getSerialNumberSymbol();
			if (snSym != null) {
				writer.write("<th class=\"thserialno tth\">").write(snSym).write("</th>");
			} else {
				writer.write("<th class=\"thserialno tth\"></th>");
			}

			table.incrementVisibleColumnCount();
		}

		boolean isMultiSelect = table.isMultiSelect();
		boolean isShowMultiSelectCheckboxes = table.isShowMultiSelectCheckboxes();
		boolean isHideMultiSelectBorder = isMultiSelect && !isShowMultiSelectCheckboxes;

		if (isMultiSelect) {
			if (isShowMultiSelectCheckboxes) {
				writer.write("<th class=\"thselect tth\">");
			} else {
				writer.write("<th class=\"thselectx\">");
			}

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
				String columnStyle = HtmlUtils.extractStyleAttribute(control.getColumnStyle(), "width");
				if (isHideMultiSelectBorder) {
					columnStyle += "border-left:0px;";
					isHideMultiSelectBorder = false;
				}
				writeTagStyle(writer, columnStyle);

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
			final boolean isWindowed = table.isWindowed();
			int index = table.getPageItemIndex();
			int lastIndex = index + table.getPageItemCount();

			table.clearPageSelectedRowCount();

			final boolean isSerialNo = table.isSerialNumbers();
			final boolean isMultiSelect = table.isMultiSelect();
			final boolean isContainerDisabled = table.isContainerDisabled();
			final boolean isContainerEditable = table.isContainerEditable();
			final boolean isShowMultiSelectCheckboxes = table.isShowMultiSelectCheckboxes();
			final boolean isHideMultiSelectBorder = isMultiSelect && !isShowMultiSelectCheckboxes;

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
			String columnStyle = null;
			boolean firstRowWrite = true;
			// Enter writer table mode. Column rendering will use table style if supported
			// by column control.
			writer.setTableMode(true);
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
				RowValueStore rowValueStore = row.getRowValueStore();
				if (isMultiSelect) {
					if (row.isSelected()) {
						table.incrementPageSelectedRowCount();
					}

					multiSelectCtrl.setValueStore(rowValueStore);

					writer.write("<td");
					if (isShowMultiSelectCheckboxes) {
						if (table.isWindowed() && columnIndex == 0) {
							writeTagStyleClass(writer, "thselectl");
						} else {
							writeTagStyleClass(writer, "thselect");
						}
						columnIndex++;
					} else {
						writeTagStyleClass(writer, "thselectx");
						columnIndex = 0;
					}

					writer.write(">");
					writer.writeStructureAndContent(multiSelectCtrl);
					writer.write("</td>");
				}

				for (Column column : table.getColumnList()) {
					if (column.isVisible()) {
						Control control = column.getControl();
						control.setValueStore(rowValueStore);
						writer.write("<td");
						// Optimization : Do not set class for each TD element. Set in CSS file only.
						// writeTagStyleClass(writer, "ttd");
						// Optimization : write column style information for first row only
						if (firstRowWrite) {
							columnStyle = control.getColumnStyle();
						} else {
							columnStyle = column.getStrippedStyle();
						}

						if (isWindowed || isHideMultiSelectBorder) {
							if (columnIndex == 0) {
								writer.write(" style=\"border-left:0px;");
								if (StringUtils.isNotBlank(columnStyle)) {
									writer.write(columnStyle);
								}
								writer.write("\"");
							} else {
								if (StringUtils.isNotBlank(columnStyle)) {
									writer.write(" style=\"").write(columnStyle).write("\"");
								}
							}
						} else {
							writeTagStyle(writer, columnStyle);
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
			// Disable writer table mode
			writer.setTableMode(false);

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
		if (userToken != null && StringUtils.isNotBlank(userToken.getColorScheme())) {
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
