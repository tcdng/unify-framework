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
package com.tcdng.unify.web.ui.widget.writer.layout;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.TabularLayout;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.layout.VerticalLayout;

/**
 * Vertical layout writer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Writes(VerticalLayout.class)
@Component("verticallayout-writer")
public class VerticalLayoutWriter extends AbstractTabularLayoutWriter {

    @Override
    protected void writeTableContent(ResponseWriter writer, TabularLayout layout, Container container)
            throws UnifyException {
        int rowIndex = 0;
        boolean isAlternate = container.isAlternate();
        for (String longName : container.getLayoutWidgetLongNames()) {
            Widget widget = container.getWidgetByLongName(longName);
            widget.setAlternateMode(isAlternate);
            if (widget.isVisible()) {
                appendRowStart(writer, layout, rowIndex);
                appendCellContent(writer, layout, widget, rowIndex, 0);
                appendRowEnd(writer);
                rowIndex++;
            } else if (widget.isHidden()) {
                writer.writeStructureAndContent(widget);
            }
        }
    }

    @Override
    protected void writeRepeatTableContent(ResponseWriter writer, TabularLayout layout, Container container)
            throws UnifyException {
        int rowIndex = 0;
        final Widget widget = container.getRepeatWidget();
        boolean isAlternate = container.isAlternate();
        for (ValueStore valueStore : container.getRepeatValueStores()) {
            widget.setValueStore(valueStore);
            widget.setAlternateMode(isAlternate);
            if (widget.isVisible()) {
                appendRowStart(writer, layout, rowIndex);
                appendCellContent(writer, layout, widget, rowIndex, 0);
                appendRowEnd(writer);
                rowIndex++;
            } else if (widget.isHidden()) {
                writer.writeStructureAndContent(widget);
            }
        }
    }

}
