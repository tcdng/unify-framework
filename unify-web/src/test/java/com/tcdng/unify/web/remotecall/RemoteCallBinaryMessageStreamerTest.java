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
 * @author The Code Department
 * @since 1.0
 */
public class RemoteCallBinaryMessageStreamerTest extends AbstractUnifyComponentTest {

    @Test(expected = NullPointerException.class)
    public void testMarshallBlankPushBinaryMessageParams() throws Exception {
        getBinaryMessageStreamer().marshal(new PushBinaryMessageParams(), new ByteArrayOutputStream());
    }

    @Test
    public void testMarshallPushBinaryMessageParamsBlankBinary() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageParams("methodName", null, null,
                new TaggedBinaryMessage("tag", null, null, "consumer", null)), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testMarshallPushBinaryMessageParams() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] message = new byte[] { 0x12, 0x02, 0x55, 0x00, 0x43 };
        getBinaryMessageStreamer().marshal(new PushBinaryMessageParams("methodName", "appOne", "destination",
                new TaggedBinaryMessage("tag", "branchCode", "departmentCode", "consumer", message)), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testUnmarshallPushBinaryMessageParamsBlankBinary() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageParams("methodName", null, null,
                new TaggedBinaryMessage("tag", "BN001", "DEPT003", "consumer", null)), baos);
        PushBinaryMessageParams tbmp = getBinaryMessageStreamer().unmarshal(PushBinaryMessageParams.class,
                new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(tbmp);
        assertNull(tbmp.getClientAppCode());
        assertNull(tbmp.getDestination());
        assertEquals("methodName", tbmp.getMethodCode());

        TaggedBinaryMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("tag", tbm.getTag());
        assertEquals("BN001", tbm.getBranchCode());
        assertEquals("DEPT003", tbm.getDepartmentCode());
        assertEquals("consumer", tbm.getConsumer());
        assertNull(tbm.getMessage());
    }

    @Test
    public void testUnmarshallPushBinaryMessageParams() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] message = new byte[] { 0x12, 0x02, 0x55, 0x00, 0x43 };
        getBinaryMessageStreamer().marshal(new PushBinaryMessageParams("methodName", "appOne", "destination",
                new TaggedBinaryMessage("tag", "BN001", "DEPT003", "consumer", message)), baos);
        PushBinaryMessageParams tbmp = getBinaryMessageStreamer().unmarshal(PushBinaryMessageParams.class,
                new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(tbmp);
        assertEquals("appOne", tbmp.getClientAppCode());
        assertEquals("methodName", tbmp.getMethodCode());
        assertEquals("destination", tbmp.getDestination());

        TaggedBinaryMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("tag", tbm.getTag());
        assertEquals("BN001", tbm.getBranchCode());
        assertEquals("DEPT003", tbm.getDepartmentCode());
        assertEquals("consumer", tbm.getConsumer());
        byte[] extMessage = tbm.getMessage();
        assertNotNull(extMessage);
        assertTrue(Arrays.equals(message, extMessage));
    }

    @Test
    public void testMarshallPushBinaryMessageResultBlank() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult(), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testMarshallPushBinaryMessageResult() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult("methodOne", null, null), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult("methodOne", "error2", null), baos);
        marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult("methodOne", "error2", "There was an error"),
                baos);
        marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testUnmarshallPushBinaryMessageResultBlank() throws Exception {
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
    public void testUnmarshallPushBinaryMessageResult() throws Exception {
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
        getBinaryMessageStreamer().marshal(new PushBinaryMessageResult("methodOne", "error2", "There was an error"),
                baos);
        marshalled = baos.toByteArray();
        result = getBinaryMessageStreamer().unmarshal(PushBinaryMessageResult.class,
                new ByteArrayInputStream(marshalled));
        assertNotNull(result);
        assertEquals("methodOne", result.getMethodCode());
        assertEquals("error2", result.getErrorCode());
        assertEquals("There was an error", result.getErrorMsg());
    }

    @Test
    public void testMarshallPullBinaryMessageParamsBlank() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageParams(), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testMarshallPullBinaryMessageParams() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageParams("methodOne", null, null), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageParams("methodOne", "appTwo", null), baos);
        marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageParams("methodOne", "appTwo", "source"), baos);
        marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testUnmarshallPullBinaryMessageParamsBlank() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageParams(), baos);
        byte[] marshalled = baos.toByteArray();
        PullBinaryMessageParams params = getBinaryMessageStreamer().unmarshal(PullBinaryMessageParams.class,
                new ByteArrayInputStream(marshalled));
        assertNotNull(params);
        assertNull(params.getMethodCode());
        assertNull(params.getClientAppCode());
        assertNull(params.getSource());
    }

    @Test
    public void testUnmarshallPullBinaryMessageParams() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageParams("methodOne", null, null), baos);
        byte[] marshalled = baos.toByteArray();
        PullBinaryMessageParams params = getBinaryMessageStreamer().unmarshal(PullBinaryMessageParams.class,
                new ByteArrayInputStream(marshalled));
        assertNotNull(params);
        assertEquals("methodOne", params.getMethodCode());
        assertNull(params.getClientAppCode());
        assertNull(params.getSource());

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageParams("methodOne", "appTwo", null), baos);
        marshalled = baos.toByteArray();
        params = getBinaryMessageStreamer().unmarshal(PullBinaryMessageParams.class,
                new ByteArrayInputStream(marshalled));
        assertNotNull(params);
        assertEquals("methodOne", params.getMethodCode());
        assertEquals("appTwo", params.getClientAppCode());
        assertNull(params.getSource());

        baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageParams("methodOne", "appTwo", "source"), baos);
        marshalled = baos.toByteArray();
        params = getBinaryMessageStreamer().unmarshal(PullBinaryMessageParams.class,
                new ByteArrayInputStream(marshalled));
        assertNotNull(params);
        assertEquals("methodOne", params.getMethodCode());
        assertEquals("appTwo", params.getClientAppCode());
        assertEquals("source", params.getSource());
    }

    @Test(expected = NullPointerException.class)
    public void testMarshallBlankPullBinaryMessageResult() throws Exception {
        getBinaryMessageStreamer().marshal(new PullBinaryMessageResult(), new ByteArrayOutputStream());
    }

    @Test
    public void testMarshallPullBinaryMessageResultBlankBinary() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageResult("methodName", null, null,
                new TaggedBinaryMessage("tag", null, null, "consumer", null)), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testMarshallPullBinaryMessageResult() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] message = new byte[] { 0x12, 0x02, 0x55, 0x00, 0x43 };
        getBinaryMessageStreamer().marshal(new PullBinaryMessageResult("methodName", "error1", "Error!",
                new TaggedBinaryMessage("tag", "BN001", "DEPT003", "consumer", message)), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testUnmarshallPullBinaryMessageResultBlankBinary() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getBinaryMessageStreamer().marshal(new PullBinaryMessageResult("methodName", null, null,
                new TaggedBinaryMessage("tag", "BN001", "DEPT003", "consumer", null)), baos);
        PullBinaryMessageResult tbmp = getBinaryMessageStreamer().unmarshal(PullBinaryMessageResult.class,
                new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(tbmp);
        assertNull(tbmp.getErrorCode());
        assertNull(tbmp.getErrorMsg());
        assertEquals("methodName", tbmp.getMethodCode());

        TaggedBinaryMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("tag", tbm.getTag());
        assertEquals("BN001", tbm.getBranchCode());
        assertEquals("DEPT003", tbm.getDepartmentCode());
        assertEquals("consumer", tbm.getConsumer());
        assertNull(tbm.getMessage());
    }

    @Test
    public void testUnmarshallPullBinaryMessageResult() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] message = new byte[] { 0x12, 0x02, 0x55, 0x00, 0x43 };
        getBinaryMessageStreamer().marshal(new PullBinaryMessageResult("methodName", "error1", "Error!",
                new TaggedBinaryMessage("tag", "BN001", "DEPT003", "consumer", message)), baos);
        PullBinaryMessageResult tbmp = getBinaryMessageStreamer().unmarshal(PullBinaryMessageResult.class,
                new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(tbmp);
        assertEquals("methodName", tbmp.getMethodCode());
        assertEquals("error1", tbmp.getErrorCode());
        assertEquals("Error!", tbmp.getErrorMsg());

        TaggedBinaryMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("tag", tbm.getTag());
        assertEquals("BN001", tbm.getBranchCode());
        assertEquals("DEPT003", tbm.getDepartmentCode());
        assertEquals("consumer", tbm.getConsumer());
        byte[] extMessage = tbm.getMessage();
        assertNotNull(extMessage);
        assertTrue(Arrays.equals(message, extMessage));
    }

    protected RemoteCallBinaryMessageStreamer getBinaryMessageStreamer() throws Exception {
        return (RemoteCallBinaryMessageStreamer) getComponent("rc-binarymessagestreamer");
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
