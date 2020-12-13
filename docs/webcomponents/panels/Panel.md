A _Panel_ is a container widget that represents a section of a page. Panels typically hold visual components which can include other panels and page elements like widgets, layout, event handlers, actions and validations. The section a panel represents can be rendered independently of other parts of a page. This is a feature that allows you to create web applications that are responsive with little or no entire page reloads. 

Panels are the primary components you will use to construct the user interfaces of your applications. They can contain other panels and in conjunction with _Layout_ components you can define pages with almost any kind of structure.

<img src="images/webcomponents/panels/panel.png" alt="panel" align="center">

Figure 1: Panels

All panels must implement the _com.tcdng.unify.web.ui.Panel_ component interface. The framework provides an abstract panel implementation _AbstractPanel_ class, which you can easily extend to implement custom panels. A concrete basic panel implementation _PanelImpl_ can also be used to construct custom structures using UPL template declarations.

The UPL listing below shows an example of _Panel_ declaration. We have a panel that contains two other panels in an horizontal layout. The two contained panels also contain other widgets.

Listing 1: Panel Example

```upl
!ui-panel:devicePanel
    layout:$d{!ui-horizontal} components:$c{picturePanel detailsPanel}

!ui-panel:picturePanel components:$c{deviceImg}
!ui-image:deviceImg style:$s{width:160px;height:160px;}
    src:$s{web/images/microchip.png}

!ui-panel:detailsPanel
    layout:$d{!ui-vertical showCaption:true captionSuffix:$s{:}}
    components:$c{frmName frmDesc}
!ui-name:frmName caption:$s{Name} size:24 binding:name
!ui-textarea:frmDesc caption:$s{Description} columns:32 rows:5
    binding:description
```


<img src="images/webcomponents/panels/device.png" alt="Device" align="center">

Figure 2: Panel Example


## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| backImageSrc | false | String | Specifies the image resource to use as background image for the panel. |
| refreshPath | false | String | Specifies the panel refresh path if the panel allows refresh.  |
| refreshEvery | false | Integer | Specifies the panel refresh period in milliseconds if the panel allows refresh. |
| legend | false | String | Specifies the panel legend. The panel is surrounded by a fieldset that uses the legend attibute value. |
| hideOnNoComponents | false | Boolean | Indicates if the panel should be hidden if it has no child components. Defaults to 'false' |
