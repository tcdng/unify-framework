A _Picture_ widget is an image control that embeds a replacable image in a page. The image is provided on the web client by the user. When the user clicks on the visual rendering of a picture control, the browser presents a dialog box that allows the user to choose an image to be used to replace the original image. 
On image selection, the image binary is pushed to server and the controller property that the control is binded to is populated. The _Picture_ widget is then re-rendered on the web client using the new image.

Prior to image selection, if the binding value object is null, the _Picture_ control is rendered using a default picture. Also, you can use the common _styleClass_ and _style_ attributes of the widget to specify how the picture is to be rendered.

This control binds to a _byte[]_ object.

<img src="images/webcomponents/controls/picture1.png" alt="Picture1" align="center">

Figure 1: Default picture on blank binding value

<img src="images/webcomponents/controls/picture2.png" alt="Picture2" align="center">

Figure 2: Picture with binding value

Listing 1: Picture UPL

```upl
!ui-picture:frmPlanet
    style:$s{width:180px;height:180px;}
    binding:planetPhoto
```
