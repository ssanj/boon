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
import boon.model._
import Equality.genEq

val diff = Difference.from[String]((v1, v2, et) => {
    val equalityType = et match {
      case IsEqual    => "is not the same as"
      case IsNotEqual => "is the same as"
    }
    oneOrMore(s"$v1 $equalityType $v2")
  }
)
```

When asserting values are the same with `=?=` :

```scala
"Hello" =?= "Yellow" |? ("greeting", diff, genEq, noContext)
```

will result in:

```bash
   - greeting [✗]
     => Hello is not the same as Yellow
```

Or when asserting a difference with `=/=` :

```scala
"Hello" =/= "Hello" |? ("greeting", diff, genEq, noContext)
```

will result in:

```bash
   - greeting [✗]
     => Hello is the same as Yellow
```
