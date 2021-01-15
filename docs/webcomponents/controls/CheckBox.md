A _CheckBox_ is a user interface component that allows a user to make one of two choices by checking or unchecking a box. It is composed of a box that can be ticked (checked) with a caption displayed on its right side.

_CheckBox_ is a data transfer control and is always binded to a _Boolean_ object. The value of the _Boolean_ object determines the state of the _CheckBox_ and vice-versa.

<img src="images/webcomponents/controls/checkbox.png" alt="CheckBox" align="center">

Listing 1: CheckBox UPL

```upl
!ui-checkbox:calcInterest
    caption:$s{Calculate Interest}
    binding:calculateInterest
    eventHandler:$d{!ui-event event:onchange action:$c{clearRateAct}}
!ui-clear:clearRateAct components:$c{rate}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| layoutCaption | false | Boolean | Indicates whether the checkbox caption should rendered as a layout label instead of by the checkbox. |
