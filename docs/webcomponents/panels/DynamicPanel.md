A _DynamicPanel_ is a placeholder panel for some other panel whose type is determined at runtime. It allows you to use the same section of your page to display panels that change based on some business logic or data. This is useful in scenarios where you want to use a common area for multiple related purposes. For instance, you create a page in your application where a user works on different types of workflow items. The _DynamicPanel_ will allow you to switch the presented user interface panel based on the type of workflow item the user is currently working on.

The _DynamicPanel_ has the _panelNameBinding_ UPL attribute that allows you to specify the binding property that references the component name of the _Panel_ type to be displayed. It also has the _panelValueBinding attribute that allows you to set the binding property that references the value object which the displayed panel will be binded to.

The panel to be displayed must implement the _StandalonePanel_ interface. The framework provide the convenient _AbstractStandalonePanel_ class which you can easily extend.


## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| panelNameBinding | true | String | Specifies the name of the backing page bean property that holds the component name of the panel to be displayed. |
| panelValueBinding | true | String | Specifies the name of the backing page bean property that holds the data object that the displayed panel will be binded to. |

