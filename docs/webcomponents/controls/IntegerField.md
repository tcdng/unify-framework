An _IntegerField_ is an input field for capturing and displaying integer values. An integer is a whole number; a number without a fraction. You can configure the input control’s precision, acceptNegative and useGrouping attributes to determine what the field would accept during user input. 

This component binds to Java integer data types including _Byte_, _Short_, _Integer_ and _Long_.

<img src="images/webcomponents/controls/integerfield.png" alt="IntegerField" align="center">

Listing 1: IntegerField UPL

```upl
!ui-integer:frmPopulationCount
    caption:$s{Population Count} binding:populationCount
    precision:14 useGrouping:true
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| precision | false | Integer | Specifies the maximum number of digits this control can accept. |
| acceptNegative | false | Boolean | Determines if a negative input value is allowed. This attribute defaults to false. |
| useGrouping | false | Boolean | Indicates grouping of the input value’s digits based on the current user session locale. This attribute to false. |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |