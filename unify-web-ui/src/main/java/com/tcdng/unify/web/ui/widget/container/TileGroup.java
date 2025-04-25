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
package com.tcdng.unify.web.ui.widget.container;

import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.ui.Tile;
import com.tcdng.unify.web.ui.widget.AbstractValueListContainer;
import com.tcdng.unify.web.ui.widget.Control;

/**
 * A container that displays a list of tiles. The list of tiles {@link Tile} is
 * obtained from the {@link #getValue()} method, which if null, prompts the
 * widget to check the current session for a tile list.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("ui-tilegroup")
@UplAttributes({ @UplAttribute(name = "columns", type = int.class),
        @UplAttribute(name = "showTitleSection", type = boolean.class, defaultVal = "false") })
public class TileGroup extends AbstractValueListContainer<ValueStore, Tile> {

    private Control imageCtrl;

    public TileGroup() {
        super(false);
    }

    @Override
    public void onPageConstruct() throws UnifyException {
        super.onPageConstruct();
        imageCtrl = (Control) addInternalWidget("!ui-image srcBinding:imageSrc binding:image hintBinding:caption");
    }

    public int getColumns() throws UnifyException {
        return getUplAttribute(int.class, "columns");
    }

    public boolean isShowTitleSection() throws UnifyException {
        return getUplAttribute(boolean.class, "showTitleSection");
    }

    public Control getImageCtrl() {
        return imageCtrl;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Tile> getItemList() throws UnifyException {
        return (List<Tile>) getValue();
    }

    @Override
    protected ValueStore newValue(Tile tile, int index) throws UnifyException {
        return createValueStore(tile, index);
    }

    @Override
    protected void onCreateValueList(List<ValueStore> valueList) throws UnifyException {

    }
}
