<h1 align="center"><img src="docs/images/unifyframework.png" alt="Unify Framework" width="360" align="center"></h1>  

<h4 align="center">A light-weight component based framework for developing web applications in Java</h4>
  
<p align="center">
<img src="https://img.shields.io/github/license/tcdng/unify-framework" alt="License" title="">
</p>

Unify Framework is a server-side component-based framework for developing web applications in Java. The entire framework is built on the concept of a pool of configurable components interacting with each other at runtime to provide complete application functionality.

The framework makes available, to the developer, a solid set of reusable and extensible software components built using simple high level abstractions that effectively hide the details of underlying complexities from the developer.

All components, from UI widgets to business logic and database components, share a common DNA allowing for the same basic simplicity across the framework. 

Components are provided for the full web application stack which makes the need for the mishmash of varying technologies very minimal.

## Features
* Inversion of Control Container
* Persistence Framework with ORM
* Transaction Management
* Business Service Framework
* Task Management
* Cluster Management with Intra-cluster Communication
* User Session Management
* File Transfer Framework
* Notification Framework
* Report Generation Framework
* Page Template Language
* Model-view-controller Framework
* Single-page Application Framework
* Rich library of user interface widgets

## Documentation

* [Documentation (Wiki)](https://github.com/tcdng/unify-framework/wiki)

## Maven

### Core libraries

```xml
<!-- Unify Core Library-->
<dependency>
    <groupId>com.tcdng.unify</groupId>
    <artifactId>unify-core</artifactId>
    <version>1.1.0</version>
</dependency>

<!-- Unify Web Library-->
<dependency>
    <groupId>com.tcdng.unify</groupId>
    <artifactId>unify-web</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Optional libraries

```xml
<!-- Embedded Jetty Server-->
<dependency>
    <groupId>com.tcdng.unify</groupId>
    <artifactId>unify-jetty</artifactId>
    <version>1.1.0</version>
</dependency>

<!-- Jasper Reports Integration-->
<dependency>
    <groupId>com.tcdng.unify</groupId>
    <artifactId>unify-jasperreports</artifactId>
    <version>1.1.0</version>
</dependency>

<!-- JCIFS Library Integration-->
<dependency>
    <groupId>com.tcdng.unify</groupId>
    <artifactId>unify-jcifs</artifactId>
    <version>1.1.0</version>
</dependency>

<!-- XChart Library Integration-->
<dependency>
    <groupId>com.tcdng.unify</groupId>
    <artifactId>unify-xchart</artifactId>
    <version>1.1.0</version>
</dependency>
```

 