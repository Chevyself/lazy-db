LazyDB [![](https://jitpack.io/v/Chevyself/lazy-db.svg)](https://jitpack.io/#Chevyself/lazy-db)
===

LazyDB is a library designed to simplify database operations, making easier to work with both SQL and MongoDB in Java. The project aims to provide a higher level of abstraction for database operations, allowing a more intuitive and streamlined experience for developers.

---

Modules
---

### SQL

The SQL module focuses on enhancing the usage of SQL databases. It allows to provide SQL code from files using `LazySchema`, set the id of elements with `SQLElement`.

### jsongo

The lazy-jsongo module simplifies working with MongoDB in Java. It addresses some limitations of the native MongoDB Java driver's codec system by leveraging the popular Gson library. lazy-jsongo allows for effortless conversion between Java objects and JSON, making it easier to work with MongoDB documents. The module aims to provide a more intuitive and flexible approach to MongoDB integration in Java, reducing the learning curve and development time.

Getting started
---

To start using the library, add the following dependency to your project:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

If you wish to use the sql module, add the following dependency:

```xml
<dependency>
    <groupId>com.github.chevyself.lazy-db</groupId>
    <artifactId>sql</artifactId>
    <version>VERSION</version>
</dependency>
```

If you wish to use the jsongo module, add the following dependency:

```xml
<dependency>
    <groupId>com.github.chevyself.lazy-db</groupId>
    <artifactId>jsongo</artifactId>
    <version>VERSION</version>
</dependency>
```

> Replace VERSION with the one you want to use, check out [JitPack](https://jitpack.io/#Chevyself/lazy-db) for more information.

Documentation
---

You can read the latest JavaDoc in [JitPack](https://javadoc.jitpack.io/com/github/Chevyself/lazy-db/latest/javadoc/index.html)

Contributing
---

Contributions to lazy-db are welcome! If you encounter any issues, have ideas for improvements, or would like to contribute new features, please open an issue or submit a pull request on the GitHub repository.

License
---

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.