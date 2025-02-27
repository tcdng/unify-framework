/*
 * Copyright 2018-2024 The Code Department.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.constant.NetworkSecurityType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.security.Authentication;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Convenient base class for email server (based on java mail).
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractEmailServer extends AbstractUnifyComponent implements EmailServer {

	private FactoryMap<String, InternalConfig> configurations;

	public AbstractEmailServer() {
		configurations = new FactoryMap<String, InternalConfig>() {

			@Override
			protected InternalConfig create(String configName, Object... params) throws Exception {
				EmailServerConfig emailServerConfig = (EmailServerConfig) params[0];
				Properties properties = new Properties(System.getProperties());
				Authenticator authenticator = null;
				properties.put("mail.smtp.host", emailServerConfig.getHostAddress());
				if (emailServerConfig.getHostPort() != null) {
					properties.put("mail.smtp.port", String.valueOf(emailServerConfig.getHostPort()));
				}

				if (StringUtils.isNotBlank(emailServerConfig.getUsername())) {
					authenticator = new SessionAuthenticator(emailServerConfig.getUsername(),
							emailServerConfig.getPassword());
				}

				if (authenticator != null || StringUtils.isNotBlank(emailServerConfig.getAuthentication())) {
					if (NetworkSecurityType.SSL.equals(emailServerConfig.getSecurityType())) {
						if (emailServerConfig.getHostPort() != null) {
							properties.put("mail.smtp.socketFactory.port",
									String.valueOf(emailServerConfig.getHostPort()));
						}

						properties.put("mail.smtp.ssl.enable", "true");
						properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
					} else if (NetworkSecurityType.TLS.equals(emailServerConfig.getSecurityType())) {
						properties.put("mail.smtp.starttls.enable", "true");
						properties.put("mail.smtp.ssl.protocols", "TLSv1 TLSv1.1 TLSv1.2");
					}

					properties.put("mail.smtp.auth", "true");
				}

				return new InternalConfig(emailServerConfig, properties, authenticator);
			}

		};
	}

	@Override
	public void configure(String configName, EmailServerConfig emailServerConfig) throws UnifyException {
		configurations.remove(configName);
		logDebug("Configuring email server with [{0}]...", emailServerConfig);
		configurations.get(configName, emailServerConfig);
	}

	@Override
	public boolean isConfigured(String configName) throws UnifyException {
		return configurations.isKey(configName);
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	protected void checkConfiguration(String configName) throws UnifyException {
		if (!configurations.isKey(configName)) {
			throw new UnifyException(UnifyCoreErrorConstants.EMAILSERVER_CONFIGURATION_UNKNOWN, configName);
		}
	}

	protected MimeMessage createMimeMessage(String configName, Email email) throws UnifyException {
		return createMimeMessage(getSession(configName), email);
	}

	protected MimeMessage createMimeMessage(Session session, Email email) throws UnifyException {
		MimeMessage mimeMessage = null;
		try {
			mimeMessage = new MimeMessage(session);
			mimeMessage.setFrom(new InternetAddress(email.getSender()));
			List<EmailRecipient> recipents = email.getRecipients();
			if (recipents.isEmpty()) {
				throw new UnifyException(UnifyCoreErrorConstants.EMAIL_RECIPIENTS_REQUIRED);
			}

			List<InternetAddress> recipientAddresses = null;
			List<InternetAddress> ccRecipientAddresses = null;
			List<InternetAddress> bccRecipientAddresses = null;
			for (EmailRecipient emailRecipient : recipents) {
				switch (emailRecipient.getType()) {
				case BCC:
					if (bccRecipientAddresses == null) {
						bccRecipientAddresses = new ArrayList<InternetAddress>();
					}
					bccRecipientAddresses.add(new InternetAddress(emailRecipient.getAddress()));
					break;
				case CC:
					if (ccRecipientAddresses == null) {
						ccRecipientAddresses = new ArrayList<InternetAddress>();
					}
					ccRecipientAddresses.add(new InternetAddress(emailRecipient.getAddress()));
					break;
				case TO:
					if (recipientAddresses == null) {
						recipientAddresses = new ArrayList<InternetAddress>();
					}
					recipientAddresses.add(new InternetAddress(emailRecipient.getAddress()));
					break;
				default:
					break;

				}
			}

			if (recipientAddresses == null) {
				throw new UnifyException(UnifyCoreErrorConstants.EMAIL_RECIPIENTS_REQUIRED);
			}
			mimeMessage.addRecipients(Message.RecipientType.TO,
					recipientAddresses.toArray(new InternetAddress[recipientAddresses.size()]));

			if (ccRecipientAddresses != null) {
				mimeMessage.addRecipients(Message.RecipientType.CC,
						ccRecipientAddresses.toArray(new InternetAddress[ccRecipientAddresses.size()]));
			}

			if (bccRecipientAddresses != null) {
				mimeMessage.addRecipients(Message.RecipientType.BCC,
						bccRecipientAddresses.toArray(new InternetAddress[bccRecipientAddresses.size()]));
			}

			mimeMessage.setSubject(email.getSubject());
			List<EmailAttachment> attachments = email.getAttachments();
			if (attachments.isEmpty()) {
				if (email.isHtmlMessage()) {
					mimeMessage.setContent(email.getMessage(), "text/html");
				} else {
					mimeMessage.setText(email.getMessage());
				}
			} else {
				Multipart multipart = new MimeMultipart();

				// Create the message content part
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				if (email.isHtmlMessage()) {
					messageBodyPart.setContent(email.getMessage(), "text/html");
				} else {
					messageBodyPart.setText(email.getMessage());
				}
				multipart.addBodyPart(messageBodyPart);

				// Add attachments if any
				for (EmailAttachment emailAttachment : attachments) {
					MimeType mimeType = FileAttachmentType.WILDCARD.mimeType();
					FileAttachmentType type = emailAttachment.getType();
					if (type != null) {
						mimeType = type.mimeType();
					}

					final DataHandler dataHandler = getDataHandler(emailAttachment, mimeType);
					messageBodyPart = new MimeBodyPart();
					messageBodyPart.setDataHandler(dataHandler);
					messageBodyPart.setHeader("Content-Type", mimeType.template());
					if (emailAttachment.isInline()) {
						messageBodyPart.setHeader("Content-ID", "<" + emailAttachment.getName() + ">");
					}

					messageBodyPart.setFileName(emailAttachment.getName());
					messageBodyPart
							.setDisposition(emailAttachment.isInline() ? MimeBodyPart.INLINE : MimeBodyPart.ATTACHMENT);
					multipart.addBodyPart(messageBodyPart);
				}

				// Add parts to message
				mimeMessage.setContent(multipart);
			}

			mimeMessage.saveChanges();
		} catch (UnifyException e) {
			throw e;
		} catch (Exception e) {
			throwOperationErrorException(e);
		}

		return mimeMessage;
	}

	protected Session getSession(String configName) throws UnifyException {
		checkConfiguration(configName);

		InternalConfig internalConfig = configurations.get(configName);
		Authenticator authenthicator = internalConfig.getAuthenticator();
		if (authenthicator == null && StringUtils.isNotBlank(internalConfig.getOrigConfig().getAuthentication())) {
			Authentication passwordAuthentication = (Authentication) getComponent(
					internalConfig.getOrigConfig().getAuthentication());
			authenthicator = new SessionAuthenticator(passwordAuthentication.getUsername(),
					passwordAuthentication.getPassword());
		}

		if (authenthicator != null) {
			return Session.getInstance(internalConfig.getProperties(), authenthicator);
		}

		return Session.getInstance(internalConfig.getProperties());
	}

	private DataHandler getDataHandler(EmailAttachment emailAttachment, MimeType mimeType) throws UnifyException {
		if (emailAttachment.getBlob() != null) {
			return new DataHandler(new ByteArrayDataSource(emailAttachment.getBlob(), mimeType.template()));
		} else if (emailAttachment.getFile() != null) {
			return new DataHandler(new FileDataSource(emailAttachment.getFile()));
		} else if (emailAttachment.getProvider() != null) {
			byte[] data = ((NotificationAttachmentProvider) getComponent(emailAttachment.getProvider()))
					.provide(emailAttachment.getSourceId());
			return new DataHandler(new ByteArrayDataSource(data, mimeType.template()));
		}

		throwOperationErrorException(new IllegalArgumentException("Email attachment has no resolvale source."));
		return null;
	}

	private class SessionAuthenticator extends Authenticator {

		private String userName;

		private String password;

		public SessionAuthenticator(String userName, String password) {
			this.userName = userName;
			this.password = password;
		}

		@Override
		protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
			return new javax.mail.PasswordAuthentication(userName, password);
		}
	}

	private class InternalConfig {

		private EmailServerConfig origConfig;

		private Properties properties;

		private Authenticator authenticator;

		public InternalConfig(EmailServerConfig origConfig, Properties properties, Authenticator authenticator) {
			this.origConfig = origConfig;
			this.properties = properties;
			this.authenticator = authenticator;
		}

		public EmailServerConfig getOrigConfig() {
			return origConfig;
		}

		public Properties getProperties() {
			return properties;
		}

		public Authenticator getAuthenticator() {
			return authenticator;
		}

	}
}
