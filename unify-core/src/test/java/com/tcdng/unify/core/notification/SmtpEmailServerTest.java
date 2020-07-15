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
@Ignore // Comment Ignore to run
public class SmtpEmailServerTest extends AbstractUnifyComponentTest {

    private static final String ACCOUNT_PASSWORD = "xxxxxxx";

    @Test
    public void testConfigureEmailServer() throws Exception {
        EmailServer emailServer = (EmailServer) getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
        emailServer.configure("abc", new EmailServerConfig("mail.tcdng.com", 465, NetworkSecurityType.SSL,
                "info@tcdng.com", ACCOUNT_PASSWORD));
        assertTrue(emailServer.isConfigured("abc"));
        assertFalse(emailServer.isConfigured("xyz"));
    }

    @Test
    public void testSendEmailSSLPort465() throws Exception {
        EmailServer emailServer = (EmailServer) getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
        Email email = Email.newBuilder().withSubject("Weekly Summary Dec 22 - Dec 28")
                .toRecipient(EmailRecipient.TYPE.TO, "lateefojulari@gmail.com").fromSender("info@tcdng.com")
                .containingMessage(
                        "Using SSL Port 465 - Opening Bal: $502.35, Credits:$1.50, Debits:$20.50,Closing Bal:$480.35")
                .build();
        assertFalse(email.isSent());

        emailServer.configure("abc", new EmailServerConfig("mail.tcdng.com", 465, NetworkSecurityType.SSL,
                "info@tcdng.com", ACCOUNT_PASSWORD));
        emailServer.sendEmail("abc", email);
        assertTrue(email.isSent());
    }

    @Test
    public void testSendEmailSSLPort25() throws Exception {
        EmailServer emailServer = (EmailServer) getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
        Email email = Email.newBuilder().withSubject("Weekly Summary Dec 22 - Dec 28")
                .toRecipient(EmailRecipient.TYPE.TO, "lateefojulari@gmail.com").fromSender("info@tcdng.com")
                .containingMessage(
                        "Using SSL Port 25 - Opening Bal: $502.35, Credits:$1.50, Debits:$20.50,Closing Bal:$480.35")
                .build();
        assertFalse(email.isSent());

        emailServer.configure("abc",
                new EmailServerConfig("tcdng.com", 25, NetworkSecurityType.SSL, "info@tcdng.com", ACCOUNT_PASSWORD));
        emailServer.sendEmail("abc", email);
        assertTrue(email.isSent());
    }

    @Test
    public void testSendEmailSSLPort25MultipleOneByOne() throws Exception {
        EmailServer emailServer = (EmailServer) getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
        Email email = Email.newBuilder().withSubject("Weekly Summary Dec 22 - Dec 28")
                .toRecipient(EmailRecipient.TYPE.TO, "lateefojulari@gmail.com").fromSender("info@tcdng.com")
                .containingMessage(
                        "Using SSL Port 25 - Opening Bal: $502.35, Credits:$1.50, Debits:$20.50,Closing Bal:$480.35")
                .build();

        emailServer.configure("abc",
                new EmailServerConfig("tcdng.com", 25, NetworkSecurityType.SSL, "info@tcdng.com", ACCOUNT_PASSWORD));
        for (int i = 0; i < 8; i++) {
            emailServer.sendEmail("abc", email);
        }
        assertTrue(email.isSent());
    }

    @Test
    public void testSendEmailSSLPort25MultipleMany() throws Exception {
        EmailServer emailServer = (EmailServer) getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
        Email[] email = new Email[8];
        email[0] = Email.newBuilder().withSubject("Weekly Summary Dec 22 - Dec 28")
                .toRecipient(EmailRecipient.TYPE.TO, "lateefojulari@gmail.com").fromSender("info@tcdng.com")
                .containingMessage(
                        "Using SSL Port 25 - Opening Bal: $502.35, Credits:$1.50, Debits:$20.50,Closing Bal:$480.35")
                .build();
        for (int i = 1; i < 8; i++) {
            email[i] = email[0];
        }

        emailServer.configure("abc",
                new EmailServerConfig("tcdng.com", 25, NetworkSecurityType.SSL, "info@tcdng.com", ACCOUNT_PASSWORD));
        emailServer.sendEmail("abc", email);
        assertTrue(email[0].isSent());
        assertTrue(email[1].isSent());
        assertTrue(email[2].isSent());
        assertTrue(email[3].isSent());
        assertTrue(email[4].isSent());
        assertTrue(email[5].isSent());
        assertTrue(email[6].isSent());
        assertTrue(email[7].isSent());
    }

    @Test
    public void testSendEmailTLSPort587() throws Exception {
        EmailServer emailServer = (EmailServer) getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
        Email email = Email.newBuilder().withSubject("Weekly Summary Dec 22 - Dec 28")
                .toRecipient(EmailRecipient.TYPE.TO, "lateefojulari@gmail.com").fromSender("info@tcdng.com")
                .containingMessage(
                        "Using TLS Port 587 - Opening Bal: $502.35, Credits:$1.50, Debits:$20.50,Closing Bal:$480.35")
                .build();
        assertFalse(email.isSent());

        emailServer.configure("abc",
                new EmailServerConfig("tcdng.com", 587, NetworkSecurityType.TLS, "info@tcdng.com", ACCOUNT_PASSWORD));
        emailServer.sendEmail("abc", email);
        assertTrue(email.isSent());
    }

    @Test
    public void testSendEmailPort25NoSecurity() throws Exception {
        EmailServer emailServer = (EmailServer) getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
        Email email = Email.newBuilder().withSubject("Weekly Summary Dec 22 - Dec 28")
                .toRecipient(EmailRecipient.TYPE.TO, "lateefojulari@gmail.com").fromSender("info@tcdng.com")
                .containingMessage(
                        "Using Port 25 No Security - Opening Bal: $502.35, Credits:$1.50, Debits:$20.50,Closing Bal:$480.35")
                .build();
        assertFalse(email.isSent());

        emailServer.configure("abc", new EmailServerConfig("tcdng.com", 25, null, "info@tcdng.com", ACCOUNT_PASSWORD));
        emailServer.sendEmail("abc", email);
        assertTrue(email.isSent());
    }

    @Override
    protected void onSetup() throws Exception {

    }

    @Override
    protected void onTearDown() throws Exception {

    }
}
