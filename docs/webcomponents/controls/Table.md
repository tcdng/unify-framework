A _Table_ control is a widget used to display substantial quantities of data organized in columns and rows. A list of homogeneous data objects is presented as rows representing individual objects and columns representing properties of each of the objects. Each column has a heading to identify what object property the column represents. 

When a _Table_ is rendered, it appears as a grid with a cell at every point a row intersects with a column. Every cell in the table holds the value of the property of the data object that the cell’s column and row represent. The contents of each cell is rendered using the _Control_ widget associated with the column to which the cell belongs. This association is specified in the UPL declaration of the Table control.

This component binds to a _java.util.List_ value object.

<img src="images/webcomponents/controls/table1.png" alt="Table1" align="center">

Listing 1: Basic table UPL

```upl
!ui-table:planetsTbl
    caption:$s{Planets 101} binding:planetInfoList
    components:$c{nameCol sizeCol moonCol}
!ui-label:nameCol
    caption:$s{Name} binding:name
    columnStyle:$s{width:180px;}
!ui-label:sizeCol
    caption:$s{Radius (km)} binding:size
    columnStyle:$s{width:120px; text-align:right;}
    formatter:$d{!integerformat useGrouping:true}
!ui-label:moonCol
    caption:$s{Moons} binding:numberOfMoons
    columnStyle:$s{width:100px; text-align:right;}
```

Listing 2: Planet information bean

```java
public class PlanetInfo {

    private String name;

    private Integer size;

    private Integer numberOfMoons;

    public PlanetInfo(String name, Integer size, Integer numberOfMoons) {
        this.name = name;
        this.size = size;
        this.numberOfMoons = numberOfMoons;
    }

    public PlanetInfo () {
    
    }

    ...
}
```


Listing 3: Solar system page controller

```java
SolarSystemPageBean pageBean = ...

ArrayList<PlanetInfo> planetInfoList = new ArrayList<PlanetInfo>();
planetInfoList.add(new PlanetInfo("Mercury", 2440, 0));
planetInfoList.add(new PlanetInfo("Venus", 6050, 0));
planetInfoList.add(new PlanetInfo("Earth", 6378, 1));
planetInfoList.add(new PlanetInfo("Mars", 3394, 2));
planetInfoList.add(new PlanetInfo("Jupiter", 68700, 69));
planetInfoList.add(new PlanetInfo("Saturn", 57550, 62));
planetInfoList.add(new PlanetInfo("Uranus", 25050, 27));
planetInfoList.add(new PlanetInfo("Neptune", 24700, 14));
pageBean.setPlanetInfoList(planetInfoList);
```

## Column Sorting

Items in a _Table_ can be sorted by column values. A directional icon appears in the header of a sortable column which, on a mouse click by the user, triggers sorting of the table items based on the property that the column represents. Sort direction is toggled between ascending and descending on subsequent clicking of the sort icon and all sorting operations apply directly on the _List_ object that the _Table_ control binds to. The sorting feature can be enabled in your UPL declaration where you specify sortable columns using the _sortable_ UPL attribute.

<img src="images/webcomponents/controls/table2.png" alt="Table2" align="center">

Listing 4: Table with sortable columns UPL

```upl
!ui-table:planetsTbl
    caption:$s{Planets 101} binding:planetInfoList
    components:$c{nameCol sizeCol moonCol}
!ui-label:nameCol
    caption:$s{Name} binding:name
    columnStyle:$s{width:180px;}
    sortable:true
!ui-label:sizeCol
    caption:$s{Radius (km)} binding:size
    columnStyle:$s{width:120px; text-align:right;}
    formatter:$d{!integerformat useGrouping:true}
    sortable:true
!ui-label:moonCol
    caption:$s{Moons} binding:numberOfMoons
    columnStyle:$s{width:120px; text-align:right;}
    sortable:true
```

## Multiple Selection

The _Table_ component can be set to operate in multi-select mode where multiple rows can be selected. In this mode, you can chose to make selection check boxes visible where a column of check boxes is rendered in the table with each row preceded by a checkbox. This allows a user to select one or more rows by checking the associated check boxes. A user can also select all rows by checking the checkbox that appears in the header of the multi-select checkbox column.

<img src="images/webcomponents/controls/table3.png" alt="Table3" align="center">

Listing 5: Table with multi-select mode UPL

```upl
!ui-table:planetsTbl
    caption:$s{Planets 101} binding:planetInfoList
    components:$c{nameCol sizeCol moonCol}
    multiSelect:true
...
```

Now, assuming a user has selected multiple rows in the table, how do we know in our program which rows the user has selected? The Table control provides a _getSelectedRowIndexes()_ method which returns an _Integer[]_ array that contains the indexes of the selected rows. We can then use these indexes to access the _List_ object that the table binds to and retrieve actual data items. These steps are performed in the _PageController_ action handling method that is invoked when the Table control is pushed to the server by a page submission or a post.


