/*
 * Copyright 2018-2022 The Code Department.
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
package com.tcdng.unify.core.util;

import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;

/**
 * Provides utility methods for XML configuration.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class XmlConfigUtils {

    private XmlConfigUtils() {

    }

    @SuppressWarnings("unchecked")
    public static <T> T readXmlConfig(Class<T> xmlDataClazz, Object xmlSrcObject) throws UnifyException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(xmlDataClazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(ValidationEvent event) {
                    return false;
                }
            });

            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            // Disable JAXB DTD validation
            xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            xmlReader.setFeature("http://xml.org/sax/features/validation", false);

            if (xmlSrcObject instanceof String) {
                Reader reader = new CharArrayReader(((String) xmlSrcObject).toCharArray());
                return (T) jaxbUnmarshaller.unmarshal(new SAXSource(xmlReader, new InputSource(reader)));
            } else if (xmlSrcObject instanceof Reader) {
                return (T) jaxbUnmarshaller.unmarshal(new SAXSource(xmlReader, new InputSource((Reader) xmlSrcObject)));
            } else if (xmlSrcObject instanceof InputStream) {
                return (T) jaxbUnmarshaller
                        .unmarshal(new SAXSource(xmlReader, new InputSource((InputStream) xmlSrcObject)));
            } else if (xmlSrcObject instanceof InputSource) {
                return (T) jaxbUnmarshaller.unmarshal((InputSource) xmlSrcObject);
            }

            return (T) jaxbUnmarshaller.unmarshal((File) xmlSrcObject);
        } catch (Exception e) {
            throw new UnifyOperationException(e,
                    XmlConfigUtils.class.getName());
        }
    }

    public static void writeXmlConfig(Object configObject, OutputStream outputStream) throws UnifyException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(configObject.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(configObject, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new UnifyOperationException(e, XmlConfigUtils.class.getName());
        }
    }

    public static void writeXmlConfigNoEscape(Object configObject, OutputStream outputStream) throws UnifyException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(configObject.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty("com.sun.xml.bind.characterEscapeHandler", new NoEscapeHandler()); 
            jaxbMarshaller.marshal(configObject, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throw new UnifyOperationException(e, XmlConfigUtils.class.getName());
        }
    }
    
    private static class NoEscapeHandler implements CharacterEscapeHandler {

        @Override
        public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
            out.write(ch, start, length);
        }  
    }
}
