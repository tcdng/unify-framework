A _ResourceController_ services client requests for static system or dynamically generated resources. The requests originate from widget instances that are embedded in a _Page_ or _Document_ in the user’s web browser. Resources requested for could be in image, portable document, video and audio format or any other format supported by the client. When a _ResourceController_ receives a request, it identifies the target resource and streams it back to the requesting client using the OutputStream object passed to its _execute()_ method. 

Static resources, like pictures, Javascript files and cascaded style sheet files, which typically do not change are streamed from the server’s file system, database or application jar files. The framework provides a set of concrete _ResourceController_ classes like _FileResourceController_, _RealPathResourceController_, _DownloadPathResourceController_ and _ClassLoaderResourceController_, for fetching file resources and also the classes like _ScopeResourceController_ and _FileAttachmentResourceController_ that stream data from memory from different application scopes.

To improve on the response time of retrieving a resource, some of the file resource controller implementations can be configured to cache frequently accessed static resources in-memory for some specified period of time.

Generally, resource controllers are non-singletons; that is a new instance is created for every client request.

## Implementing  a ResourceController

Every resource controller must implement the _ResourceController_ component interface. The interface has methods that are manipulated by the framework at runtime to service requests for resources. 
* The _setContentType()_ and _getContentType()_ methods are used for specifying the expected and the returned MIME string constant for the resource type respectively.
* The _setResourceName()_ is used by the request processing mechanism to set the name of the resource, the target resource, that the requesting client is expecting.
* The _getMetaDataKeys()_ returns a set of string keys for meta-data that is associated with retrieved resource. The response mechanism uses these keys to get the actual meta-data values by iteratively calling the controller’s _getMetaData()_ method with the keys supplied as parameters.
* The _prepareExecution()_ method is called just before the primary _execute()_ method of the controller. Here the controller is expected analyze the target resource, pull its meta-data and prepare it for streaming.
* The _execute()_ method extracts the target resource and streams it to the _OutputStream_ object that is supplied to it.

You do not necessarily have to implement all of these methods. The framework provides a  convenient _AbstractResourceController_ class which your implementation class can extend. The abstract class implements all _ResourceController_ interface methods except for the _prepareExecution()_ and _execute()_ methods. It also provides utility methods for obtaining the target resource name and preparing the resource meta-data.


Listing 1: Simple File Resource Controller

```java
@Component(name = "/resource/simplefile")
public class SimpleFileResourceController extends AbstractResourceController {

    private File file;

    public SimpleFileResourceController() {
        super(true);// Secure: client must be logged in
    }

    @Override
    public void prepareExecution() throws UnifyException {
        // Do preparation here.
        // Get File object and prepare meta-data
        setContentDisposition(getResourceName());

        file = new File(getResourceName());
        if (file.exists()) {
            setContentLength(file.length());
        }

        setContentType("application/octet-stream");
    }

    @Override
    public void execute(OutputStream outputStream) throws UnifyException {
        // Extract and atream resource
        if (file.exists()) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                IOUtils.writeAll(outputStream, inputStream);
            } catch (FileNotFoundException e) {
                throwOperationErrorException(e);
            } finally {
                IOUtils.close(inputStream);
            }
        }
    }
}
```
