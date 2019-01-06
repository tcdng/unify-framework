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
package com.tcdng.unify.web.ui.data;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.ui.Control;

/**
 * Validation information.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class ValidationInfo {

    private String id;

    private String borderId;

    private String notificationId;

    private String validationCode;

    private String message;

    private String borderStyle;

    public ValidationInfo(Control control, String validationCode) throws UnifyException {
        this(control, validationCode, null, "");
    }

    public ValidationInfo(Control control, String validationCode, String message, String borderStyle)
            throws UnifyException {
        this.id = control.getId();
        this.borderId = control.getBorderId();
        this.notificationId = control.getNotificationId();
        this.validationCode = validationCode;
        this.message = message;
        this.borderStyle = borderStyle;
    }

    public String getId() {
        return id;
    }

    public String getBorderId() {
        return borderId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public String getMessage() {
        return message;
    }

    public String getBorderStyle() {
        return borderStyle;
    }

    public boolean isPass() {
        return message == null;
    }
}
