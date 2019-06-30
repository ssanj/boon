# collection

Imports:

```
import syntax.collection._
```


## positional

Run Assertions on values in corresponding positions of a `NonEmptySeq`. Matches the lengths of values and Assertions before asserting each of the values against the corresponding Assertion.

```scala
positional(oneOrMore(1,2,3,4), "values")(oneOrMore(_ =?= 1 | "one", _ =?= 2 | "two", _ =?= 3 | "three", _ =?= 4 | "four"))
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
positional(oneOrMore(1,2,3,4), "values")(oneOrMore(_ =?= 1 | "one", _ =?= 3 | "two", _ =?= 3 | "three", _ =?= 1 | "four"))
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
positionalSeq(Vector(1,2,3,4), "values")(oneOrMore(_ =?= 1 | "one", _ =?= 3 | "two", _ =?= 3 | "three", _ =?= 1 | "four"))
```

## positionalMap

Runs Assertions on a Map. Assertions will be run in the order specified by the `Ordering` of the Map key.

```scala
val m1 = Map("Bilbo" -> List("Ring"), "Sam" -> List("Bread", "Cheese"), "Frodo" -> List("Biscuits"))


positionalMap(m1,"hobbits")(oneOrMore(
  (k,v) => k == "Bilbo" && v.contains("Ring") | "Bilbo has the ring",
  (k,v) => k == "Frodo" && v.contains("Biscuits") | "Frodo has biscuits",
  (k,v) => k == "Sam" && Seq("Bread", "Cheese").forall(v.contains) | "Sam has bread and cheese"
))
```

When run returns:

```
  - hobbits has length of 3 [✓]
  - hobbits(0).Bilbo has the ring [✓]
  - hobbits(1).Frodo has biscuits [✓]
  - hobbits(2).Sam has bread and cheese [✓]
```

with a failing Assertion:

```scala
positionalMap(m1,"hobbits")(oneOrMore(
  (k,v) => k == "Bilbo" && v.contains("Flute") | "Bilbo has the flute",
  (k,v) => k == "Frodo" && v.contains("Ring") | "Frodo has ring",
  (k,v) => k == "Sam" && Seq("Bread", "Cheese").forall(v.contains) | "Sam has bread and cheese"
))
```

when run:

```
  - hobbits has length of 3 [✓]
  - hobbits(0).Bilbo has the flute [✗]
    => false != true
    at ...
      #: expected value at hobbits(0) -> ("Bilbo", List("Ring"))
         values -> (hobbits(0) -> ("Bilbo", List("Ring")), hobbits(1) -> ("Frodo", List("Biscuits")), hobbits(2) -> ("Sam", List("Bread", "Cheese")))
  - hobbits(1).Frodo has ring [✗]
    => false != true
    at ...
      #: expected value at hobbits(1) -> ("Frodo", List("Biscuits"))
         values -> (hobbits(0) -> ("Bilbo", List("Ring")), hobbits(1) -> ("Frodo", List("Biscuits")), hobbits(2) -> ("Sam", List("Bread", "Cheese")))
  - hobbits(2).Sam has bread and cheese [✓]
```