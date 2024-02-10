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
package com.tcdng.unify.web.ui.widget.data;

/**
 * Message box captions.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class MessageBoxCaptions {

	private String mainCaption;

	private String okCaption;

	private String yesCaption;

	private String noCaption;

	private String retryCaption;

	private String cancelCaption;

	public MessageBoxCaptions(String mainCaption) {
		this.mainCaption = mainCaption;
	}
	
	public String getMainCaption() {
		return mainCaption;
	}

	public void setMainCaption(String mainCaption) {
		this.mainCaption = mainCaption;
	}

	public String getOkCaption() {
		return okCaption;
	}

	public void setOkCaption(String okCaption) {
		this.okCaption = okCaption;
	}

	public String getYesCaption() {
		return yesCaption;
	}

	public void setYesCaption(String yesCaption) {
		this.yesCaption = yesCaption;
	}

	public String getNoCaption() {
		return noCaption;
	}

	public void setNoCaption(String noCaption) {
		this.noCaption = noCaption;
	}

	public String getRetryCaption() {
		return retryCaption;
	}

	public void setRetryCaption(String retryCaption) {
		this.retryCaption = retryCaption;
	}

	public String getCancelCaption() {
		return cancelCaption;
	}

	public void setCancelCaption(String cancelCaption) {
		this.cancelCaption = cancelCaption;
	}
	
	
}
