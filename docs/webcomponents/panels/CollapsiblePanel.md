A _CollapsiblePanel_ allows you to toggle its contents between an expanded state and a collapsed state. In the expanded state the contents of the panel are visible while they are hidden in the collapsed state. The user triggers a toggle by clicking on the toggle control in the panel’s header. The contents of the collapsible panel is the rendering of widgets referenced by the _content_ attribute in its UPL declaration.

<img src="images/webcomponents/panels/collapsiblepanel.png" alt="CollapsiblePanel" align="center">

Listing 1: Collapsible Panel UPL

```upl
!ui-collapsiblepanel:loanCalcPanel
    panelCaption:$s{Loan Schedule Calculator}
    contentLayout:$d{!ui-vertical showCaption:true}
    content:$c{loanType loanAmt loanTerm rate startDt calcBtn}

!ui-select:loanType
    caption:$s{Loan Type:} binding:loanType
    list:loantypelist blankOption:$s{}  style:$s{width:140px;}
!ui-money:loanAmt
    caption:$s{Loan Amount:} binding:loanAmt
    currency:USD useGrouping:true precision:8 scale:2
!ui-group:loanTerm
    caption:$s{Loan Term:} space:true
    components:$c{frmTerm frmPeriod}
!ui-integer:frmTerm
    size:4 precision:3 binding:term
!ui-select:frmPeriod
    style:$s{width:100px;} binding:termPeriod
    list:termperiodlist blankOption:$s{}
!ui-decimal:rate
    caption:$s{Interest Rate(%):}
    size:8 precision:4 scale:2 binding:rate
!ui-date:startDt
    caption:$s{Start Date:} binding:startDate
!ui-button:calcBtn caption:$s{Calculate}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| panelCaption | true | String | Specifies the collapsible panel caption. |
| contentLayout | false | String | Specifies the descriptor of the Layout component to use when rendering the collapsible panel content. A descriptor is specified using the descriptor token $d{}. |
| content | true | UplElementReferences | Specifies the widgets contained by the collapsible panel. This is set by enclosing a list of child widget names in the component list token $c{}. |

