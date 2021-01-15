A _DropdownCheckList_ is an input control with a read-only text area and a dropdown of selectable items. In the default and collapsed state, the control displays comma-separated descriptions of selected items in a text field. In the expanded state, a dropdown presents each item with a corresponding checkbox that allows the user to make selections. The list items are backed by a _ListCommand_ component.

This component binds to a _java.util.List_ value object which contains the keys of selected items.

<img src="images/webcomponents/controls/dropdownchecklist.png" alt="DropdownCheckList" align="center">

Listing 1: DropdownCheckList UPL

```upl
!ui-dropdownchecklist:workingMonths
    caption:$s{Work Month(s):} binding:workingMonthList
    selectAllOption:$s{Select All Months}
    list:$s{monthinyearlist} 
    columns:3
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| selectAllOption | false | String | Indicates whether a select all checkbox is rendered with the attribute value set as label. |
| columns | false | Integer | Specifies the number of columns list items should be rendered. Defaults to one. |
| list | true | String | Name of the _ListCommand_ that backs the list items. |
| listParams | false | String | A list of names of parameters to be passed to the _execute()_ method of the _ListCommand_ that backs the list items. The actual values passed to the _execute()_ method are gotten from the bean properties or any of the container scopes. |
| listParamType | false | String | Indicates how the _listParams_ attribute is treated. Value can be set to any string in the set: {IMMEDIATE,CONTROL,PANEL}. IMMEDIATE means _listParams_ values are used directly as the list parameters. CONTROL means the list parameters are resolved within the _CheckList_ instance value scope. PANEL mean the list parameters are resolved within the _CheckList_ parent panel value scope. This attribute defaults to CONTROL. |
| listKey | false | String | The property of list item objects to use as keys in the list. A key represents the value of a selected item. This attribute defaults to 'listKey'. |
| listDescription | false | String | The property of list item objects to use as description. Descriptions are the actual labels displayed for each item. This attribute defaults to 'listDescription'. |
| flow | false | Boolean | Determines whether check boxes are rendered in a continuous manner that wraps within the surrounding container. This attribute defaults to false. |
