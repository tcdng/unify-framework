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

package com.tcdng.unify.core.message;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;

/**
 * Message resolver tests
 * 
 * @author The Code Department
 * @since 1.0
 */
public class MessageResolverTest extends AbstractUnifyComponentTest {

    @Test
    public void testResolveApplicationMessage() throws Exception {
        MessageResolver mr = getMessageResolver();
        assertEquals("Prime time today", mr.resolveApplicationMessage("Prime time {0}", "today"));
        assertEquals("Prime time yesterday", mr.resolveApplicationMessage("$s{Prime time {0}}", "yesterday"));
        assertEquals("Hello World!", mr.resolveApplicationMessage("$m{messageresolver.helloworld}"));
        assertEquals("The sky is blue", mr.resolveApplicationMessage("$m{messageresolver.theskyis}", "blue"));
    }

    @Test
    public void testResolveSessionMessage() throws Exception {
        MessageResolver mr = getMessageResolver();
        assertEquals("Prime time today", mr.resolveSessionMessage("Prime time {0}", "today"));
        assertEquals("Prime time yesterday", mr.resolveSessionMessage("$s{Prime time {0}}", "yesterday"));
        assertEquals("Hello World!", mr.resolveSessionMessage("$m{messageresolver.helloworld}"));
        assertEquals("The sky is blue", mr.resolveSessionMessage("$m{messageresolver.theskyis}", "blue"));
    }

    @Test
    public void testResolveMessage() throws Exception {
        MessageResolver mr = getMessageResolver();
        assertEquals("Prime time today", mr.resolveMessage(Locale.getDefault(), "Prime time {0}", "today"));
        assertEquals("Prime time yesterday", mr.resolveMessage(Locale.getDefault(), "$s{Prime time {0}}", "yesterday"));
        assertEquals("Hello World!", mr.resolveMessage(Locale.getDefault(), "$m{messageresolver.helloworld}"));
        assertEquals("The sky is blue", mr.resolveMessage(Locale.getDefault(), "$m{messageresolver.theskyis}", "blue"));
    }

    @Test
    public void testResolveUnifyPropertyMessages() throws Exception {
        MessageResolver mr = getMessageResolver();
        assertEquals("Currency", mr.resolveMessage(Locale.getDefault(), "$u{chart.label.a}"));
        assertEquals("Currency NGN", mr.resolveMessage(Locale.getDefault(), "$u{chart.label.b}", "NGN"));
        assertEquals("Currency XAF on 2022-12-08", mr.resolveMessage(Locale.getDefault(), "$u{chart.label.c}", "XAF", "2022-12-08"));
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }

    @Override
	protected void doAddSettingsAndDependencies() throws Exception {
        addContainerSetting("chart.label.a", "Currency");
        addContainerSetting("chart.label.b", "Currency {0}");
        addContainerSetting("chart.label.c", "Currency {0} on {1}");
	}

    private MessageResolver getMessageResolver() throws Exception {
        return (MessageResolver) getComponent(ApplicationComponents.APPLICATION_MESSAGE_RESOLVER);
    }
}
