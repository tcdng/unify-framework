A _TabbedPanel_ is a container that allows switching between panels that have tabs. The panels represent and display the widgets contained by the _TabbedPanel_ in sequence and with only one panel being displayed at any time. The tabs, titled using the widget captions, are by default arranged in a horizontal bar at the top of the _TabbedPanel_ content area. They can also be arranged by the left, right or bottom of the content area depending on how the _TabbedPanel_ instance is declared.

<img src="images/webcomponents/panels/tabbedpanel.png" alt="TabbedPanel" align="center">

Listing 1: Tabbed Panel UPL

```upl
!ui-tabbedpanel:countryFlags
    tabPosition:top
    style:$s{width:380px;height:260px;}
    components:$c{img1 img2 img3 img4}
    
!ui-image:img1
    caption:$s{Croatia}
    style:$s{width:100%;height:100%;}
    src:$s{web/images/flags/croatia.png}
!ui-image:img2
    caption:$s{Argentina}
    style:$s{width:100%;height:100%;}
    src:$s{web/images/flags/argentina.png}
!ui-image:img3
    caption:$s{Nigeria}
    style:$s{width:100%;height:100%;}
    src:$s{web/images/flags/nigeria.png}
!ui-image:img4
    caption:$s{Iceland}
    style:$s{width:100%;height:100%;}
    src:$s{web/images/flags/iceland.png}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| tabPosition | false | String | Specifies the position of the _TabbedPanel_ tabs relative to the content area. The value of this attribute can be any string in the set: {top, right, bottom, left}. Defaults to 'top'. |
