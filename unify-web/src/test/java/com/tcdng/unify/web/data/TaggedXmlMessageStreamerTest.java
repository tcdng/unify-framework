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

package com.tcdng.unify.web.data;

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
 * Tagged XML message streamer test.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class TaggedXmlMessageStreamerTest extends AbstractUnifyComponentTest {

    @Test
    public void testMarshallBlankTaggedXmlMessageParams() throws Exception {
        getTaggedXmlMessageStreamer().marshal(new TaggedXmlMessageParams(), new ByteArrayOutputStream());
    }

    @Test
    public void testMarshallTaggedXmlMessageParamsBlankBinary() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getTaggedXmlMessageStreamer()
                .marshal(new TaggedXmlMessageParams("methodName", new TaggedXmlMessage("tag", "consumer", null)), baos);
        byte[] marshalled = baos.toByteArray();
        assertTrue(marshalled.length > 0);
    }

    @Test
    public void testMarshallTaggedXmlMessageParams() throws Exception {
        StringWriter sw = new StringWriter();
        String message = "<Transaction><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction>";
        getTaggedXmlMessageStreamer().marshal(
                new TaggedXmlMessageParams("methodName", "appOne", new TaggedXmlMessage("tag", "consumer", message)),
                sw);
        String marshalled = sw.toString();
        assertEquals(
                "<TaggedXmlMessageParams methodCode = \"methodName\" clientAppCode = \"appOne\" tag = \"tag\" consumer = \"consumer\"><Transaction><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction></TaggedXmlMessageParams>",
                marshalled);
    }

    @Test
    public void testUnmarshallTaggedXmlMessageParamsBlankXml() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getTaggedXmlMessageStreamer()
                .marshal(new TaggedXmlMessageParams("methodName", new TaggedXmlMessage("tag", "consumer", null)), baos);
        TaggedXmlMessageParams tbmp = getTaggedXmlMessageStreamer().unmarshal(TaggedXmlMessageParams.class,
                new ByteArrayInputStream(baos.toByteArray()));
        assertNotNull(tbmp);
        assertNull(tbmp.getClientAppCode());
        assertEquals("methodName", tbmp.getMethodCode());

        TaggedXmlMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("consumer", tbm.getConsumer());
        assertEquals("tag", tbm.getTag());
        assertNull(tbm.getMessage());
    }

    @Test
    public void testUnmarshallTaggedXmlMessageParams() throws Exception {
        TaggedXmlMessageParams tbmp = getTaggedXmlMessageStreamer().unmarshal(TaggedXmlMessageParams.class,
                "<TaggedXmlMessageParams methodCode = \"methodName\" clientAppCode = \"appOne\" tag = \"tag\" consumer = \"consumer\"><Transaction tranCode = \"02\"><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction></TaggedXmlMessageParams>");
        assertNotNull(tbmp);
        assertEquals("appOne", tbmp.getClientAppCode());
        assertEquals("methodName", tbmp.getMethodCode());

        TaggedXmlMessage tbm = tbmp.getTaggedMessage();
        assertNotNull(tbm);
        assertEquals("consumer", tbm.getConsumer());
        assertEquals("tag", tbm.getTag());
        String extXml = tbm.getMessage();
        assertNotNull(extXml);
        assertEquals("<Transaction tranCode = \"02\"><Currency>NGN</Currency><Amount>2500.00</Amount></Transaction>",
                extXml);
    }

    protected TaggedXmlMessageStreamer getTaggedXmlMessageStreamer() throws Exception {
        return (TaggedXmlMessageStreamer) getComponent("taggedxmlmessage-streamer");
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

}
