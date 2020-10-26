/*
 * Copyright 2018-2020 The Code Department.
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

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.widget.control.Table;
import com.tcdng.unify.web.ui.widget.data.DialogCrudInfo;

/**
 * Dialog table CRUD panel.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("ui-dialogtablecrudpanel")
@UplBinding("web/panels/upl/dialogtablecrudpanel.upl")
public class DialogTableCrudPanel extends AbstractDialogPanel {

    @Override
    public void switchState() throws UnifyException {
        super.switchState();
        DialogCrudInfo<?>  dialogCrudInfo = getDialogCrudInfo();
        dialogCrudInfo.init();
        switch(dialogCrudInfo.getMode()) {
            case MAINTAIN:
                setVisible("saveFrmBtn", false);
                setVisible("updateFrmBtn", true);
                setVisible("deleteFrmBtn", true);
                setVisible("cancelFrmBtn", true);
                break;
            case CREATE:
                setVisible("saveFrmBtn", true);
                setVisible("updateFrmBtn", false);
                setVisible("deleteFrmBtn", false);
                setVisible("cancelFrmBtn", false);
            default:
                break;
        }
    }

    @Action
    public void prepareCreate() throws UnifyException {
        DialogCrudInfo<?>  dialogCrudInfo = getDialogCrudInfo();
        dialogCrudInfo.newItem();
        switchState();
    }

    @Action
    public void prepareMaintain() throws UnifyException {
        DialogCrudInfo<?>  dialogCrudInfo = getDialogCrudInfo();
        dialogCrudInfo.selectItem(getWidgetByShortName(Table.class, "itemListTbl").getViewIndex());
        switchState();
    }

    @Action
    public void save() throws UnifyException {
        DialogCrudInfo<?>  dialogCrudInfo = getDialogCrudInfo();
        dialogCrudInfo.saveItem();
        dialogCrudInfo.newItem();
        switchState();
    }

    @Action
    public void update() throws UnifyException {
        DialogCrudInfo<?>  dialogCrudInfo = getDialogCrudInfo();
        dialogCrudInfo.newItem();
        switchState();
    }

    @Action
    public void delete() throws UnifyException {
        DialogCrudInfo<?>  dialogCrudInfo = getDialogCrudInfo();
        dialogCrudInfo.removeItem();
        dialogCrudInfo.newItem();
        switchState();
    }

    @Action
    public void cancel() throws UnifyException {
        DialogCrudInfo<?>  dialogCrudInfo = getDialogCrudInfo();
        dialogCrudInfo.newItem();
        switchState();
    }
    
    private DialogCrudInfo<?> getDialogCrudInfo() throws UnifyException {
        return  getValue(DialogCrudInfo.class);
    }
}
