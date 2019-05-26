#### Either ####

Imports:

```
import syntax.either._
```

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| left_? | expects Left value | <code>left_?\[String, Int\](leftValue)(_.endsWith("or1") \\| "ends with or1")</code> |
| right_? | expects a Right value | <code>right_?\[String, Int\](rightValue)(_ =/= 10 \\| "right is not ten")</code> |
