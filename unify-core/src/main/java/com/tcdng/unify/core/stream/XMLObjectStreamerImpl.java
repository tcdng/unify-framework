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
package com.tcdng.unify.core.stream;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.data.AbstractPool;
import com.tcdng.unify.core.data.FactoryMap;

/**
 * Implementation of XML object streamer based on JAXB.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = ApplicationComponents.APPLICATION_XMLOBJECTSTREAMER, description = "JAXB XML Object Streamer")
public class XMLObjectStreamerImpl extends AbstractObjectStreamer implements XMLObjectStreamer {

    @Configurable("32")
    private int maxPoolSize;

    @Configurable("2000")
    private long getTimeout;

    @Configurable("false")
    private boolean nicelyFormatted;

    private FactoryMap<Class<?>, JAXBContextPool> jaxbContextPools;

    public XMLObjectStreamerImpl() {
        jaxbContextPools = new FactoryMap<Class<?>, JAXBContextPool>() {

            @Override
            protected JAXBContextPool create(Class<?> clazz, Object... params) throws Exception {
                JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
                return new JAXBContextPool(jaxbContext, getTimeout, 0, maxPoolSize);
            }

        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset) throws UnifyException {
        Unmarshaller unmarshaller = jaxbContextPools.get(type).getUnmarshallerPool().borrowObject();
        try {
            XMLReader xmlReader = getSkipDTDValidationReader();
            if (charset == null) {
                return (T) unmarshaller.unmarshal(new SAXSource(xmlReader, new InputSource(inputStream)));
            } else {
                return (T) unmarshaller.unmarshal(
                        new SAXSource(xmlReader, new InputSource(new InputStreamReader(inputStream, charset))));
            }
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            jaxbContextPools.get(type).getUnmarshallerPool().returnObject(unmarshaller);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unmarshal(Class<T> type, Reader reader) throws UnifyException {
        Unmarshaller unmarshaller = jaxbContextPools.get(type).getUnmarshallerPool().borrowObject();
        try {
            XMLReader xmlReader = getSkipDTDValidationReader();
            return (T) unmarshaller.unmarshal(new SAXSource(xmlReader, new InputSource(reader)));
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            jaxbContextPools.get(type).getUnmarshallerPool().returnObject(unmarshaller);
        }
        return null;
    }

    @Override
    public <T> T unmarshal(Class<T> type, String string) throws UnifyException {
        return unmarshal(type, new StringReader(string));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset, boolean ignoreNameSpaces)
            throws UnifyException {
        Unmarshaller unmarshaller = jaxbContextPools.get(type).getUnmarshallerPool().borrowObject();
        try {
            if (ignoreNameSpaces) {
                if (charset == null) {
                    return (T) unmarshaller.unmarshal(getSkipNamespaceXMLReader(inputStream));
                } else {
                    return (T) unmarshaller
                            .unmarshal(getSkipNamespaceXMLReader(new InputStreamReader(inputStream, charset)));
                }
            }

            if (charset == null) {
                return (T) unmarshaller.unmarshal(inputStream);
            } else {
                return (T) unmarshaller.unmarshal(new InputStreamReader(inputStream, charset));
            }
        } catch (JAXBException e) {
            throwOperationErrorException(e);
        } catch (XMLStreamException e) {
            throwOperationErrorException(e);
        } finally {
            jaxbContextPools.get(type).getUnmarshallerPool().returnObject(unmarshaller);
        }
        return null;
    }

    @Override
    public <T> T unmarshal(Class<T> type, InputStream inputStream, boolean ignoreNameSpaces) throws UnifyException {
        return unmarshal(type, inputStream, null, ignoreNameSpaces);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unmarshal(Class<T> type, Reader reader, boolean ignoreNameSpaces) throws UnifyException {
        Unmarshaller unmarshaller = jaxbContextPools.get(type).getUnmarshallerPool().borrowObject();
        try {
            if (ignoreNameSpaces) {
                return (T) unmarshaller.unmarshal(getSkipNamespaceXMLReader(reader));
            }

            return (T) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throwOperationErrorException(e);
        } catch (XMLStreamException e) {
            throwOperationErrorException(e);
        } finally {
            jaxbContextPools.get(type).getUnmarshallerPool().returnObject(unmarshaller);
        }
        return null;
    }

    @Override
    public <T> T unmarshal(Class<T> type, String string, boolean ignoreNameSpaces) throws UnifyException {
        return unmarshal(type, new StringReader(string), ignoreNameSpaces);
    }

    @Override
    public void marshal(Object object, OutputStream outputStream, Charset charset) throws UnifyException {
        Marshaller marshaller = jaxbContextPools.get(object.getClass()).getMarshallerPool().borrowObject();
        try {
            if (charset == null) {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            } else {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, charset.name());
            }

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, nicelyFormatted);

            marshaller.marshal(object, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            jaxbContextPools.get(object.getClass()).getMarshallerPool().returnObject(marshaller);
        }
    }

    @Override
    public void marshal(Object object, Writer writer) throws UnifyException {
        Marshaller marshaller = jaxbContextPools.get(object.getClass()).getMarshallerPool().borrowObject();
        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, nicelyFormatted);
            marshaller.marshal(object, writer);
            writer.flush();
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            jaxbContextPools.get(object.getClass()).getMarshallerPool().returnObject(marshaller);
        }
    }

    @Override
    public String marshal(Object object) throws UnifyException {
        StringWriter writer = new StringWriter();
        marshal(object, writer);
        return writer.toString();
    }

    private class JAXBContextPool {

        private MarshallerPool marshallerPool;

        private UnmarshallerPool unmarshallerPool;

        public JAXBContextPool(JAXBContext jaxbContext, long getTimeout, int minSize, int maxSize) {
            marshallerPool = new MarshallerPool(jaxbContext, getTimeout, minSize, maxSize);
            unmarshallerPool = new UnmarshallerPool(jaxbContext, getTimeout, minSize, maxSize);
        }

        public MarshallerPool getMarshallerPool() {
            return marshallerPool;
        }

        public UnmarshallerPool getUnmarshallerPool() {
            return unmarshallerPool;
        }
    }

    private class MarshallerPool extends AbstractPool<Marshaller> {

        private JAXBContext jaxbContext;

        public MarshallerPool(JAXBContext jaxbContext, long getTimeout, int minSize, int maxSize) {
            super(getTimeout, minSize, maxSize);
            this.jaxbContext = jaxbContext;
        }

        @Override
        protected Marshaller createObject(Object... params) throws Exception {
            return jaxbContext.createMarshaller();
        }

        @Override
        protected void onGetObject(Marshaller marshaller, Object... params) throws Exception {

        }

        @Override
        protected void destroyObject(Marshaller marshaller) {

        }

    }

    private class UnmarshallerPool extends AbstractPool<Unmarshaller> {

        private JAXBContext jaxbContext;

        public UnmarshallerPool(JAXBContext jaxbContext, long getTimeout, int minSize, int maxSize) {
            super(getTimeout, minSize, maxSize);
            this.jaxbContext = jaxbContext;
        }

        @Override
        protected Unmarshaller createObject(Object... params) throws Exception {
            return jaxbContext.createUnmarshaller();
        }

        @Override
        protected void onGetObject(Unmarshaller unmarshaller, Object... params) throws Exception {

        }

        @Override
        protected void destroyObject(Unmarshaller unmarshaller) {

        }

    }

    private XMLReader getSkipDTDValidationReader() throws Exception {
        XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        // Disable JAXB DTD validation
        xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        xmlReader.setFeature("http://xml.org/sax/features/validation", false);
        return xmlReader;
    }

    private class SkipNamespaceXMLReader extends StreamReaderDelegate {

        public SkipNamespaceXMLReader(XMLStreamReader reader) {
            super(reader);
        }

        @Override
        public String getAttributeNamespace(int index) {
            return "";
        }

        @Override
        public String getNamespaceURI() {
            return "";
        }
    }

    private SkipNamespaceXMLReader getSkipNamespaceXMLReader(InputStream inputStream) throws XMLStreamException {
        return new SkipNamespaceXMLReader(XMLInputFactory.newInstance().createXMLStreamReader(inputStream));
    }

    private SkipNamespaceXMLReader getSkipNamespaceXMLReader(Reader reader) throws XMLStreamException {
        return new SkipNamespaceXMLReader(XMLInputFactory.newInstance().createXMLStreamReader(reader));
    }
}
