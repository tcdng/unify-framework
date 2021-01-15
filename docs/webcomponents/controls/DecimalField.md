A _DecimalField_ is an input field for capturing and displaying decimal values. You can specify the field’s decimal precision and scale, whether grouping should be used and if negative values are allowed. Input characters are limited based on the specified attribute values.

This component binds to decimal data types including _Double_, _Float_ and _BigDecimal_.

<img src="images/webcomponents/controls/decimalfield.png" alt="DecimalField" align="center">

Listing 1: DecimalField UPL

```upl
!ui-decimal:gravity
    caption:$s{Gravity (m/s^2)} binding:gravity
    precision:10 scale:3
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| precision | false | Integer | Specifies the maximum number of digits this control can accept. |
| scale | false | Integer | Specifies the maximum number of digits after the decimal point. |
| acceptNegative | false | Boolean | Determines if a negative input value is allowed. This attribute defaults to false. |
| useGrouping | false | Boolean | Indicates grouping of the input value’s digits based on the current user session locale. This attribute to false. |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |