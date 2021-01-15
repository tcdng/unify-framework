The _DateField_ is an input field for capturing and displaying dates. Displayed dates are formatted in the current user session locale. Clicking on the _DateField_ dropdown button reveals a date picker calendar for choosing a date. The choosen date is set in the _DateField_ input field.

This component binds to _java.util.Date_ data type.

<img src="images/webcomponents/controls/datefield.png" alt="DateField" align="center">

Listing 1: DateField UPL

```upl
!ui-date:birthDt
    caption:$s{Date of Birth}
    binding:dateOfBirth clearable:false required:true
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| size | false | Integer | Size of the input field in character columns. |
| clearable | false | Boolean | Determines whether the _DateField_ value can be cleared. If set to true it enables the _Clear_ button in the date picker. If false, the _Clear_ button is disabled. This attribute defaults to false. |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |