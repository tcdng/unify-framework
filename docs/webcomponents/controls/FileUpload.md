A _FileUpload_ control allows a user to select one or more files from the client device to be uploaded to the server.  When the user clicks on the browse button element of the control, the browser opens a file selection dialog. The user selects one or more files and on closure of the selection dialog, the text area element of the control is populated with the names of selected files. Selected files are not automatically uploaded.

This control always binds to an _UploadFile[]_ page bean property which will hold contents and details of uploaded files pushed from the browser to the server.

In normal mode, selected files can be uploaded to the server by clicking on the 'Upload' button element of the control.

<img src="images/webcomponents/controls/fileupload1.png" alt="FileUpload1" align="center">

Listing 1: FileUpload in normal mode UPL

```upl
!ui-fileupload:frmPhotos
    style:$s{width:400px;} binding:photos
    accept:WORD multiple:true
    uploadPath:$n{/uploadPhotos}
```

If the control’s _selectOnly_ attribute is set to false the control operates in a different mode where the Upload button element is not visible. Here files are uploaded by some other page action that references the file upload control in a post or submit action.

<img src="images/webcomponents/controls/fileupload2.png" alt="FileUpload2" align="center">

Listing 2: FileUpload in select-only mode UPL

```upl
!ui-fileupload:frmPhotos
    style:$s{width:300px;} binding:photos
    accept:IMAGES multiple:true selectOnly:true

!ui-button:submitBtn caption:$s{Submit}
    eventHandler:$d{!ui-event event:onclick action:$c{submitAct}}
!ui-post:submitAct path:$n{/submitForm} components:$c{frmPhotos ...}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| accept | false | String | Specifies the file types this control should accept. Value can be set to any string in the set: {AUDIO,CSV,EXCEL,IMAGE,PDF, TEXT,VIDEO,WILDCARD,WORD}. Defaults to WILDCARD. |
| maxSize | false | Integer | The maximum file size, in kilobytes (KB), accepted by this control. |
| maxSizeBinding | false | String | Specifies the bean property or container scope attribute whose value is to be used as maximum accepted file size in kilobytes (KB). |
| multiple | false | Boolean | Indicates if control allows user to select multiple files. Defaults to 'false'. |
| selectOnly | false | Boolean | Indicates the control’s mode. If set to true, the control Upload button is hidden and the control operates in select-only mode. Defaults to 'false' |
| browseCaption | false | String | Specifies the caption of the browse button. Defaults to $m{button.browse}. |
| uploadCaption | false | String | Specifies the caption of the upload button. Defaults to $m{button.upload}. |
| uploadPath | false | String | Specifies the path of the _PageController_ action that handles upload requests from this component. |

