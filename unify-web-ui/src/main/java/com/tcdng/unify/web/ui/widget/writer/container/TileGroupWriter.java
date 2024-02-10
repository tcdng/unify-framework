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
package com.tcdng.unify.web.ui.widget.writer.container;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Writes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.ui.Tile;
import com.tcdng.unify.web.ui.widget.Container;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.EventHandler;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.container.TileGroup;
import com.tcdng.unify.web.ui.widget.writer.AbstractContainerWriter;

/**
 * Tile group writer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Writes(TileGroup.class)
@Component("tilegroup-writer")
public class TileGroupWriter extends AbstractContainerWriter {

    @Override
	protected void doWriteBehavior(ResponseWriter writer, Widget widget, EventHandler[] handlers)
			throws UnifyException {
		super.doWriteBehavior(writer, widget, handlers);
		TileGroup tileGroup = (TileGroup) widget;
		Control imageCtrl = tileGroup.getImageCtrl();
		for (ValueStore valueStore : tileGroup.getValueList()) {
			String actionPath = ((Tile) valueStore.getValueObject()).getActionPath();
			if (actionPath != null) {
				imageCtrl.setValueStore(valueStore);
				writePathEventHandlerJS(writer, imageCtrl.getId(), imageCtrl.getBinding(), "onclick", "post",
						actionPath);
			}
		}
	}

    @Override
    protected void writeLayoutContent(ResponseWriter writer, Container container) throws UnifyException {
        TileGroup tileGroup = (TileGroup) container;
        List<ValueStore> valueStoreList = tileGroup.getValueList();
        if (!valueStoreList.isEmpty()) {
            int columns = tileGroup.getColumns();
            if (columns <= 0) {
                columns = 1;
            }

            int childIndex = 0;
            int numCards = valueStoreList.size();
            int rows = numCards / columns;
            if ((numCards % columns) > 0) {
                rows++;
            }

            Control imageCtrl = tileGroup.getImageCtrl();
            boolean isShowTitleSection = tileGroup.isShowTitleSection();
            writer.write("<div style=\"display:table;\"><div style=\"display:table-row;\">");
            for (int i = 0; i < columns; i++) {
                writer.write("<div style=\"display:table-cell;\">");
                for (int j = 0; j < rows; j++) {
                    int k = j * columns + i;
                    if (k < numCards) {
                        ValueStore valueStore = valueStoreList.get(childIndex++);
                        writer.write("<div class=\"tgtile\">");
                        imageCtrl.setValueStore(valueStore);
                        writer.writeStructureAndContent(imageCtrl);
                        if (isShowTitleSection) {
                            writer.write("<span id=\"").write(imageCtrl.getPrefixedId("spn_")).write("\">");
                            writer.writeWithHtmlEscape(((Tile) valueStore.getValueObject()).getCaption());
                            writer.write("</span>");
                        }
                        writer.write("</div>");
                    }
                }
                writer.write("</div>");
            }
            writer.write("</div></div>");
        }
    }
}
