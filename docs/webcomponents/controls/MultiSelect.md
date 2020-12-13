A _MultiSelect_ widget is a list control that allows a user to select one or more items from a list of items. All items are presented with their descriptions in a vertical list contained in a scrollable window. The list of items is populated using the _List<Listable>_ object which is gotten from the _ListCommand_ component that the _MultiSelect_ control is wired to. 

This component binds to a _List_ data object that holds the keys of selected items. 

<img src="images/webcomponents/controls/multiselect.png" alt="MultiSelect" align="center">

Listing 1: MultiSelect UPL

```upl
!ui-multiselect:frmWorkDays
    caption:$s{Work Days} binding:workDayList
    style:$s{height:180px;}
    list:$s{dayinweeklist}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| list | true | String | Name of the _ListCommand_ that backs the list items. |
| listParams | false | String | A list of names of parameters to be passed to the _execute()_ method of the _ListCommand_ that backs the list items. The actual values passed to the _execute()_ method are gotten from the bean properties or any of the container scopes. |
| listParamType | false | String | Indicates how the _listParams_ attribute is treated. Value can be set to any string in the set: {IMMEDIATE,CONTROL,PANEL}. IMMEDIATE means _listParams_ values are used directly as the list parameters. CONTROL means the list parameters are resolved within the _CheckList_ instance value scope. PANEL mean the list parameters are resolved within the _CheckList_ parent panel value scope. This attribute defaults to CONTROL. |
| listKey | false | String | The property of list item objects to use as keys in the list. A key represents the value of a selected item. This attribute defaults to 'listKey'. |
| listDescription | false | String | The property of list item objects to use as description. Descriptions are the actual labels displayed for each item. This attribute defaults to 'listDescription'. |
