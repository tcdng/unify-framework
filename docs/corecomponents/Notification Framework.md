## Overview

Enterprise applications are built to satisfy the needs of an organization particularly in its operations where it uses software to produce and provide goods and services to its clients. These applications are built on software systems that model business processes that constitute the organization’s operations. In most cases a business processes needs to provide different types of information to personnel and clients, notifying them of activities or events that occur at  each of its different stages. Notification is usually in the form of messages that appear directly to a user working on the application itself or as messages sent through an external communications system.

A banking application, for instance, that is required to automatically generate and send a weekly transaction summary of an account to a customer, generate and send a message containing password details after a user password reset, generate and send a notifications to a system administrator when a critical system error occurs, amongst many other similar requirements, needs a mechanism to facilitate those requirements.

Currently, the framework provides components for email notification and interface definitions only for SMS notification

## Email

The framework provides an _EmailServer_ component with interface methods for the configuration of remote email host servers and sending of email to a specific configured remote server. There is also an _Email_ data object that encapsulates the properties of an email message.

<img src="images/corecomponents/emailcomponents.png" alt="Email Components" width="561" align="center">

Figure 1.0 Email components

Sending email requires the following steps:
1.  Configure a remote email host on an _EmailServer_ component instance. This should be done once for a specific configuration; typically on first use or on application startup. Re-configuration can also be done if the remote email host’s settings change during the _EmailServer_ component lifetime.
2.  Compose one or more _Email_ objects.
3.  Send composed _Email_ objects using any of the _EmailServer_ _sendEmail()_ methods.

### Configuring an Email Server

To configure a remote email host on an _EmailServer_ component, you need to build an _EmailServerConfig_ data object with details that include:
* Host address that identifies the remote host on the network.
* Host port that specifies the port to connect to on the remote host.
* Network security type which can be one of the enumerated types in _NetworkSecurityType_.
* Optional username and password if login is required at the remote host.
* Optional authentication component name if login is required at the remote host and authentication details are provided by an _Authentication_ component.

Since an _EmailServer_ supports multiple remote host configurations, you need to supply a unique name for each configuration.

Listing 1: Configuring an email server

```java
// Get server component
EmailServer emailServer = (EmailServer)
    getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
    
// Build configuration
EmailServerConfig config = EmailServerConfig.newBuilder()
    .hostAddress("smtp.tcdng.com")
    .hostPort(465)
    .useSecurityType(NetworkSecurityType.SSL)
    .username("tom")
    .password("abc123")
    .build();
        
// Configure with configuration name 'smtpServer1'
emailServer.configure("smtpServer1", config);
```

### Composing an Email

You compose an email using an instance of the _Email.Builder_ class. The builder class has methods that allow you to specify the email subject, message body, message type, one or more recipients, sender and one or more attachments. The message body of your email can be plain text or HTML format.
* Use the _withSubject()_ method to set the email subject.
* Add a recipient using the _toRecipient()_ and _toRecipients()_ methods to set email recipients. Use _EmailRecipient.TYPE.TO_ type for normal recipients, _EmailRecipient.TYPE.CC_ for a carbon copy and _EmailRecipient.TYPE.BCC_ for a blind carbon copy.
* Use the _fromSender()_ method to set the email sender address.
* Use the _containingMessage()_ method to set the email message content which can be plain text or HTML.
* Use any of the _withAttachment()_ methods to attach objects to the email.
* Indicate using the asHtml() method if email message content is in HTML format.

Listing 2: Composing an email

```java
Email email = Email.newBuilder()
    .withSubject("Monthly Summary Nov 2019")
    .fromSender("backoffice@starbank.com")
    .toRecipient(EmailRecipient.TYPE.TO, "john.doe@gmail.com")
    .containingMessage("Opening Bal: $502.35, Credits:$1.50, Debits:$20.50,
        Closing Bal:$480.35")
    .withAttachment("TransactionDetails.pdf",
        new File("TransactionDetails.pdf"), FileAttachmentType.PDF)
    .build();
```

### Sending an Email

Send email using any of the _EmailServer_ instance _sendEmail()_ methods.

Listing 3: Sending an email

```java
/// Compose email
Email email = ...

// Get email server
EmailServer emailServer = (EmailServer)
    getComponent(ApplicationComponents.APPLICATION_DEFAULTEMAILSERVER);
    
// Send email using 'smtpServer1' configuration
emailServer.sendEmail(“smtpServer1”, email);

if (email.isSent()) {
    // Email successfully sent
} else {
    // Email send failure
}
```

## Short Message Service (SMS)

SMS is a text-messaging service that enables telephone and mobile devices to exchange short text messages. Messages are limited to a maximum of 160 alpha-numeric characters.

The framework provides an _SmsServer_ component with interface methods for the configuration of SMS gateways and for sending of SMS. The _Sms_ data object represents an SMS message.

<img src="images/corecomponents/smscomponents.png" alt="SMS Components" width="559" align="center">
 
Figure 2. SMS components

Sending SMS requires the following steps:
1.  Configure an SMS gateway on an _SmsServer_ component instance. This should be done once for a specific configuration; typically on first use or on application startup. Re-configuration can also be done if the SMS gateway’s settings change during the _SmsServer_ component lifetime.
2.  Compose one or more _Sms_ objects.
3.  Send composed _Sms_ objects using any of the _SmsServer_ _sendSms()_ methods.

### Configuring an SMS Server

To configure an SMS gateway on an _SmsServer_ component, you need to build an _SmsServerConfig_ data object with details that include:
* Host address that identifies the gateway on the network.
* Host port that specifies the port to connect to on the gateway.
* Network security type which can be one of the enumerated types in _NetworkSecurityType_.
* Optional username and password if login is required at the gateway.
* Optional authentication component name if login is required at the gateway and authentication details are provided by an _Authentication_ component.

Since an _SmsServer_ supports multiple gateway configurations, you need to supply a unique name for each configuration.

Listing 4: Configuring an SMS server

```java
// Get server component
SmsServer smsServer = ...
    
// Build configuration
SmsServerConfig config = SmsServerConfig.newBuilder()
    .hostAddress("gateway.smszone.com")
    .hostPort(9500)
    .useSecurityType(NetworkSecurityType.SSL)
    .username("harry")
    .password("xyz432")
    .build();
        
// Configure with configuration name 'smsGateway1'
smsServer.configure("smsGateway1", config);
```

### Composing an SMS Message

An Sms message has a simple structure that contains the sender phone number, the reciever phone number, the message text and an optional message ID. Perform the steps below, in no particular order, to compose an _Sms_ object using the _Sms.Builder_.
* Use the _withId()_ method to set the SMS ID.
* Use the _fromSender()_ method to set the sender phone number.
* Use the _fromReceiver()_ method to set the receiver phone number.
* Use the _containingMessage()_ method to set the message text.

Listing 5: Composing an SMS

```java
Sms sms = Sms.newBuilder()
    .withId("2")
    .fromSender("7205")
    .toReciever("+234802094XXXX")
    .containingMessage("Account: 0123456789, Balance:N920,051.00")
    .build();
```

### Sending an SMS

Send SMS using any of the _SmsServer_ instance _sendSms()_ methods.

Listing 6: Sending an SMS

```java
/// Compose SMS
Sms sms = ...

// Get sms server
SmsServer smsServer = ...
    
// Send sms using 'smsGateway1' configuration
smsServer.sendSms(“smsGateway1”, sms);

if (sms.isSent()) {
    // Sms successfully sent
} else {
    // Sms send failure
}
```
