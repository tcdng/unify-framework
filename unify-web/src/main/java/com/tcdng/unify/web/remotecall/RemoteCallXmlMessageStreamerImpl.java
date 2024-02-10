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

package com.tcdng.unify.web.remotecall;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Stack;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.data.TaggedXmlMessage;
import com.tcdng.unify.core.stream.AbstractObjectStreamer;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.XmlUtils;
import com.tcdng.unify.web.util.HtmlUtils;

/**
 * Remote call XML message streamer.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("rc-xmlmessagestreamer")
public class RemoteCallXmlMessageStreamerImpl extends AbstractObjectStreamer implements RemoteCallXmlMessageStreamer {

    @Override
    public <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset) throws UnifyException {
        return unmarshal(type, new InputSource(inputStream));
    }

    @Override
    public <T> T unmarshal(Class<T> type, Reader reader) throws UnifyException {
        return unmarshal(type, new InputSource(reader));
    }

    @Override
    public <T> T unmarshal(Class<T> type, String string) throws UnifyException {
        return unmarshal(type, new InputSource(new StringReader(string)));
    }

    @Override
    public void marshal(Object object, OutputStream outputStream, Charset charset, PrintFormat printFormat)
            throws UnifyException {
        if (charset != null) {
            marshal(object, new OutputStreamWriter(outputStream, charset));
        } else {
            marshal(object, new OutputStreamWriter(outputStream));
        }
    }

    @Override
    public void marshal(Object object, Writer writer, PrintFormat printFormat) throws UnifyException {
        SAXParser saxParser = null;
        try {
            if (PushXmlMessageParams.class.equals(object.getClass())) {
                saxParser = XmlUtils.borrowSAXParser();
                PushXmlMessageParams params = (PushXmlMessageParams) object;
                TaggedXmlMessage msg = params.getTaggedMessage();
                writer.write("<PushXmlMessage");
                writeAttribute(writer, "methodCode", params.getMethodCode());
                writeAttribute(writer, "clientAppCode", params.getClientAppCode());
                writeAttribute(writer, "destination", params.getDestination());
                if (msg != null) {
                    writeAttribute(writer, "tag", msg.getTag());
                    writeAttribute(writer, "branchCode", msg.getBranchCode());
                    writeAttribute(writer, "departmentCode", msg.getDepartmentCode());
                    writeAttribute(writer, "consumer", msg.getConsumer());
                }
                writer.write(">");
                if (msg != null) {
                    String xml = msg.getMessage();
                    if (StringUtils.isNotBlank(xml)) {
                        // Validate
                        InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
                        saxParser.parse(is, new DefaultHandler());
                        writer.write(xml);
                    }
                }
                writer.write("</PushXmlMessage>");
            } else if (PushXmlMessageResult.class.equals(object.getClass())) {
                PushXmlMessageResult result = (PushXmlMessageResult) object;
                writer.write("<PushXmlMessageResult");
                writeAttribute(writer, "methodCode", result.getMethodCode());
                writeAttribute(writer, "errorCode", result.getErrorCode());
                writer.write(">");
                if (StringUtils.isNotBlank(result.getErrorMsg())) {
                    writer.write("<errorMsg>");
                    writer.write(HtmlUtils.getStringWithHtmlEscape(result.getErrorMsg()));
                    writer.write("</errorMsg>");
                }
                writer.write("</PushXmlMessageResult>");
            } else if (PullXmlMessageParams.class.equals(object.getClass())) {
                PullXmlMessageParams params = (PullXmlMessageParams) object;
                writer.write("<PullXmlMessage");
                writeAttribute(writer, "methodCode", params.getMethodCode());
                writeAttribute(writer, "clientAppCode", params.getClientAppCode());
                writeAttribute(writer, "source", params.getSource());
                writer.write(">");
                writer.write("</PullXmlMessage>");
            } else if (PullXmlMessageResult.class.equals(object.getClass())) {
                saxParser = XmlUtils.borrowSAXParser();
                PullXmlMessageResult result = (PullXmlMessageResult) object;
                writer.write("<PullXmlMessageResult");
                writeAttribute(writer, "methodCode", result.getMethodCode());
                writeAttribute(writer, "errorCode", result.getErrorCode());
                TaggedXmlMessage msg = result.getTaggedMessage();
                if (msg != null) {
                    writeAttribute(writer, "tag", msg.getTag());
                    writeAttribute(writer, "branchCode", msg.getBranchCode());
                    writeAttribute(writer, "departmentCode", msg.getDepartmentCode());
                    writeAttribute(writer, "consumer", msg.getConsumer());
                }
                writer.write(">");

                if (StringUtils.isNotBlank(result.getErrorMsg())) {
                    writer.write("<errorMsg>");
                    writer.write(HtmlUtils.getStringWithHtmlEscape(result.getErrorMsg()));
                    writer.write("</errorMsg>");
                }

                if (msg != null) {
                    String xml = msg.getMessage();
                    if (StringUtils.isNotBlank(xml)) {
                        // Validate
                        InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
                        saxParser.parse(is, new DefaultHandler());
                        writer.write(xml);
                    }
                }
                writer.write("</PullXmlMessageResult>");
            } else {
                throwOperationErrorException(
                        new RuntimeException("Unsupported stream object type - " + object.getClass()));
            }
            writer.flush();
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            if (saxParser != null) {
                XmlUtils.restoreSAXParser(saxParser);
            }
        }
    }

    @Override
    public byte[] marshal(Object object, PrintFormat printFormat) throws UnifyException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshal(object, baos);
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private <T> T unmarshal(Class<T> type, InputSource inputSource) throws UnifyException {
        SAXParser saxParser = null;
        try {
            saxParser = XmlUtils.borrowSAXParser();
            if (PushXmlMessageParams.class.equals(type)) {
                PushXmlMessageParamsReader readerHandler = new PushXmlMessageParamsReader();
                saxParser.parse(inputSource, readerHandler);
                return (T) readerHandler.getParams();
            } else if (PushXmlMessageResult.class.equals(type)) {
                PushXmlMessageResultReader readerHandler = new PushXmlMessageResultReader();
                saxParser.parse(inputSource, readerHandler);
                return (T) readerHandler.getResult();
            } else if (PullXmlMessageParams.class.equals(type)) {
                PullXmlMessageParamsReader readerHandler = new PullXmlMessageParamsReader();
                saxParser.parse(inputSource, readerHandler);
                return (T) readerHandler.getParams();
            } else if (PullXmlMessageResult.class.equals(type)) {
                PullXmlMessageResultReader readerHandler = new PullXmlMessageResultReader();
                saxParser.parse(inputSource, readerHandler);
                return (T) readerHandler.getResult();
            } else {
                throwOperationErrorException(new RuntimeException("Unsupported stream object type - " + type));
            }
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        } finally {
            if (saxParser != null) {
                XmlUtils.restoreSAXParser(saxParser);
            }
        }

        return null;
    }

    private void writeAttribute(Writer writer, String qname, String val) throws IOException {
        if (StringUtils.isNotBlank(val)) {
            writer.write(" ");
            writer.write(qname);
            writer.write(" = \"");
            writer.write(val);
            writer.write("\"");
        }
    }

    private void writeAttribute(StringBuilder sb, String qname, String val) throws IOException {
        if (StringUtils.isNotBlank(val)) {
            sb.append(" ").append(qname).append(" = \"").append(val).append("\"");
        }
    }

    private class PushXmlMessageParamsReader extends DefaultHandler {

        private StringBuilder sb;

        private String methodCode;

        private String clientAppCode;

        private String destination;

        private String tag;

        private String branchCode;

        private String departmentCode;

        private String consumer;

        private Stack<String> track;

        private PushXmlMessageParams params;

        public PushXmlMessageParamsReader() {
            track = new Stack<String>();
        }

        public PushXmlMessageParams getParams() {
            return params;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if ("PushXmlMessage".equals(qName)) {
                if (track.size() != 0) {
                    throw new SAXException("Bad tagged XML message params structure!");
                }

                methodCode = attributes.getValue("methodCode");
                if (StringUtils.isBlank(methodCode)) {
                    throw new SAXException("Missing 'methodCode' attribute");
                }

                destination = attributes.getValue("destination");
                clientAppCode = attributes.getValue("clientAppCode");

                tag = attributes.getValue("tag");
                if (StringUtils.isBlank(tag)) {
                    throw new SAXException("Missing 'tag' attribute");
                }

                branchCode = attributes.getValue("branchCode");
                departmentCode = attributes.getValue("departmentCode");
                consumer = attributes.getValue("consumer");
                sb = new StringBuilder();
            } else {
                if (track.size() == 0) {
                    throw new SAXException("Invalid root element!");
                }

                sb.append("<").append(qName);
                try {
                    // Append attributes
                    int len = attributes.getLength();
                    for (int i = 0; i < len; i++) {
                        writeAttribute(sb, attributes.getQName(i), attributes.getValue(i));
                    }
                } catch (IOException e) {
                    throw new SAXException(e);
                }
                sb.append(">");
            }

            track.push(qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            sb.append(new String(ch, start, length));
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            track.pop();
            if ("PushXmlMessage".equals(qName)) {
                if (track.size() != 0) {
                    throw new SAXException("Bad tagged XML message structure!");
                }

                String xml = sb.toString();
                if (StringUtils.isBlank(xml)) {
                    xml = null;
                }
                params = new PushXmlMessageParams(methodCode, clientAppCode, destination,
                        new TaggedXmlMessage(tag, branchCode, departmentCode, consumer, xml));
            } else {
                sb.append("</").append(qName);
                sb.append(">");
            }
        }

    }

    private class PushXmlMessageResultReader extends DefaultHandler {

        private String methodCode;

        private String errorCode;

        private String errorMsg;

        private Stack<String> track;

        private PushXmlMessageResult result;

        public PushXmlMessageResultReader() {
            track = new Stack<String>();
        }

        public PushXmlMessageResult getResult() {
            return result;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (track.size() > 1) {
                throw new SAXException("Unexpected elements!");
            }

            if ("PushXmlMessageResult".equals(qName)) {
                methodCode = attributes.getValue("methodCode");
                errorCode = attributes.getValue("errorCode");
            } else {
                if (track.size() == 0) {
                    throw new SAXException("Invalid root element!");
                }
            }

            track.push(qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if ("errorMsg".equals(track.peek())) {
                errorMsg = new String(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            track.pop();
            if (track.size() == 0) {
                result = new PushXmlMessageResult(methodCode, errorCode, errorMsg);
            }
        }

    }

    private class PullXmlMessageParamsReader extends DefaultHandler {

        private String methodCode;

        private String clientAppCode;

        private String source;

        private Stack<String> track;

        private PullXmlMessageParams params;

        public PullXmlMessageParamsReader() {
            track = new Stack<String>();
        }

        public PullXmlMessageParams getParams() {
            return params;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (track.size() > 0) {
                throw new SAXException("Unexpected elements!");
            }

            if ("PullXmlMessage".equals(qName)) {
                methodCode = attributes.getValue("methodCode");
                clientAppCode = attributes.getValue("clientAppCode");
                source = attributes.getValue("source");
            } else {
                if (track.size() == 0) {
                    throw new SAXException("Invalid root element!");
                }
            }

            track.push(qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {

        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            track.pop();
            if (track.size() == 0) {
                params = new PullXmlMessageParams(methodCode, clientAppCode, source);
            }
        }

    }

    private class PullXmlMessageResultReader extends DefaultHandler {

        private StringBuilder sb;

        private String methodCode;

        private String errorCode;

        private String errorMsg;

        private String tag;

        private String branchCode;

        private String departmentCode;

        private String consumer;

        private Stack<String> track;

        private PullXmlMessageResult result;

        public PullXmlMessageResultReader() {
            track = new Stack<String>();
        }

        public PullXmlMessageResult getResult() {
            return result;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if ("PullXmlMessageResult".equals(qName)) {
                if (track.size() != 0) {
                    throw new SAXException("Bad tagged XML message params structure!");
                }

                methodCode = attributes.getValue("methodCode");
                if (StringUtils.isBlank(methodCode)) {
                    throw new SAXException("Missing 'methodCode' attribute");
                }

                errorCode = attributes.getValue("errorCode");

                tag = attributes.getValue("tag");
                if (StringUtils.isBlank(tag)) {
                    throw new SAXException("Missing 'tag' attribute");
                }

                branchCode = attributes.getValue("branchCode");
                departmentCode = attributes.getValue("departmentCode");
                consumer = attributes.getValue("consumer");
                sb = new StringBuilder();
            } else {
                if (track.size() == 0) {
                    throw new SAXException("Invalid root element!");
                }

                if (!"errorMsg".equals(qName)) {
                    sb.append("<").append(qName);
                    try {
                        // Append attributes
                        int len = attributes.getLength();
                        for (int i = 0; i < len; i++) {
                            writeAttribute(sb, attributes.getQName(i), attributes.getValue(i));
                        }
                    } catch (IOException e) {
                        throw new SAXException(e);
                    }
                    sb.append(">");
                }
            }

            track.push(qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if ("errorMsg".equals(track.peek())) {
                errorMsg = new String(ch, start, length);
            } else {
                sb.append(new String(ch, start, length));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            track.pop();
            if ("PullXmlMessageResult".equals(qName)) {
                if (track.size() != 0) {
                    throw new SAXException("Bad tagged XML message structure!");
                }

                String xml = sb.toString();
                if (StringUtils.isBlank(xml)) {
                    xml = null;
                }

                result = new PullXmlMessageResult(methodCode, errorCode, errorMsg,
                        new TaggedXmlMessage(tag, branchCode, departmentCode, consumer, xml));
            } else if (!"errorMsg".equals(qName)) {
                sb.append("</").append(qName);
                sb.append(">");
            }
        }

    }

}
