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

package com.tcdng.unify.web.data;

import com.tcdng.unify.web.constant.TopicEventType;

/**
 * Topic event.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class TopicEvent {

	private TopicEventType eventType;

	private String topic;

	public TopicEvent(TopicEventType eventType, String topic) {
		this.eventType = eventType;
		this.topic = topic;
	}

	public TopicEventType getEventType() {
		return eventType;
	}

	public String getTopic() {
		return topic;
	}

}
