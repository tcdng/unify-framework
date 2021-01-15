The _WordField_ widget is a text input control that accepts only alphabetic characters. 

This component binds to a _String_ object. 

<img src="images/webcomponents/controls/wordfield.png" alt="WordField" align="center">

Listing 1: WordField UPL

```upl
!ui-word:frmCapital
    caption:$s{Capital} binding:capital
    minLen:1 maxLen:24 case:UPPER
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| size | false | Integer | Specifies the visible number of character columns that the text field should have. |
| minLen | false | Integer | Specifies the minimum number of characters for control. The length of input text is checked during page validation only. |
| maxLen | false | Integer | Specifies the maximum number of characters this control can accept. This attribute restricts the maximum number of characters input by the user. It is also checked during page validation. |
| case | false | String | Specifies if all input characters should be set to upper case or lower case characters. It can be set to either UPPER or LOWER. |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |