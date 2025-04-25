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
package com.tcdng.unify.core.util.xml.adapter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * CData XML adapter.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class CDataXmlAdapter {

	private static final String CDATA_START = "<![CDATA[";

	private static final String CDATA_END = "]]>";

	private static final String CDATA_BLANK = CDATA_START + CDATA_END;
    
    public static class Serializer extends JsonSerializer<String> {

		@Override
		public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			if (value != null) {
				gen.writeString(CDATA_START + value + CDATA_END);
				return;
			}

			gen.writeString(CDATA_BLANK);
		}
    	
    }
    
    public static class Deserializer extends JsonDeserializer<String> {

		@Override
		public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String val = p.getText();
			if (val != null && val.startsWith(CDATA_START) && val.endsWith(CDATA_END)) {
				return val.substring(CDATA_START.length(), val.length() - CDATA_END.length());

			}

			return val;
		}
    	
    }
}
