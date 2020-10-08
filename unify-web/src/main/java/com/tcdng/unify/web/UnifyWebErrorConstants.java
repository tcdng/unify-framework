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
package com.tcdng.unify.web;

/**
 * Unify web error constants.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface UnifyWebErrorConstants {

    /** Gateway bean unknown handler. Gateway Bean ={0}, Handler = {1} */
    String CONTROLLER_UNKNOWN_REMOTECALL_HANDLER = "UW_0001";

    /**
     * Controller invalid remote call handler method signature. Type={0}, Method =
     * {1}
     */
    String CONTROLLER_INVALID_REMOTECALL_HANDLER_SIGNATURE = "UW_0002";

    /**
     * Gateway call setup with category {0} and code {1} exists.
     */
    String REMOTECALL_CLIENT_SETUP_CODE_EXISTS = "UW_0003";

    /**
     * Gateway call setup with category {0} and code {1} is unknown.
     */
    String REMOTECALL_CLIENT_SETUP_CODE_UNKNOWN = "UW_0004";

    /**
     * API discovery remote call with code {0} for component {1} exists: Found
     * component = {2}.
     */
    String APIDISCOVERY_REMOTECALL_CODE_EXISTS = "UW_0005";

    /**
     * API discovery remote call with code {0} is unknown.
     */
    String APIDISCOVERY_REMOTECALL_CODE_UNKNOWN = "UW_0006";

    /**
     * Remote call client error. Code = {0} and message = {1}.
     */
    String REMOTECALL_CLIENT_ERROR = "UW_0007";

    /**
     * Remote call error.
     */
    String REMOTECALL_ERROR = "UW_0008";

    /**
     * Remote call not input stream type.
     */
    String REMOTECALL_NOT_INPUTSTREAM = "UW_0009";

    /**
     * Tenant part expected in request URL.
     */
    String TENANT_PART_EXPECTED_IN_URL = "UW_000A";

    /** Login required */
    String LOGIN_REQUIRED = "UW_0010";
}
