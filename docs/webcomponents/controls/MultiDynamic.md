A _MultiDynamic_ component is a data transfer control used for capturing multiple input values with each value captured using a different rendered child control. The type of child controls used are determined at runtime. This control is useful when you want the input fields presented to a user to change dynamically based on a choice that the user has made or on some other business logic.

This component always binds to a _List_ of _Input_ objects where it gets input type information for rendering input elements on the browser.

An _Input_ data object that holds a field definition that includes a field name, a field a description, a data type, an input control UPL descriptor and a flag for indicating if a field is mandatory. There is also a value property that holds the value to be rendered by or captured by the field.

<img src="images/webcomponents/controls/multidynamic.png" alt="MultiDynamic" align="center">

Listing 1: MultiDynamic UPL

```upl
!ui-multidynamic:reportParams
    binding:reportParamList
    
!ui-button:generateBtn
    caption:$s{Generate}
    eventHandler:$d{!ui-event event:onclick action:$c{generateAct}}
!ui-post:generateAct path:$n{/generateReport} components:$c{reportParams}
```

Listing 2: Report generator page bean

```java
ReportGenerationPageBean pageBean = ...

List<Input<?>> paramList = new ArrayList<Input<?>>();
paramList.add(new StringInput("name", "Name", "!ui-text", true));
paramList.add(new StringInput("gender", "Gender",
                    "!ui-select list:$s{genderlist} blankOption:$s{}", true) );
paramList.add(new DateInput("dob", "Date of Birth", "!ui-date", true));
paramList.add(new StringInput("tin", "Tax ID", "!ui-text", false));
pageBean.setReportParamList(paramList);
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| isRequiredSymbol | false | String | Specifies the special character used to mark a child control as required. Defaults to $s{*}. |
| captionSuffix | false | String | Specifies the character that is appended to the trailing part of each child control’s caption. Defaults to $s{:}. |
