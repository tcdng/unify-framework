A _PasswordField_ widget is a text input control that is used for securely capturing a password. Typically, input characters are hidden by rendering each character using the same visual symbol. Actual input characters can not be discerned by visual inspection. The password field accepts any input character and like any other text input field, you can set the minimum and maximum acceptable input character length.

This control binds to a _String_ object.

<img src="images/webcomponents/controls/passwordfield.png" alt="PasswordField" align="center">

Listing 1: PasswordField UPL

```upl
!ui-password:frmPassword
    caption:$s{Password} binding:password
    minLen:6 maxLen:24
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| minLen | false | Integer | Specifies the minimum number of characters for control. The length of input text is checked during page validation only. |
| maxLen | false | Integer | Specifies the maximum number of characters this control can accept. This attribute restricts the maximum number of characters input by the user. It is also checked during page validation. |
| autocomplete | false | Boolean | Indicates if the password field can be automatically populated by the client password manager. Defaults to 'false' |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |