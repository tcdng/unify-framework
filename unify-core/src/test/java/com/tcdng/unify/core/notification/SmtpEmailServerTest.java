/*
 * Copyright 2018-2020 The Code Department.
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

package com.tcdng.unify.core.notification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.tcdng.unify.core.AbstractUnifyComponentTest;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.constant.NetworkSecurityType;

/**
 * SMTP Email server tests.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class SmtpEmailServerTest extends AbstractUnifyComponentTest {

    @Test
    public void testConfigureEmailServer() throws Exception {
        EmailServer emailServer = (EmailServer) getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
        emailServer.configure("abc", getEmailServerConfig());
        assertTrue(emailServer.isConfigured("abc"));
        assertFalse(emailServer.isConfigured("xyz"));
    }

    @Ignore
    @Test
    public void testSendEmail() throws Exception {
        EmailServer emailServer = (EmailServer) getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
        Email email = Email.newBuilder().withSubject("Weekly Summary Nov 22 - Nov 28")
                .toRecipient(EmailRecipient.TYPE.TO, "lateefojulari@gmail.com").fromSender("info@tcdng.com")
                .containingMessage("Opening Bal: $502.35, Credits:$1.50, Debits:$20.50,Closing Bal:$480.35").build();
        assertFalse(email.isSent());

        emailServer.configure("abc", getEmailServerConfig());
        emailServer.sendEmail("abc", email);
        assertTrue(email.isSent());
    }
    
    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
    
    private EmailServerConfig getEmailServerConfig() {
//        return new EmailServerConfig("tcdng.com", 465, NetworkSecurityType.SSL, "info@tcdng.com",
//                "xxxxxxxx");
//        return new EmailServerConfig("mail.tcdng.com", 26, null, "info@tcdng.com",
//                "xxxxxxx");
      return new EmailServerConfig("tcdng.com", 587, NetworkSecurityType.TLS, "info@tcdng.com",
      "xxxxx");
    }
}
