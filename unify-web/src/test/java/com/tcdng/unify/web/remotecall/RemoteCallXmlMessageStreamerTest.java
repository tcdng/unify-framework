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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.data.TaggedXmlMessage;

/**
 * Remote call XML message streamer test.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class RemoteCallXmlMessageStreamerTest extends AbstractUnifyComponentTest {

    @Test
    public void testMarshallBlankPushXmlMessageParams() throws Exception {
        getXmlMessageStreamer().marshal(new PushXmlMessageParams(), new ByteArrayOutputStream());
    }

    @Test
    public void testMarshallPushXmlMessageParamsBlankXml() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PushXmlMessageParams("methodName", null, null,
                new TaggedXmlMessage("tag", null, null, "consumer", null)), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testMarshallPushXmlMessageParams() throws Exception {
        StringWriter sw = new StringWriter();
        String message = "<Transaction><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction>";
        getXmlMessageStreamer().marshal(new PushXmlMessageParams("methodName", "appOne", "destination",
                new TaggedXmlMessage("tag", "BRN002", "DEPT125", "consumer", message)), sw);
        String marshalled = sw.toString();
        assertEquals(
                "<PushXmlMessage methodCode = \"methodName\" clientAppCode = \"appOne\" destination = \"destination\" tag = \"tag\" branchCode = \"BRN002\" departmentCode = \"DEPT125\" consumer = \"consumer\"><Transaction><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction></PushXmlMessage>",
                marshalled);
    }

    @Test
    public void testUnmarshallPushXmlMessageParamsBlankXml() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PushXmlMessageParams("methodName", null, null,
                new TaggedXmlMessage("tag", null, "DEPT336", "consumer", null)), baos);
        PushXmlMessageParams tbmp = getXmlMessageStreamer().unmarshal(PushXmlMessageParams.class,
                new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(tbmp);
        assertNull(tbmp.getClientAppCode());
        assertNull(tbmp.getDestination());
        assertEquals("methodName", tbmp.getMethodCode());

        TaggedXmlMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("tag", tbm.getTag());
        assertNull(tbm.getBranchCode());
        assertEquals("DEPT336", tbm.getDepartmentCode());
        assertEquals("consumer", tbm.getConsumer());
        assertNull(tbm.getMessage());
    }

    @Test
    public void testUnmarshallPushXmlMessageParams() throws Exception {
        PushXmlMessageParams tbmp = getXmlMessageStreamer().unmarshal(PushXmlMessageParams.class,
                "<PushXmlMessage methodCode = \"methodName\" destination = \"destination\" clientAppCode = \"appOne\" tag = \"tag\" branchCode = \"BRN002\" consumer = \"consumer\"><Transaction tranCode = \"02\"><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction></PushXmlMessage>");
        assertNotNull(tbmp);
        assertEquals("appOne", tbmp.getClientAppCode());
        assertEquals("destination", tbmp.getDestination());
        assertEquals("methodName", tbmp.getMethodCode());

        TaggedXmlMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("tag", tbm.getTag());
        assertEquals("BRN002", tbm.getBranchCode());
        assertNull(tbm.getDepartmentCode());
        assertEquals("consumer", tbm.getConsumer());
        String extXml = tbm.getMessage();
        assertNotNull(extXml);
        assertEquals("<Transaction tranCode = \"02\"><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction>",
                extXml);
    }

    @Test
    public void testMarshallPushXmlMessageResultBlank() throws Exception {
        StringWriter sw = new StringWriter();
        getXmlMessageStreamer().marshal(new PushXmlMessageResult(), sw);
        assertEquals("<PushXmlMessageResult></PushXmlMessageResult>", sw.toString());
    }

    @Test
    public void testMarshallPushXmlMessageResult() throws Exception {
        StringWriter sw = new StringWriter();
        getXmlMessageStreamer().marshal(new PushXmlMessageResult("methodOne", null, null), sw);
        assertEquals("<PushXmlMessageResult methodCode = \"methodOne\"></PushXmlMessageResult>", sw.toString());

        sw = new StringWriter();
        getXmlMessageStreamer().marshal(new PushXmlMessageResult("methodOne", "error2", null), sw);
        assertEquals("<PushXmlMessageResult methodCode = \"methodOne\" errorCode = \"error2\"></PushXmlMessageResult>",
                sw.toString());

        sw = new StringWriter();
        getXmlMessageStreamer().marshal(new PushXmlMessageResult("methodOne", "error2", "There was an error"), sw);
        assertEquals(
                "<PushXmlMessageResult methodCode = \"methodOne\" errorCode = \"error2\"><errorMsg>There was an error</errorMsg></PushXmlMessageResult>",
                sw.toString());
    }

    @Test
    public void testUnmarshallPushXmlMessageResultBlank() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PushXmlMessageResult(), baos);
        byte[] marshalled = baos.toByteArray();
        PushXmlMessageResult result =
                getXmlMessageStreamer().unmarshal(PushXmlMessageResult.class, new ByteArrayInputStream(marshalled));
        assertNotNull(result);
        assertNull(result.getMethodCode());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMsg());
    }

    @Test
    public void testUnmarshallPushXmlMessageResult() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PushXmlMessageResult("methodOne", null, null), baos);
        byte[] marshalled = baos.toByteArray();
        PushXmlMessageResult result =
                getXmlMessageStreamer().unmarshal(PushXmlMessageResult.class, new ByteArrayInputStream(marshalled));
        assertNotNull(result);
        assertEquals("methodOne", result.getMethodCode());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMsg());

        baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PushXmlMessageResult("methodOne", "error2", null), baos);
        marshalled = baos.toByteArray();
        result = getXmlMessageStreamer().unmarshal(PushXmlMessageResult.class, new ByteArrayInputStream(marshalled));
        assertNotNull(result);
        assertEquals("methodOne", result.getMethodCode());
        assertEquals("error2", result.getErrorCode());
        assertNull(result.getErrorMsg());

        baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PushXmlMessageResult("methodOne", "error2", "There was an error"), baos);
        marshalled = baos.toByteArray();
        result = getXmlMessageStreamer().unmarshal(PushXmlMessageResult.class, new ByteArrayInputStream(marshalled));
        assertNotNull(result);
        assertEquals("methodOne", result.getMethodCode());
        assertEquals("error2", result.getErrorCode());
        assertEquals("There was an error", result.getErrorMsg());
    }

    @Test
    public void testMarshallPullXmlMessageParamsBlank() throws Exception {
        StringWriter sw = new StringWriter();
        getXmlMessageStreamer().marshal(new PullXmlMessageParams(), sw);
        assertEquals("<PullXmlMessage></PullXmlMessage>", sw.toString());
    }

    @Test
    public void testMarshallPullXmlMessageParams() throws Exception {
        StringWriter sw = new StringWriter();
        getXmlMessageStreamer().marshal(new PullXmlMessageParams("methodOne", null, null), sw);
        assertEquals("<PullXmlMessage methodCode = \"methodOne\"></PullXmlMessage>", sw.toString());

        sw = new StringWriter();
        getXmlMessageStreamer().marshal(new PullXmlMessageParams("methodOne", "appThree", null), sw);
        assertEquals("<PullXmlMessage methodCode = \"methodOne\" clientAppCode = \"appThree\"></PullXmlMessage>",
                sw.toString());

        sw = new StringWriter();
        getXmlMessageStreamer().marshal(new PullXmlMessageParams("methodOne", "appThree", "source"), sw);
        assertEquals(
                "<PullXmlMessage methodCode = \"methodOne\" clientAppCode = \"appThree\" source = \"source\"></PullXmlMessage>",
                sw.toString());
    }

    @Test
    public void testUnmarshallPullXmlMessageParamsBlank() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PullXmlMessageParams(), baos);
        byte[] marshalled = baos.toByteArray();
        PullXmlMessageParams params =
                getXmlMessageStreamer().unmarshal(PullXmlMessageParams.class, new ByteArrayInputStream(marshalled));
        assertNotNull(params);
        assertNull(params.getMethodCode());
        assertNull(params.getClientAppCode());
        assertNull(params.getSource());
    }

    @Test
    public void testUnmarshallPullXmlMessageParams() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PullXmlMessageParams("methodOne", null, null), baos);
        byte[] marshalled = baos.toByteArray();
        PullXmlMessageParams params =
                getXmlMessageStreamer().unmarshal(PullXmlMessageParams.class, new ByteArrayInputStream(marshalled));
        assertNotNull(params);
        assertEquals("methodOne", params.getMethodCode());
        assertNull(params.getClientAppCode());
        assertNull(params.getSource());

        baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PullXmlMessageParams("methodOne", "appN", null), baos);
        marshalled = baos.toByteArray();
        params = getXmlMessageStreamer().unmarshal(PullXmlMessageParams.class, new ByteArrayInputStream(marshalled));
        assertNotNull(params);
        assertEquals("methodOne", params.getMethodCode());
        assertEquals("appN", params.getClientAppCode());
        assertNull(params.getSource());

        baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PullXmlMessageParams("methodOne", "appN", "sourceA"), baos);
        marshalled = baos.toByteArray();
        params = getXmlMessageStreamer().unmarshal(PullXmlMessageParams.class, new ByteArrayInputStream(marshalled));
        assertNotNull(params);
        assertEquals("methodOne", params.getMethodCode());
        assertEquals("appN", params.getClientAppCode());
        assertEquals("sourceA", params.getSource());
    }

    @Test
    public void testMarshallBlankPullXmlMessageResult() throws Exception {
        getXmlMessageStreamer().marshal(new PullXmlMessageResult(), new ByteArrayOutputStream());
    }

    @Test
    public void testMarshallPullXmlMessageResultBlankXml() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PullXmlMessageResult("methodName", null, null,
                new TaggedXmlMessage("tag", null, null, "consumer", null)), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testMarshallPullXmlMessageResult() throws Exception {
        StringWriter sw = new StringWriter();
        String message = "<Transaction><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction>";
        getXmlMessageStreamer().marshal(new PullXmlMessageResult("methodName", "error20", "Some ErrorPart",
                new TaggedXmlMessage("tag", "BRN002", "DEPT125", "consumer", message)), sw);
        String marshalled = sw.toString();
        assertEquals(
                "<PullXmlMessageResult methodCode = \"methodName\" errorCode = \"error20\" tag = \"tag\" branchCode = \"BRN002\" departmentCode = \"DEPT125\" consumer = \"consumer\"><errorMsg>Some ErrorPart</errorMsg><Transaction><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction></PullXmlMessageResult>",
                marshalled);
    }

    @Test
    public void testUnmarshallPullXmlMessageResultBlankXml() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getXmlMessageStreamer().marshal(new PullXmlMessageResult("methodName", null, null,
                new TaggedXmlMessage("tag", "BR-0001", "DEPR2D2", "consumer", null)), baos);
        PullXmlMessageResult tbmp = getXmlMessageStreamer().unmarshal(PullXmlMessageResult.class,
                new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(tbmp);
        assertEquals("methodName", tbmp.getMethodCode());
        assertNull(tbmp.getErrorCode());
        assertNull(tbmp.getErrorMsg());

        TaggedXmlMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("tag", tbm.getTag());
        assertEquals("BR-0001", tbm.getBranchCode());
        assertEquals("DEPR2D2", tbm.getDepartmentCode());
        assertEquals("consumer", tbm.getConsumer());
        assertNull(tbm.getMessage());
    }

    @Test
    public void testUnmarshallPullXmlMessageResult() throws Exception {
        PullXmlMessageResult tbmp = getXmlMessageStreamer().unmarshal(PullXmlMessageResult.class,
                "<PullXmlMessageResult methodCode = \"methodName\" errorCode = \"error10\" tag = \"tag\" consumer = \"consumer\"><errorMsg>ErrorPart Message!</errorMsg><Transaction tranCode = \"02\"><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction></PullXmlMessageResult>");
        assertNotNull(tbmp);
        assertEquals("methodName", tbmp.getMethodCode());
        assertEquals("error10", tbmp.getErrorCode());
        assertEquals("ErrorPart Message!", tbmp.getErrorMsg());

        TaggedXmlMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("consumer", tbm.getConsumer());
        assertEquals("tag", tbm.getTag());
        String extXml = tbm.getMessage();
        assertNotNull(extXml);
        assertEquals("<Transaction tranCode = \"02\"><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction>",
                extXml);
    }

    protected RemoteCallXmlMessageStreamer getXmlMessageStreamer() throws Exception {
        return (RemoteCallXmlMessageStreamer) getComponent("rc-xmlmessagestreamer");
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
