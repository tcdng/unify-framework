The _NameField_ widget is a text input control that accepts only characters that qualify as a name character. A name character is any alphanumeric character and can be extended to include the underscore character, the dollar character and the period character. Keyboard character inputs to this control that do not meet the name criteria are discarded.

This component binds to a _String_ object. 

<img src="images/webcomponents/controls/namefield.png" alt="NameField" align="center">

Listing 1: NameField UPL

```upl
!ui-name:frmFirstName
    caption:$s{First Name} binding:firstName
    minLen:1 maxLen:24
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| minLen | false | Integer | Specifies the minimum number of characters for control. The length of input text is checked during page validation only. |
| maxLen | false | Integer | Specifies the maximum number of characters this control can accept. This attribute restricts the maximum number of characters input by the user. It is also checked during page validation. |
| case | false | String | Specifies if all input characters should be set to upper case or lower case characters. It can be set to either UPPER or LOWER.  |
| underscore | false | Boolean | Indicates if the name field control should accept the underscore character (_). Defaults to 'false'. |
| dollar | false | Boolean | Indicates if the name field control should accept the dollar character ($). Defaults to 'false'. |
| period | false | Boolean | Indicates if the name field control should accept the period character (.). Defaults to 'false'. |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |