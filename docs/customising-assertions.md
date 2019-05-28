## Customising Assertions

If you want to customise the equality or difference function used on a Predicate use the `|?` function:

```scala
import boon._
import boon.model.Equality.genEq
import boon.model.Difference

val diff = Difference.from[String]((v1, v2) => oneOrMore(s"$v1 is not the same as $v2"))

"Hello" =?= "Yellow" |? ("greeting", diff, genEq, noContext)
```

which will result in:

```bash
   - greeting [✗]
     => Hello is not the same as Yellow
```
