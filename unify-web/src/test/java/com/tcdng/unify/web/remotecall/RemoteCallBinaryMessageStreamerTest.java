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

package com.tcdng.unify.web.remotecall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.data.TaggedBinaryMessage;
import com.tcdng.unify.web.remotecall.PushBinaryMessageParams;
import com.tcdng.unify.web.remotecall.PushBinaryMessageResult;
import com.tcdng.unify.web.remotecall.RemoteCallBinaryMessageStreamer;

/**
 * Remote call binary message streamer test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class RemoteCallBinaryMessageStreamerTest extends AbstractUnifyComponentTest {

    @Test(expected = NullPointerException.class)
    public void testMarshallBlankTaggedBinaryMessageParams() throws Exception {
        getBinaryMessageStreamer().marshal(new PushBinaryMessageParams(), new ByteArrayOutputStream());
    }

    @Test
    public void testMarshallTaggedBinaryMessageParamsBlankBinary() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(
                new PushBinaryMessageParams("methodName", new TaggedBinaryMessage("tag", "consumer", null)), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testMarshallTaggedBinaryMessageParams() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] message = new byte[] { 0x12, 0x02, 0x55, 0x00, 0x43 };
        getBinaryMessageStreamer().marshal(new PushBinaryMessageParams("methodName", "appOne",
                new TaggedBinaryMessage("tag", "consumer", message)), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testUnmarshallTaggedBinaryMessageParamsBlankBinary() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(
                new PushBinaryMessageParams("methodName", new TaggedBinaryMessage("tag", "consumer", null)), baos);
        PushBinaryMessageParams tbmp = getBinaryMessageStreamer().unmarshal(PushBinaryMessageParams.class,
                new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(tbmp);
        assertNull(tbmp.getClientAppCode());
        assertEquals("methodName", tbmp.getMethodCode());

        TaggedBinaryMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("consumer", tbm.getConsumer());
        assertEquals("tag", tbm.getTag());
        assertNull(tbm.getMessage());
    }

    @Test
    public void testUnmarshallTaggedBinaryMessageParams() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] message = new byte[] { 0x12, 0x02, 0x55, 0x00, 0x43 };
        getBinaryMessageStreamer().marshal(new PushBinaryMessageParams("methodName", "appOne",
                new TaggedBinaryMessage("tag", "consumer", message)), baos);
        PushBinaryMessageParams tbmp = getBinaryMessageStreamer().unmarshal(PushBinaryMessageParams.class,
                new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(tbmp);
        assertEquals("appOne", tbmp.getClientAppCode());
        assertEquals("methodName", tbmp.getMethodCode());

        TaggedBinaryMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("consumer", tbm.getConsumer());
        assertEquals("tag", tbm.getTag());
        byte[] extMessage = tbm.getMessage();
        assertNotNull(extMessage);
        assertTrue(Arrays.equals(message, extMessage));
    }

    @Test
    public void testMarshallTaggedBinaryMessageResultBlank() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult(), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testMarshallTaggedBinaryMessageResult() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult("methodOne", null, null), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult("methodOne", "error2", null), baos);
        marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer()
                .marshal(new PushBinaryMessageResult("methodOne", "error2", "There was an error"), baos);
        marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testUnmarshallTaggedBinaryMessageResultBlank() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult(), baos);
        byte[] marshalled = baos.toByteArray();
        PushBinaryMessageResult result = getBinaryMessageStreamer().unmarshal(PushBinaryMessageResult.class,
                new ByteArrayInputStream(marshalled));
        assertNotNull(result);
        assertNull(result.getMethodCode());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMsg());
    }

    @Test
    public void testUnmarshallTaggedBinaryMessageResult() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult("methodOne", null, null), baos);
        byte[] marshalled = baos.toByteArray();
        PushBinaryMessageResult result = getBinaryMessageStreamer().unmarshal(PushBinaryMessageResult.class,
                new ByteArrayInputStream(marshalled));
        assertNotNull(result);
        assertEquals("methodOne", result.getMethodCode());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMsg());

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult("methodOne", "error2", null), baos);
        marshalled = baos.toByteArray();
        result = getBinaryMessageStreamer().unmarshal(PushBinaryMessageResult.class,
                new ByteArrayInputStream(marshalled));
        assertNotNull(result);
        assertEquals("methodOne", result.getMethodCode());
        assertEquals("error2", result.getErrorCode());
        assertNull(result.getErrorMsg());

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer()
                .marshal(new PushBinaryMessageResult("methodOne", "error2", "There was an error"), baos);
        marshalled = baos.toByteArray();
        result = getBinaryMessageStreamer().unmarshal(PushBinaryMessageResult.class,
                new ByteArrayInputStream(marshalled));
        assertNotNull(result);
        assertEquals("methodOne", result.getMethodCode());
        assertEquals("error2", result.getErrorCode());
        assertEquals("There was an error", result.getErrorMsg());
    }

    protected RemoteCallBinaryMessageStreamer getBinaryMessageStreamer() throws Exception {
        return (RemoteCallBinaryMessageStreamer) getComponent("taggedbinarymessage-streamer");
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
