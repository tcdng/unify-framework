A _SearchField_ is an input control with a popup window that allows a user to search for and apply an input value.  The capability is particularly  useful when a user needs to provide an input value that comes from a potentially large dataset. In such scenarios, it would be impractical to present all the items of the dataset on the browser. The popup window has a filter input text field into which the user enters a filter string that is used to search for and limit the number of items presented as options for selection. Selecting an item from the presented options closes the search popup window and sets the value of the _SearchField_ control.

The _SearchField_ text field shows the description of the item selected by the user. The actual value of the _SearchField_ is the key value of the selected item and this is the value that is relayed to the server as input from the user.

To use a _SearchField_, you need to specify the _SearchProvider_ component that the field will use to perform a search any time the user enters a filter in the search window. A _SearchProvider_ is a specialized _ListCommand_ component with a specific _search()_ method for handling search requests. The _List<Listable>_ object returned from the _search()_ method is used to populate the options for selection in the popup window. The framework provides convenient base _AbstractSearchProviderListCommand_ and _AbstractDBSearchProvider_ classes that can be easily extended to create concrete _SearchProvider_ components.

This component binds to a Java non-array data type object. 

<img src="images/webcomponents/controls/searchfield.png" alt="SearchField" align="center">


Listing 1: SearchField UPL

```upl
!ui-search:frmCountryOfDest
    caption:$s{Country of Destination} binding:countryOfDestCode
    list:$s{country-searchprovider}
```

Listing 2: Country entity

```java
@Table("COUNTRY")
public class Country extends AbstractEntity {

    @Id
    private String isoCode;
    
    @Column(length = 64)
    String description;
    
    ...
}
```

Listing 3: Country search provider

```java
@Component("country-searchprovider")
public class CountrySearchProvider extends AbstractDBSearchProvider {

    public CountrySearchProvider() {
        super(Country.class, "isoCode", "description");
    }
}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| list | true | String | Specifies the name of the _SearchProvider_ component that backs the list items of the search window.  |
| filterLabel | false | String | Specifies the label to use for the search window filter input field. Defaults to $m{search.filter}. |
| buttonImgSrc | false | String | Specifies the image resource to use as the _SearchField_ button icon. Defaults to $t{images/search.png} |
