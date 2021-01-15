An _AccordionPanel_ is a panel that has multiple vertical captioned sections with only one section expanded at any time. Clicking the captioned header of any collapsed section expands it and collapses the previous expanded section. The accordion panel allows you to utilize the same space for presenting one of multiple sections or panels at any one time.

The sections in an accordion panel are determined by the widgets specified at the _components_ attribute of its UPL declaration. A section of the accordion panel is allocated for each of the specified widgets with the total number of sections equal to the number of widgets. The caption of the header of each section is set to the caption of its allocated widget.

For this component, the UPL attributes _layout_, _cascade_ and _space_ are ignored.

<img src="images/webcomponents/panels/accordionpanel.png" alt="AccordionPanel" align="center">

Listing 1: Accordion Panel UPL

```upl
!ui-accordionpanel:bookPreviewPanel
    style:$s{width:360px;}
    components:$c{chapter1Label chapter2Label chapter3Label}
!ui-label:chapter1Label
    style:$s{font-style:italic;}
    caption:$s{Chapter 1} binding:chapter1Text   
!ui-label:chapter2Label
    style:$s{font-style:italic;}
    caption:$s{Chapter 2} binding:chapter2Text   
!ui-label:chapter3Label
    style:$s{font-style:italic;}
    caption:$s{Chapter 3} binding:chapter2Text   
```
