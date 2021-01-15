The _Layout_ component is used by a _Container_ to determine how to visually arrange the visible child widgets it contains within its boundary. Layouts are specified in the UPL declaration of your containers using the layout attribute and the appropriate inline descriptor tokens.

A _Layout_ is also a UPL component, so the way it is rendered depends on how the attributes of the layout instance are configured.

There are three basic layouts provided by the framework: the horizontal, vertical and grid layouts. All three layouts are tabular layouts and their component implementations are derived from the fundamental base _AbstractTabularLayout_ class. 

## Horizontal Layout

A _HorizontalLayout_ is a tabular layout that arranges child widgets in a single horizontal row.

<img src="images/webcomponents/layout/horizontallayout.png" alt="HorizontalLayout" align="center">

Listing 1: Horizontal Layout UPL

```upl
!ui-panel:bioPanel layout:$d{!ui-horizontal showCaption:true captionSuffix:$s{:}}
    components:$c{firstName lastName age}
!ui-name:firstName caption:$s{First Name} binding:firstName
!ui-name:lastName caption:$s{Last Name} binding:lastName
!ui-integer:age caption:$s{Age} binding:age
```

## Vertical Layout

A _VerticalLayout_ is a tabular layout that arranges child widgets in a single vertical column.

<img src="images/webcomponents/layout/verticallayout.png" alt="VerticalLayout" align="center">

Listing 2: Vertical Layout UPL

```upl
!ui-panel:bioPanel layout:$d{!ui-vertical showCaption:true captionSuffix:$s{:}}
    components:$c{firstName lastName age}
!ui-name:firstName caption:$s{First Name} binding:firstName
!ui-name:lastName caption:$s{Last Name} binding:lastName
!ui-integer:age caption:$s{Age} binding:age
```

## Grid Layout

A _GridLayout_ is a tabular layout that arranges child widgets in a grid. The number of columns in the grid is determine by the value of the layout’s _columns_ attribute.

<img src="images/webcomponents/layout/gridlayout.png" alt="GridLayout" align="center">

Listing 3: Grid Layout UPL

```upl
!ui-panel:bioPanel layout:$d{!ui-grid columns:2 showCaption:true captionSuffix:$s{:}}
    components:$c{firstName lastName age}
!ui-name:firstName caption:$s{First Name} binding:firstName
!ui-name:lastName caption:$s{Last Name} binding:lastName
!ui-integer:age caption:$s{Age} binding:age
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| styleClass | false | String | Presentation style class name to be applied to the layout body. Default value is the component name of the layout. Usually the name of a class selector appended to a CSS declaration block. |
| style | false | String | Presentation style to be applied to layout body. Usually a CSS style expression. |
| showCaption | false | Boolean | Indicates if the captions of the layout child widgets should be rendered. Defaults to 'false. |
| captionStyle | false | String | Specifies the visual styling to be applied to widget captions. Usually a CSS style. |
| captionSuffix | false | String | The string to append to the trailing edge of every caption in the layout.  |
| widths | false | String | A list of dimension values (in CSS dimensions) that determine the widths of corresponding columns of the _TabularLayout_. The list is specified using the $l{} UPL token. |
| heights | false | String | A list of dimension values (in CSS dimensions) that determine the heights of corresponding rows of the _TabularLayout_. The list is specified using the $l{} UPL token. |
