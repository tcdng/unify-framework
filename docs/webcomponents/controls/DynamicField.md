A _DynamicField_ is an input control whose rendered form is determined by the resolved value of its _descriptorBinding_ attribute. The resolved value, discerned at runtime, is a UPL descriptor of a control that will be rendered in position the _DynamicField_. This allows you to change the type of input control displayed based on some business logic at runtime.

<img src="images/webcomponents/controls/dynamicfield.png" alt="DynamicField" align="center">

Listing 1: DynamicField UPL

```upl
!ui-dynamic:collateralReq
    caption:$s{Requirement}
    binding:requirement
    descriptorBinding:inputControl
```

Listing 2: Loan Application Page Bean

```java
LoanApplicationPage pageBean = ...
pageBean.setRequirement(RequirementType.OPTIONAL);
pageBean.setInputControl("!ui-select list:requirementtypelist");
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| descriptorBinding | true | String | Specifies the name of bean property or container scope attribute whose value is the UPL descriptor of the component to be rendered. |
