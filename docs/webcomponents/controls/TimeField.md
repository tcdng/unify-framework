The _TimeField_ is an input field for capturing and displaying time. It has a text area that displays time and a dropdown button by the side with a clock icon. To capture time, a user clicks on the dropdown button which reveals a dropdown window for setting the time components. The user can set hours, minutes and the time period indicator using the corresponding increment and decrement buttons. Clicking on the Set button closes the _TimeField_ dropdown and sets the value of the set time in the control’s text area.

The _TimeField_ component binds to _java.util.Date_ data object. You extract the time components of the _Date_ object using _Calendar_ utilities.

<img src="images/webcomponents/controls/timefield.png" alt="TimeField" align="center">

Listing 1: TimeField UPL

```upl
!ui-time:frmStartTime
    caption:$s{Start Time} binding:startTime
    clearable:true
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| clearable | false | Boolean | Indicates whether the _TimeField_ value can be cleared. If set to true it enables the 'Clear' button in the _TimeField_ dropdown, otherwise the button is disabled. Defaults to 'false'. |
| buttunImgSrc | false | String | Specifies the image resource to use as the _TimeField_ button icon. Defaults to $t{images/clock.png} |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |