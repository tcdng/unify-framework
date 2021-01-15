## Overview

A software component is a software unit that performs a well-defined part of the functionality of a software system.
It has an interface behind which it hides the complexity of its implementation thereby allowing for the production of systems that are more manageable.

In Unify framework, all components intended to run in the Unify container must implement the _UnifyComponent_ interface. Unify components can have configurable properties which are basically class fields annotated with the _@Configurable_ annotation.

Component-container interaction is facilitated through a _UnifyComponentContext_ object that is associated with a component during its initialization. Using the context object, a component can access container related functions like getting a reference to another component, getting component configuration information, getting global application context information, getting container setting, etc.

## Implementing a Component

To implement a component, it is recommended you find a suitable abstract base class depending on your requirements and then extend it. The most fundamental base class for components that is provided by the framework is the _AbstractUnifyComponent_ class.
1. Define a component interface
2. Implement the concrete class annotated with _@Component_

Listing 1: A component interface

```java
public interface LoanCalculator extends UnifyComponent {

    Double calculateInterest(Double principal, int years);
}
```

Listing 2: A component implementation

```java
@Component("simple-loancalculator")
public class SimpleLoanCalculator extends AbstractUnifyComponent implements
        LoanCalculator {

    @Configurable("2.5")
    private Double interestRate; //Configurable property with default to 2.5
    
    @Override
    public Double calculateInterest(Double principal, int years) {
        return interestRate/100 * principal * years;
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }
}
```

## Referencing a Component

Listing 3: Get reference from context

```java
// Get component context
UnifyComponentContext unifyComponentContext = ...

// Get reference
LoanCalculator loanCalculator = (LoanCalculator) unifyComponentContext
        .getComponent("simple-loancalculator");

// Use reference
Double interest = loanCalculator.calculateInterest(3000.23, 5);
```

Listing 4: Configurable reference

```java
@Component("somebusinessservice")
public class SomeBusinessService implements UnifyComponent {

    @Configurable
    private LoanCalculator loanCalculator; // Automatic resolution

    public Double calculateTotalPayment(Double principal, int years) {
        // Use component reference
        Double interest = loanCalculator.calculateInterest(principal, years);
        Double totalPayment = principal + interest;
        return totalPayment;
    }
...
}
```
