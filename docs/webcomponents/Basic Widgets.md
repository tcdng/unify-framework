A _Widget_ is component rendered as an element of a graphical user interface that either displays information, provides a means for a user to interact with an application or both.

## Types

[AssignmentBox](/tcdng/unify-framework/wiki/AssignmentBox), [Button](/tcdng/unify-framework/wiki/Button), [CheckBox](/tcdng/unify-framework/wiki/CheckBox), [CheckList](/tcdng/unify-framework/wiki/CheckList), [DateField](/tcdng/unify-framework/wiki/DateField), [DecimalField](/tcdng/unify-framework/wiki/DecimalField), [DropdownCheckList](/tcdng/unify-framework/wiki/DropdownCheckList), [DynamicField](/tcdng/unify-framework/wiki/DynamicField), [FileAttachment](/tcdng/unify-framework/wiki/FileAttachment), [FileDownload](/tcdng/unify-framework/wiki/FileDownload), [FileUpload](/tcdng/unify-framework/wiki/FileUpload), [Image](/tcdng/unify-framework/wiki/Image), [IntegerField](/tcdng/unify-framework/wiki/IntegerField), [Label](/tcdng/unify-framework/wiki/Label), [MoneyField](/tcdng/unify-framework/wiki/MoneyField), [MultiDynamic](/tcdng/unify-framework/wiki/MultiDynamic), [MultiSelect](/tcdng/unify-framework/wiki/MultiSelect), [NameField](/tcdng/unify-framework/wiki/NameField), [PasswordField](/tcdng/unify-framework/wiki/PasswordField), [Picture](/tcdng/unify-framework/wiki/Picture), [Rack](/tcdng/unify-framework/wiki/Rack), [RadioButtons](/tcdng/unify-framework/wiki/RadioButtons), [SearchField](/tcdng/unify-framework/wiki/SearchField), [SingleSelect](/tcdng/unify-framework/wiki/SingleSelect), [Table](/tcdng/unify-framework/wiki/Table), [TextArea](/tcdng/unify-framework/wiki/TextArea), [TextField](/tcdng/unify-framework/wiki/TextField), [TimeField](/tcdng/unify-framework/wiki/TimeField), [WordField](/tcdng/unify-framework/wiki/WordField)

## Common Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| caption | false | String | A brief explanatory text appended to a component. The caption is usually displayed around the rendered component with its position depending on the layout of the component’s  container. Some components, like _Button_,  manage the display of their captions, usually diplaying it inside their structure. |
| captionBinding | false | String | Specifies the bean property or container scope attribute whose value is used as caption. |
| styleClass | false | String | Presentation style class name to be applied to component.Default value is the name of the component. Usually the name of a class selector appended to a CSS declaration block. |
| styleClassBinding | false | String | Specifies the bean property or container scope attribute whose value is used as style class. |
| style | false | String | Presentation style to be applied to component. Usually a CSS style expression. |
| hint | false | String | A text that provides a suggestion on a component’s purpose or usage. Usually activated and displayed on the web client when the user pointer hovers over the rendered component. |
| hintBinding | false | String | Specifies the bean property or container scope attribute whose value is used as hint. |
| binding | true | String | The name of the property of the data object, usually a _PageBean_, that the component binds to.  If the data object is unset, then this refers to an attribute in the session, application or request context data objects. |
| columnStyle | false | String | Style applied to widget when rendered as a _Table_ column. |
| privilege | false | String | The privilege code assigned to the component. Used for restricting user access to a component. When this attribute is set, the system checks if the user agent in the current session has the required privilege. If not, the system prevents access to the component by either hiding or disabling it.  Every user agent has access to a component if it has no privilege set. |
| readOnly | false | Boolean | Indicates widget should be rendered in read-only mode. Defaults to 'false'. |
| eventHandler | false | EventHandler[] | Specifies event handlers for events that are triggered on this widget. Set using a list of descriptor tags $d{}. |


