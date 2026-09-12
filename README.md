# Best Brightness POS

Java Swing point-of-sale system for Best Brightness cleaning products.

## Features
- User login
- Product management
- Sales cart with stock validation
- 10% discount for totals of R500 or more
- Receipt generation
- SQLite database storage for users, products, sales, and sale items

## Default Login
- Username: `admin`
- Password: `admin123`

## Run
This is a Maven project that can be opened directly in NetBeans.

```bash
mvn test
mvn exec:java
```

On first run the application creates `bestbrightness.db` in the project directory.
