A _MessageBoxPanel_ displays a message in a panel with an icon indicating the type of message and also one or more option buttons. Typically shown in a modal popup panel, the message box panel is used primarily to facilitate a dialog with the user. The message presented by the message box panel informs or queries the user who then relays a choice by clicking on any of the option buttons.

The _MessageBoxPanel_ binds to the _com.tcdng.unify.web.ui.data.MessageBox_ object whose properties caption and message hold the caption of the message box panel and the message to be presented respectively. Other properties, _messageMode_ and _messageIcon_, determine the available option buttons and message icon presented. The optional _resultPath_ property allows you to set the action path of _PageController_ method that will handle any of the options clicked by the user. The values of these properties are set using the appropriate _MessageBox_ constructors.

<img src="images/webcomponents/panels/messageboxpanel.png" alt="MessageBoxPanel" align="center">

Listing 1: MessageBox Panel UPL

```upl
!ui-messageboxpanel:launchMessageBoxPanel
    binding:confirmMessageBox
    messageResultPath:$n{/confirmLaunch}

!ui-button:launchBtn caption:$s{Launch}
    eventHandler:$d{!ui-event event:onclick action:$c{launchAct}}
!ui-post:launchAct path:$n{/performLaunch}
```

Listing 2: Page Controller

```java
@ResultMappings({
    @ResultMapping(
        name = "showconfirmlaunch",
        response = { "!showpopupresponse popup:$s{launchMessageBoxPanel}" }),
    @ResultMapping(name = "launchdone", response = { "!hidepopupresponse" }),
    ...})
public class LaunchRocketController
    extends AbstractPageController<LaunchRocketPageBean> {
    ...
    
    @Action
    public String performLaunch() throws UnifyException {
        if (detectAnomaly()) {
            LaunchRocketPageBean pageBean = getPageBean();
            MessageBox confirmMessageBox = new MessageBox(MessageIcon.WARNING,
                MessageMode.YES_NO, "Probe Launch",
                "Spatial anomaly detected off Bajor. Proceed with launch anyway?");
            pageBean.setConfirmMessageBox(confirmMessageBox);
            return "showconfirmlaunch";
        }

        launchProbe();
        return "launchdone";
    }

    @Action
    public String confirmLaunch() throws UnifyException {
        MessageResult result = this.getRequestTarget(MessageResult.class);
        if (MessageResult.YES.equals(result)) {
            // Launch confirmed
            launchProbe();
            hintUser("message.probelaunch.successful");
        } else {
            hintUser("message.probelaunch.aborted");
        }

        return "launchdone";
    }
    
    ...
}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| messageResultPath | false | String | Specifies the action path to call when the user clicks on any of the message box buttons. Defaults to the path referenced by the binded _MessageBox_ object’s _resultPath_ property. |


## Message Modes

| Name | Description |
|------|-------------|
| MessageMode.OK | The mode constant for a message box that contains one action button: the OK button. |
| MessageMode.OK_CANCEL | The mode constant for a message box that contains two action buttons: the OK and CANCEL buttons. |
| MessageMode.YES_NO | The mode constant for a message box that contains two action buttons: the YES and NO buttons. |
| MessageMode.YES\_NO\_CANCEL | The mode constant for a message box that contains three action buttons: the YES, NO and CANCEL buttons. |
| MessageMode.RETRY_CANCEL | The mode constant for a message box that contains two action buttons: the RETRY and CANCEL buttons. |


## Message Icons

| Name | Description |
|------|-------------|
| MessageIcon.INFO | <img src="images/webcomponents/panels/msg_info.png" alt="Info" width="32" align="left"> A constant that displays an icon indicating information in the message box. |
| MessageIcon.WARNING | <img src="images/webcomponents/panels/msg_warning.png" alt="Warning" width="32" align="left"> A constant that displays an icon indicating a warning message in the message box. |
| MessageIcon.ERROR | <img src="images/webcomponents/panels/msg_error.png" alt="Error" width="32" align="left"> A constant that displays an icon indicating an error message in the message box. |
| MessageIcon.QUESTION | <img src="images/webcomponents/panels/msg_question.png" alt="Question" width="32" align="left"> A constant that displays an icon indicating a question in the message box. |


## Message Result

| Name | Description |
|------|-------------|
| MessageResult.OK | Request target value returned when user clicks the OK button. |
| MessageResult.CANCEL | Request target value returned when user clicks the CANCEL button. |
| MessageResult.YES | Request target value returned when user clicks the YES button. |
| MessageResult.NO | Request target value returned when user clicks the NO button. |
| MessageResult.RETRY | Request target value returned when user clicks the RETRY button. |

