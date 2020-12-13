A _FileDownload_ is a control, rendered as a button, that can be clicked to initiate the download of a file resource from the server.

<img src="images/webcomponents/controls/filedownload.png" alt="FileDownload" align="center">

Listing 1: FileDownload using static resource UPL

```upl
!ui-filedownload:frmFileDownload
    caption:$s{Download (PDF)}
    fileSrc:$s{web/downloads/Unify Framework Software Developer Guide.pdf}
```

The _FileDownload_ control can be binded to a _DownloadFile_ bean property or container scope attribute. With this approach, the _DownloadFile_ data object contains data on the file resource to be downloaded.
 
Listing 2: FileDownload using bean property UPL

```upl
!ui-filedownload:certFileDownload
    caption:$s{Download Contract}
    fileBindig:contract
```

Listing 3: Contact Page Bean 

```java
// Set download file page bean property
ContractPageBean pageBean = ...
DownloadFile contract = ...
pageBean.setContract(contract);
```

The _FileDownload_ control can also be wired to a _FileDownloadHandler_ component which handles all file download requests that come from the control. You achieve this by setting the _handler_ attribute of your file download UPL descriptor to the name of the _FileDownloadHandler_ component.

Listing 4: FileDownload using handler component UPL 

```upl
!ui-filedownload:chequeImgDownload
    caption:$s{Cheque Image} binding:chequeId
    handler:$s{cheque-downloadhandler}
```

Listing 5: Cheque Download Handler 

```java
@Component(“cheque-downloadhandler”)
public class ChequeDownloadHandler extends AbstractUnifyComponent
    implements FileDownloadHandler {

    @Override
    public DownloadFile handleFileDownload(String id) throws UnifyException {
        // Get cheque image data using supplied cheque id
        ...

        // Construct download file using image data
        DownloadFile chequeImageFile = new DownloadFile(...);
        return  chequeImageFile;
    }
}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| fileSrc | false | String | The name of the file resource to be downloaded on click of control. |
| fileBinding | false | String | Specifies the bean property or container scope attribute whose value references the _DownloadFile_ object for the control. |
| handler | false | String | The name of a FileDownloadHandler component used for handling download requests for this control.  |
| imageSrc | false | String | Specifies the image resource to be used when rendering button.  |

