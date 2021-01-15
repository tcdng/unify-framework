The _PageController_ is a web component that receives user action requests originating from a page in the web browser and returns appropriate responses. It is also linked to a session _PageBean_ that serves as the primary value object that backs all the widget instances declared in the UPL page template bound to it. Parameters sent with browser requests are used to populate the _PageBean_ properties of the page controller through binded UPL page elements. Requests are handled by a set of dedicated methods that are named to match the action part of the request URLs. Its within these methods that the _PageController_ is expected to interact with business logic components, populate response data into any of the _PageBean_ properties and then decide on which result mappings to route to.An action handling method accepts no parameters, returns a _String_ object and is annotated with the _@Action_ annotation.

<img src="images/webcomponents/controllers/pagecontroller.png" alt="PageController" align="center">

Figure 1: Page Controller Components

A web client accesses a page controller action by constructing a correct client request URL string. A combination of the page controller name and the action name (the method name) form the trailing part of the request URL. For instance if we have a request URL “http://10.200.100.10/bankingapp/retail/accounts/openAccount” the system would retrieve a _PageController_  with name “/retail/accounts” and invoke the controller method with name “openAccount”. As noted earlier, the _openAccount()_ method must be marked with the @Action annotation and must have the correct signature.

<img src="images/webcomponents/controllers/urlbreakdown.png" alt="URLBreakdown" width="560" align="center">

Figure 2: URL Breakdown

## Result Mappings

A _PageController_ is annotated with the _@ResultMappings_ annotation that defines the mapping of the strings returned by the page controller action methods to specific responses. Every _@ResultMapping_ has a unique name and one or more response declarations that determine the nature of data that is sent back to the client. Responses are represented by _PageControllerResponse_ components and they generate the actual response to be sent back to the browser. 

### ForwardResponse

Used for generating a forward to path response that instructs the web client to forward to a new location determined by specified URL. The target URL is resolved from the path or pathBinding attribute of the component.

```java
@ResultMapping(name = "forwardtoapplication",
        response = { "!forwardresponse path:$s{/application/openPage}" }
```

#### Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| path | false | String | Specifies the path to forward to. |
| pathBinding | false | String | Specifies the name of the _PageBean_ property whose value is the path to forward to. Allows dynamic determination of forward path by changing the value of the _PageBean_ property at runtime. |

### HidePopupResponse

Used for generating a hide popup response that instructs the web client to hide the current document’s popup if it is visible.

```java
@ResultMapping(name = "searchdone", response = { "!hidepopupresponse" })
```

### HintUserResponse

Used for generating a hint user response that instructs the web client to display a message as a hint to the user. The message is fetched from the request context _USER_HINT_LIST_ attribute which would have been previously set using any of the _RequestContextUtil.hintUser()_ methods . There is no need to add this response to the response element list of your _@ResultMapping_ annotation as the framework implicitly adds one. 

```java
@ResultMapping(name = “hintUser”, response = { "!hintuserresponse" })
```

### LoadContentResponse

Used for generating a load entire page content response that instructs the web client to reload the entire contents of the page associated with the _PageController_. The page is loaded within the content element of the current document. A result mapping with name of string constant _ResultMappingConstants.OPEN_ that maps to a _LoadContentResponse_ is implicitly declared for every page controller instance. This mapping is used for handling the result of the _AbstractPageController_ default implementation of the _openPage()_ action method.

```java
@ResultMapping(name = ResultMappingConstants.OPEN,
        response = { "!loadcontentresponse" })
```

### LoadDocumentResponse

Used for generating a load document response that instructs the web client to load the entire document associated with a _PageController_. The web client loads the entire document HTML, behavior scripts and the accompanying page assets like style sheets and Javascript resource files. A result mapping with name of string constant _ResultMappingConstants.INDEX_ is implicitly declared for every page controller instance. This mapping is used for the handling the result of the _AbstractPageController_ default implementation of the _index()_ action method.

```java
@ResultMapping(name = ResultMappingConstants.INDEX,
        response = { "!loaddocumentresponse" })
```

### PostResponse

Used for generating a post to path response that instructs the web client to perform a post using a specific URL. The target URL is resolved from the path or pathBinding attribute of the response component.

```java
@ResultMapping(name = "managemenuitems",
        response = { "!postresponse path:$s{/system/menuitem/openPage}" })
```

#### Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| path | false | String | Specifies the path to post to. |
| pathBinding | false | String | Specifies the name of the _PageBean_ property whose value is the path to post to. Allows dynamic determination of post path by changing the value of the _PageBean_ property at runtime. |


### RefreshPanelResponse

Used for generating a refresh panel response that instructs the web client to refresh the contents of one or more panels. The target panel list, a list of long names, is obtained from the component’s panels attribute. If the panels attribute is not set then the component fetches the target panel list from the request context _REFRESH_PANEL_LONGNAMES_ attribute set using the _RequestContextUtil.setResponseRefreshPanels()_ method.

```java
@ResultMapping(name = "refreshmain",
    response = { "!refreshpanelresponse panels:$l{mainBodyPanel}" })
```

#### Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| panels | false | String[] | Specifies a list of long names of panels to refresh. |


### ShowPopupResponse

Used for generating a show popup response that instructs the web client to show a particular panel in the document’s popup window. It has a _popup_ attribute for specifying the short name of the target popup panel. If the _popup_ attribute is not set then the component fetches the target panel from the request context _REQUEST_POPUP_NAME_ attribute set using the _RequestContextUtil.setRequestPopupName()_ method.

```java
@ResultMapping(name = "showapplicationmessage",
    response = { "!showpopupresponse popup:$s{messageBoxPopup}"})
```

#### Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| popup | false | String | Specifies the short name of the panel to display in the popup window. |

### SwitchPanelResponse

Used for generating a switch panel response that makes a _SwitchPanel_ page widget switch its view to a specific child panel and then instruct the web client to refresh the contents of the switch panel. A _SwitchPanel_ is a special panel that contains multiple child panels but has just one visible at any time. We use the panels attribute to specify the child panels to switch to. 

```java
@ResultMapping(name = "switchchangepassword", response = {
    "!switchpanelresponse panels:$l{loginSequencePanel.changePasswordBodyPanel}"})
```

#### Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| panels | true | String[] | Secifies a list of long names of child panels to switch to. |

## Template Binding

A _PageController_ must be binded to a page-defining UPL template. A UPL template is a text file that contains descriptors that describe the user interface components - widgets - that constitute a page. It also describes how the user interface components are laid out, the various events, actions and input validations they are associated to.

We bind a page controller to a UPL template by annotating the controller with the _@UplBinding_ annotation with its value element set to the relative path of the target UPL template file. UPL templates can also be binded through inheritance which applies when a page controller extends another page controller that already has a UPL binding. In this scenario, the resulting page definition of a page controller is a cummulative merge of all UPL templates bound to the page controller and all preceding super classes. Page elements defined in a subclass controller page definition automatically override similarly named elements in the page definitions bound to its super classes.

At runtime, _Page_ object is created based on the resolved UPL definitions obtained from UPL templates binded to a page controller and its super classes. The _Page_ object represents the state of the page presented on the client device. It contains instances of all user interface components as defined in the resolved UPL definitions. Also, the _Page_ is a session object that resides in the user session context and its state can be manipulated by convenient methods when you extend the _AbstractPageController_ class.

## Implementing  a PageController

When implementing a _PageController_, it is recommended that you extend the _AbstractPageController_ class using an appropriate implementation of a _PageBean_. The _AbstractPageController_ class is a convenient base class with full implementation of the _PageController_ interface. It provides a rich set of protected methods that allow for obtaining references to the session page bean, page elements, setting the visual state of page elements, setting document and page attributes, showing or hiding page popups, setting off user hints and handling basic request commands.

<img src="images/webcomponents/controllers/simplecalculator.png" alt="SimpleCalculator" align="center">

Figure 3: Simple calculator

Listing 1: Simple calculator page bean.

```java
public class SimpleCalculatorPageBean extends AbstractPageBean {

    private BigDecimal number1;

    private BigDecimal number2;

    private BigDecimal result;

    private String message;

    ... (getters/setters)

}
```

Listing 2 Simple calculator UPL

```upl
//Simple calculator UPL
!ui-document caption:$s{Simple Calculator} components:$c{basePanel}

!ui-panel:basePanel layout:$d{!ui-vertical style:$s{padding-left:4px;}}
    components:$c{simpleCalculatorPanel actionPanel}

//Input and result panel
!ui-panel:simpleCalculatorPanel
    layout:$d{!ui-vertical captionSuffix:$s{:} showCaption:true}
    components:$c{number1 number2 result hint}

!ui-decimal:number1 caption:$s{First Number} binding:number1
    focus:true
!ui-decimal:number2 caption:$s{Second Number} binding:number2
!ui-decimal:result caption:$s{Result} binding:result
!ui-label:hint style:$s{font-weight:bold;} binding:message

//Action panel
!ui-panel:actionPanel layout:$d{!ui-horizontal}  
    components:$c{addBtn subBtn}
!ui-button:addBtn caption:$s{Add} 
    eventHandler:$d{!ui-event event:onclick action:$c{addAct}}
!ui-button:subBtn caption:$s{Subtract}
    eventHandler:$d{!ui-event event:onclick action:$c{subAct}}

//Actions
!ui-post:addAct path:$n{/add} components:$c{number1 number2}
!ui-post:subAct path:$n{/subtract} components:$c{number1 number2}
```

Listing 3: Simple calculator page controller.

```java
@Component(name = "/simplecalculator")
@UplBinding("simplecalculator.upl")
@ResultMappings({
    @ResultMapping(name = "refresh",
    response = { "!refreshpanelresponse panels:$l{simpleCalculatorPanel}" }) })
public class SimpleCalculatorPageController
    extends AbstractPageController<SimpleCalculatorPageBean> {

    public SimpleCalculatorPageController() {
        super(SimpleCalculatorPageBean.class);
    }

    @Action
    public String add() throws UnifyException {
        SimpleCalculatorPageBean pageBean = getPageBean();
        // Perform addition
        BigDecimal firstNumber = pageBean.getNumber1();
        BigDecimal secondNumber = pageBean.getNumber2();
        BigDecimal result = firstNumber.add(secondNumber);

        // Set result
        pageBean.setResult(result);
        pageBean.setMessage("Added second number to first number.");

        // Refresh calculator panel
        return "refresh";
    }

    @Action
    public String subtract() throws UnifyException {
        SimpleCalculatorPageBean pageBean = getPageBean();
        // Perform subtraction
        BigDecimal firstNumber = pageBean.getNumber1();
        BigDecimal secondNumber = pageBean.getNumber2();
        BigDecimal result = firstNumber.subtract(secondNumber);

        // Set result
        pageBean.setResult(result);
        pageBean.setMessage("Subtracted second number from first number.");

        // Refresh calculator panel
        return "refresh";
    }

    @Override
    protected void onIndexPage() throws UnifyException {
        // Set result field state to non-editable
        setPageWidgetEditable("result", false);
    }
}
```