/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.web.ui.control;

import java.util.Collections;
import java.util.List;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.constant.ShiftDirectionConstants;
import com.tcdng.unify.web.ui.Control;

/**
 * A table that allows users to shift row contents up or down.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-rack")
@UplAttributes({ @UplAttribute(name = "rowSelectable", type = boolean.class, defaultValue = "true") })
public class Rack extends Table {

    private ShiftButtons shiftCtrl;

    private Control shiftDirectionCtrl;

    private int shiftDirection;

    @Override
    public void onPageInitialize() throws UnifyException {
        StringBuilder sb = new StringBuilder(
                "!ui-shiftbuttons  caption:$m{table.rack.shift} columnStyle:$s{width:100px;} style:$s{text-align:center;}");
        appendUplAttribute(sb, "binding");
        shiftCtrl = (ShiftButtons) addExternalChildControl(sb.toString());
        shiftDirectionCtrl = (Control) addInternalChildControl("!ui-hidden binding:shiftDirection");

        super.onPageInitialize();
    }

    @Override
    public String getShiftDirectionId() throws UnifyException {
        return shiftDirectionCtrl.getId();
    }

    public Control getShiftDirectionCtrl() {
        return shiftDirectionCtrl;
    }

    @Override
    public String getShiftTopId() throws UnifyException {
        return shiftCtrl.getShiftTopGroupId();
    }

    @Override
    public String getShiftUpId() throws UnifyException {
        return shiftCtrl.getShiftUpGroupId();
    }

    @Override
    public String getShiftDownId() throws UnifyException {
        return shiftCtrl.getShiftDownGroupId();
    }

    @Override
    public String getShiftBottomId() throws UnifyException {
        return shiftCtrl.getShiftBottomGroupId();
    }

    public int isShiftDirection() {
        return shiftDirection;
    }

    public void setShiftDirection(int shiftDirection) {
        this.shiftDirection = shiftDirection;
    }

    @Override
    public void addPageAliases() throws UnifyException {
        super.addPageAliases();
        addPageAlias(shiftDirectionCtrl);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doShift() throws UnifyException {
        List<Object> list = (List<Object>) getValue();
        if (list != null && list.size() > 1) {
            boolean swap = false;
            int viewIndex = getViewIndex();
            int oldIndex = viewIndex;
            switch (shiftDirection) {
            case ShiftDirectionConstants.TOP:
                viewIndex = 0;
                break;
            case ShiftDirectionConstants.UP:
                if (viewIndex > 0) {
                    viewIndex--;
                    swap = true;
                }
                break;
            case ShiftDirectionConstants.DOWN:
                if (viewIndex < (list.size() - 1)) {
                    viewIndex++;
                    swap = true;
                }
                break;
            case ShiftDirectionConstants.BOTTOM:
                viewIndex = list.size() - 1;
                break;
            }

            if (oldIndex != viewIndex) {
                setViewIndex(viewIndex);
                if (swap) {
                    Collections.swap(getValueList(), oldIndex, viewIndex);
                    Collections.swap(list, oldIndex, viewIndex);
                } else {
                    getValueList().add(viewIndex, getValueList().remove(oldIndex));
                    list.add(viewIndex, list.remove(oldIndex));
                }
            }
        }
    }

}
