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
package com.tcdng.unify.web.ui.widget.writer.control;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.constant.ExtensionType;
import com.tcdng.unify.web.ui.widget.ResponseWriter;
import com.tcdng.unify.web.ui.widget.Widget;
import com.tcdng.unify.web.ui.widget.control.AbstractPopupTextField;

/**
 * Abstract base class for popup text field writers.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public abstract class AbstractPopupTextFieldWriter extends TextFieldWriter {

    @Override
    protected void doWriteBehavior(ResponseWriter writer, Widget widget) throws UnifyException {
        AbstractPopupTextField popupTextField = (AbstractPopupTextField) widget;
        super.doWriteBehavior(writer, popupTextField);

        ExtensionType extensionType = popupTextField.getExtensionType();
        boolean popupEnabled = isPopupEnabled(popupTextField);
        if (popupEnabled) {
            // Append popup JS
            if (popupTextField.isPopupAlways()
                    || (popupTextField.isContainerEditable() && !popupTextField.isContainerDisabled())) {
                String facId = popupTextField.getId();
                if (extensionType.isExtended()) {
                    facId = popupTextField.getFacadeId();
                }

                String cmdTag = popupTextField.getBinding();
                if (!popupTextField.getExtensionType().isFacadeEdit()) {
                    writeOpenPopupJS(writer, "onenter", facId, cmdTag, popupTextField.getBorderId(),
                            popupTextField.getPopupId(), popupTextField.getDisplayTimeOut(), getOnShowAction(),
                            getOnShowParam(popupTextField), getOnHideAction(), getOnHideParam(popupTextField));

                    if (popupTextField.isOpenPopupOnFac()) {
                        writeOpenPopupJS(writer, "onclick", facId, cmdTag, popupTextField.getBorderId(),
                                popupTextField.getPopupId(), popupTextField.getDisplayTimeOut(), getOnShowAction(),
                                getOnShowParam(popupTextField), getOnHideAction(), getOnHideParam(popupTextField));
                    }

                    writeOpenPopupJS(writer, "onclick", popupTextField.getPopupButtonId(), cmdTag,
                            popupTextField.getBorderId(), popupTextField.getPopupId(), popupTextField.getDisplayTimeOut(),
                            getOnShowAction(), getOnShowParam(popupTextField), getOnHideAction(),
                            getOnHideParam(popupTextField));
                }
            }
        }

        doWritePopupTextFieldBehaviour(writer, popupTextField, popupEnabled);
    }

    @Override
    protected void writeTrailingAddOn(ResponseWriter writer, Widget widget) throws UnifyException {
        AbstractPopupTextField popupTextField = (AbstractPopupTextField) widget;
        writer.write("<button tabindex=\"-1\"");
        writeTagId(writer, popupTextField.getPopupButtonId());
        writeTagStyleClass(writer, "tpbutton g_fsm");
        if (popupTextField.isContainerDisabled()) {
            writer.write(" disabled");
        }
        writer.write(">");

        if (isWithFontSymbolManager()) {
            writer.write(resolveSymbolHtmlHexCode(popupTextField.getButtonSymbol()));
        } else {
            writer.write("<img src=\"");
            writer.writeFileImageContextURL(popupTextField.getButtonImageSrc());
            writer.write("\"/>");
        }
        writer.write("</button>");
    }

    @Override
    protected void writeBaseAddOn(ResponseWriter writer, Widget widget) throws UnifyException {
        AbstractPopupTextField popupTextField = (AbstractPopupTextField) widget;
        if (isPopupEnabled(popupTextField)) {
            writer.write("<div");
            writeTagId(writer, popupTextField.getPopupId());
            writeTagStyleClass(writer, "ui-text-popup-win");
            writer.write(">");
            writePopupContent(writer, popupTextField);
            writer.write("</div>");
        }
    }

    protected boolean isPopupEnabled(AbstractPopupTextField popupTextField) throws UnifyException {
        if (popupTextField.isPopupOnEditableOnly()) {
            return popupTextField.isContainerEditable() && !popupTextField.isContainerDisabled();
        }
        return true;
    }
    
    protected abstract void writePopupContent(ResponseWriter writer, AbstractPopupTextField popupTextField)
            throws UnifyException;

    protected abstract void doWritePopupTextFieldBehaviour(ResponseWriter writer, AbstractPopupTextField popupTextField,
            boolean popupEnabled) throws UnifyException;

    /**
     * Returns the name of action to fire on show of popup.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract String getOnShowAction() throws UnifyException;

    /**
     * Generates a JSON object for show action parameter
     * 
     * @param popupTextField
     *            the popup text field
     * @return the generated object
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract String getOnShowParam(AbstractPopupTextField popupTextField) throws UnifyException;

    /**
     * Returns the name of action to fire on hide of popup.
     * 
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract String getOnHideAction() throws UnifyException;

    /**
     * Generates a JSON object for hide action parameter
     * 
     * @param popupTextField
     *            the popup text field
     * @return the generated object
     * @throws UnifyException
     *             if an error occurs
     */
    protected abstract String getOnHideParam(AbstractPopupTextField popupTextField) throws UnifyException;

}
