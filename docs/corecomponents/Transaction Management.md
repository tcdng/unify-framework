## Overview

A database transaction is a logical unit of work that is executed against a database
for data insertion, retrieval or updates. It consists of one or more database operations
that must be successfully completed before changes can be committed to the database.
If any of the database operations fails, there should be no persistent effect, on the database, whatsoever.
Thus, database transactions are utilized for consistency and to effectively maintain the integrity of data stored in the database.

Unify framework makes available mechanisms for managing transactions including support for transaction propagation control, nested transactions, cross-database transactions and savepoints. These mechanisms, provided by the _DatabaseTransactionManager_ component, are integrated with the platform's persistence framework and business service framework.

<img src="images/corecomponents/txnmanagementcomponents.png" alt="Transaction Management Components" width="357" align="center">

Figure 1.0 Transaction management components

You apply transaction management to your applications by:
* Manipulating the _DatabaseTransactionManager_ and _Database_ components programmatically
* Utilizing the integration with _BusinessService_ components using dedicated Java annotations

## Programmatic Transaction Management

Perform transaction management programmatically using the _DatabaseTransactionManager_ component.

Listing 1: Programmatic transaction management

```java
// Get transaction manager
DatabaseTransactionManager tm = ...

// Get database
Database db = ...

// Records to create
Author author = ...
List<Book> bookList = ...

// Perform transaction
tm.beginTransaction(); //Start database transaction
try {
    // Execute database operations
    Long authorId = (Long) db().create(author);
    for (Book book: bookList) {
        book.setAuthorId(authorId);
        db().create(book);
    }
} catch(Exception e) {
    tm.setRollback(); // Rollback entire transaction on exception
    throw e;
} finally {
    tm.endTransaction(); // End database transaction
}
```

Listing 2: Programmatic transaction management with savepoint

```java
// Get transaction manager
DatabaseTransactionManager tm = ...

// Get database
Database db = ...

// Perform transaction with save point
tm.beginTransaction();
try {
    // Execute database operations
    db.create(apple);
    tm.setSavePoint(); // Savepoint

    db.create(new Fruit("banana", "yellow", 45.00));
    ...
} catch(Exception e) {
    tm.rollbackToSavePoint(); // Rollback transaction to savepoint
    throw e;
} finally {
    tm.endTransaction();
}
```

## Transaction Management using Annotation

Annotation-based transaction management is implemented using _BusinessService_ methods and _@Transactional_ annotation.
Method entry and exit points serve as database transaction boundaries.

Listing 3: Annotation on business service component

```java
@Transactional
@Component("fruitstore-service")
public class FruitStoreServiceImpl extends AbstractBusinessService
    implements FruitStoreService {
    
    @Override
    public List<Fruit> findFruits(Query<Fruit> query) throws UnifyException {
        // Execute database operations
        return db().findAll(query;
    }
...
}
```

Listing 4: Annotation directly on business service method

```java
@Component("library-service")
public class LibraryServiceImpl extends AbstractBusinessService
    implements LibraryService {
    
    @Override
    @Transactional
    public Long createBooks(Author author, List<Book> bookList)
        throws UnifyException {
        // Execute database operations
        Long authorId = (Long) db().create(author);
        for (Book book: bookList) {
            book.setAuthorId(authorId);
            db().create(book);
        }
        
        // Return author ID
        return authorId;
    }
...
}
```

## Supported Transaction Propagation

| Propagation | Description |
|-------------|-------------|
| REQUIRED | A new transaction is created for method if caller has no transaction otherwise method joins caller’s transaction. |
| REQUIRES_NEW | Always creates a new transaction for method and suspends the caller’s transaction if it has one. |
| SUPPORTS | Method joins the caller’s transaction if it has one otherwise no transaction is used. |
| NOT_SUPPORTED | Method does not use any transaction. Caller’s transaction is suspended if it has one. |
| MANDATORY | Method joins caller’s transaction. Caller must have a transaction otherwise an exception is thrown. | 
| NEVER | Method does not use any transaction. Caller must not have a transaction otherwise an exception is thrown. |


