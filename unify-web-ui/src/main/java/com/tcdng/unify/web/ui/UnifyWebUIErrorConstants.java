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
package com.tcdng.unify.web.ui;

/**
 * Unify web UI error constants.
 * 
 * @author The Code Department
 * @since 4.1
 */
public interface UnifyWebUIErrorConstants {

    /** Missing JS action handler. Component = {0} */
    String MISSING_ACTION_HANDLER = "UWI_0001";

    /** Page panel with ID not found. ID={0} */
    String PAGE_PANEL_WITH_ID_NOT_FOUND = "UWI_0002";

    /**
     * Page bean unknown result. Page controller ={0}, ResultMapping = {1}
     */
    String CONTROLLER_UNKNOWN_RESULT = "UWI_0004";

    /**
     * Controller invalid action handler method signature. Type={0}, Method = {1}
     */
    String CONTROLLER_INVALID_ACTION_HANDLER_SIGNATURE = "UWI_0005";

    /** Key combination {0} is invalid */
    String KEYCOMBO_IS_INVALID = "UWI_0006";

    /** BasicPage controller has no document source. Bean = {0} */
    String PAGECONTROLLER_NO_DOCUMENT_SOURCE = "UWI_0007";

    /**
     * LogUserEvent handler for page element {0} is referencing unknown action {1}.
     */
    String EVENTHANDLER_REFERENCING_UNKNOWN_ACTION = "UWI_0008";

    /** Multiple command parameters in request. Request = {0} */
    String MULTIPLE_COMMAND_PARAMETERS_IN_REQUEST = "UWI_0009";

    /** Page action {0} refers to unknown page validation {1} */
    String PAGEACTION_REFERS_UNKNOWN_PAGEVALIDATION = "UWI_000A";

    /**
     * No such attribute {0} is associated with this component's {1} value object or
     * session, application and request scopes
     */
    String NO_SUCH_ATTRIBUTE_ASSOCIATED = "UWI_000B";

    /** widget with long name {0} is unknown. Container = {1}. */
    String WIDGET_WITH_LONGNAME_UNKNOWN = "UWI_000C";

    /** widget with short name {0} is unknown. Container = {1}. */
    String WIDGET_WITH_SHORTNAME_UNKNOWN = "UWI_000D";

    /**
     * No such command handler for widget {0}. Handler = {1}.
     */
    String WIDGET_UNKNOWN_COMMANDHANDLER = "UWI_000E";

    /**
     * widget invalid command handler method signature. Type={0}, Method = {1}
     */
    String WIDGET_INVALID_COMMAND_HANDLER_SIGNATURE = "UWI_000F";

    /**
     * No writer found for UPL component {0}
     */
    String UPLCOMPONENT_NO_WRITER = "UWI_0010";
}
