An _AssignmentBox_ is a data multi-control user interface component that allows a user to move items from an available list to a selected list. It also allows the removal of items from selected list back to the available list. Each list has a multi-select window that is backed by a _ListCommand_ component. Four directional buttons provide means by which movement actions are triggered – two buttons move only selected items in either directions and the other two move all items in multi-select window.

This component binds to a _java.util.List_ value object that contains the keys of selected items.

<img src="images/webcomponents/controls/assignmentbox.png" alt="AssignmentBox" width="600" align="center">

Listing 1: Assignment Box UPL

```upl
!ui-assignmentbox:privAssignmentBox
    binding:privilegeIdList
    filterCaption1:$s{Category} filterList1:$s{categorylist}
    filterCaption2:$s{Module} filterList2:$s{modulelist}
    assignCaption:$s{Assigned Privileges}
    assignList:privilegeinlist
    unassignCaption:$s{Available Privileges}
    unassignList:privilegenotinlist
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| assignList | true | String | Name of _ListCommand_ component that backs selected item list. |
| unassignList | true | String | Name of _ListCommand_ component that backs available item list. |
| filterList1 | false | String | Name of _ListCommand_ component that backs filter list 1. If supplied, a dropdown is rendered for filtering. |
| filterList2 | false | String | Name of _ListCommand_ component that backs filter list 2. If supplied, a dropdown is rendered for filtering. |
| assignCaption | true | String | Caption of selected items multi-select window. Can be a plain string or a message key. |
| unassignCaption | true | String | Caption of available items multi-select window. Can be a plain string or a message key. |
| filterCaption1 | false | String | Caption of filter 1 dropdown list. Required only if filterList1 attribute is set. |
| filterCaption2 | false | String | Caption of filter 2 dropdown list. Required only if filterList2 attribute is set. |
| size | false | Integer | Size of multi-select windows in rows. |
