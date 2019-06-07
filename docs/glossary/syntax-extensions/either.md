# Either

Imports:

```
import syntax.either._
```

## isLeft

Asserts that an Either is a Left


```scala
val eitherVal = Left[String, Boolean]("so failed")

isLeft[String, Boolean](eitherVal)
```

## left_?

Runs an Assertion on the left value of an Either

```scala
val eitherVal = Left[String, Int]("meggaerror1")

left_?[String, Int](eitherVal)(_.endsWith("or1") | "ends with 'or1'")
```

## isRight

Asserts that an Either is a Right

```scala
val eitherVal = Right[String, Boolean](false)

isRight[String, Boolean](eitherVal)
```

## right_?

Runs an Assertion on the right value of an Either

```scala
val eitherVal = Right[String, Int](20)

right_?[String, Int](eitherVal)(_ =/= 10 | "twenty is not ten")
```

## eithers

Run a multiple Assertions on multiple Either values. Matches the lengths of values and Assertions before asserting each of the values against the corresponding Assertion.

```scala
def error(value: String): Either[String, Int] = Left(value)

def success(value: Int): Either[String, Int] = Right(value)

val l1 = oneOrMore(success(1), error("e1"), success(3), success(4))

type E = Either[String, Int]


val assertions = oneOrMore(isRight(_:E), isLeft(_:E), isRight(_:E), isRight(_:E))

eithers(l1)(assertions)
```

which results in:

```
  - match lengths [✓]
  - is Right [✓]
  - is Left [✓]
  - is Right [✓]
  - is Right [✓]
```

With a failing assertion:

```scala
val assertions2 = oneOrMore(isRight(_:E), isRight(_:E), isRight(_:E), isRight(_:E))

eithers(l1)(assertions2)
```

returns:

```
  - match lengths [✓]
  - is Right [✓]
  - is Right [✗]
    => expected Right got: Left("e1")
    at ...
      #: values -> NES(Right(1),Left("e1"),Right(3),Right(4))
  - is Right [✓]
  - is Right [✓]
```
