A _TextArea_ is a text input control that allows a user enter multiple lines of text. The size of the input area an be specified by widget styling or by setting the associated dimension attributes – _rows_ and _columns_. When a user enters text with rows more that set for the control, a vertical scroll bar appears and the _TextArea_ window scrolls to the row of the new text being entered; the _TextArea_ is not resized.

This component binds to a _String_ object. 

<img src="images/webcomponents/controls/textarea.png" alt="TextArea" align="center">

Listing 1: TextArea UPL

```upl
!ui-textarea:frmNotes
    caption:$s{Notes} binding:notes
    rows:6 columns:32
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| columns | false | Integer | Specifies the number of character columns that the text area should have.  |
| rows | false | Integer | Specifies the number of rows that the text area should have. |
| minLen | false | Integer | Specifies the minimum number of characters this control can accept. The length of input text is checked during page validation only. |
| maxLen | false | Integer | Specifies the maximum number of characters this control can accept. This attribute restricts the number of characters that can be input by the user. It is also checked during page validation. |
| wordWrap | false | Boolean | Indicates whether input text should be automatically wrapped when character count of a row exceeds the text area columns. Defaults to 'true'. |
| scrollToEnd | false | Boolean | Indicates whether text area window should be scrolled to make the last text row visible any time the control is rendered. Defaults to 'false'. |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |