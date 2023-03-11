/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.web.constant;

/**
 * System information constants.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface SystemInfoConstants {

    String SYSTEMINFO_CONTROLLER_NAME = "/reserved/systeminfo";

    String UNAUTHORIZED_CONTROLLER_NAME = "/unauthorized";

    String SHOW_SYSTEM_EXCEPTION_MAPPING = "showsystemexception";

    String FORWARD_TO_APPLICATION_MAPPING = "forwardtoapplication";

    String HIDE_SYSTEM_INFO_MAPPING = "hidesysteminfo";

    String LOGIN_REQUIRED_FLAG = "reserved.systeminfo.loginrequired.flag";

    String EXCEPTION_MESSAGE_KEY = "reserved.systeminfo.message";

    String EXCEPTION_STACKTRACE_KEY = "reserved.systeminfo.stacktrace";
}
