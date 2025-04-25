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
package com.tcdng.unify.core.stream;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.data.FactoryMap;

/**
 * Implementation of XML object streamer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component(name = ApplicationComponents.APPLICATION_XMLOBJECTSTREAMER, description = "XML Object Streamer")
public class XmlObjectStreamerImpl extends AbstractObjectStreamer implements XmlObjectStreamer {
	
	private FactoryMap<Class<?>, XmlMapper> mappers;

	public XmlObjectStreamerImpl() {
		mappers = new FactoryMap<Class<?>, XmlMapper>() {

			@Override
			protected XmlMapper create(Class<?> clazz, Object... params) throws Exception {
				return new XmlMapper();
			}

		};
	}

	@Override
	public <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset) throws UnifyException {
		XmlMapper unmarshaller = mappers.get(type);
		try {
			if (charset == null) {
				return unmarshaller.readValue(inputStream, type);
			} else {
				return unmarshaller.readValue(new InputStreamReader(inputStream, charset), type);
			}
		} catch (Exception e) {
			throwOperationErrorException(e);
		}

		return null;
	}

	@Override
	public <T> T unmarshal(Class<T> type, Reader reader) throws UnifyException {
		XmlMapper unmarshaller = mappers.get(type);
		try {
			return unmarshaller.readValue(reader, type);
		} catch (Exception e) {
			throwOperationErrorException(e);
		}

		return null;
	}

	@Override
	public <T> T unmarshal(Class<T> type, String string) throws UnifyException {
		return unmarshal(type, new StringReader(string));
	}

	@Override
	public <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset, boolean ignoreNameSpaces)
			throws UnifyException {
		return unmarshal(type, inputStream, charset);
	}

	@Override
	public <T> T unmarshal(Class<T> type, InputStream inputStream, boolean ignoreNameSpaces) throws UnifyException {
		return unmarshal(type, inputStream, null, ignoreNameSpaces);
	}

	@Override
	public <T> T unmarshal(Class<T> type, Reader reader, boolean ignoreNameSpaces) throws UnifyException {
		return unmarshal(type, reader);
	}

	@Override
	public <T> T unmarshal(Class<T> type, String string, boolean ignoreNameSpaces) throws UnifyException {
		return unmarshal(type, new StringReader(string), ignoreNameSpaces);
	}

	@Override
	public void marshal(Object object, OutputStream outputStream, Charset charset, PrintFormat printFormat)
			throws UnifyException {
		XmlMapper marshaller = mappers.get(object.getClass());
		try {
			Writer writer = charset == null ? new OutputStreamWriter(outputStream, StandardCharsets.UTF_8.name())
					: new OutputStreamWriter(outputStream, charset.name());

			writer.write("<?xml version=\"1.0\" encoding=\"");
			writer.write(charset == null ? StandardCharsets.UTF_8.name() : charset.name());
			writer.write("\" standalone=\"yes\"?>");

			if (PrintFormat.PRETTY.equals(printFormat)) {
				writer.write("\n");
				marshaller.enable(SerializationFeature.INDENT_OUTPUT);
			}
			
			marshaller.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
			marshaller.writeValue(writer, object);
			writer.flush();
		} catch (Exception e) {
			throwOperationErrorException(e);
		}
	}

	@Override
	public void marshal(Object object, Writer writer, PrintFormat printFormat) throws UnifyException {
		XmlMapper marshaller = mappers.get(object.getClass());
		try {
			writer.write("<?xml version=\"1.0\" encoding=\"");
			writer.write(StandardCharsets.UTF_8.name());
			writer.write("\" standalone=\"yes\"?>");

			if (PrintFormat.PRETTY.equals(printFormat)) {
				writer.write("\n");
				marshaller.enable(SerializationFeature.INDENT_OUTPUT);
			}
			
			marshaller.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
			marshaller.writeValue(writer, object);
			writer.flush();
		} catch (Exception e) {
			throwOperationErrorException(e);
		}
	}

	@Override
	public String marshal(Object object, PrintFormat printFormat) throws UnifyException {
		StringWriter writer = new StringWriter();
		marshal(object, writer, printFormat);
		return writer.toString();
	}


}
