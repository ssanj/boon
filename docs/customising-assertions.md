# Customising Assertions

## Customising Output

Sometimes we want to customise the failure messages of an Assertion. Given the following:

```scala
!List.empty[String].isEmpty | "empty List is empty"
```

we get the following when run:

```
   - empty List is empty [✗]
     => false != true
```

If we don't like this message we can easily change it completely or add some extra information to it.

With any Predicate, you can use the `>>` operator to completely replace (*Replace*) the failure messages with something more descriptive:

```scala
!List.empty[String].isEmpty >> (oneOrMore("empty List is not empty", "I expected empty!"), Replace) | "empty List is empty"
```

which results in:

```bash
   - empty List is empty [✗]
     => empty List is not empty
        I expected empty!
```

You can also add (*Append*) some additional messages to the original failed output:

```scala
!List.empty[String].isEmpty >> (oneOrMore("empty List is not empty", "I expected empty!"), Append) | "empty List is empty"
```

which results in:

```bash
   - empty List is empty [✗]
     => false != true
        empty List is not empty
        I expected empty!
```

## Using a Custom Difference and Equality

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
