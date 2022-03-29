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
package com.tcdng.unify.web.ui.widget.validation;

import java.util.List;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.business.GenericService;
import com.tcdng.unify.core.database.Entity;
import com.tcdng.unify.core.database.Query;
import com.tcdng.unify.web.ui.DataTransfer;
import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.widget.AbstractPageValidation;
import com.tcdng.unify.web.ui.widget.Control;
import com.tcdng.unify.web.ui.widget.Widget;

/**
 * Unique record page validation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-uniquevalidation")
@UplAttributes({ @UplAttribute(name = "type", type = Class.class),
        @UplAttribute(name = "idProperty", type = String.class, defaultVal = "id"),
        @UplAttribute(name = "idType", type = Class.class, defaultVal = "java.lang.Long") })
public class UniqueValidation extends AbstractPageValidation {

    @SuppressWarnings({ "unchecked"})
    @Override
    public boolean validate(List<Widget> widgets, DataTransfer dataTransfer) throws UnifyException {
        Class<? extends Entity> validationClazz = (Class<? extends Entity>) getUplAttribute(Class.class, "type");
        if (validationClazz == null) {
            validationClazz = (Class<? extends Entity>) dataTransfer.getValidationClass();
        }

        Class<?> idClazz = (Class<?>) getUplAttribute(Class.class, "idType");
        if (idClazz == null) {
            idClazz = dataTransfer.getValidationIdClass();
        }

        if (validationClazz != null) {
            Query<? extends Entity> criteria = Query.of(validationClazz);
            String idProperty = getUplAttribute(String.class, "idProperty");
            Object id = getTransferValue(idClazz, idProperty, dataTransfer);
            if (id != null) {
                criteria.addNotEquals("id", id);
            }

            StringBuilder sb = new StringBuilder();
            boolean appendSymbol = false;
            for (Widget widget : widgets) {
                if (widget.isVisible() || widget.isHidden()) {
                    DataTransferBlock transferBlock = dataTransfer.getDataTransferBlock(widget.getId());
                    if (transferBlock != null) {
                        Object value = transferBlock.getValue();
                        if (value != null) {
                            criteria.addEquals(transferBlock.getShortProperty(), value);

                            if (appendSymbol) {
                                sb.append(',');
                            } else {
                                appendSymbol = true;
                            }

                            String caption = widget.getUplAttribute(String.class, "caption");
                            sb.append(caption).append(" '").append(value).append('\'');
                            continue;
                        }
                    }

                    break;
                }
            }

            if (sb.length() > 0
                    && ((GenericService) this.getComponent(ApplicationComponents.APPLICATION_GENERICSERVICE))
                            .countAll(criteria) > 0) {
                String message = getSessionMessage("validation.uniquerecordexists", sb.toString());
                addValidationFail((Control) widgets.get(0), "unique", message);
                return false;
            }
        }

        addValidationPass((Control) widgets.get(0), null);
        return true;
    }

}
