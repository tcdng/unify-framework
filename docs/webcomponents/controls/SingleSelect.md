A _SingleSelect_ widget is a list control that allows a user to select one item from a list of items. In the default state the _SingleSelect_ control has a read-only text area with a dropdown button. When the dropdown button is clicked, a dropdown window appears containing a vertical list of items from which the user can select just one item. On selection, the dropdown disappears and the option selected by the user is set in the widget’s text area. The actual value of the _SingleSelect_ is the key value of the selected item and this is the value that is transfered to the server.

The list of items presented in the _SingleSelect_ dropdown window is obtained from the _ListCommand_ component that is associated with the widget.

This component binds to a Java non-array data type object. 

<img src="images/webcomponents/controls/singleselect.png" alt="SearchField" align="center">

Listing 1: SingleSelect UPL

```upl
!ui-select:frmCar
    caption:$s{Favorite Car} binding:code
    list:$s{carlist} blankOption:$s{}
```

Listing 2: Car Listable class

```java
public class Car implements Listable {

    private String code;

    private String name;
    
    public Car(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getListKey() {
        return code;
    }

    @Override
    public String getListDescription() {
        return name;
    }

    ...
}
```

Listing 3: Car list command

```java
@Component("carlist")
public class CarListCommand extends AbstractListCommand {

    @Override
    public List<? extends Listable> execute(Locale locale, Object... params)
            throws UnifyException {
        List<Car> list = new ArrayList<CarData>();
        list.add(new Car("HND", "Honda"));
        list.add(new Car("LEX", "Lexus"));
        list.add(new Car("MRC", "Mercedes-Benz"));
        list.add(new Car("TYA", "Toyota"));
        return list;
    }
}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| blankOption | false | String | Indicates that the control should have a blank option as the first item with its label set to the string value of the attribute. |
| list | true | String | Name of the _ListCommand_ that backs the list items. |
| listParams | false | String | A list of names of parameters to be passed to the _execute()_ method of the _ListCommand_ that backs the list items. The actual values passed to the _execute()_ method are gotten from the bean properties or any of the container scopes. |
| listParamType | false | String | Indicates how the _listParams_ attribute is treated. Value can be set to any string in the set: {IMMEDIATE,CONTROL,PANEL}. IMMEDIATE means _listParams_ values are used directly as the list parameters. CONTROL means the list parameters are resolved within the _CheckList_ instance value scope. PANEL mean the list parameters are resolved within the _CheckList_ parent panel value scope. This attribute defaults to CONTROL. |
| listKey | false | String | The property of list item objects to use as keys in the list. A key represents the value of a selected item. This attribute defaults to 'listKey'. |
| listDescription | false | String | The property of list item objects to use as description. Descriptions are the actual labels displayed for each item. This attribute defaults to 'listDescription'. |
| required | false | Boolean | Indicates if an input value for this control is required. It is taken into consideration during form validation. Defaults to 'false'. |