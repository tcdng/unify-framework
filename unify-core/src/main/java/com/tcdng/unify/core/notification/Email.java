/*
 * Copyright 2018-2025 The Code Department.
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.constant.FileAttachmentType;
import com.tcdng.unify.core.data.FileAttachment;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ErrorUtils;
import com.tcdng.unify.core.util.StringUtils;

/**
 * An email data object.
 * 
 * @author The Code Department
 * @since 4.1
 */
public class Email {

	private Object id;

	private String subject;

	private String message;

	private String senderContact;

	private String senderName;

	private String error;

	private List<EmailRecipient> recipients;

	private List<EmailAttachment> attachments;

	private boolean htmlMessage;

	private boolean sent;

	public Email(String error) {
		this.error = error;
	}

	private Email(String subject, String message, String senderContact, String senderName, List<EmailRecipient> recipients,
			List<EmailAttachment> attachments, Object id, boolean htmlMessage) {
		this.subject = subject;
		this.message = message;
		this.senderContact = senderContact;
		this.senderName = senderName;
		this.recipients = recipients;
		this.attachments = attachments;
		this.id = id;
		this.htmlMessage = htmlMessage;
	}

	public String getSenderContact() {
		return senderContact;
	}

	public String getSenderName() {
		return senderName;
	}

	public boolean isWithSenderName() {
		return senderName != null;
	}
	
	public List<EmailRecipient> getRecipients() {
		return recipients;
	}

	public List<EmailAttachment> getAttachments() {
		return attachments;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public Object getId() {
		return id;
	}

	public boolean isHtmlMessage() {
		return htmlMessage;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isWithError() {
		return error != null;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private String subject;

		private String message;

		private String senderAddress;

		private String senderName;

		private List<EmailRecipient> recipients;

		private List<EmailAttachment> attachments;

		private Object id;

		private boolean htmlMessage;

		private Set<String> contacts;

		private Builder() {
			this.recipients = new ArrayList<EmailRecipient>();
			this.contacts = new HashSet<String>();
		}

		public Builder toRecipient(EmailRecipient.TYPE type, String recipientAddress) {
			addRecipient(type, recipientAddress);
			return this;
		}

		public Builder toRecipients(EmailRecipient.TYPE type, Collection<String> recipientAddresses) {
			for (String recipientAddress : recipientAddresses) {
				addRecipient(type, recipientAddress);
			}
			return this;
		}

		public Builder fromSender(String senderAddress) {
			this.senderAddress = senderAddress;
			return this;
		}

		public Builder fromSender(String senderAddress, String senderName) {
			this.senderAddress = senderAddress;
			this.senderName = senderName;
			return this;
		}

		public Builder containingMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder withId(Object id) {
			this.id = id;
			return this;
		}

		public Builder withSubject(String subject) {
			this.subject = subject;
			return this;
		}

		public Builder withAttachment(String name, File file, FileAttachmentType type) {
			getAttachments().add(new EmailAttachment(type, name, file));
			return this;
		}

		public Builder withAttachment(String name, File file, FileAttachmentType type, boolean inline) {
			getAttachments().add(new EmailAttachment(type, name, file, inline));
			return this;
		}

		public Builder withAttachment(String name, byte[] blob, FileAttachmentType type) {
			getAttachments().add(new EmailAttachment(type, name, blob));
			return this;
		}

		public Builder withAttachment(String name, byte[] blob, FileAttachmentType type, boolean inline) {
			getAttachments().add(new EmailAttachment(type, name, blob, inline));
			return this;
		}

		public Builder withAttachment(String name, String provider, String sourceId, FileAttachmentType type) {
			getAttachments().add(new EmailAttachment(type, name, provider, sourceId));
			return this;
		}

		public Builder withAttachment(String name, String provider, String sourceId, FileAttachmentType type,
				boolean inline) {
			getAttachments().add(new EmailAttachment(type, name, provider, sourceId, inline));
			return this;
		}

		public Builder withAttachment(FileAttachment fileAttachment) {
			getAttachments().add(new EmailAttachment(fileAttachment.getType(), fileAttachment.getFileName(),
					fileAttachment.getData()));
			return this;
		}

		public Builder withAttachments(Collection<FileAttachment> fileAttachments) {
			for (FileAttachment fileAttachment : fileAttachments) {
				getAttachments().add(new EmailAttachment(fileAttachment.getType(), fileAttachment.getFileName(),
						fileAttachment.getData()));
			}
			return this;
		}

		public Builder asHTML(boolean htmlMessage) {
			this.htmlMessage = htmlMessage;
			return this;
		}

		private void addRecipient(EmailRecipient.TYPE type, String recipientAddress) {
			if (contacts.add(recipientAddress)) {
				recipients.add(new EmailRecipient(type, recipientAddress));
			}
		}

		public Email build() throws UnifyException {
			if (StringUtils.isBlank(subject)) {
				ErrorUtils.throwBuildError("Email subject is required");
			}

			if (StringUtils.isBlank(message)) {
				ErrorUtils.throwBuildError("Email message is required");
			}

			if (StringUtils.isBlank(senderAddress)) {
				ErrorUtils.throwBuildError("Email sender address is required");
			}

			if (recipients.isEmpty()) {
				ErrorUtils.throwBuildError("Email requires at least one recipient");
			}
			return new Email(subject, message, senderAddress, senderName, DataUtils.unmodifiableList(recipients),
					DataUtils.unmodifiableList(attachments), id, htmlMessage);
		}

		private List<EmailAttachment> getAttachments() {
			if (attachments == null) {
				attachments = new ArrayList<EmailAttachment>();
			}

			return attachments;
		}
	}
}
