A _Label_ is a control used for displaying non-editable text in a location on a page. They are usually used to add descriptive text various sections of a page that provide the user with useful information. A _Label_ has an area in which text is displayed. Normally, text that are longer than the area’s width are wrapped unless the styling rules of the _Label_ component has been changed.

Text to be displayed is gotten from the caption attribute or the _String_ value of the bean property or the container scope attribute the label binds to. 

<img src="images/webcomponents/controls/label1.png" alt="Label1" align="center">

Listing 1: Label using bean property UPL

```upl
!ui-label:disclaimerLabel
    style:$s{border:1px solid #CCC;width:180px;}
    binding:disclaimer
```

Listing 2: Contract page bean

```java
ContractPageBean pageBean = ...
String disclaimer = "Not to be used as a medical tricorder in any situation.";
pageBean.setDisclaimer(disclaimer);
```

<img src="images/webcomponents/controls/label2.png" alt="Label2" align="center">

Listing 3: Label using caption UPL

```upl
!ui-label:movieLabel
    style:$s{font:italic 28px Arial;}
    caption:$s{The Night the Bridge Fell Down}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| htmlEscape | false | Boolean | Indicates whether HTML elements in label text should be escaped. Defaults to 'true'. |
