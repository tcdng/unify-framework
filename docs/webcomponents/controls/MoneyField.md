A _MoneyField_ is an input control that is used for capturing or presenting a money value. It is composed of a text field for entering or displaying the money amount and an adjacent button with a caption that indicates the money currency. The text field accepts only decimal numbers in a format that is based on the how the _MoneyField_ attributes are set in its UPL declaration. In can operate in single currency or multi-currency mode.

This component always binds to a _Money_ property.

<img src="images/webcomponents/controls/moneyfield1.png" alt="MoneyField1" align="center">

Listing 1: MoneyField in single currency mode UPL

```upl
!ui-money:frmPettyCash
    caption:$s{Petty Cash} binding:pettyCash
    currency:GBP precision:6 scale:2
```

Listing 2: Account page bean

```java
AccountsPageBean pageBean = ...
Money pettyCash = ...
pageBean.setPettyCash(pettyCash);
```

In multi-currency mode, clicking on the currency button reveals a dropdown window with a list of currencies that a user can choose from. Choosing a currency by selecting any of the items in the currency list hides the dropdown window and sets the _MoneyField_ currency.

<img src="images/webcomponents/controls/moneyfield2.png" alt="MoneyField2" align="center">

Listing 3: MoneyField in multi-currency mode UPL

```upl
!ui-money:frmPettyCash
    caption:$s{Petty Cash} binding:pettyCash
    currency:NGN precision:8 scale:2 useGrouping:true
    multiCurrency:true
```

Having all currencies available as options may not be suitable for certain use cases in practice. In such situations, we can limit the currency options to specific currencies by setting the _listParams_ attribute of the _MoneyField_ to a list of currency codes we want to restrict the user to. 

<img src="images/webcomponents/controls/moneyfield3.png" alt="MoneyField3" align="center">

Listing 4: MoneyField in multi-currency mode with restriction UPL

```upl
!ui-money:frmPettyCash
    caption:$s{Petty Cash} binding:pettyCash
    currency:USD precision:8 scale:2 useGrouping:true
    multiCurrency:true
    listParams:$l{GBP USD EUR}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| currency | true | String | Specifies the default currency of the money field. The value, which should be a valid ISO 4217 currency code, is used as the money field currency if  the money field binds to a blank _Money_ property. |
| list | false | String | The name of the _ListCommand_ that backs the money field currency options. Defaults to “currencylist”. |
| listParams | false | String | A list of parameters to be passed to the execute() method of the _ListCommand_ that backs the money field currency options. If the value of the list attribute is set to the default value “currencylist”, then this attribute is a list of ISO4217 currency codes. |
| precision | false | Integer | Specifies the maximum number of digits this control can accept. |
| scale | false | Integer | Specifies the maximum number of digits after the decimal point. |
| acceptNegative | false | Boolean | Determines if a negative input value is allowed. This attribute defaults to false. |
| useGrouping | false | Boolean | Indicates grouping of the input value’s digits based on the current user session locale. This attribute to false. |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |