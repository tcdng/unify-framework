A _Button_ is a user interface component that is clicked to initiate some action. It is a data-holder control with the ability to keep a single value that is transfered to the server during a click event. A button is typically rendered as a rectangle surrounding a text.

<img src="images/webcomponents/controls/button1.png" alt="Button" align="center">


Listing 1: Button UPL

```upl
!ui-button:startBtn caption:$s{Start}
```

<img src="images/webcomponents/controls/button2.png" alt="Button with Icon" align="center">


Listing 2: Button with icon UPL

```upl
!ui-button:startBtn caption:$s{Start}
    imageSrc:$s{web/images/start.png}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| imageSrc | true | String | Specifies the image resource to be used when rendering button. |
| debounce | true | boolean | Indicates if widget should participate in debounce action. Defaults to 'true'. |
