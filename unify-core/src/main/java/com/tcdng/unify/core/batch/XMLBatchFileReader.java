/*
 * Copyright 2018 The Code Department
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
package com.tcdng.unify.core.batch;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Parameter;
import com.tcdng.unify.core.annotation.Parameters;
import com.tcdng.unify.core.business.BusinessLogicInput;
import com.tcdng.unify.core.data.SAXParserPool;
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.ThreadUtils;

/**
 * XML batch file reader.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(name = "xml-batchfilereader", description = "$m{batchfilereader.xml}")
@Parameters({ @Parameter(name = XMLBatchFileReaderInputConstants.BATCH_TAG_NAME,
        description = "$m{batchfilereader.xml.batchtagname}", editor = "!ui-name minLen:1 size:24", mandatory = true),
        @Parameter(name = XMLBatchFileReaderInputConstants.BATCHITEM_TAG_NAME,
                description = "$m{batchfilereader.xml.batchitemtagname}", editor = "!ui-name minLen:1 size:24",
                mandatory = true) })
public class XMLBatchFileReader extends AbstractBatchFileReader {

    private static enum ErrorType {
        START_BATCH_TAG_MISSING(
                UnifyCoreErrorConstants.XMLBATCHFILEREADER_STARTTAG_MISSING), START_BATCHITEM_TAG_MULTIPLE(
                        UnifyCoreErrorConstants.XMLBATCHFILEREADER_BATCHITEMTAG_MULTIPLE), BATCHITEM_TAG_UNKNOWN(
                                UnifyCoreErrorConstants.XMLBATCHFILEREADER_BATCHITEMTAG_UNKNOWN), BATCHITEM_MULTIPLE(
                                        UnifyCoreErrorConstants.XMLBATCHFILEREADER_BATCHITEMTAG_UNKNOWN), BATCHITEM_EXCEPTION(
                                                UnifyCoreErrorConstants.XMLBATCHFILEREADER_BATCHITEM_EXCEPTION);

        private final String errorCode;

        private ErrorType(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    };

    private static SAXParserPool saxParserPool;

    private BatchFileSAXReader batchFileSAXReader;

    @Override
    public void open(BusinessLogicInput input, BatchFileConfig batchFileConfig, Object[] file) throws UnifyException {
        this.batchFileSAXReader = new BatchFileSAXReader(batchFileConfig,
                input.getParameter(String.class, XMLBatchFileReaderInputConstants.BATCH_TAG_NAME),
                input.getParameter(String.class, XMLBatchFileReaderInputConstants.BATCHITEM_TAG_NAME),
                IOUtils.detectAndOpenInputStream(file[0]));
        this.batchFileSAXReader.start();
    }

    @Override
    public void close() {
        if (this.batchFileSAXReader != null) {
            this.batchFileSAXReader.stop();
        }
    }

    @Override
    public boolean readNextRecord(ValueStore valueStore) throws UnifyException {
        return this.batchFileSAXReader.readRecord(valueStore);
    }

    @Override
    public boolean skipNextRecord() throws UnifyException {
        return this.batchFileSAXReader.skipRecord();
    }

    private static SAXParserPool getSAXParserPool() {
        if (saxParserPool == null) {
            synchronized (XMLBatchFileReader.class) {
                if (saxParserPool == null) {
                    saxParserPool = new SAXParserPool();
                }
            }
        }

        return saxParserPool;
    }

    private class BatchFileSAXReader extends DefaultHandler implements Runnable {

        private Map<String, FieldValue> valuesByFieldName;

        private ValueStore valueStore;

        private String batchTagName;

        private String batchItemTagName;

        private String text;

        private String errQName;

        private ErrorType errorType;

        private InputStream inputStream;

        private boolean batchTagFlag;

        private boolean batchItemTagFlag;

        private boolean isSyncPoint;

        private boolean isSkip;

        private boolean isExit;

        public BatchFileSAXReader(BatchFileConfig batchFileConfig, String batchTagName, String batchItemTagName,
                InputStream inputStream) {
            this.batchTagName = batchTagName;
            this.batchItemTagName = batchItemTagName;
            this.inputStream = inputStream;
            this.valuesByFieldName = new HashMap<String, FieldValue>();
            for (BatchFileFieldConfig bffc : batchFileConfig.getFieldConfigs()) {
                this.valuesByFieldName.put(bffc.getReaderFieldName(), new FieldValue(bffc));
            }
        }

        @Override
        public void run() {
            try {
                SAXParser saxParser = XMLBatchFileReader.getSAXParserPool().borrowObject();
                try {
                    saxParser.parse(this.inputStream, this);
                } finally {
                    XMLBatchFileReader.getSAXParserPool().returnObject(saxParser);
                }
            } catch (XMLSAXException e) {

            } catch (Exception e) {
                logError(e);
            } finally {
                IOUtils.close(inputStream);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (!this.batchTagFlag) {
                if (this.batchTagName.equals(qName)) {
                    this.batchTagFlag = true;
                    return;
                } else {
                    this.internalStop(ErrorType.START_BATCH_TAG_MISSING, qName);
                }
            }

            if (this.batchItemTagName.equals(qName)) {
                if (this.batchItemTagFlag) {
                    this.internalStop(ErrorType.START_BATCHITEM_TAG_MULTIPLE, qName);
                }

                this.clearReadValues();
                this.batchItemTagFlag = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (this.batchTagName.equals(qName)) {
                this.batchTagFlag = false;
                this.internalStop(null, null);
            }

            if (this.batchItemTagName.equals(qName)) {
                try {
                    // Wait for value store to be available
                    while (!this.isSkip && !this.isExit && this.valueStore == null) {
                        ThreadUtils.yield();
                    }

                    if (!this.isSkip && !this.isExit && this.valueStore != null) {
                        // Write record
                        for (FieldValue fieldValue : this.valuesByFieldName.values()) {
                            Formatter<?> formatter = null;
                            if (fieldValue.getConfig().isFormatter()) {
                                formatter = (Formatter<?>) getComponent(fieldValue.getConfig().getFormatter());
                            }

                            this.valueStore.store(fieldValue.getConfig().getFieldName(), fieldValue.getValue(),
                                    formatter);
                        }
                    }

                } catch (Exception e) {
                    logError(e);
                    this.internalStop(ErrorType.BATCHITEM_EXCEPTION, qName);
                } finally {
                    this.batchItemTagFlag = false;
                    this.isSyncPoint = false;
                    this.isSkip = false;
                    this.valueStore = null;
                }

                return;
            }

            FieldValue fieldValue = this.valuesByFieldName.get(qName);
            if (fieldValue != null) {
                if (fieldValue.getValue() == null) {
                    fieldValue.setValue(this.text);
                } else {
                    this.internalStop(ErrorType.BATCHITEM_MULTIPLE, qName);
                }
            } else {
                this.internalStop(ErrorType.BATCHITEM_TAG_UNKNOWN, qName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.text = new String(ch, start, length);
        }

        public boolean readRecord(ValueStore valueStore) throws UnifyException {
            this.isSyncPoint = true;
            this.valueStore = valueStore;
            while (!this.isExit && isSyncPoint) {
                ThreadUtils.yield();
            }

            if (this.errorType != null) {
                throw new UnifyException(this.errorType.getErrorCode(), this.errQName);
            }

            return !this.isExit;
        }

        public boolean skipRecord() throws UnifyException {
            return this.isSkip = true;
        }

        public void start() {
            if (!this.isExit) {
                new Thread(this).start();
            }
        }

        public void stop() {
            this.isSyncPoint = false;
            this.isSkip = false;
            this.isExit = true;
        }

        private void internalStop(ErrorType errorType, String errQName) throws XMLSAXException {
            this.errorType = errorType;
            this.errQName = errQName;
            this.isSyncPoint = false;
            this.isExit = true;
            throw new XMLSAXException();
        }

        private void clearReadValues() {
            for (Map.Entry<String, FieldValue> entry : this.valuesByFieldName.entrySet()) {
                entry.getValue().setValue(null);
            }
        }

    }

    private class XMLSAXException extends SAXException {

        private static final long serialVersionUID = 3111591481791978987L;

    }

    private class FieldValue {

        private BatchFileFieldConfig config;

        private String value;

        public FieldValue(BatchFileFieldConfig config) {
            this.config = config;
        }

        public BatchFileFieldConfig getConfig() {
            return config;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
