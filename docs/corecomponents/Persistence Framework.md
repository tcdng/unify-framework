## Overview

The persistence framework provides components for storing and maintaining data in a relational database. Data is organized in table structures that have relationships with one another. Each table has a set of columns and represents a business entity with a corresponding set of attributes. An instance of a particular entity is stored as a row – a data record in the equivalent table. Persistence operations also allows rows to be created, retrieved, updated and deleted.

The framework allows you to map a Java class and its fields, as an entity definition, to a database table and its columns respectively.

Actual database servers are connected to by datasource components using appropriate database dialect components. 

<img src="images/corecomponents/databasecomponents.png" alt="Database Components" width="587" align="center">

Figure 1.0 Database components

## Entity Definition

An entity class can be mapped to a database table, view or a table-view combination. You define an entity class by annotating a Java bean class that implements the _Entity_ interface with the _@Table_ or _@View_ annotation and using other supporting annotations like _@Id_, _@Column_  and _@ListOnly_ to specify persistent properties.

### Table Mapping

_Author_ entity class maps to 'AUTHOR' table.

```java
@Table
public class Author extends AbstractSequencedEntity {

    @Column
    private String name;

    @Column
    private String telephone;

    @Column
    private String email;
    
    public Author(String name, String telephone, String email) {
        this.name = name;
        this.telephone = telephone;
        this.email = email;
    }
    
    public Author() {
    
    }

    ... (getters/setters)
}
```

```sql
CREATE TABLE AUTHOR(
    AUTHOR_ID BIGINT PRIMARY KEY NOT NULL,
    NAME VARCHAR(32) NOT NULL,
    TELEPHONE VARCHAR(32) NOT NULL,
    EMAIL VARCHAR(32) NOT NULL
)
```

### Table-view Mapping

_Book_ entity class maps to 'BOOK' table and 'V_BOOK' view.

```java
@Table
public class Book extends AbstractSequencedEntity {

    @ForeignKey(Author.class)
    private Long authorId;

    @Column
    private String title;

    @Column
    private String genre;

    @Column
    private Double price;

    @ListOnly(key = "authorId", property = "name")
    private String authorName;

    ... (getters/setters)
}
```

```sql
CREATE TABLE BOOK(
    BOOK_ID BIGINT PRIMARY KEY NOT NULL,
    AUTHOR_ID BIGINT NOT NULL,
    TITLE VARCHAR(32) NOT NULL,
    GENRE VARCHAR(32) NOT NULL,
    PRICE FLOAT NOT NULL,
)
```

```sql
CREATE VIEW V_BOOK(
    BOOK_ID, AUTHOR_ID, TITLE, GENRE, PRICE, AUTHOR_NAME
) AS SELECT T1.BOOK_ID, T1.AUTHOR_ID, T1.TITLE, T1.GENRE, T1.PRICE, T2.NAME
FROM BOOK T1 LEFT JOIN AUTHOR T2 ON T2.AUTHOR_ID = T1.AUTHOR_ID;
```

### Managed View Mapping

_BookView_ entity class maps to 'V_BOOK' view.

```java
@View(
    name = "V_BOOK", primaryAlias = "t1",
    tables = {
        @TableRef(alias = "t1", entity = Book.class),
        @TableRef(alias = "t2", entity = Author.class) },
    restrictions = {
        @ViewRestriction(type = RestrictionType.EQUALS,
        leftProperty = "t1.authorId", rightProperty = "t2.id")})
public class BookView extends AbstractEntity {

    @Id
    private Long bookId;

    @ListOnly(property = "t1.title")
    private String bookTitle;

    @ListOnly(property = "t1.genre")
    private String bookGenre;

    @ListOnly(property = "t1.price")
    private Double bookPrice;

    @ListOnly(property = "t2.id")
    private Long authorId;

    @ListOnly(property = "t2.name")
    private String authorName;
...
}
```

```sql
CREATE VIEW V_BOOK(
    BOOK_ID, AUTHOR_ID, BOOK_TITLE, BOOK_GENRE, BOOK_PRICE, AUTHOR_NAME
) AS SELECT T1.BOOK_ID, T1.AUTHOR_ID, T1.TITLE, T1.GENRE, T1.PRICE, T2.NAME
FROM BOOK T1 LEFT JOIN AUTHOR T2 ON T2.AUTHOR_ID = T1.AUTHOR_ID;
```

