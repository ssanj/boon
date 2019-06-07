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
