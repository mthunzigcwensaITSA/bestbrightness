# Best Brightness POS

Java Swing point-of-sale system for Best Brightness cleaning products.

## Features
- User login
- Product management
- Sales cart with stock validation
- 10% discount for totals of R500 or more
- Receipt generation
- SQLite database storage for users, products, sales, and sale items

## First-Time Setup
- Username: `admin`
- On the first GUI launch, the application prompts you to create the initial admin password.
- For headless or automated startup, set `BEST_BRIGHTNESS_ADMIN_PASSWORD` before running the app.

## Run
This is a Maven project that can be opened directly in NetBeans.

```bash
mvn test
BEST_BRIGHTNESS_ADMIN_PASSWORD=your-password mvn exec:java
```

On first run the application creates `bestbrightness.db` in the project directory.
