# collection

Imports:

```
import syntax.collection._
```


## positional

Run Assertions on values in corresponding positions of a `NonEmptySeq`. Matches the lengths of values and Assertions before asserting each of the values against the corresponding Assertion.

```scala
positional[Int](oneOrMore(1,2,3,4), "values")(oneOrMore(_ =?= 1 | "one", _ =?= 2 | "two", _ =?= 3 | "three", _ =?= 4 | "four"))
```

with a successful Assertion:

```
  - values has length of 4 [✓]
  - values(0).one [✓]
  - values(1).two [✓]
  - values(2).three [✓]
  - values(3).four [✓]
```

with a failing Assertion:

```scala
positional[Int](oneOrMore(1,2,3,4), "values")(oneOrMore(_ =?= 1 | "one", _ =?= 3 | "two", _ =?= 3 | "three", _ =?= 1 | "four"))
```

When run returns:

```
  - values has length of 4 [✓]
  - values(0).one [✓]
  - values(1).two [✗]
    => 2 != 3
    at ...
      #: expected value at values(1) -> 2
       values -> (values(0) -> 1, values(1) -> 2, values(2) -> 3, values(3) -> 4)
  - values(2).three [✓]
  - values(3).four [✗]
    => 4 != 1
    at ...
      #: expected value at values(3) -> 4
         values -> (values(0) -> 1, values(1) -> 2, values(2) -> 3, values(3) -> 4)
```

## positionalSeq

positionalSeq is the same as positional only with the input value being a `Seq` instead of a `NonEmptySeq`

```scala
positional[Int](Vector(1,2,3,4), "values")(oneOrMore(_ =?= 1 | "one", _ =?= 3 | "two", _ =?= 3 | "three", _ =?= 1 | "four"))

```

## positionalMap

