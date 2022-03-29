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
import com.tcdng.unify.core.data.ValueStore;
import com.tcdng.unify.core.format.Formatter;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.ThreadUtils;
import com.tcdng.unify.core.util.XmlUtils;

/**
 * XML batch file reader.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(name = "xml-batchfilereader", description = "$m{batchfilereader.xml}")
@Parameters({
        @Parameter(
                name = XMLBatchFileReaderInputConstants.BATCH_TAG_NAME,
                description = "$m{batchfilereader.xml.batchtagname}",
                editor = "!ui-name minLen:1 size:24",
                mandatory = true),
        @Parameter(
                name = XMLBatchFileReaderInputConstants.BATCHITEM_TAG_NAME,
                description = "$m{batchfilereader.xml.batchitemtagname}",
                editor = "!ui-name minLen:1 size:24",
                mandatory = true) })
public class XMLBatchFileReader extends AbstractBatchFileReader {

    private static enum ErrorType {
        START_BATCH_TAG_MISSING(UnifyCoreErrorConstants.XMLBATCHFILEREADER_STARTTAG_MISSING),
        START_BATCHITEM_TAG_MULTIPLE(UnifyCoreErrorConstants.XMLBATCHFILEREADER_BATCHITEMTAG_MULTIPLE),
        BATCHITEM_TAG_UNKNOWN(UnifyCoreErrorConstants.XMLBATCHFILEREADER_BATCHITEMTAG_UNKNOWN),
        BATCHITEM_MULTIPLE(UnifyCoreErrorConstants.XMLBATCHFILEREADER_BATCHITEMTAG_UNKNOWN),
        BATCHITEM_EXCEPTION(UnifyCoreErrorConstants.XMLBATCHFILEREADER_BATCHITEM_EXCEPTION);

        private final String errorCode;

        private ErrorType(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    };

    private BatchFileSAXReader batchFileSAXReader;

    @Override
    public void open(BatchFileReadConfig batchFileReadConfig, Object... file) throws UnifyException {
        batchFileSAXReader = new BatchFileSAXReader(batchFileReadConfig,
                batchFileReadConfig.getParameter(String.class, XMLBatchFileReaderInputConstants.BATCH_TAG_NAME),
                batchFileReadConfig.getParameter(String.class, XMLBatchFileReaderInputConstants.BATCHITEM_TAG_NAME),
                IOUtils.detectAndOpenInputStream(file[0]));
        batchFileSAXReader.start();
    }

    @Override
    public boolean detectPreferredBean() throws UnifyException {
        return false;
    }

    @Override
    public Class<?> getPreferredBean() throws UnifyException {
        return null;
    }

    @Override
    public void close() {
        if (batchFileSAXReader != null) {
            batchFileSAXReader.stop();
        }
    }

    @Override
    public boolean readNextRecord(ValueStore valueStore) throws UnifyException {
        return batchFileSAXReader.readRecord(valueStore);
    }

    @Override
    public boolean skipNextRecord() throws UnifyException {
        return batchFileSAXReader.skipRecord();
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

        public BatchFileSAXReader(BatchFileReadConfig batchFileConfig, String batchTagName, String batchItemTagName,
                InputStream inputStream) {
            this.batchTagName = batchTagName;
            this.batchItemTagName = batchItemTagName;
            this.inputStream = inputStream;
            this.valuesByFieldName = new HashMap<String, FieldValue>();
            for (BatchFileFieldConfig bffc : batchFileConfig.getFieldConfigList()) {
                this.valuesByFieldName.put(bffc.getFileFieldName(), new FieldValue(bffc));
            }
        }

        @Override
        public void run() {
            SAXParser saxParser = null;
            try {
                saxParser = XmlUtils.borrowSAXParser();
                saxParser.parse(inputStream, this);
            } catch (Exception e) {
                logError(e);
            } finally {
                if (saxParser != null) {
                    XmlUtils.restoreSAXParser(saxParser);
                }
                
                IOUtils.close(inputStream);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (!batchTagFlag) {
                if (batchTagName.equals(qName)) {
                    batchTagFlag = true;
                    return;
                } else {
                    this.internalStop(ErrorType.START_BATCH_TAG_MISSING, qName);
                }
            }

            if (batchItemTagName.equals(qName)) {
                if (batchItemTagFlag) {
                    internalStop(ErrorType.START_BATCHITEM_TAG_MULTIPLE, qName);
                }

                clearReadValues();
                batchItemTagFlag = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (batchTagName.equals(qName)) {
                batchTagFlag = false;
                internalStop(null, null);
            }

            if (batchItemTagName.equals(qName)) {
                try {
                    // Wait for value store to be available
                    while (!isSkip && !isExit && valueStore == null) {
                        ThreadUtils.yield();
                    }

                    if (!isSkip && !isExit && valueStore != null) {
                        // Write record
                        for (FieldValue fieldValue : valuesByFieldName.values()) {
                            Formatter<?> formatter = null;
                            if (fieldValue.getConfig().isFormatter()) {
                                formatter = (Formatter<?>) getComponent(fieldValue.getConfig().getFormatter());
                            }

                            valueStore.store(fieldValue.getConfig().getBeanFieldName(), fieldValue.getValue(),
                                    formatter);
                        }
                    }

                } catch (Exception e) {
                    logError(e);
                    internalStop(ErrorType.BATCHITEM_EXCEPTION, qName);
                } finally {
                    batchItemTagFlag = false;
                    isSyncPoint = false;
                    isSkip = false;
                    valueStore = null;
                }

                return;
            }

            FieldValue fieldValue = valuesByFieldName.get(qName);
            if (fieldValue != null) {
                if (fieldValue.getValue() == null) {
                    fieldValue.setValue(this.text);
                } else {
                    internalStop(ErrorType.BATCHITEM_MULTIPLE, qName);
                }
            } else {
                internalStop(ErrorType.BATCHITEM_TAG_UNKNOWN, qName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            text = new String(ch, start, length);
        }

        public boolean readRecord(ValueStore valueStore) throws UnifyException {
            this.valueStore = valueStore;
            isSyncPoint = true;
            while (!isExit && isSyncPoint) {
                ThreadUtils.yield();
            }

            if (errorType != null) {
                throw new UnifyException(errorType.getErrorCode(), errQName);
            }

            return !isExit;
        }

        public boolean skipRecord() throws UnifyException {
            return isSkip = true;
        }

        public void start() {
            if (!isExit) {
                new Thread(this).start();
            }
        }

        public void stop() {
            isSyncPoint = false;
            isSkip = false;
            isExit = true;
        }

        private void internalStop(ErrorType errorType, String errQName) throws XMLSAXException {
            this.errorType = errorType;
            this.errQName = errQName;
            this.isSyncPoint = false;
            this.isExit = true;
            throw new XMLSAXException();
        }

        private void clearReadValues() {
            for (Map.Entry<String, FieldValue> entry : valuesByFieldName.entrySet()) {
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
