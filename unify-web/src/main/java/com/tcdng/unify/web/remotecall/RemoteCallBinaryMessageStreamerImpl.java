/*
 * Copyright (c) 2018-2025 The Code Department.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.constant.PrintFormat;
import com.tcdng.unify.core.data.TaggedBinaryMessage;
import com.tcdng.unify.core.stream.AbstractObjectStreamer;

/**
 * Remote call tagged binary message streamer.
 * 
 * @author The Code Department
 * @since 4.1
 */
@Component("rc-binarymessagestreamer")
public class RemoteCallBinaryMessageStreamerImpl extends AbstractObjectStreamer
        implements RemoteCallBinaryMessageStreamer {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset) throws UnifyException {
        try {
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            if (PushBinaryMessageParams.class.equals(type)) {
                String clientAppCode = (String) ois.readObject();
                String methodCode = (String) ois.readObject();
                String destination = (String) ois.readObject();
                return (T) new PushBinaryMessageParams(methodCode, clientAppCode, destination,
                        readTaggedBinaryObject(ois));
            } else if (PushBinaryMessageResult.class.equals(type)) {
                String methodCode = (String) ois.readObject();
                String errorCode = (String) ois.readObject();
                String errorMsg = (String) ois.readObject();
                return (T) new PushBinaryMessageResult(methodCode, errorCode, errorMsg);
            } else if (PullBinaryMessageParams.class.equals(type)) {
                String clientAppCode = (String) ois.readObject();
                String methodCode = (String) ois.readObject();
                String source = (String) ois.readObject();
                return (T) new PullBinaryMessageParams(methodCode, clientAppCode, source);
            } else if (PullBinaryMessageResult.class.equals(type)) {
                String methodCode = (String) ois.readObject();
                String errorCode = (String) ois.readObject();
                String errorMsg = (String) ois.readObject();

                return (T) new PullBinaryMessageResult(methodCode, errorCode, errorMsg, readTaggedBinaryObject(ois));
            } else {
                throwOperationErrorException(new RuntimeException("Unsupported stream object type - " + type));
            }
        } catch (Exception e) {
            throwOperationErrorException(e);
        }

        return null;
    }

    @Override
    public <T> T unmarshal(Class<T> type, Reader reader) throws UnifyException {
        throwUnsupportedOperationException();
        return null;
    }

    @Override
    public <T> T unmarshal(Class<T> type, String string) throws UnifyException {
        throwUnsupportedOperationException();
        return null;
    }

    @Override
    public void marshal(Object object, OutputStream outputStream, Charset charset, PrintFormat printFormat) throws UnifyException {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            if (PushBinaryMessageParams.class.equals(object.getClass())) {
                PushBinaryMessageParams tmParams = (PushBinaryMessageParams) object;
                oos.writeObject(tmParams.getClientAppCode());
                oos.writeObject(tmParams.getMethodCode());
                oos.writeObject(tmParams.getDestination());
                writeTaggedBinaryObject(oos, tmParams.getTaggedMessage());
            } else if (PushBinaryMessageResult.class.equals(object.getClass())) {
                PushBinaryMessageResult rma = (PushBinaryMessageResult) object;
                oos.writeObject(rma.getMethodCode());
                oos.writeObject(rma.getErrorCode());
                oos.writeObject(rma.getErrorMsg());
            } else if (PullBinaryMessageParams.class.equals(object.getClass())) {
                PullBinaryMessageParams tmParams = (PullBinaryMessageParams) object;
                oos.writeObject(tmParams.getClientAppCode());
                oos.writeObject(tmParams.getMethodCode());
                oos.writeObject(tmParams.getSource());
            } else if (PullBinaryMessageResult.class.equals(object.getClass())) {
                PullBinaryMessageResult rma = (PullBinaryMessageResult) object;
                oos.writeObject(rma.getMethodCode());
                oos.writeObject(rma.getErrorCode());
                oos.writeObject(rma.getErrorMsg());
                writeTaggedBinaryObject(oos, rma.getTaggedMessage());
            } else {
                throwOperationErrorException(
                        new RuntimeException("Unsupported stream object type - " + object.getClass()));
            }

            oos.flush();
        } catch (IOException e) {
            throwOperationErrorException(e);
        }
    }

    @Override
    public void marshal(Object object, Writer writer, PrintFormat printFormat) throws UnifyException {
        throwUnsupportedOperationException();
    }

    @Override
    public byte[] marshal(Object object, PrintFormat printFormat) throws UnifyException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshal(object, baos);
        return baos.toByteArray();
    }

    private TaggedBinaryMessage readTaggedBinaryObject(ObjectInputStream ois) throws Exception {
        String tag = (String) ois.readObject();
        String branchCode = (String) ois.readObject();
        String departmentCode = (String) ois.readObject();
        String consumer = (String) ois.readObject();
        int length = ois.readInt();
        byte[] message = null;
        if (length > 0) {
            message = new byte[length];
            ois.readFully(message);
        }

        return new TaggedBinaryMessage(tag, branchCode, departmentCode, consumer, message);
    }

    private void writeTaggedBinaryObject(ObjectOutputStream oos, TaggedBinaryMessage tm) throws IOException {
        oos.writeObject(tm.getTag());
        oos.writeObject(tm.getBranchCode());
        oos.writeObject(tm.getDepartmentCode());
        oos.writeObject(tm.getConsumer());
        byte[] message = tm.getMessage();
        int length = 0;
        if (message != null) {
            length = message.length;
        }

        oos.writeInt(length);
        if (length > 0) {
            oos.write(message);
        }
    }

}
