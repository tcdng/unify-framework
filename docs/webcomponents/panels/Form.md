A _Form_ is a container widget that contains multiple fields used to enter data. The fields are different types of input widgets that allow the user to enter various types of data depending on the nature of the information being captured. For instance, in a form to capture a person’s bio, we have text fields for capturing first name and last name, a date field for capturing date of birth and a decimal field for capturing the person’s height. Each field has a label that tells the user what the field is for and the nature of input content expected.

<img src="images/webcomponents/panels/form.png" alt="Form" align="center">

The framework adopts a model for _Form_ components where the input fields are grouped in sections with each section having a label that indicates the inherent characteristics of fields that are grouped under it. This is especially useful when your form has a multitude of fields and you want to make data capture simpler and presentation much more organized. The state of each section can be controlled independently allowing your program to determine the state of a group of fields by section. For instance, you may want to disable a section of your form from user input based on the user’s current system privileges. This is a useful feature, for instance, when you want to manage how multiple user groups access a form at different stages of a business process or workflow.

As with any type of data capture, you would want to ensure the correctness of the data captured at the form level. A _Form_ provides visual cues that inform the user on the necessity and validity of input each input value. This is acheived by the framework’s validation mechanism using associated validation components and associated UPL attributes of the form’s input fields.

To capture data using a _Form_ component, you have to bind its UPL declaration to a data object property of the backing _PageBean_. The data object will have its own propeties, with appropriate setters and getters, which the form fields will bind to. So all data captured on a form is transfered to one data object.

Listing 1: Form UPL

```upl
!ui-panel:registrationPanel
    layout:$d{!ui-vertical} components:$c{registrationForm submitBtn}
    
!ui-form:registrationForm
    captionSuffix:$s{:} binding:registration
    section:$d{!ui-section caption:$s{Registrant}
        components:$c{regName regGender regDob regAddress}}
    section:$d{!ui-section caption:$s{Vehicle}
        components:$c{vecNo vecMake vecColor}}

// Registrant
!ui-text:regName
    caption:$s{Name} size:40 required:true binding:registrantName
!ui-select:regGender
    caption:$s{Gender} list:genderlist blankOption:$s{} required:true
    binding:registrantGender
!ui-date:regDob
    caption:$s{Date of Birth} required:true binding:registrantDob
!ui-textarea:regAddress
    caption:$s{Address} required:true binding:registrantAddress

// Vehicle
!ui-text:vecNo
    caption:$s{VIN} size:40 required:true binding:vehicleIdentificationNo
!ui-text:vecMake
    caption:$s{Make} required:true binding:vehicleMake
!ui-text:vecColor
    caption:$s{Color} required:true binding:vehicleColor

// Action
!ui-button:submitBtn
    caption:$s{Submit}
    eventHandler:$d{!ui-event event:onclick action:$c{submitFormAct}}
    
!ui-post:submitFormAct
    path:$n{/submitForm} components:$c{registrationForm}
```

Listing 2: Vehicle registration data object

```java
public class VehicleRegistration {

    private String registrantName;

    private Gender registrantGender;

    private Date registrantDob;

    private String registrantAddress;

    private String vehicleIdentificationNo;

    private String vehicleMake;

    private String vehicleColor;

    ...
}
```

## Attributes

| Name | Required | Type | Description |
|------|----------|------|-------------|
| section | true | Section | An array of _Section_ descriptors with fields grouping. |
| columns | false | Integer | Specifies the number of columns in which the form’s fields are arranged. Defaults to 1. |
| requiredSymbol | false | String | Specifies the symbol used to indicate that a field is required. Defaults to '*'. |
| captionSuffix | false | String | String appended to the trailing edge of every field caption in the form. |
