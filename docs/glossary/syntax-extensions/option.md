#### Option ####

Imports:

```
import syntax.option._
```


| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| some_? | expects a Some(value) | <code>some_?\[Int\](20.some)(_ =/= 10  \\| "Some(20) is not ten")</code>  |
| none_? | expects a None | <code>none_?\[Int\](noneValue)(pass \\| "expected None")</code> |
