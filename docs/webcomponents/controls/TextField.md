The _TextField_ widget is a text input control that allows a user to enter characters from the keyboard. A user can enter any text composed of alphabets, numbers or special characters as there is no restriction on the type of characters acceptable by the control. Generally, the _TextField_ control is used for capturing short text because it’s viewing area is restricted to a single line.

This component binds to a _String_ object. 

<img src="images/webcomponents/controls/textfield.png" alt="TextField" align="center">

Listing 1: TextField UPL

```upl
!ui-text:frmBookTitle
    caption:$s{Title} binding:bookTitle
    size:32 minLen:1 maxLen:40
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| size | false | Integer | Specifies the visible number of character columns that the text field should have. |
| minLen | false | Integer | Specifies the minimum number of characters for control. The length of input text is checked during page validation only. |
| maxLen | false | Integer | Specifies the maximum number of characters this control can accept. This attribute restricts the maximum number of characters input by the user. It is also checked during page validation. |
| case | false | String | Specifies if all input characters should be set to upper case or lower case characters. It can be set to either UPPER or LOWER. |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |
