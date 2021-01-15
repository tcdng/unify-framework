A _Container_ is a widget that is used to constrain a group of widgets, referred to as child widgets, to a particular visual area and some common rules. Basically, you use a _Container_ to manage a group of child widgets. Some of what you can do with a _Container_ includes:

* Set the layout rule of the container and determine how its child widgets are laid out during rendering.
* Set a common value store data object for all child widgets replacing the default _PageBean_ object.
* Show or hide entire set of child widgets by setting the parent _Container_ visible state to true or false.
* Disable or enable entire set of child widgets by setting the parent _Container_ disabled state to true or false.
* Control the editable state of child input widgets by setting the parent _Container_ editable state.

Typically, child widgets are associated with their parent container in the UPL declaration of the parent _Container_. This is done by setting the _components_ attribute of the _Container_ declaration to a component reference list of IDs of the child widgets. Specifying the component reference list requires the use of a component list token $c{}.

All containers must implement the _com.tcdng.unify.web.ui.Container_ interface. The framework provides a convenient _AbstractContainer_ class which you can  extend easily when you are implementing a new type of _Container_.

## Types

[Panel](/tcdng/unify-framework/wiki/Panel), [AccordionPanel](/tcdng/unify-framework/wiki/AccordionPanel), [CollapsiblePanel](/tcdng/unify-framework/wiki/CollapsiblePanel), [DynamicPanel](/tcdng/unify-framework/wiki/DynamicPanel), [MessageBoxPanel](/tcdng/unify-framework/wiki/MessageBoxPanel), [RepeatPanel](/tcdng/unify-framework/wiki/RepeatPanel), [TabbedPanel](/tcdng/unify-framework/wiki/TabbedPanel), [Form](/tcdng/unify-framework/wiki/Form)

## Common Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| layout | false | String | Specifies the descriptor of the _Layout_ component to use when rendering the _Container_. A descriptor is specified using the descriptor token $d{}. Some containers will ignore this attribute even if it is set.  |
| components | true | UplElementReferences | Specifies the child widgets associated with the _Container_. This is set by enclosing a list of child widget IDs in the component list token $c{}.  |
| cascade | false | Boolean | Indicates if the _Container_ should spread the contents of the _List_ value it binds to across its child widgets. Each child widget will be binded to a single item in the list and in sequence when the _Container_’s _cascadeValueStore()_ method is invoked. Defaults to 'false'. |
| space | false | Boolean | Indicates if a space should be put in place of child widgets that are not visible when the _Container_ is rendered. Defaults to 'false'. |
