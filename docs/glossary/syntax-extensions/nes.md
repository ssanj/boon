# NonEmptySeq

Imports:

```
import syntax.nes._
```


## positional

Run Assertions on values in corresponding positions. Matches the lengths of values and Assertions before asserting each of the values against the corresponding Assertion.

```scala
positional[Int](oneOrMore(1,2,3,4))(oneOrMore(_ =?= 1 | "one", _ =?= 2 | "two", _ =?= 3 | "three", _ =?= 4 | "four"))
```

When run results in:

```
  - match lengths [✓]
  - one [✓]
  - two [✓]
  - three [✓]
  - four [✓]
```

with a failing Assertion:

```scala
positional[Int](oneOrMore(1,2,3,4))(oneOrMore(_ =?= 1 | "one", _ =?= 3 | "two", _ =?= 3 | "three", _ =?= 1 | "four"))
```

When run returns:

```
  - match lengths [✓]
  - one [✓]
  - two [✗]
    => 2 != 3
    at ...
      #: values -> NES(1,2,3,4)
  - three [✓]
  - four [✗]
    => 4 != 1
    at ...
      #: values -> NES(1,2,3,4)
```