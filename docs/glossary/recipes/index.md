boon has a very minimal syntax. Can we still write Assertions that compare with more feature-rich testing frameworks like Specs2 and ScalaTest?

Here are some common Assertions:

## Two collections have the same elements irrespective of order

```scala
val l1 = List(1,2,3,4,5)
val l2 = l1.reverse

l1.toSet =?= l2.toSet | "same elements"
```

## Two collections have some of the same elements

```scala
val l1 = List(1,2,3,4,5)
val l2 = List(3,5)

l2.forall(l1.contains) | "some of the same elements"
```

## A collection has a Left value

```scala
val values = List(1,2,3,4,5).map(x => if (x % 2 == 0) Right(x) else Left(x))

values.exists(_.isLeft)  | "has a Left"
```

## A collection has a Left and Right value

```scala
val values = List(1,2,3,4,5).map(x => if (x % 2 == 0) Right(x) else Left(x))

values.exists(_.isLeft)  | "has a Left" and
values.exists(_.isRight) | "has a Right"
```

## A collection that has Left and Right values in particular positions

```scala
import syntax.either._
import syntax.nes._

def error(value: String): Either[String, Int] = Left(value)

def success(value: Int): Either[String, Int] = Right(value)

val l1 = oneOrMore(success(1), error("e1"), success(3), success(4))

type E = Either[String, Int]


val assertions = oneOrMore(isRight(_:E), isLeft(_:E), isRight(_:E), isRight(_:E))

positional[E](l1)(assertions)
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

positional[E](l1)(assertions2)
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

## A String matches a Regular expression

```scala
import syntax.regex._

val message = """HTTP/1.1 400: {"message":"body cannot be converted to Test: CNil: El(DownField(myValue)"}"""
val regex = """HTTP/1\.1 400.*DownField\(myValue\)""".r
message =^= regex | "expect decode error"
```

## An Exception matches on type and Message

```scala
import syntax.exception._

List.empty[String].head =!=[NoSuchElementException](_ =?= "head of empty list" | "head on empty List")
```

## An Option matches on content

```scala
import syntax.option._

some_?[String](Option("Hello"))(_ =?= "Hello" | "some value")
```

## A custom Assertion that matches a number within a bounds



```scala
import model.Predicate

def between(value: Int)(lower: Int, upper: Int): Predicate[Boolean] =
  value >= lower && value <= upper
```

Usage:

```scala
between(10)(20, 30) | "10 is between 1 and 20"
```

outputs:

```
- 10 is between 1 and 20 [✗]
  => false != true
```

Now if we want a better error message we can craft our own:

```scala
 def between(value: Int)(lower: Int, upper: Int) = {
  val message = oneOrMore(s"$value is not between ${lower} and ${upper}")
  (value >= lower && value <= upper) >> (message, Replace)
 }
```

and now when we run it, it outputs:

```
- 10 is between 1 and 20 [✗]
  => 10 is not between 20 and 30
```