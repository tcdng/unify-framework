The _Controller_ component is a web component that handles requests made by a web application client and produces appropriate responses on completion. Requests may be for fetching the result of executing a business logic or for retrieving a system resource. Controllers form the bridge between web layer and both business module and system resource layers. All controllers must implement _com.tcdng.unify.web.Controller_ component interface.
 
In the _Controller_ interface, there is an _isSecured()_ method that is used by the framework to determine if access to the _Controller_ is restricted to users that are already authenticated and logged in. Attempted access to a secured _Controller_ by a user in an unauthorized session throws a system exception.

There are four types of unify component Controller interfaces:
* _PageController_ which is used for handling document and page user action requests that come from a page on a browser. Document and page action requests usually trigger some business logic and generally return HTML or JSON strings to the browser. Page controllers are singletons.
* _ResourceController_ which is used for handling resource requests that come from a page on a browser. Resource requests generally initiate the return of a byte stream representing a system resource like images, files and generated reports. Request controllers are non-singletons that last only for a request cycle.
* _RemoteCallController_ which is used for handling inter-system remote calls. Remote-call controllers are singletons.
* _PlainController_ which allows for low level handling of HTTP request and response objects. Plain controllers are singletons.

Typically, an application has multiple controllers for handling the myriad of different types of requests it receives. The framework provides a dedicated component, the _ControllerManager_, that manages all controllers and handles the translation and routing of a request to a specific controller for processing. 

