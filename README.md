# boon [![Build Status](https://travis-ci.org/ssanj/boon.svg?branch=master)](https://travis-ci.org/ssanj/boon) [ ![Download](https://api.bintray.com/packages/ssanj/maven/boon/images/download.svg) ](https://bintray.com/ssanj/maven/boon/_latestVersion)

boon is a small framework for testing pure code. boon is:

1. Opinionated
1. Focuses on testing pure code
1. Has no external library dependencies
1. Fast
1. Strongly typed

boon is inspired by [ScalaCheck](https://www.scalacheck.org) - which is a simple but very powerful Property-Based Testing framework.

Some things that are unique to boon:

1. Purity - test failures don't throw Exceptions
1. First Class Assertions - Assertions can be named and combined
1. Failure context - Failures can have write out a context of all useful information
1. Two failure modes - Assertions can either fail on the first error (**Seq**uential) or continue running other Assertions (**Ind**ependent)

## Usage in SBT ##

Add the following to your `build.sbt` file:

```scala
libraryDependencies += "net.ssanj" %% "boon" % "0.0.1-b32" % Test

testFrameworks += new TestFramework("boon.sbt.BoonFramework")

resolvers += Resolver.bintrayRepo("ssanj", "maven")
```

You can now run all the usual `sbt` test commands such as: `test` and `testOnly`.

## API

### Predicate

At the heart of boon, is a **Predicate**. A predicate is a boolean expression. The most basic way of creating a predicate in boon, is by testing two values for equality:

```scala
actualValue =?= expectedValue
```

_The `=?=` operator is a typesafe equality operator. See [Operators](#operators) for additional operators_.

For example to test two Int operands for equality:

```scala
1 =?= 1
```

### Assertion

A predicate with a description is an **Assertion**:

```scala
actualValue =?= expectedValue | "description"
```

For example:

```scala
1 =?= 1 | "one is one"
```

Assertions are first class constructs in boon and can be combined with the `and` method to give even more Assertions:

```scala
1 =?= 1             | "Int equality"    and
"Hello" =?= "Hello" | "String equality" and
true =?= true       | "Boolean equality"
```

### Test

**Assertions** are grouped into a **Test**:

```scala
test("equality of things") {
  1 =?= 1             | "Int equality"    and
  "Hello" =?= "Hello" | "String equality" and
  true =?= true       | "Boolean equality"
}
```

### Suite

**Tests** are grouped into a **Suite**. All Suites must follow these rules:
- Must be an `object` - This prevents inheritance abuse
- Extend `SuiteLike`
- Override the `tests` method

Here is an outline of a simple **Suite**:

```scala
object MySuite extends SuiteLike("Stuff I want to test") {

  val test1 =

  val test2 =
  ..
  val testn =

  override def tests = oneOrMore(test1, test2 ... testn)
}
```

A more complete example:

```scala
package example

import boon._

object MyFirstSuite extends SuiteLike("Simple Stuff") {

  private val t1 = test("equality of things") {
    1 =?= 1             | "Int equality"    and
    "Hello" =?= "Hello" | "String equality" and
    true =?= true       | "Boolean equality"
  }

  override def tests = oneOrMore(t1)
}
```

As shown above, to define a Suite, you need the following import:

```scala
import boon._
```

The `tests` method on `SuiteLike` is defined as:

```
def tests: NonEmptySeq[Test]
```

`NonEmptySeq` is a collection that must have at least one element. It is similar to `NonEmptyList` in [Cats](https://typelevel.org/cats/datatypes/nel.html) and [Scalaz](https://scalaz.github.io/scalaz/scalaz-2.10-7.0.4/doc/index.html#scalaz.NonEmptyList). It can be constructed using the `oneOrMore` function for when you have one or more tests. Requiring a `NonEmptySeq` when defining a Suite means that you can't create a Suite without any tests. This is something that is possible in other testing frameworks and leads to confusion when an empty Suite passes (since it has no tests). boon strives to ensure that invalid states can't be represented.

Running the above **Suite** produces the following output:

![boon-output](images/boon-my-first-suite.png)

Let's update our String equality predicate so it fails:

```scala
"Hello" =?= "Yello" | "String equality"
```

Now when we run the Suite it produces the following output:

![failure-output](images/boon-my-first-suite-failure.png)

### Booleans are Predicates

Any `Boolean` expression can be turned into a Predicate. That Predicate can then be made an Assertion:

```scala
List.empty[String].isEmpty | "empty List is empty"
```

The main difference is that if the Assertion fails you get a Boolean failure not a type-specific failure:

```bash
[info]    - empty List is empty [✗]
[info]      => false != true
```

With a Predicate, you can use the `>>` operator to override the failure message to something more descriptive:

```scala
List.empty[String].isEmpty >> oneOrMore("empty List is not empty", "I expected empty!") | "empty List is empty"
```

Results in:

```bash
[info]    - empty List is empty [✗]
[info]      => empty List is not empty
[info]         I expected empty!
```

### Block Assertions

If you want to run multiple Assertions on an expression you can do so with the `%@` operator:

```scala
%@(List(1, 2, 3, 4, 5)) { list =>
  list.sum =?= 15            | "list sum" and
  list.take(2) =?= List(1,2) | "list take"
}
```

## Extensions

To use boon with your own custom types, you need three functions:

1. `(T, T) => Boolean` - defines how two values of a type `T` are equated This is similar to the Cats [Eq](https://typelevel.org/cats/typeclasses/eq.html) typeclass.
1. `T => String` - defines how an instance of type `T` is displayed. This is similar to the Cats [Show](https://typelevel.org/cats/typeclasses/show.html) typeclass
1. `(T, T) => NonEmptySeq[String]` -  defines how the differences between two instances of type `T` are displayed on failure

These three functions are bundled into the `BoonType` typeclass.

For instance, given a `Person` class:

```scala
final case class Name(value: String)
final case class Age(value: Int)
final case class Person(name: Name, age: Age)
```

you could use one of the helper functions on `BoonType` to generate a `default` instance:

```scala
implicit val personBoonType = BoonType.defaults[Person]
```

What `BoonType.defaults` does is to use scala's `==` for equality, `.toString` for display Strings and default difference of `t1 != t2`.

After defining the above you can make Assertions on Person instances:

```scala
val p1 = Person(Name("Royd Eris"), Age(30))
val p2 = Person(Name("Royd Eris"), Age(30))
val p3 = Person(Name("Melantha Jhirl"), Age(26))

p1 =?= p2 | "Person instances with same data are equal" and
p2 =/= p3 | "Person instances with different data are not equal" and
```

If we change `p1 =?= p3` we get:

```bash
[info]    - Person instances with same data are equal [✗]
[info]      => Person(Name(Royd Eris),Age(30)) != Person(Name(Melantha Jhirl),Age(26))
```

If you don't want to use default instances, you can use one of the many methods on [BoonType](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/BoonType.scala) to create `BoonType` instances.

## Glossary ##

### Operators ###

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| =?=  | Typesafe Predicate for equality  | 1 + 2 =?= 3 |
| =/=  | Typesafe Predicate for inequality  | 1 + 2 =/= 4 |
| \\|   | Converts a Predicate to an Assertion | 1 + 2 =?= 3 \\| "addition" |
| %@  | Multiple Assertions on a single value | %@(List(1,2,3)){ l => <br> &nbsp;&nbsp;l.length =?= 5 \\| "length" and <br>&nbsp;&nbsp;l.contains(2) &nbsp;&nbsp;&nbsp;\\| "has 2" <br>} |

### Methods ###

| Method  | What it's for | Example |
| ------------- | ------------- | ------------- |
| and  | Combine Assertions | 1 + 2 =?= 3 \\| "1+2" and <br>4 + 5 =?= 9 \\| "4+5" |
| fail | Fail an Assertion | fail("reason") \| "assertion name" |
| pass | Pass an Assertion | pass \| "assertion name" |
| test | create a Test | <code>test(name) {<br>&nbsp;&nbsp;one or more assertions<br>}</code> |

---

### More Operators ###

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| \\|   | Also adds a context to an Assertion. *The context is displayed when an assertion fails* | x * y =?= 3 \\|("multiplication", "x" -> x.toString, "y" -> y.toString)  |
| >> | Provides custom errors on failure | 1 =?= 2 >> oneOrMore("error1","error2") |


### More Methods ###

| Method  | What it's for | Example |
| ------------- | ------------- | ------------- |
| xtest | ignore a test | <code>xtest(name) {<br>&nbsp;&nbsp;one or more assertions<br>}</code> |
| truthTable | truth table for a tabulated test | <code>val multTable = truthTable(<br>&nbsp;&nbsp;(1, 4)&nbsp;&nbsp;&nbsp;-> tval(4),<br>&nbsp;&nbsp;(2, 6)&nbsp;&nbsp;&nbsp;-> tval(12),<br>&nbsp;&nbsp;(5, 10)&nbsp;&nbsp;-> tval(50),<br>&nbsp;&nbsp;(7, 7)&nbsp;&nbsp;&nbsp;-> tval(49),<br>&nbsp;&nbsp;(-2, -1) -> tval(2),<br>&nbsp;&nbsp;(10, 20) -> tval(200)<br>)</code> |
| tval | truth table value | <code>(2, 6) -> tval(12)</code> |
| table | tabulated test | <br>table[(Int, Int), Int]("Multiplication", multTable)(n => n._1 * n._2)</code> |
| oneOrMore | create a NonEmptySeq | <code>override val tests = oneOrMore(test1, test2)</code> |
---

### Syntax Extensions ###

Syntax extensions are more for ease-of-use than something that must be used. It's pretty easy to write these extensions yourself. The following extensions have been created to save you even more time.

#### Exceptions ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| =!=  | Compares an Exception thrown by class and message | <code>flakey =!=[RuntimeException](_ =?= "Boom!" \\| "expected Boom!")</code> |
|   | Also compares an Exception by class and message | <code>ex =!=[RuntimeException](_ =?= "Boom!" \\| "expected Boom!")</code> |

#### Regular Expressions ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| =^= | Compares a String to a regular expression | "Path error: /some/path" =^= "Path error:.*".r |

#### Option ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| some_? | expects a Some(value) | <code>some_?\[Int\](20.some)(_ =/= 10  \\| "Some(20) is not ten")</code>  |
| none_? | expects a None | <code>none_?\[Int\](noneValue)(pass \\| "expected None")</code> |

#### Either ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| left_? | expects Left value | <code>left_?\[String, Int\](leftValue)(_.endsWith("or1") \\| "ends with or1")</code> |
| right_? | expects a Right value | <code>right_?\[String, Int\](rightValue)(_ =/= 10 \\| "right is not ten")</code> |

#### null ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| null_? | expects a null value | <code>null_?(nullValue)(pass \\| "null value")</code> |
| null_! | expects a non-null value | <code>null_!(notNullString)(_  =?= "notNull" \\| "not null String" )</code> |

---


## Publishing

To publish a new version perform the following tasks:

```
publish
bintrayRelease
```