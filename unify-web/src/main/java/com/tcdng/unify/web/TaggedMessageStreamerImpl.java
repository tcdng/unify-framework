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

package com.tcdng.unify.web;

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
import com.tcdng.unify.core.data.TaggedMessage;
import com.tcdng.unify.core.stream.AbstractObjectStreamer;

/**
 * Tagged message streamer.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("taggedmessage-streamer")
public class TaggedMessageStreamerImpl extends AbstractObjectStreamer implements TaggedMessageStreamer {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unmarshal(Class<T> type, InputStream inputStream, Charset charset) throws UnifyException {
        try {
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            if (TaggedMessageParams.class.equals(type)) {
                String clientAppCode = (String) ois.readObject();
                String methodCode = (String) ois.readObject();
                String tag = (String) ois.readObject();
                int length = ois.readInt();
                byte[] message = null;
                if (length > 0) {
                    message = new byte[length];
                    ois.readFully(message);
                }

                TaggedMessageParams tmParams = new TaggedMessageParams(methodCode, new TaggedMessage(tag, message));
                tmParams.setClientAppCode(clientAppCode);
                return (T) tmParams;
            } else if (TaggedMessageResult.class.equals(type)) {
                String methodCode = (String) ois.readObject();
                String errorCode = (String) ois.readObject();
                String errorMsg = (String) ois.readObject();
                return (T) new TaggedMessageResult(methodCode, errorCode, errorMsg);
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
    public void marshal(Object object, OutputStream outputStream, Charset charset) throws UnifyException {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            if (TaggedMessageParams.class.equals(object.getClass())) {
                TaggedMessageParams tmParams = (TaggedMessageParams) object;
                oos.writeObject((String) tmParams.getClientAppCode());
                oos.writeObject((String) tmParams.methodCode());

                TaggedMessage tm = tmParams.getTaggedMessage();
                oos.writeObject((String) tm.getTag());
                byte[] message = tm.getMessage();
                int length = 0;
                if (message != null) {
                    length = message.length;
                }

                oos.writeInt(length);
                if (length > 0) {
                    oos.write(message);
                }
            } else if (TaggedMessageResult.class.equals(object.getClass())) {
                TaggedMessageResult rma = (TaggedMessageResult) object;
                oos.writeObject(rma.getMethodCode());
                oos.writeObject(rma.getErrorCode());
                oos.writeObject(rma.getErrorMsg());
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
    public void marshal(Object object, Writer writer) throws UnifyException {
        throwUnsupportedOperationException();
    }

    @Override
    public byte[] marshal(Object object) throws UnifyException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshal(object, baos);
        return baos.toByteArray();
    }

}
