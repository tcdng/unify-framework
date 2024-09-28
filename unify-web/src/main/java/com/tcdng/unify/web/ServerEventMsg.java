/*
 * Copyright 2018-2024 The Code Department.
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
 * Server event message.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ServerEventMsg {

	private String srcClientId;

	private String cmd;
	
	private String param;

	public ServerEventMsg(String srcClientId, String cmd, String param) {
		this.srcClientId = srcClientId;
		this.cmd = cmd;
		this.param = param;
	}

	public ServerEventMsg() {

	}

	public String getSrcClientId() {
		return srcClientId;
	}

	public void setSrcClientId(String srcClientId) {
		this.srcClientId = srcClientId;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
}