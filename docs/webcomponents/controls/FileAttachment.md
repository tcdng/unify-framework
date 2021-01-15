A _FileAttachment_ is an input control used to manage one or more file attachments. It allows you to pick a file from the browser file selection dialogue and append it to a list of files. An appended file is an attachment and it is automatically uploaded to the server where it is cached or processed by a _FileAttachmentHandler_ component. You can view and also remove files that are already attached.

This component binds to a _FileAttachmentsInfo_ data object. The _FileAttachmentsInfo_ data object can be constructed in adhoc or fixed mode which determines how the input control behaves.

Listing 1: FileAttachment UPL

```upl
!ui-fileattachment:frmAttachmentList
    caption:$s{Attachments} binding:fileAttachments
    style:$s{width:280px;}
```

If the _FileAttachmentsInfo_ data object is constructed in adhoc mode, the _FileAttachment_ control maintains a dynamic list of attachments from zero attached item to a maximum number. The maximum number is determined by the _maxAutoItems_ property of the _FileAttachmentsInfo_ data object.

<img src="images/webcomponents/controls/fileattachment1.png" alt="FileAttachment1" align="center">

Listing 2: Page Bean ad-hoc mode 

```java
// Adhoc mode with maximum of four attachments
CustomerOnboardingPageBean pageBean = ...
pageBean.setFileAttachments(new FileAttachmentsInfo(FileAttachmentType.IMAGE, 4));
```

If the _FileAttachmentsInfo_ data object is constructed in fixed mode, the input control then works with a fixed list of file attachment definitions that as set in the data object.

<img src="images/webcomponents/controls/fileattachment2.png" alt="FileAttachment2" align="center">

Listing 3: Page Bean fixed mode 

```java
// Fixed mode
CustomerOnboardingPageBean pageBean = ...
FileAttachmentsInfo fileAttachments = new FileAttachmentsInfo(
        new FileAttachmentInfo("driversLicense", "Driver's License",
                FileAttachmentType.IMAGE),
        new FileAttachmentInfo("passport", "International Passport",
                FileAttachmentType.IMAGE),
        new FileAttachmentInfo("birthCert", "Birth Certificate",
                FileAttachmentType.PDF));
CustomerOnboardingPageBean pageBean = ...
pageBean.setFileAttachments(fileAttachments);
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| viewPath | false | String | Specifies the attachment item viewer path. |