Listing 6: Extracting selected items

```java
public class SolarSystemPageController
    extends AbstractPageController<SolarSystemPageBean> {
    ...
    
    @Action
    public String performAnalysis() throws UnifyException {
        SolarSystemPageBean pageBean = getPageBean();
        Table table = getPageWidgetByShortName(Table.class, "planetsTbl");
        Integer[] selectIndexes = table.getSelectedRowIndexes();
        for(Integer index: selectIndexes) {
            PlanetInfo planetInfo = pageBean.getPlanetInfoList().get(index);
            // Analyze planet information here
            ...
        }

        return "showAnalysis";
    }
    
    ...
}
```

## Enabling Table Pagination

The number of items in the _List_ object that a _Table_ binds to can easily run into hundreds of items. In such cases, you will want the table to present only some of those items at once using the concept of pages. Each page will contain upto a specific number of items and not more. When you enable pagination, your _Table_ instance will be rendered in a window with an additional pagination bar. The pagination bar is used for selecting the number of items per page and also navigating through the resulting pages.

<img src="images/webcomponents/controls/table4.png" alt="Table4" align="center">

Listing 7: Table with pagination UPL

```upl
!ui-table:planetsTbl
    caption:$s{Planets 101} binding:planetInfoList
    components:$c{nameCol sizeCol moonCol}
    multiSelect:true
    style:$s{width:480px;height:300px;} pagination:true
...
```

## Table Editing
You can setup a _Table_ to allow user input to make changes to the properties of data objects in the _List_ object IT binds to. A table has the _editable_ property that can be set programatically to put a table into editable mode. The table is editable if the table’s disabled property is set to false, its editable property is set to true and its parent container is editable. Now, for an editable _Table_, the ability of a user to edit cell values depends on the nature of the column controls specified in the _Table_ UPL declaration. To edit cell values, we have to use, for the table column controls, input controls like _TextField_, _DateField_, CheckBox_, _SingleSelect_,  etc.

<img src="images/webcomponents/controls/table5.png" alt="Table5" align="center">

Listing 8: Table column editable mode UPL

```upl
!ui-table:planetsTbl
    caption:$s{Planets 101} binding:planetInfoList
    components:$c{nameCol sizeCol moonCol}
!ui-label:nameCol
    caption:$s{Name} binding:name
    columnStyle:$s{width:180px;}
!ui-label:sizeCol
    caption:$s{Radius (km)} binding:size
    columnStyle:$s{width:120px; text-align:right;}
    formatter:$d{!integerformat useGrouping:true}
!ui-integer:moonCol
    caption:$s{Moons} binding:numberOfMoons
    precision:4
```

Listing 9: Setting table editable

```java
public class SolarSystemPageController
    extends AbstractPageController<SolarSystemPageBean> {
    ...
    
    @Action
    public String prepareForEdit() throws UnifyException {
        ...
        setPageWidgetEditable("planetsTbl", true);
        
        return "refresh";
    }
}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| components | true | UplReferences | Specifies the IDs of the components to be used for rendering the table columns. Set using the components tag $c{}.   |
| selDependentList | false | UplReferences | Specifies the IDs of the components, on the same page with the _Table_,  that are enabled only when a row is selected. Basically used when we want to automatically disable related controls when the content of a _Table_ control is empty. Set using the components tag $c{}.  |
| multiSelDependentList | false | UplReferences | Specifies the IDs of the components, on the same page with the _Table_,  that are enabled only when at least one or more rows are selected. Basically used when we want to automatically disable related controls when the is no multi-selected item in a _Table_ control. Set using the components tag $c{}. |
| rowEventHandler | false | EventHandler[] | Specifies event handlers for events that are triggered on a table row. Set using a list of descriptor tags $d{}. |
| bodyStyle | false | String | Specifies styling to be applied to the table body. |
| selectBinding | false | String | Specifies which list item boolean property that multi-select checkbox column should bind to. |
| rowSelectable | false | Boolean | Indicates if a row in the table is selectable. Defaults to 'false' |
| serialNumbers | false | Boolean | Indicates if a serial number column should be added to the table. Rows are numbered in sequence corresponding to item position in list. Defaults to 'false'. |
| multiSelect | false | Boolean | Indicates if multi-selection option is enabled for table. Defaults to 'false'. |
| windowed | false | Boolean | Indicates if a scrollable window should be added to the table. Defaults to 'false'. |
| pagination | false | Boolean | Indicates if table content is ton be paginated. If set to true, the table is automaticaly windowed and a pagination bar is added. Defaults to 'false'. |
