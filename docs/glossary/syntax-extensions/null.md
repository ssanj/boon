# null

Imports:

```
import syntax.nulls._
```

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| null_? | expects a null value | <code>null_?(nullValue)(pass \\| "null value")</code> |
| null_! | expects a non-null value | <code>null_!(notNullString)(_  =?= "notNull" \\| "not null String" )</code> |