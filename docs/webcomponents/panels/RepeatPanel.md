A _RepeatPanel_ is a panel whose declared component structure is replicated based on the list of data items it binds to. The number of times its internal structure is replicated equals the number of items in the binded list. The content of each replicated instance is populated using the corresponding list data item. The replication layout is determined by the _Layout_ descriptor specified by the layout attribute in the repeat panel UPL declaration.

Adopting a _RepeatPanel_ is ideal when we require summarized information of multiple homogeneous items using a consistent representation, usually in the form of tiles or cards and with each presented item having one or more control widgets that the user can interact with to view more details.

<img src="images/webcomponents/panels/repeatpanel.png" alt="RepeatPanel" align="center">

Listing 1: Repeat Panel UPL

```upl
!ui-repeatpanel:transactionsPanel
    layout:$d{!ui-vertical style:$s{width:340px;}}
    components:$c{channelPanel} binding:channelList

!ui-panel:channelPanel
    layout:$d{!ui-horizontal style:$s{width:100%;} widths:$l{none 100%}}
    style:$s{border:1px solid #DDD;padding:2px;background-color:#F8F8F8;}
    components:$c{channelImg infoPanel}
!ui-image:channelImg
    srcBinding:image
    style:$s{padding 2px;width:72px;height:72px;}
!ui-panel:infoPanel
    layout:$d{!ui-vertical style:$s{width:100%;}}
    components:$c{channelName tranPanel detailBtn}
!ui-label:channelName
    binding:channelName
    style:$s{font-size:11pt;font-weight:bold;color:#222;}
!ui-panel:tranPanel
    layout:$d{!ui-vertical showCaption:true inlineCaption:true}
    components:$c{totalAmount transactions}
!ui-label:totalAmount
    caption:$s{Amount:} binding:totalAmount
    layoutCaption:true style:$s{padding-left:2px;}
!ui-label:transactions
    caption:$s{Transactions:} binding:transactions
    layoutCaption:true style:$s{padding-left:2px;}
!ui-button:detailBtn
    caption:$s{Details...} style:$s{float:right;} binding:channelCode
    eventHandler:$d{!ui-event event:onclick action:$c{detailsAct}}

!ui-post:detailsAct path:$n{/showChannelDetails}
```

Listing 2: Page Bean

```java
TransactionsPageBean pageBean = ...

List<TransactionChannel> channelList = new ArrayList<TransactionChannel>();
channelList.add(
    new TransactionChannel("web/images/channel/atm.png",
        "Automated Teller Machine (ATM)", "ATM",
        "USD 98,299.00", 162));
channelList.add(
    new TransactionChannel("web/images/channel/mobile.png",
        "Mobile Banking", "MLB", "USD 202,389.35", 3456));
channelList.add(
    new TransactionChannel("web/images/channel/pos.png",
        "Point of Sale (POS)", "POS", "USD 526,889.93", 4267));
pageBean.setChannelList(channelList);
```

