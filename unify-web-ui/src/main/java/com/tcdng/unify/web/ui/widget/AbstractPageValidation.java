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
package com.tcdng.unify.web.ui.widget;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.upl.UplElementReferences;
import com.tcdng.unify.web.ui.DataTransfer;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.data.ValidationInfo;

/**
 * Base class for UI validation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@UplAttributes({ @UplAttribute(name = "borderErrStyle", type = String.class, defaultVal = "2px solid #F0C0C0"),
        @UplAttribute(name = "components", type = UplElementReferences.class) })
public abstract class AbstractPageValidation extends AbstractBehavior implements PageValidation {

    @Configurable
    private PageManager pageManager;

    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getBorderErrStyle() throws UnifyException {
        return getUplAttribute(String.class, "borderErrStyle");
    }

    /**
     * Adds validation pass information to request scope.
     * 
     * @param control
     *            the user interface component
     * @param validationCode
     *            the validation code
     * @throws UnifyException
     *             if an error occurs
     */
    protected void addValidationPass(Control control, String validationCode) throws UnifyException {
        getRequestContextUtil().addRequestValidationInfo(control.getId(), new ValidationInfo(control, validationCode));
    }

    /**
     * Adds validation fail information to request scope.
     * 
     * @param control
     *            the user interface component
     * @param validationCode
     *            the validation code
     * @param message
     *            the error message
     * @throws UnifyException
     *             if an error occurs
     */
    protected void addValidationFail(Control control, String validationCode, String message) throws UnifyException {
        getRequestContextUtil().addRequestValidationInfo(control.getId(),
                new ValidationInfo(control, validationCode, message, getBorderErrStyle()));
    }

    /**
     * Returns a converted binding value
     * 
     * @param clazz
     *            the type to convert to
     * @param transferBlock
     *            the data transfer block
     * @throws UnifyException
     *             if an error occurs
     */
    protected <T> T getTransferValue(Class<T> clazz, DataTransferBlock transferBlock) throws UnifyException {
        return convert(clazz, transferBlock.getValue(), null);
    }

    /**
     * Returns a converted binding value
     * 
     * @param clazz
     *            the type to convert to
     * @param property
     *            the property
     * @param transfer
     *            the data transfer object
     * @throws UnifyException
     *             if an error occurs
     */
    protected <T> T getTransferValue(Class<T> clazz, String property, DataTransfer transfer) throws UnifyException {
        for (DataTransferBlock transferBlock : transfer.getDataTransferBlocks()) {
            if (property.equals(transferBlock.getLongProperty()) || property.equals(transferBlock.getShortProperty())) {
                return convert(clazz, transferBlock.getValue(), null);
            }
        }

        return null;
    }

    /**
     * Returns application page manager.
     */
    protected PageManager getPageManager() {
        return pageManager;
    }
}