## Database Operations
### Creating Database Records

Create a database record by passing a corresponding entity class instance to _Database_ component _create()_ method.

```java
//Get database
Database db = ...

//Prepare entity instance
Author author =
    new Author("Paul Horowitz", "+12345678", "paul.horowitz@castle.com");

// Create record
Long id = (Long) db.create(author);
```

Entity class instances just hold data and are not tied to table rows. Create multiple records using the same object. 

```java
//Get database
Database db = ...

//Create multiple records using same object
Author author = new Author();
while(...data available...) {
    String name = ...
    String telephone = ...
    String email = ...
    
    author.setName(name);
    author.setTelephone(telephone);
    author.setEmail(email);
    
    db.create(author);
}
```

### Retrieving Database Records

Retrieve a single record by ID.

```java
//Get database
Database db = ...

// Retrieve record by ID
Long bookId = ...
Book book1 = db.find(Book.class, bookId); // Fetch table fields only
Book book2 = db.list(Book.class, bookId); // Fetch table and view fields
```

Retrieve a single record by query.

```java
//Get database
Database db = ...

// Retrieve record with title 'Art of Electronics'
Query<Book> query = Query.of(Book.class)
                    .addEquals("title", "Art of Electronics");
Book book1 = db.find(query); // Fetch table fields only
Book book2 = db.list(query); // Fetch table and view fields
```

Retrieve multiple records by query

```java
//Get database
Database db = ...

// Retrieve 'sci-fi' books with price above 10.50
Query<Book> query = Query.of(Book.class)
                    .addEquals("genre", "sci-fi")
                    .addGreaterThan("price", Double.valueOf(10.50));
List<Book> bookList1 = db.findAll(query); // Fetch table fields only
List<Book> bookList2 = db.listAll(query); // Fetch table and view fields
```

Field retrieval map for various property types and methods.

| Method | @Id | @Version | @Column | @ListOnly | @Child | @ChildList |
|--------|-----|----------|---------|-----------|--------|------------|
| find() | x | x | x |   | x | x |
| findAll() | x | x | x |   |   |   |
| findLean() | x | x | x |   |   |   |
| list() | x | x | x | x | x | x |
| listAll() | x | x | x | x  |   |   |
| listLean() | x | x | x | x |   |   |


### Updating Database Records

Update an entire record by ID.

```java
//Get database
Database db = ...

// Update author record by ID
Long authorId = ...

Author author = db.find(Author.class, authorId);
author.setName("Helon Habila");
author.setTelephone("+234802345678");
author.setEmail("helon.habila@writers.com");

db.updateById(author);
```

Update specific columns in a record by ID.

```java
//Get database
Database db = ...

// Update author email and telephone by ID
Long authorId = ...

db.updateById(Author.class, authorId,
        new Update().add("email", "tom.jones@music.com")
            .add("telephone", "+12993884774"));
```

Update multiple records that match certain criteria.

```java
//Get database
Database db = ...

// Update price of all 'horror' books with price less 24.25
// to 30.00
Query<Book> query = Query.of(Book.class)
                    .addEquals("genre", "horror")
                    .addLessThan("price", Double.valueOf(24.25));
db.updateAll(query, new Update().add("price", 30.00));
```

### Deleting Database Records

Delete a record by ID.

```java
//Get database
Database db = ...

// Update author record by ID
Long authorId = ...
db.delete(Author.class, authorId);
```

Delete multiple records that match certain criteria.

```java
//Get database
Database db = ...

// Delete all books with title like 'The Sky'.
db.deleteAll(Query.of(Book.class)
                    .addLike("title", "The Sky"));
```

## Datasource Configuration

You can configure a datasource in the unify container configuration file __'unify.xml'__.

```xml
<component name="application-datasource"
    class="com.tcdng.unify.core.database.sql.SqlDataSourceImpl">
    <properties>
        <property name="driver" value="org.hsqldb.jdbcDriver" />
        <property name="connectionUrl" value="jdbc:hsqldb:hsql://<HOST>/<DATABASE>" />
        <property name="appSchema" value="PUBLIC" />
        <property name="username" value="<USERNAME>" />
        <property name="password" value="<PASSWORD>" />
        <property name="dialect" value="hsqldb-dialect" />
    </properties>
</component>
```