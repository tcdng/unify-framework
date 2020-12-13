The _Image_ widget is a control that allows us to embed an image within a page. You can set the dimensions the image using the styling attributes of the component and also attach page events. The _Image_ control obtains its image data from one of various sources including:
* a static file resource specified by the UPL attributes _src_ or _srcBinding_,
* a _byte[]_ array in memory referenced by the property the control binds to,
* and an _ImageGenerator_ component referenced by the property the control binds to. 

The static file resource option has a precedence over the two other image source options.

<img src="images/webcomponents/controls/image.png" width="180" alt="Image" align="center"> 

Listing 1: Image control with static file resource UPL

```upl
!ui-image:profileImg
    src:$s{web/images/profile.png}
    style:$s{width:240px;height:420px;}
```

Use the _byte[]_ array option where your image is stored in memory.
 
Listing 2: Image control using byte array UPL

```upl
!ui-image:profileImg
    binding:profileImage
    style:$s{width:240px;height:420px;}
```
 
Listing 3: Profile page bean using byte array

```java
ProfilePageBean pageBean = ...
byte[] profileImg = ...
pageBean.setProfileImage(profileImg);
```

The _ImageGenerator_ option is useful when you want to generate images, to be embedded in a page, on the fly. 
 
Listing 4: Image control using image generator UPL

```upl
!ui-image:profileImg
    binding:imageGenerator
    style:$s{width:240px;height:420px;}
```
 
Listing 5:  Profile page bean using image generator

```java
ProfilePageBean pageBean = ...
ImageGenerator imageGenerator = ...
pageBean.setImageGenerator(imageGenerator);
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| src | false | String | Specifies the source of the image which is name of the image file resource or scope attribute to be rendered by this control. |
| srcBinding | false | String | Specifies the bean property or container scope attribute whose value is the source of the image. |
| scope | false | String | Specifies the image source scope if the source string value is enclosed in the context scope tag $o{}. The value of this attribute can be any string in the set: {APPLICATION,SESSION}. |
| clearOnRead | false | Boolean | Indicates if image source should be cleared from context when read. It is taken into consideration only when the image source is scoped. Defaults to 'true'. |
| debounce | true | boolean | Indicates if widget should participate in debounce action. Defaults to 'false'. |
