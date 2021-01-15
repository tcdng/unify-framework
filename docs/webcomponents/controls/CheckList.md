A _CheckList_ component is a _ListControl_ that allows a user to one or more items from a list by checking corresponding checkboxes.

This component binds to a _java.util.List_ value object that contains the keys of selected items. The list items are backed by a _ListCommand_ component.

<img src="images/webcomponents/controls/checklist.png" alt="CheckList" align="center">

Listing 1: CheckList UPL

```upl
!ui-checklist:workDays
    caption:$s{Select work days:} binding:workDayList
    list:$s{dayinweeklist}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| list | true | String | Name of the _ListCommand_ that backs the list items. |
| listParams | false | String | A list of names of parameters to be passed to the _execute()_ method of the _ListCommand_ that backs the list items. The actual values passed to the _execute()_ method are gotten from the bean properties or any of the container scopes. |
| listParamType | false | String | Indicates how the _listParams_ attribute is treated. Value can be set to any string in the set: {IMMEDIATE|CONTROL|PANEL}. IMMEDIATE means _listParams_ values are used directly as the list parameters. CONTROL means the list parameters are resolved within the _CheckList_ instance value scope. PANEL mean the list parameters are resolved within the _CheckList_ parent panel value scope. This attribute defaults to CONTROL. |
| listKey | false | String | The property of list item objects to use as keys in the list. A key represents the value of a selected item. This attribute defaults to 'listKey'. |
| listDescription | false | String | The property of list item objects to use as description. Descriptions are the actual labels displayed for each item. This attribute defaults to 'listDescription'. |
| flow | false | Boolean | Determines whether check boxes are rendered in a continuous manner that wraps within the surrounding container. This attribute defaults to false. |
