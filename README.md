<h1 align="center"><img src="docs/images/unifyframework.png" alt="Unify Framework" width="360" align="center"></h1>  

<h4 align="center">A light-weight component based framework for developing web applications in Java</h4>
  
<p align="center">
<img src="https://img.shields.io/github/license/tcdng/unify-framework" alt="License" title="">
</p>

Unify Framework is a full-stack server-side component-based framework for developing web applications in Java. The entire framework is built on the simple concept of a pool of configurable components interacting with each other at runtime to provide complete application functionality.

The pool of components is maintained by a lightweight container that provides the necessary context for components to interact. The container, an inversion of control container, manages for the most part, the instantiation, configuration, initialization, pooling and termination of components. It does not provide any other major system functionality. All other core system functionality like transaction management, data persistence, task scheduling and intra-cluster communication, for instance, are provided indirectly by specialized components and not by the container itself.

The framework makes available a pre-defined set of components that can be used or extended to develop your applications.

The fundamental classes for the container and pre-defined components are provided in the two main framework libraries: unify-core and unify-web.

### Unify Core
* Dependency Injection
* Persistence Framework with ORM
* Transaction Management
* Business Service Framework
* Task Management
* Cluster Management with Intra-cluster Communication
* User Session Management
* File Transfer Framework
* Notification Framework
* Report Generation Framework
* Unify Page Language

### Unify Web
* Model-view-controller Framework
* Single-page Application Framework
* Rich library of user interface widgets
  