#### Try ####

Imports:

```
import syntax.`try`._
```

If you want to match Failures with the exception syntax also include:

```
import syntax.exception._
```

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| failure_? | expects Failure value | <code>val tryVal1 = Try(throw new RuntimeException("Ooops!"))<br/><br/>failure_?(tryVal1)(_ =!=\[RuntimeException\](_ =?= "Ooops!" \\| "ex message"))</code> |
| success_? | expects a Success value | <code>success_?(tryVal1)(_ =?= "hello" \\| "String success")</code> |