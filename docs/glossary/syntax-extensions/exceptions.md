# Exceptions

Imports:

```
import syntax.exception._
```

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| =!=  | Compares an Exception thrown by class and message | <code>flakey =!=[RuntimeException](_ =?= "Boom!" \\| "expected Boom!")</code> |
|   | Also compares an Exception by class and message | <code>ex =!=[RuntimeException](_ =?= "Boom!" \\| "expected Boom!")</code> |
