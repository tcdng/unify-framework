The _RadioButtons_ widget is a list control that allows a user to choose only one item from a list of items. Items are presented in sequence with each item rendered as a radio button that is labeled with the item’s description. A user chooses an item by selecting a radio button. This action unselects any previously selected button.

This control binds to a _String_ object which hold the value of the selected radio button.

<img src="images/webcomponents/controls/radiobuttons.png" alt="RadioButtons" align="center">

Listing 1: RadioButtons UPL

```upl
!ui-radiobuttons:frmGender
    caption:$s{Gender} binding:gender
    list:$s{genderlist}
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
