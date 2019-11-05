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
package com.tcdng.unify.web;

/**
 * Unify web error constants.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface UnifyWebErrorConstants {

    /** Missing JS action handler. Component = {0} */
    String MISSING_ACTION_HANDLER = "UW_0001";

    /** Page panel with ID not found. ID={0} */
    String PAGE_PANEL_WITH_ID_NOT_FOUND = "UW_0002";

    /** Page bean unknown action. Page controller ={0}, Action = {1} */
    String CONTROLLER_UNKNOWN_ACTION = "UW_0003";

    /**
     * Page bean unknown result. Page controller ={0}, ResultMapping = {1}
     */
    String CONTROLLER_UNKNOWN_RESULT = "UW_0004";

    /** Login required */
    String LOGIN_REQUIRED = "UW_0005";

    /**
     * Controller invalid action handler method signature. Type={0}, Method = {1}
     */
    String CONTROLLER_INVALID_ACTION_HANDLER_SIGNATURE = "UW_0006";

    /** Key combination {0} is invalid */
    String KEYCOMBO_IS_INVALID = "UW_0007";

    /** BasicPage controller has no document source. Bean = {0} */
    String PAGECONTROLLER_NO_DOCUMENT_SOURCE = "UW_0008";

    /**
     * LogUserEvent handler for page element {0} is referencing unknown action {1}.
     */
    String EVENTHANDLER_REFERENCING_UNKNOWN_ACTION = "UW_0009";

    /** Multiple command parameters in request. Request = {0} */
    String MULTIPLE_COMMAND_PARAMETERS_IN_REQUEST = "UW_000A";

    /** Page action {0} refers to unknown page validation {1} */
    String PAGEACTION_REFERS_UNKNOWN_PAGEVALIDATION = "UW_000B";

    /**
     * No such attribute {0} is associated with this component's {1} value object or
     * session, application and request scopes
     */
    String NO_SUCH_ATTRIBUTE_ASSOCIATED = "UW_000C";

    /** widget with long name {0} is unknown. Container = {1}. */
    String WIDGET_WITH_LONGNAME_UNKNOWN = "UW_000D";

    /** widget with short name {0} is unknown. Container = {1}. */
    String WIDGET_WITH_SHORTNAME_UNKNOWN = "UW_000E";

    /**
     * No such command handler for widget {0}. Handler = {1}.
     */
    String WIDGET_UNKNOWN_COMMANDHANDLER = "UW_000F";

    /**
     * widget invalid command handler method signature. Type={0}, Method = {1}
     */
    String WIDGET_INVALID_COMMAND_HANDLER_SIGNATURE = "UW_0010";

    /**
     * No writer found for UPL component {0}
     */
    String UPLCOMPONENT_NO_WRITER = "UW_0011";

    /** Gateway bean unknown handler. Gateway Bean ={0}, Handler = {1} */
    String CONTROLLER_UNKNOWN_REMOTECALL_HANDLER = "UW_0012";

    /**
     * Controller invalid remote call handler method signature. Type={0}, Method =
     * {1}
     */
    String CONTROLLER_INVALID_REMOTECALL_HANDLER_SIGNATURE = "UW_0013";

    /**
     * Gateway call setup with category {0} and code {1} exists.
     */
    String REMOTECALL_CLIENT_SETUP_CODE_EXISTS = "UW_0014";

    /**
     * Gateway call setup with category {0} and code {1} is unknown.
     */
    String REMOTECALL_CLIENT_SETUP_CODE_UNKNOWN = "UW_0015";

    /**
     * API discovery remote call with code {0} for component {1} exists: Found
     * component = {2}.
     */
    String APIDISCOVERY_REMOTECALL_CODE_EXISTS = "UW_0016";

    /**
     * API discovery remote call with code {0} is unknown.
     */
    String APIDISCOVERY_REMOTECALL_CODE_UNKNOWN = "UW_0017";

    /**
     * Remote call client error. Code = {0} and message = {1}.
     */
    String REMOTECALL_CLIENT_ERROR = "UW_0018";

    /**
     * Remote call error.
     */
    String REMOTECALL_ERROR = "UW_0019";

    /**
     * Remote call not input stream type.
     */
    String REMOTECALL_NOT_INPUTSTREAM = "UW_001A";

    /**
     * Access to resource denied.
     */
    String RESOURCE_ACCESS_DENIED = "UW_001B";
}
